package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.exception.ContestTargetFormatException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class Contest extends Subevent {

    private static final Logger LOGGER = LoggerFactory.getLogger(Contest.class);

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
        super.prepare(context);
        // when target is a static value or a save difficulty class, source wins by matching or exceeding target.
        // when target is an ability check, source wins only by exceeding target.
        boolean mustExceedTarget;
        try {
          mustExceedTarget = this.subeventJson.getJsonObject("target_contest").getString("subevent").equals("ability_check");
        } catch (NullPointerException e) {
            mustExceedTarget = false;
        }
        this.subeventJson.putBoolean("must_exceed_target", mustExceedTarget);
    }

    @Override
    public void invoke(RPGLContext context) throws Exception {
        super.invoke(context);
        int sourceResult = this.getSourceResult(context);
        int targetResult = this.getTargetResult(context);
        if (this.subeventJson.getBoolean("must_exceed_target")) {
            targetResult++;
        }

        if (sourceResult >= targetResult) {
            this.resolveNestedSubevents("source_wins", context);
        } else {
            this.resolveNestedSubevents("target_wins", context);
        }
    }

    int getSourceResult(RPGLContext context) throws Exception {
        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.joinSubeventData(this.subeventJson.getJsonObject("source_contest"));
        abilityCheck.setSource(this.getSource());
        abilityCheck.prepare(context);
        abilityCheck.subeventJson.getJsonArray("tags").asList().addAll(this.subeventJson.getJsonArray("tags").asList());
        abilityCheck.setTarget(this.getSource());
        abilityCheck.invoke(context);
        return abilityCheck.get();
    }

    int getTargetResult(RPGLContext context) throws Exception {
        Integer targetResult = this.subeventJson.getInteger("target_contest");
        if (targetResult != null) {
            return targetResult;
        }

        JsonObject targetContestJson = this.subeventJson.getJsonObject("target_contest");
        String subeventId = targetContestJson.getString("subevent");
        if (subeventId.equals("calculate_save_difficulty_class")) {
            return this.getTargetResultAsSaveDifficultyClass(targetContestJson, context);
        } else if (subeventId.equals("ability_check")) {
            return this.getTargetResultAsAbilityCheck(targetContestJson, context);
        }

        ContestTargetFormatException e = new ContestTargetFormatException(subeventId);
        LOGGER.error(e.getMessage());
        throw e;
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
        abilityCheck.setSource(this.getTarget());
        abilityCheck.prepare(context);
        abilityCheck.subeventJson.getJsonArray("tags").asList().addAll(this.subeventJson.getJsonArray("tags").asList());
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
