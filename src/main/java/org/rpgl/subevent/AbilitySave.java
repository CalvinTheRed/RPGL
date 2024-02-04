package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

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

    // TODO base and target damage collections?

    public AbilitySave() {
        super("ability_save");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new AbilitySave();
        clone.joinSubeventData(this.json);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new AbilitySave();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public AbilitySave invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (AbilitySave) super.invoke(context, originPoint);
    }

    @Override
    public AbilitySave joinSubeventData(JsonObject other) {
        return (AbilitySave) super.joinSubeventData(other);
    }

    @Override
    public AbilitySave prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        this.json.asMap().putIfAbsent("use_origin_difficulty_class_ability", false);
        this.calculateDifficultyClass(context);
        return this;
    }

    @Override
    public AbilitySave run(RPGLContext context, JsonArray originPoint) throws Exception {
        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.joinSubeventData(new JsonObject() {{
            this.putString("ability", json.getString("ability"));
            this.putString("skill", json.getString("skill"));
            this.putJsonArray("tags", new JsonArray(json.getJsonArray("tags").asList()));
            this.putJsonArray("determined", json.getJsonArray("determined"));
        }});
        abilityCheck.setSource(super.getTarget());
        abilityCheck.prepare(context, originPoint);
        abilityCheck.setTarget(super.getSource());
        abilityCheck.invoke(context, originPoint);

        if (abilityCheck.get() < this.getDifficultyClass()) {
            this.resolveNestedSubevents("fail", context, originPoint);
        } else {
            this.resolveNestedSubevents("pass", context, originPoint);
        }
        return this;
    }

    @Override
    public AbilitySave setOriginItem(String originItem) {
        return (AbilitySave) super.setOriginItem(originItem);
    }

    @Override
    public AbilitySave setSource(RPGLObject source) {
        return (AbilitySave) super.setSource(source);
    }

    @Override
    public AbilitySave setTarget(RPGLObject target) {
        return (AbilitySave) super.setTarget(target);
    }

    /**
     * This helper method calculates and records the save DC of the ability save.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void calculateDifficultyClass(RPGLContext context) throws Exception {
        CalculateDifficultyClass calculateDifficultyClass = new CalculateDifficultyClass();

        Integer difficultyClass = this.getDifficultyClass();
        if (difficultyClass == null) {
            calculateDifficultyClass.joinSubeventData(new JsonObject() {{
                this.putString("difficulty_class_ability", json.getString("difficulty_class_ability"));
                this.putJsonArray("tags", json.getJsonArray("tags").deepClone());
            }});
        } else {
            calculateDifficultyClass.joinSubeventData(new JsonObject() {{
                this.putInteger("difficulty_class", difficultyClass);
                this.putJsonArray("tags", json.getJsonArray("tags").deepClone());
            }});
        }

        calculateDifficultyClass.setOriginItem(super.getOriginItem());
        calculateDifficultyClass.setSource(this.json.getBoolean("use_origin_difficulty_class_ability")
                ? UUIDTable.getObject(super.getSource().getOriginObject())
                : super.getSource()
        );
        calculateDifficultyClass.prepare(context, this.getSource().getPosition());
        calculateDifficultyClass.setTarget(super.getSource());
        calculateDifficultyClass.invoke(context, this.getSource().getPosition());

        this.json.putInteger("difficulty_class", calculateDifficultyClass.get());
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
    void resolveNestedSubevents(String passOrFail, RPGLContext context, JsonArray originPoint) throws Exception {
        JsonArray subeventJsonArray = this.json.getJsonArray(passOrFail);
        if (subeventJsonArray != null) {
            for (int i = 0; i < subeventJsonArray.size(); i++) {
                JsonObject subeventJson = subeventJsonArray.getJsonObject(i);
                Subevent subevent = Subevent.SUBEVENTS.get(subeventJson.getString("subevent")).clone(subeventJson);
                subevent.setSource(super.getSource());
                subevent.prepare(context, originPoint);
                subevent.setTarget(super.getTarget());
                subevent.invoke(context, originPoint);
            }
        }
    }

    /**
     * Returns the save's difficulty class. Note that this method may return null if called before the subevent is
     * prepared.
     *
     * @return the save's difficulty class
     */
    public Integer getDifficultyClass() {
        return this.json.getInteger("difficulty_class");
    }

}
