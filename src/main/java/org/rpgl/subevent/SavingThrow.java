package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

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
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new SavingThrow();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        this.addTag("saving_throw");
        this.calculateDifficultyClass(context);
        this.json.asMap().putIfAbsent("damage", new ArrayList<>());
        this.getBaseDamage(context);
    }

    @Override
    public void invoke(RPGLContext context) throws Exception {
        this.verifySubevent(this.subeventId);

        // Override invoke() code to insert additional post-preparatory logic
        RPGLObject target = this.getTarget();
        String saveAbility = this.getAbility(context);
        super.addBonus(new JsonObject() {{
            this.putInteger("bonus", target.getAbilityModifierFromAbilityName(saveAbility, context));
            this.putJsonArray("dice", new JsonArray());
        }});

        context.processSubevent(this, context);
        this.run(context);
        context.viewCompletedSubevent(this);
    }

    @Override
    public void run(RPGLContext context) throws Exception {
        if (this.isNotCanceled()) {
            this.roll();
            if (this.get() < this.json.getInteger("save_difficulty_class")) {
                this.getTargetDamage(context);
                this.deliverDamage("all", context);
                this.resolveNestedSubevents("fail", context);
            } else {
                this.getTargetDamage(context);
                this.deliverDamage(this.json.getString("damage_on_pass"), context);
                this.resolveNestedSubevents("pass", context);
            }
        }
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
        CalculateSaveDifficultyClass calculateSaveDifficultyClass = new CalculateSaveDifficultyClass();
        String difficultyClassAbility = this.json.getString("difficulty_class_ability");
        calculateSaveDifficultyClass.joinSubeventData(new JsonObject() {{
            this.putString("difficulty_class_ability", difficultyClassAbility);
            this.putJsonArray("tags", json.getJsonArray("tags").deepClone());
        }});
        RPGLObject source = this.getSource();
        calculateSaveDifficultyClass.setSource(source);
        calculateSaveDifficultyClass.prepare(context);
        calculateSaveDifficultyClass.setTarget(source);
        calculateSaveDifficultyClass.invoke(context);
        this.json.putInteger("save_difficulty_class", calculateSaveDifficultyClass.get());
    }

    /**
     * This helper method collects, rolls, and stores all target-agnostic damage for this Subevent.
     *
     * @param context the context in which the base damage for this Subevent is being calculated
     *
     * @throws Exception if an exception occurs
     */
    void getBaseDamage(RPGLContext context) throws Exception {
        /*
         * Collect base typed damage dice and bonuses
         */
        DamageCollection baseDamageCollection = new DamageCollection();
        baseDamageCollection.joinSubeventData(new JsonObject() {{
            this.putJsonArray("damage", json.getJsonArray("damage").deepClone());
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("base_damage_collection");
            }});
        }});
        baseDamageCollection.setSource(this.getSource());
        baseDamageCollection.prepare(context);
        baseDamageCollection.setTarget(this.getSource());
        baseDamageCollection.invoke(context);

        /*
         * Roll base damage dice
         */
        DamageRoll baseDamageRoll = new DamageRoll();
        baseDamageRoll.joinSubeventData(new JsonObject() {{
            this.putJsonArray("damage", baseDamageCollection.getDamageCollection().deepClone());
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("base_damage_roll");
            }});
        }});
        baseDamageRoll.setSource(this.getSource());
        baseDamageRoll.prepare(context);
        baseDamageRoll.setTarget(this.getSource());
        baseDamageRoll.invoke(context);

        /*
         * Replace damage key with base damage calculation
         */
        this.json.putJsonArray("damage", baseDamageRoll.getDamage());
    }

    /**
     * This helper method collects, rolls, and stores all target-specific damage for this Subevent.
     *
     * @param context the context in which the target damage for this Subevent is being calculated
     *
     * @throws Exception if an exception occurs
     */
    void getTargetDamage(RPGLContext context) throws Exception {
        /*
         * Collect target typed damage dice and bonuses
         */
        DamageCollection targetDamageCollection = new DamageCollection();
        targetDamageCollection.joinSubeventData(new JsonObject() {{
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("target_damage_collection");
            }});
        }});
        targetDamageCollection.setSource(this.getSource());
        targetDamageCollection.prepare(context);
        targetDamageCollection.setTarget(this.getTarget());
        targetDamageCollection.invoke(context);

        /*
         * Roll target damage dice
         */
        DamageRoll targetDamageRoll = new DamageRoll();
        targetDamageRoll.joinSubeventData(new JsonObject() {{
            this.putJsonArray("damage", targetDamageCollection.getDamageCollection().deepClone());
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("target_damage_roll");
            }});
        }});
        targetDamageRoll.setSource(this.getSource());
        targetDamageRoll.prepare(context);
        targetDamageRoll.setTarget(this.getTarget());
        targetDamageRoll.invoke(context);

        this.json.getJsonArray("damage").asList().addAll(targetDamageRoll.getDamage().asList());
    }

    /**
     * This helper method resolves any nested Subevents within this Subevent in accordance to whether <code>target</code>
     * passed or fails its saving throw.
     *
     * @param passOrFail a String indicating whether the saving throw was passed or failed
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

    /**
     * This helper method delivers the final damage collection to the Subevent target.
     *
     * @param damageProportion the proportion of the damage to be dealt (can be <code>"all"</code>, <code>"half"</code>,
     *                         or <code>"none"</code>).
     * @param context          the context in which the damage is being delivered to the target.
     *
     * @throws Exception if an exception occurs
     */
    void deliverDamage(String damageProportion, RPGLContext context) throws Exception {
        if (!"none".equals(damageProportion)) {
            DamageDelivery damageDelivery = new DamageDelivery();
            damageDelivery.joinSubeventData(new JsonObject() {{
                this.putJsonArray("damage", json.getJsonArray("damage"));
                this.putString("damage_proportion", damageProportion);
                this.putJsonArray("tags", json.getJsonArray("tags").deepClone());
            }});
            damageDelivery.setSource(this.getSource());
            damageDelivery.prepare(context);
            damageDelivery.setTarget(this.getTarget());
            damageDelivery.invoke(context);
            this.getTarget().receiveDamage(damageDelivery, context);
        }
    }

}
