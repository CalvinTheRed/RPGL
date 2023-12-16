package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.List;

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
    public void prepare(RPGLContext context, List<RPGLResource> resources) throws Exception {
        super.prepare(context, resources);
        this.calculateDifficultyClass(context, resources);
    }

    @Override
    public void run(RPGLContext context, List<RPGLResource> resources) throws Exception {
        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.joinSubeventData(new JsonObject() {{
            this.putString("ability", json.getString("ability"));
            this.putString("skill", json.getString("skill"));
            this.putJsonArray("tags", new JsonArray(json.getJsonArray("tags").asList()));
            this.putJsonArray("determined", json.getJsonArray("determined"));
        }});
        abilityCheck.setSource(this.getSource());
        abilityCheck.prepare(context, resources);
        abilityCheck.setTarget(this.getTarget());
        abilityCheck.invoke(context, resources);

        if (abilityCheck.get() < this.json.getInteger("save_difficulty_class")) {
            this.resolveNestedSubevents("fail", context, resources);
        } else {
            this.resolveNestedSubevents("pass", context, resources);
        }
    }

    /**
     * This helper method calculates and records the save DC of the ability save.
     *
     * @param context the context this Subevent takes place in
     * @param resources a list of resources used to produce this subevent
     *
     * @throws Exception if an exception occurs.
     */
    void calculateDifficultyClass(RPGLContext context, List<RPGLResource> resources) throws Exception {
        CalculateSaveDifficultyClass calculateSaveDifficultyClass = new CalculateSaveDifficultyClass();
        String difficultyClassAbility = this.json.getString("difficulty_class_ability");
        calculateSaveDifficultyClass.joinSubeventData(new JsonObject() {{
            this.putString("difficulty_class_ability", difficultyClassAbility);
            this.putJsonArray("tags", json.getJsonArray("tags").deepClone());
        }});
        calculateSaveDifficultyClass.setOriginItem(this.getOriginItem());
        calculateSaveDifficultyClass.setSource(this.getSource());
        calculateSaveDifficultyClass.prepare(context, resources);
        calculateSaveDifficultyClass.setTarget(this.getSource());
        calculateSaveDifficultyClass.invoke(context, resources);
        this.json.putInteger("save_difficulty_class", calculateSaveDifficultyClass.get());
    }

    /**
     * This helper method resolves any nested Subevents within this Subevent in accordance to whether <code>target</code>
     * passed or failed its ability save.
     *
     * @param passOrFail a String indicating whether the ability save was passed or failed
     * @param context the context this Subevent takes place in
     * @param resources a list of resources used to produce this subevent
     *
     * @throws Exception if an exception occurs.
     */
    void resolveNestedSubevents(String passOrFail, RPGLContext context, List<RPGLResource> resources) throws Exception {
        JsonArray subeventJsonArray = this.json.getJsonArray(passOrFail);
        if (subeventJsonArray != null) {
            for (int i = 0; i < subeventJsonArray.size(); i++) {
                JsonObject subeventJson = subeventJsonArray.getJsonObject(i);
                Subevent subevent = Subevent.SUBEVENTS.get(subeventJson.getString("subevent")).clone(subeventJson);
                subevent.setSource(this.getSource());
                subevent.prepare(context, resources);
                subevent.setTarget(this.getTarget());
                subevent.invoke(context, resources);
            }
        }
    }

}
