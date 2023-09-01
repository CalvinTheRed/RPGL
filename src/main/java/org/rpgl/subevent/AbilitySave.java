package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

/**
 * This Subevent is dedicated to making an ability save and resolving all fallout from making the save. This is a
 * high-level Subevent which can be referenced in an RPGLEvent template.
 * <br>
 * <br>
 * Source: an RPGLObject requiring that other RPGLObjects make an ability save
 * <br>
 * Target: an RPGLObject making an ability save
 *
 * @author Calvin Withun
 */
public class AbilitySave extends Subevent {

    public AbilitySave() {
        super("ability_save");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new AbilitySave();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new AbilitySave();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        // Add tag so nested subevents such as DamageCollection can know they hail from an ability save.
        this.addTag("ability_save");
        this.calculateDifficultyClass(context);
    }

    @Override
    public void run(RPGLContext context) throws Exception {
        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.joinSubeventData(new JsonObject() {{
            this.putString("ability", json.getString("ability"));
            this.putString("skill", json.getString("skill"));
            this.putJsonArray("tags", new JsonArray(json.getJsonArray("tags").asList()));
            this.putJsonArray("determined", json.getJsonArray("determined"));
        }});
        abilityCheck.setSource(this.getSource());
        abilityCheck.prepare(context);
        abilityCheck.setTarget(this.getTarget());
        abilityCheck.invoke(context);

        if (abilityCheck.get() < this.json.getInteger("save_difficulty_class")) {
            this.resolveNestedSubevents("fail", context);
        } else {
            this.resolveNestedSubevents("pass", context);
        }
    }

    /**
     * This helper method calculates and records the save DC of the ability save.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void calculateDifficultyClass(RPGLContext context) throws Exception {
        CalculateSaveDifficultyClass calculateSaveDifficultyClass = new CalculateSaveDifficultyClass();
        String difficultyClassAbility = this.json.getString("difficulty_class_ability");
        calculateSaveDifficultyClass.joinSubeventData(new JsonObject() {{
            this.putString("difficulty_class_ability", difficultyClassAbility);
            this.putJsonArray("tags", json.getJsonArray("tags").deepClone());
        }});
        calculateSaveDifficultyClass.setOriginItem(this.getOriginItem());
        calculateSaveDifficultyClass.setSource(this.getSource());
        calculateSaveDifficultyClass.prepare(context);
        calculateSaveDifficultyClass.setTarget(this.getSource());
        calculateSaveDifficultyClass.invoke(context);
        this.json.putInteger("save_difficulty_class", calculateSaveDifficultyClass.get());
    }

    /**
     * This helper method resolves any nested Subevents within this Subevent in accordance to whether <code>target</code>
     * passed or failed its ability save.
     *
     * @param passOrFail a String indicating whether the ability save was passed or failed
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void resolveNestedSubevents(String passOrFail, RPGLContext context) throws Exception {
        JsonArray subeventJsonArray = this.json.getJsonArray(passOrFail);
        if (subeventJsonArray != null) {
            for (int i = 0; i < subeventJsonArray.size(); i++) {
                JsonObject subeventJson = subeventJsonArray.getJsonObject(i);
                Subevent subevent = Subevent.SUBEVENTS.get(subeventJson.getString("subevent")).clone(subeventJson);
                subevent.setSource(this.getSource());
                subevent.prepare(context);
                subevent.setTarget(this.getTarget());
                subevent.invoke(context);
            }
        }
    }

}
