package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public void prepare(RPGLContext context, List<RPGLResource> resources) throws Exception {
        super.prepare(context, resources);

        // Add tag so nested subevents such as DamageCollection can know they hail from a saving throw.
        this.addTag("saving_throw");

        this.calculateDifficultyClass(context, resources);
        this.json.asMap().putIfAbsent("damage", new ArrayList<>());
        this.getBaseDamage(context, resources);
    }

    @Override
    public void invoke(RPGLContext context, List<RPGLResource> resources) throws Exception {
        this.verifySubevent(this.subeventId);

        // Override invoke() code to insert additional post-preparatory logic
        RPGLObject target = this.getTarget();
        String saveAbility = this.getAbility(context);
        super.addBonus(new JsonObject() {{
            this.putInteger("bonus", target.getAbilityModifierFromAbilityName(saveAbility, context));
            this.putJsonArray("dice", new JsonArray());
        }});

        context.processSubevent(this, context, resources);
        this.run(context, resources);
        context.viewCompletedSubevent(this);
    }

    @Override
    public void run(RPGLContext context, List<RPGLResource> resources) throws Exception {
        if (this.isNotCanceled()) {
            this.roll();
            if (this.get() < this.json.getInteger("save_difficulty_class")) {
                this.getTargetDamage(context, resources);
                this.deliverDamage("all", context, resources);
                this.resolveNestedSubevents("fail", context, resources);
            } else {
                this.getTargetDamage(context, resources);
                this.deliverDamage(this.json.getString("damage_on_pass"), context, resources);
                this.resolveNestedSubevents("pass", context, resources);
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
        calculateSaveDifficultyClass.setSource(Objects.requireNonNullElse(
                this.json.getBoolean("use_origin_difficulty_class_ability"), false)
                ? UUIDTable.getObject(this.getSource().getOriginObject())
                : this.getSource()
        );
        calculateSaveDifficultyClass.prepare(context, resources);
        calculateSaveDifficultyClass.setTarget(this.getSource());
        calculateSaveDifficultyClass.invoke(context, resources);
        this.json.putInteger("save_difficulty_class", calculateSaveDifficultyClass.get());
    }

    /**
     * This helper method collects, rolls, and stores all target-agnostic damage for this Subevent.
     *
     * @param context the context in which the base damage for this Subevent is being calculated
     * @param resources a list of resources used to produce this subevent
     *
     * @throws Exception if an exception occurs
     */
    void getBaseDamage(RPGLContext context, List<RPGLResource> resources) throws Exception {
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
        baseDamageCollection.setOriginItem(this.getOriginItem());
        baseDamageCollection.setSource(this.getSource());
        baseDamageCollection.prepare(context, resources);
        baseDamageCollection.setTarget(this.getSource());
        baseDamageCollection.invoke(context, resources);

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
        baseDamageRoll.setOriginItem(this.getOriginItem());
        baseDamageRoll.setSource(this.getSource());
        baseDamageRoll.prepare(context, resources);
        baseDamageRoll.setTarget(this.getSource());
        baseDamageRoll.invoke(context, resources);

        /*
         * Replace damage key with base damage calculation
         */
        this.json.putJsonArray("damage", baseDamageRoll.getDamage());
    }

    /**
     * This helper method collects, rolls, and stores all target-specific damage for this Subevent.
     *
     * @param context the context in which the target damage for this Subevent is being calculated
     * @param resources a list of resources used to produce this subevent
     *
     * @throws Exception if an exception occurs
     */
    void getTargetDamage(RPGLContext context, List<RPGLResource> resources) throws Exception {
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
        targetDamageCollection.setOriginItem(this.getOriginItem());
        targetDamageCollection.setSource(this.getSource());
        targetDamageCollection.prepare(context, resources);
        targetDamageCollection.setTarget(this.getTarget());
        targetDamageCollection.invoke(context, resources);

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
        targetDamageRoll.setOriginItem(this.getOriginItem());
        targetDamageRoll.setSource(this.getSource());
        targetDamageRoll.prepare(context, resources);
        targetDamageRoll.setTarget(this.getTarget());
        targetDamageRoll.invoke(context, resources);

        this.json.getJsonArray("damage").asList().addAll(targetDamageRoll.getDamage().asList());
    }

    /**
     * This helper method resolves any nested Subevents within this Subevent in accordance to whether <code>target</code>
     * passed or failed its saving throw.
     *
     * @param passOrFail a String indicating whether the saving throw was passed or failed
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

    /**
     * This helper method delivers the final damage collection to the Subevent target.
     *
     * @param damageProportion the proportion of the damage to be dealt (can be <code>"all"</code>, <code>"half"</code>,
     *                         or <code>"none"</code>).
     * @param context the context in which the damage is being delivered to the target.
     * @param resources a list of resources used to produce this subevent
     *
     * @throws Exception if an exception occurs
     */
    void deliverDamage(String damageProportion, RPGLContext context, List<RPGLResource> resources) throws Exception {
        if (!"none".equals(damageProportion)) {
            DamageDelivery damageDelivery = new DamageDelivery();
            damageDelivery.joinSubeventData(new JsonObject() {{
                this.putJsonArray("damage", json.getJsonArray("damage"));
                this.putString("damage_proportion", damageProportion);
                this.putJsonArray("tags", json.getJsonArray("tags").deepClone());
            }});
            damageDelivery.setOriginItem(this.getOriginItem());
            damageDelivery.setSource(this.getSource());
            damageDelivery.prepare(context, resources);
            damageDelivery.setTarget(this.getTarget());
            damageDelivery.invoke(context, resources);

            JsonObject damageByType = damageDelivery.getTarget().receiveDamage(damageDelivery, context);
            if (this.json.asMap().containsKey("vampirism")) {
                VampiricSubevent.handleVampirism(this, damageByType, context, resources);
            }
        }
    }

}
