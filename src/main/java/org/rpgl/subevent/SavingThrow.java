package org.rpgl.subevent;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;

import java.util.Map;

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
        clone.joinSubeventJson(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new SavingThrow();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        this.calculateDifficultyClass(context);
        if (this.subeventJson.get("damage") != null) {
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
        this.addBonus(target.getAbilityModifierFromAbilityScore(context, (String) this.subeventJson.get("save_ability")));
        if (target.isProficientInSavingThrow(context, (String) this.subeventJson.get("save_ability"))) {
            this.addBonus(target.getProficiencyBonus(context));
        }

        context.processSubevent(this);

        /*
         * Resume normal invoke() logic here
         */
        this.roll();
        this.checkForReroll(context); // TODO eventually have this in a while loop?
        if (this.get() < (Long) this.subeventJson.get("save_difficulty_class")) {
            this.resolveSaveFail(context);
        } else {
            this.resolveSavePass(context);
        }
    }

    /**
     * 	<p>
     * 	<b><i>calculateDifficultyClass</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void calculateDifficultyClass(RPGLContext context)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method calculates and records the save DC of the saving throw.
     * 	</p>
     *
     *  @param context the context this Subevent takes place in
     *
     * 	@throws Exception if an exception occurs.
     */
    void calculateDifficultyClass(RPGLContext context) throws Exception {
        CalculateSaveDifficultyClass calculateSaveDifficultyClass = new CalculateSaveDifficultyClass();
        String calculateSaveDifficultyClassJsonString = String.format("""
                        {
                            "subevent": "calculate_save_difficulty_class",
                            "difficulty_class_ability": "%s"
                        }
                        """,
                this.subeventJson.get("difficulty_class_ability").toString()
        );
        JsonObject calculateSaveDifficultyClassJson = JsonParser.parseObjectString(calculateSaveDifficultyClassJsonString);
        calculateSaveDifficultyClass.joinSubeventJson(calculateSaveDifficultyClassJson);
        RPGLObject source = this.getSource();
        calculateSaveDifficultyClass.setSource(source);
        calculateSaveDifficultyClass.prepare(context);
        calculateSaveDifficultyClass.setTarget(source);
        calculateSaveDifficultyClass.invoke(context);
        this.subeventJson.put("save_difficulty_class", calculateSaveDifficultyClass.get());
    }

    /**
     * 	<p>
     * 	<b><i>getBaseDamage</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void getBaseDamage(RPGLContext context)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method collects the base damage of the saving throw. This includes all target-agnostic damage dice and
     * 	bonuses involved in the saving throw's damage roll.
     * 	</p>
     *
     *  @param context the context this Subevent takes place in
     *
     * 	@throws Exception if an exception occurs.
     */
    void getBaseDamage(RPGLContext context) throws Exception {
        /*
         * Collect base typed damage dice and bonuses
         */
        BaseDamageDiceCollection baseDamageDiceCollection = new BaseDamageDiceCollection();
        String baseDamageDiceCollectionJsonString = String.format("""
                        {
                            "subevent": "base_damage_dice_collection",
                            "damage": %s
                        }
                        """,
                this.subeventJson.get("damage").toString()
        );
        JsonObject baseDamageDiceCollectionJson = JsonParser.parseObjectString(baseDamageDiceCollectionJsonString);
        baseDamageDiceCollection.joinSubeventJson(baseDamageDiceCollectionJson);
        baseDamageDiceCollection.prepare(context);
        baseDamageDiceCollection.invoke(context);

        /*
         * Roll base damage dice
         */
        BaseDamageRoll baseDamageRoll = new BaseDamageRoll();
        String baseDamageRollJsonString = String.format("""
                        {
                            "subevent": "base_damage_roll",
                            "damage": %s
                        }
                        """,
                baseDamageDiceCollection.getDamageDiceCollection().toString()
        );
        JsonObject baseDamageRollJson = JsonParser.parseObjectString(baseDamageRollJsonString);
        baseDamageRoll.joinSubeventJson(baseDamageRollJson);
        baseDamageRoll.prepare(context);
        baseDamageRoll.invoke(context);

        /*
         * Replace damage key with base damage calculation
         */
        this.subeventJson.put("damage", baseDamageRoll.getDamage());
    }

    /**
     * 	<p>
     * 	<b><i>resolveSavePass</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void resolveSavePass(RPGLContext context)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method resolves the Subevent in the case that <code>target</code> passes its saving throw.
     * 	</p>
     *
     *  @param context the context this Subevent takes place in
     *
     * 	@throws Exception if an exception occurs.
     */
    void resolveSavePass(RPGLContext context) throws Exception {
        this.resolvePassDamage(context);
        this.resolveNestedSubevents(context, "pass");
    }

    /**
     * 	<p>
     * 	<b><i>resolveSaveFail</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void resolveSaveFail(RPGLContext context)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method resolves the Subevent in the case that <code>target</code> fails its saving throw.
     * 	</p>
     *
     *  @param context the context this Subevent takes place in
     *
     * 	@throws Exception if an exception occurs.
     */
    void resolveSaveFail(RPGLContext context) throws Exception {
        this.resolveFailDamage(context);
        this.resolveNestedSubevents(context, "fail");
    }

    /**
     * 	<p>
     * 	<b><i>getTargetDamage</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * JsonObject getTargetDamage(RPGLContext context)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method returns all target-specific damage dice and bonuses involved in the saving throw's damage roll.
     * 	</p>
     *
     *  @param context the context this Subevent takes place in
     *  @return a collection of rolled damage dice and bonuses
     *
     * 	@throws Exception if an exception occurs.
     */
    JsonObject getTargetDamage(RPGLContext context) throws Exception {
        /*
         * Collect target typed damage dice and bonuses
         */
        TargetDamageDiceCollection targetDamageDiceCollection = new TargetDamageDiceCollection();
        String targetDamageDiceCollectionJsonString = """
                {
                    "subevent": "target_damage_dice_collection",
                    "damage": [ ]
                }
                """; // TODO can the empty array be moved to prepare()?
        JsonObject targetDamageDiceCollectionJson = JsonParser.parseObjectString(targetDamageDiceCollectionJsonString);
        targetDamageDiceCollection.joinSubeventJson(targetDamageDiceCollectionJson);
        targetDamageDiceCollection.prepare(context);
        targetDamageDiceCollection.invoke(context);

        /*
         * Roll target damage dice
         */
        TargetDamageRoll targetDamageRoll = new TargetDamageRoll();
        String targetDamageRollJsonString = String.format("""
                        {
                            "subevent": "target_damage_roll",
                            "damage": %s
                        }
                        """,
                targetDamageDiceCollection.getDamageDiceCollection().toString()
        );
        JsonObject targetDamageRollJson = JsonParser.parseObjectString(targetDamageRollJsonString);
        targetDamageRoll.joinSubeventJson(targetDamageRollJson);
        targetDamageRoll.prepare(context);
        targetDamageRoll.invoke(context);

        return targetDamageRoll.getDamage();
    }

    /**
     * 	<p>
     * 	<b><i>resolvePassDamage</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void resolvePassDamage(RPGLContext context)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method determines and delivers the saving throw's damage roll in the case where <code>target</code>
     * 	passed its saving throw. This may deal half damage or no damage, according to the saving throw JSON data.
     * 	</p>
     *
     *  @param context the context this Subevent takes place in
     *
     * 	@throws Exception if an exception occurs.
     */
    void resolvePassDamage(RPGLContext context) throws Exception {
        JsonObject baseDamage = (JsonObject) this.subeventJson.get("damage");
        String damageOnPass = (String) this.subeventJson.get("damage_on_pass");
        if (baseDamage != null && !"none".equals(damageOnPass)) {
            /*
             * Add base and target damage into final damage quantities
             */
            for (Map.Entry<String, Object> targetDamageEntry : getTargetDamage(context).entrySet()) {
                String damageType = targetDamageEntry.getKey();
                if (baseDamage.containsKey(damageType)) {
                    Long baseTypedDamage = (Long) baseDamage.get(damageType);
                    baseTypedDamage += (Long) targetDamageEntry.getValue();
                    if (baseTypedDamage < 0L) {
                        baseTypedDamage = 0L; // You can never deal less than 0 points of damage when you deal damage
                    }
                    baseDamage.put(damageType, baseTypedDamage);
                } else {
                    baseDamage.entrySet().add(targetDamageEntry);
                }
            }

            /*
             * Account for half or no damage on pass (this should be a redundant check if this code is reached)
             */
            if ("half".equals(damageOnPass)) {
                for (Map.Entry<String, Object> damageEntryElement : baseDamage.entrySet()) {
                    Long value = (Long) damageEntryElement.getValue();
                    value /= 2L;
                    damageEntryElement.setValue(value);
                }
            }

            this.deliverDamage(context);
        }
    }

    /**
     * 	<p>
     * 	<b><i>resolveFailDamage</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void resolveFailDamage(RPGLContext context)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method determines and delivers the saving throw's damage roll in the case where <code>target</code>
     * 	failed its saving throw. This should only ever deal full damage.
     * 	</p>
     *
     *  @param context the context this Subevent takes place in
     *
     * 	@throws Exception if an exception occurs.
     */
    void resolveFailDamage(RPGLContext context) throws Exception {
        JsonObject baseDamage = (JsonObject) this.subeventJson.get("damage");
        if (baseDamage != null) {
            for (Map.Entry<String, Object> targetDamageEntry : getTargetDamage(context).entrySet()) {
                String damageType = targetDamageEntry.getKey();
                if (baseDamage.containsKey(damageType)) {
                    Long baseTypedDamage = (Long) baseDamage.get(damageType);
                    baseTypedDamage += (Long) targetDamageEntry.getValue();
                    baseDamage.put(damageType, baseTypedDamage);
                } else {
                    baseDamage.entrySet().add(targetDamageEntry);
                }
            }

            this.deliverDamage(context);
        }
    }

    /**
     * 	<p>
     * 	<b><i>resolveNestedSubevents</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void resolveNestedSubevents(RPGLContext context, String passOrFail)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method resolves any nested Subevents within this Subevent in accordance to whether <code>target</code>
     * 	passed or fails its saving throw.
     * 	</p>
     *
     *  @param context the context this Subevent takes place in
     *
     * 	@throws Exception if an exception occurs.
     */
    void resolveNestedSubevents(RPGLContext context, String passOrFail) throws Exception {
        JsonArray subeventJsonArray = (JsonArray) this.subeventJson.get(passOrFail);
        if (subeventJsonArray != null) {
            for (Object subeventJsonElement : subeventJsonArray) {
                JsonObject subeventJson = (JsonObject) subeventJsonElement;
                Subevent subevent = Subevent.SUBEVENTS.get((String) subeventJson.get("subevent")).clone(subeventJson);
                subevent.prepare(context);
                subevent.invoke(context);
            }
        }
    }

    /**
     * 	<p>
     * 	<b><i>deliverDamage</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void deliverDamage(RPGLContext context)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method delivers a final quantity of damage to <code>target</code> after the saving throw has been calculated.
     * 	</p>
     *
     *  @param context the context this Subevent takes place in
     *
     * 	@throws Exception if an exception occurs.
     */
    void deliverDamage(RPGLContext context) throws Exception {
        DamageDelivery damageDelivery = new DamageDelivery();
        String damageDeliveryJsonString = String.format("""
                        {
                            "subevent": "damage_delivery",
                            "damage": %s
                        }
                        """,
                this.subeventJson.get("damage").toString()
        );
        JsonObject damageDeliveryJson = JsonParser.parseObjectString(damageDeliveryJsonString);
        damageDelivery.joinSubeventJson(damageDeliveryJson);
        damageDelivery.setSource(this.getSource());
        damageDelivery.prepare(context);
        damageDelivery.setTarget(this.getTarget());
        damageDelivery.invoke(context);
        this.getTarget().receiveDamage(context, damageDelivery);
    }

}
