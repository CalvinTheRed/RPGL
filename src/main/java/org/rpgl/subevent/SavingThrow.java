package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.Map;
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
public class SavingThrow extends ContestRoll {

    public SavingThrow() {
        super("saving_throw");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new SavingThrow();
        clone.joinSubeventData(this.subeventJson);
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
        this.calculateDifficultyClass(context);
        if (this.subeventJson.getJsonArray("damage") != null) {
            this.getBaseDamage(context);
        }
    }

    @Override
    public void invoke(RPGLContext context) throws Exception {
        /*
         * Override normal invoke() logic
         */
        this.verifySubevent(this.subeventId);

        RPGLObject target = this.getTarget();
        String saveAbility = this.subeventJson.getString("save_ability");
        this.addBonus(target.getAbilityModifierFromAbilityName(context, saveAbility));
        if (target.isProficientInSavingThrow(context, saveAbility)) {
            this.addBonus(target.getEffectiveProficiencyBonus(context));
        }

        context.processSubevent(this);

        /*
         * Resume normal invoke() logic here
         */
        this.roll();
        this.checkForReroll(context); // TODO eventually have this in a while loop? Add this to AttackRoll?
        if (this.get() < this.subeventJson.getInteger("save_difficulty_class")) {
            this.resolveSaveFail(context);
        } else {
            this.resolveSavePass(context);
        }
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
        String difficultyClassAbility = this.subeventJson.getString("difficulty_class_ability");
        calculateSaveDifficultyClass.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "calculate_save_difficulty_class");
            this.putString("difficulty_class_ability", difficultyClassAbility);
        }});
        RPGLObject source = this.getSource();
        calculateSaveDifficultyClass.setSource(source);
        calculateSaveDifficultyClass.prepare(context);
        calculateSaveDifficultyClass.setTarget(source);
        calculateSaveDifficultyClass.invoke(context);
        this.subeventJson.putInteger("save_difficulty_class", calculateSaveDifficultyClass.get());
    }

    /**
     * This helper method collects the base damage of the saving throw. This includes all target-agnostic damage dice and
     * bonuses involved in the saving throw's damage roll.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void getBaseDamage(RPGLContext context) throws Exception {
        /*
         * Collect base typed damage dice and bonuses
         */
        BaseDamageCollection baseDamageCollection = new BaseDamageCollection();
        JsonArray damage = this.subeventJson.getJsonArray("damage");
        baseDamageCollection.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "base_damage_collection");
            this.putJsonArray("damage", damage.deepClone());
        }});
        baseDamageCollection.setSource(this.getSource());
        baseDamageCollection.prepare(context);
        baseDamageCollection.setTarget(this.getTarget());
        baseDamageCollection.invoke(context);

        /*
         * Roll base damage dice
         */
        BaseDamageRoll baseDamageRoll = new BaseDamageRoll();
        baseDamageRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "base_damage_roll");
            this.putJsonArray("damage", baseDamageCollection.getDamageCollection().deepClone());
        }});
        baseDamageRoll.setSource(this.getSource());
        baseDamageRoll.prepare(context);
        baseDamageRoll.setTarget(this.getTarget());
        baseDamageRoll.invoke(context);

        /*
         * Replace damage key with base damage calculation
         */
        this.subeventJson.putJsonObject("damage", baseDamageRoll.getDamage());
    }

    /**
     * This helper method resolves the Subevent in the case that <code>target</code> passes its saving throw.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void resolveSavePass(RPGLContext context) throws Exception {
        this.resolvePassDamage(context);
        this.resolveNestedSubevents(context, "pass");
    }

    /**
     * This helper method resolves the Subevent in the case that <code>target</code> fails its saving throw.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void resolveSaveFail(RPGLContext context) throws Exception {
        this.resolveFailDamage(context);
        this.resolveNestedSubevents(context, "fail");
    }

    /**
     * This helper method returns all target-specific damage dice and bonuses involved in the saving throw's damage roll.
     *
     * @param context the context this Subevent takes place in
     * @return a collection of rolled damage dice and bonuses
     *
     * @throws Exception if an exception occurs.
     */
    JsonObject getTargetDamage(RPGLContext context) throws Exception {
        /*
         * Collect target typed damage dice and bonuses
         */
        TargetDamageCollection targetDamageCollection = new TargetDamageCollection();
        targetDamageCollection.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "target_damage_collection");
            this.putJsonArray("damage", new JsonArray()); // TODO can the empty array be moved to prepare()?
        }});
        targetDamageCollection.setSource(this.getSource());
        targetDamageCollection.prepare(context);
        targetDamageCollection.setTarget(this.getTarget());
        targetDamageCollection.invoke(context);

        /*
         * Roll target damage dice
         */
        TargetDamageRoll targetDamageRoll = new TargetDamageRoll();
        targetDamageRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "target_damage_roll");
            this.putJsonArray("damage", targetDamageCollection.getDamageCollection().deepClone());
        }});
        targetDamageRoll.setSource(this.getSource());
        targetDamageRoll.prepare(context);
        targetDamageRoll.setTarget(this.getTarget());
        targetDamageRoll.invoke(context);

        return targetDamageRoll.getDamage();
    }

    /**
     * This helper method determines and delivers the saving throw's damage roll in the case where <code>target</code>
     * passed its saving throw. This may deal half damage or no damage, according to the saving throw JSON data.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void resolvePassDamage(RPGLContext context) throws Exception {
        JsonObject baseDamage = Objects.requireNonNullElse(this.subeventJson.getJsonObject("damage"), new JsonObject());
        String damageOnPass = this.subeventJson.getString("damage_on_pass");
        if (!"none".equals(damageOnPass)) {
            /*
             * Add base and target damage into final damage quantities
             */
            JsonObject targetDamage = getTargetDamage(context);
            for (Map.Entry<String, Object> targetDamageEntry : targetDamage.asMap().entrySet()) {
                String damageType = targetDamageEntry.getKey();
                if (baseDamage.asMap().containsKey(damageType)) {
                    Integer baseTypedDamage = baseDamage.getInteger(damageType);
                    baseTypedDamage += targetDamage.getInteger(targetDamageEntry.getKey());
                    if (baseTypedDamage < 0) {
                        baseTypedDamage = 0; // You can never deal less than 0 points of damage when you deal damage
                    }
                    baseDamage.putInteger(damageType, baseTypedDamage);
                } else {
                    baseDamage.asMap().entrySet().add(targetDamageEntry);
                }
            }

            /*
             * Account for half or no damage on pass (this should be a redundant check if this code is reached)
             */
            if ("half".equals(damageOnPass)) {
                for (Map.Entry<String, ?> damageEntryElement : baseDamage.asMap().entrySet()) {
                    Integer value = baseDamage.removeInteger(damageEntryElement.getKey());
                    baseDamage.putInteger(damageEntryElement.getKey(), value / 2);
                }
            }

            this.deliverDamage(context);
        }
    }

    /**
     * This helper method determines and delivers the saving throw's damage roll in the case where <code>target</code>
     * failed its saving throw. This should only ever deal full damage.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void resolveFailDamage(RPGLContext context) throws Exception {
        JsonObject baseDamage = Objects.requireNonNullElse(this.subeventJson.getJsonObject("damage"), new JsonObject());
        JsonObject targetDamage = this.getTargetDamage(context);
        for (Map.Entry<String, Object> targetDamageEntry : targetDamage.asMap().entrySet()) {
            String damageType = targetDamageEntry.getKey();
            if (baseDamage.asMap().containsKey(damageType)) {
                Integer baseTypedDamage = baseDamage.getInteger(damageType);
                baseTypedDamage += targetDamage.getInteger(targetDamageEntry.getKey());
                baseDamage.putInteger(damageType, baseTypedDamage);
            } else {
                baseDamage.asMap().entrySet().add(targetDamageEntry);
            }
        }
        this.deliverDamage(context);
    }

    /**
     * This helper method resolves any nested Subevents within this Subevent in accordance to whether <code>target</code>
     * passed or fails its saving throw.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void resolveNestedSubevents(RPGLContext context, String passOrFail) throws Exception {
        JsonArray subeventJsonArray = this.subeventJson.getJsonArray(passOrFail);
        if (subeventJsonArray != null) {
            for (int i = 0; i < subeventJsonArray.size(); i++) {
                JsonObject subeventJson = subeventJsonArray.getJsonObject(i);
                Subevent subevent = Subevent.SUBEVENTS.get(subeventJson.getString("subevent")).clone(subeventJson);
                subevent.prepare(context);
                subevent.invoke(context);
            }
        }
    }

    /**
     * This helper method delivers a final quantity of damage to <code>target</code> after the saving throw has been calculated.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void deliverDamage(RPGLContext context) throws Exception {
        DamageDelivery damageDelivery = new DamageDelivery();
        JsonObject damage = this.subeventJson.getJsonObject("damage");
        damageDelivery.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "damage_delivery");
            this.putJsonObject("damage", damage.deepClone());
        }});
        damageDelivery.setSource(this.getSource());
        damageDelivery.prepare(context);
        damageDelivery.setTarget(this.getTarget());
        damageDelivery.invoke(context);
        this.getTarget().receiveDamage(context, damageDelivery);
    }

}
