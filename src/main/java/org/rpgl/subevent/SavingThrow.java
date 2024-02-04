package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.function.AddBonus;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.util.ArrayList;

/**
 * This Subevent is dedicated to making a saving throw and resolving all fallout from making the save. This is a
 * high-level Subevent which can be referenced in an RPGLEvent template.
 * <br>
 * <br>
 * Source: an RPGLObject requiring that other RPGLObjects make a saving throw
 * <br>
 * Target: an RPGLObject making a saving throw
 *
 * @author Calvin Withun
 */
public class SavingThrow extends Roll {

    public SavingThrow() {
        super("saving_throw");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new SavingThrow();
        clone.joinSubeventData(this.json);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new SavingThrow();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public SavingThrow invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        this.verifySubevent(this.subeventId);

        // Override invoke() code to insert additional post-preparatory logic
        new AddBonus().execute(null, this, new JsonObject() {{
                /*{
                    "function": "add_bonus",
                    "bonus": [
                        {
                            "formula": "modifier",
                            "ability": <getAbility>,
                            "object": {
                                "from": "subevent",
                                "object", "target"
                            }
                        }
                    ]
                }*/
            this.putString("function", "add_bonus");
            this.putJsonArray("bonus", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "modifier");
                    this.putString("ability", getAbility(context));
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "subevent");
                        this.putString("object", "target");
                    }});
                }});
            }});
        }}, context, originPoint);

        context.processSubevent(this, context, originPoint);
        this.run(context, originPoint);
        context.viewCompletedSubevent(this);
        return this;
    }

    @Override
    public SavingThrow joinSubeventData(JsonObject other) {
        return (SavingThrow) super.joinSubeventData(other);
    }

    @Override
    public SavingThrow prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        this.json.asMap().putIfAbsent("damage", new ArrayList<>());
        this.json.asMap().putIfAbsent("use_origin_difficulty_class_ability", false);
        this.calculateDifficultyClass(context);
        this.getBaseDamage(context, originPoint);
        return this;
    }

    @Override
    public SavingThrow run(RPGLContext context, JsonArray originPoint) throws Exception {
        if (this.isNotCanceled()) {
            this.roll();
            if (super.get() < this.getDifficultyClass()) {
                this.getTargetDamage(context, originPoint);
                this.deliverDamage("all", context, originPoint);
                this.resolveNestedSubevents("fail", context, originPoint);
            } else {
                this.getTargetDamage(context, originPoint);
                this.deliverDamage(this.json.getString("damage_on_pass"), context, originPoint);
                this.resolveNestedSubevents("pass", context, originPoint);
            }
        }
        return this;
    }

    @Override
    public SavingThrow setOriginItem(String originItem) {
        return (SavingThrow) super.setOriginItem(originItem);
    }

    @Override
    public SavingThrow setSource(RPGLObject source) {
        return (SavingThrow) super.setSource(source);
    }

    @Override
    public SavingThrow setTarget(RPGLObject target) {
        return (SavingThrow) super.setTarget(target);
    }

    @Override
    public SavingThrow grantAdvantage() {
        return (SavingThrow) super.grantAdvantage();
    }

    @Override
    public SavingThrow grantDisadvantage() {
        return (SavingThrow) super.grantDisadvantage();
    }

    @Override
    public String getAbility(RPGLContext context) {
        return this.json.getString("save_ability");
    }

    /**
     * This helper method calculates and records the save DC of the saving throw.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void calculateDifficultyClass(RPGLContext context) throws Exception {
        Integer difficultyClass = this.getDifficultyClass();

        CalculateDifficultyClass calculateDifficultyClass = new CalculateDifficultyClass()
                .joinSubeventData(difficultyClass == null
                        ? new JsonObject() {{
                            this.putString("difficulty_class_ability", json.getString("difficulty_class_ability"));
                            this.putJsonArray("tags", json.getJsonArray("tags").deepClone());
                        }}
                        : new JsonObject() {{
                            this.putInteger("difficulty_class", difficultyClass);
                            this.putJsonArray("tags", json.getJsonArray("tags").deepClone());
                        }})
                .setOriginItem(super.getOriginItem())
                .setSource(this.json.getBoolean("use_origin_difficulty_class_ability")
                        ? UUIDTable.getObject(super.getSource().getOriginObject())
                        : super.getSource()
                )
                .prepare(context, this.getSource().getPosition())
                .setTarget(super.getSource())
                .invoke(context, this.getSource().getPosition());

        this.json.putInteger("difficulty_class", calculateDifficultyClass.get());
    }

    /**
     * This helper method collects, rolls, and stores all target-agnostic damage for this Subevent.
     *
     * @param context the context in which the base damage for this Subevent is being calculated
     * @param originPoint the point from which this subevent emanates
     *
     * @throws Exception if an exception occurs
     */
    void getBaseDamage(RPGLContext context, JsonArray originPoint) throws Exception {
        /*
         * Collect base typed damage dice and bonuses
         */
        DamageCollection baseDamageCollection = new DamageCollection()
                .joinSubeventData(new JsonObject() {{
                    this.putJsonArray("damage", json.getJsonArray("damage").deepClone());
                    this.putJsonArray("tags", new JsonArray() {{
                        this.asList().addAll(json.getJsonArray("tags").asList());
                        this.addString("base_damage_collection");
                    }});
                }})
                .setOriginItem(super.getOriginItem())
                .setSource(super.getSource())
                .prepare(context, originPoint)
                .setTarget(super.getSource())
                .invoke(context, originPoint);

        /*
         * Roll base damage dice
         */
        DamageRoll baseDamageRoll = new DamageRoll()
                .joinSubeventData(new JsonObject() {{
                    this.putJsonArray("damage", baseDamageCollection.getDamageCollection().deepClone());
                    this.putJsonArray("tags", new JsonArray() {{
                        this.asList().addAll(json.getJsonArray("tags").asList());
                        this.addString("base_damage_roll");
                    }});
                }})
                .setOriginItem(super.getOriginItem())
                .setSource(super.getSource())
                .prepare(context, originPoint)
                .setTarget(super.getSource())
                .invoke(context, originPoint);

        /*
         * Replace damage key with base damage calculation
         */
        this.json.putJsonArray("damage", baseDamageRoll.getDamage());
    }

    /**
     * This helper method collects, rolls, and stores all target-specific damage for this Subevent.
     *
     * @param context the context in which the target damage for this Subevent is being calculated
     * @param originPoint the point from which this subevent emanates
     *
     * @throws Exception if an exception occurs
     */
    void getTargetDamage(RPGLContext context, JsonArray originPoint) throws Exception {
        /*
         * Collect target typed damage dice and bonuses
         */
        DamageCollection targetDamageCollection = new DamageCollection()
                .joinSubeventData(new JsonObject() {{
                    this.putJsonArray("tags", new JsonArray() {{
                        this.asList().addAll(json.getJsonArray("tags").asList());
                        this.addString("target_damage_collection");
                    }});
                }})
                .setOriginItem(super.getOriginItem())
                .setSource(super.getSource())
                .prepare(context, originPoint)
                .setTarget(super.getTarget())
                .invoke(context, originPoint);

        /*
         * Roll target damage dice
         */
        DamageRoll targetDamageRoll = new DamageRoll()
                .joinSubeventData(new JsonObject() {{
                    this.putJsonArray("damage", targetDamageCollection.getDamageCollection().deepClone());
                    this.putJsonArray("tags", new JsonArray() {{
                        this.asList().addAll(json.getJsonArray("tags").asList());
                        this.addString("target_damage_roll");
                    }});
                }})
                .setOriginItem(super.getOriginItem())
                .setSource(super.getSource())
                .prepare(context, originPoint)
                .setTarget(super.getTarget())
                .invoke(context, originPoint);

        this.json.getJsonArray("damage").asList().addAll(targetDamageRoll.getDamage().asList());
    }

    /**
     * This helper method resolves any nested Subevents within this Subevent in accordance to whether <code>target</code>
     * passed or failed its saving throw.
     *
     * @param passOrFail a String indicating whether the saving throw was passed or failed
     * @param context the context this Subevent takes place in
     * @param originPoint the point from which this subevent emanates
     *
     * @throws Exception if an exception occurs.
     */
    void resolveNestedSubevents(String passOrFail, RPGLContext context, JsonArray originPoint) throws Exception {
        JsonArray subeventJsonArray = this.json.getJsonArray(passOrFail);
        if (subeventJsonArray != null) {
            for (int i = 0; i < subeventJsonArray.size(); i++) {
                JsonObject subeventJson = subeventJsonArray.getJsonObject(i);
                Subevent.SUBEVENTS.get(subeventJson.getString("subevent")).clone(subeventJson)
                        .setSource(super.getSource())
                        .prepare(context, originPoint)
                        .setTarget(super.getTarget())
                        .invoke(context, originPoint);
            }
        }
    }

    /**
     * This helper method delivers the final damage collection to the Subevent target.
     *
     * @param damageProportion the proportion of the damage to be dealt (can be <code>"all"</code>, <code>"half"</code>,
     *                         or <code>"none"</code>)
     * @param context the context in which the damage is being delivered to the target
     * @param originPoint the point from which this subevent emanates
     *
     * @throws Exception if an exception occurs
     */
    void deliverDamage(String damageProportion, RPGLContext context, JsonArray originPoint) throws Exception {
        if (!"none".equals(damageProportion)) {
            DamageDelivery damageDelivery = new DamageDelivery()
                    .joinSubeventData(new JsonObject() {{
                        this.putJsonArray("damage", json.getJsonArray("damage"));
                        this.putString("damage_proportion", damageProportion);
                        this.putJsonArray("tags", json.getJsonArray("tags").deepClone());
                    }})
                    .setOriginItem(super.getOriginItem())
                    .setSource(super.getSource())
                    .prepare(context, originPoint)
                    .setTarget(super.getTarget())
                    .invoke(context, originPoint);

            JsonObject damageByType = damageDelivery.getDamage();
            if (this.json.asMap().containsKey("vampirism")) {
                VampiricSubevent.handleVampirism(this, damageByType, context, originPoint);
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
