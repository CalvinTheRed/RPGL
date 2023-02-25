package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.Objects;

public class Contest extends Subevent {

    public Contest() {
        super("contest");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new Contest();
        clone.joinSubeventData(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new Contest();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        // die wins on a tie against a DC / save DC, but not against another ability check
        boolean mustExceedTarget = (this.subeventJson.getInteger("target") != null)
                || (this.subeventJson.getJsonObject("target").getString("subevent").equals("calculate_save_difficulty_class"));
        this.subeventJson.putBoolean("must_exceed_target", mustExceedTarget);
    }

    @Override
    public void invoke(RPGLContext context) throws Exception {
        super.invoke(context);
        int sourceResult = this.getSourceResult(context);
        int targetResult = this.getTargetResult(context);
        targetResult += this.subeventJson.getBoolean("must_exceed_target") ? 1 : 0;

        if (sourceResult >= targetResult) {
            this.resolveNestedSubevents("source_wins", context);
        } else {
            this.resolveNestedSubevents("target_wins", context);
        }
    }

    int getSourceResult(RPGLContext context) throws Exception {
        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.joinSubeventData(this.subeventJson.getJsonObject("source"));
        abilityCheck.subeventJson.getJsonArray("tags").asList().addAll(this.subeventJson.getJsonArray("tags").asList());
        abilityCheck.setSource(this.getSource());
        abilityCheck.prepare(context);
        abilityCheck.setTarget(this.getSource());
        abilityCheck.invoke(context);
        return abilityCheck.get();
    }

    int getTargetResult(RPGLContext context) throws Exception {
        Integer targetResult = this.subeventJson.getInteger("target");
        if (targetResult != null) {
            return targetResult;
        }

        JsonObject targetJson = this.subeventJson.getJsonObject("target");
        if (targetJson.getString("subevent").equals("calculate_save_difficulty_class")) {
            return this.getTargetResultAsSaveDifficultyClass(targetJson, context);
        } else if (targetJson.getString("subevent").equals("ability_check")) {
            return this.getTargetResultAsAbilityCheck(targetJson, context);
        }

        throw new Exception("contest target data invalid"); // TODO handle this better
    }

    int getTargetResultAsSaveDifficultyClass(JsonObject targetJson, RPGLContext context) throws Exception {
        CalculateSaveDifficultyClass calculateSaveDifficultyClass = new CalculateSaveDifficultyClass();
        calculateSaveDifficultyClass.joinSubeventData(targetJson);
        calculateSaveDifficultyClass.setSource(this.getTarget());
        calculateSaveDifficultyClass.prepare(context);
        calculateSaveDifficultyClass.setTarget(this.getTarget());
        calculateSaveDifficultyClass.invoke(context);
        return calculateSaveDifficultyClass.get();
    }

    int getTargetResultAsAbilityCheck(JsonObject targetJson, RPGLContext context) throws Exception {
        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.joinSubeventData(targetJson);
        abilityCheck.subeventJson.getJsonArray("tags").asList().addAll(this.subeventJson.getJsonArray("tags").asList());
        abilityCheck.setSource(this.getTarget());
        abilityCheck.prepare(context);
        abilityCheck.setTarget(this.getTarget());
        abilityCheck.invoke(context);
        return abilityCheck.get();
    }

    void resolveNestedSubevents(String whoWins, RPGLContext context) throws Exception {
        JsonArray subeventJsonArray = Objects.requireNonNullElse(this.subeventJson.getJsonArray(whoWins), new JsonArray());
        for (int i = 0; i < subeventJsonArray.size(); i++) {
            JsonObject nestedSubeventJson = subeventJsonArray.getJsonObject(i);
            Subevent subevent = Subevent.SUBEVENTS.get(nestedSubeventJson.getString("subevent")).clone(nestedSubeventJson);
            subevent.setSource(this.getSource());
            subevent.prepare(context);
            subevent.setTarget(this.getTarget());
            subevent.invoke(context);
        }
    }

}
