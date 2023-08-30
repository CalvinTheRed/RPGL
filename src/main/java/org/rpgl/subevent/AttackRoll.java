package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.util.ArrayList;
import java.util.Objects;

/**
 * This Subevent is dedicated to making an attack roll and resolving all fallout from making the attack. This is a
 * high-level Subevent which can be referenced in an RPGLEvent template.
 * <br>
 * <br>
 * Source: an RPGLObject making an attack
 * <br>
 * Target: an RPGLObject being attacked
 *
 * @author Calvin Withun
 */
public class AttackRoll extends Roll {

    public AttackRoll() {
        super("attack_roll");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new AttackRoll();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new AttackRoll();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        this.json.asMap().putIfAbsent("withhold_damage_modifier", false);

        // Add tags so nested subevents such as DamageCollection can know they
        // hail from an attack roll made using a particular attack ability.
        this.addTag("attack_roll");
        this.addTag(this.getAbility(context));

        // Proficiency is added by effects during subevent processing

        // Add weapon attack bonus, if applicable
        if (this.getOriginItem() != null) {
            this.addBonus(new JsonObject() {{
                this.putInteger("bonus", UUIDTable.getItem(getOriginItem()).getAttackBonus());
                this.putJsonArray("dice", new JsonArray());
            }});
        }
    }

    @Override
    public void run(RPGLContext context) throws Exception {
        if (this.isNotCanceled()) {
            this.roll();
            this.json.asMap().putIfAbsent("damage", new ArrayList<>());
            this.addBonus(new JsonObject() {{
                this.putInteger("bonus", getSource().getAbilityModifierFromAbilityName(getAbility(context), context));
                this.putJsonArray("dice", new JsonArray());
            }});

            int armorClass = this.getTargetArmorClass(context);
            if (this.isCriticalHit(context)) {
                this.getBaseDamage(context);
                this.getTargetDamage(context);
                this.getCriticalHitDamage(context);
                this.resolveDamage(context);
                this.resolveNestedSubevents("hit", context);
            } else if (this.isCriticalMiss() || this.get() < armorClass) {
                this.resolveNestedSubevents("miss", context);
            } else {
                this.getBaseDamage(context);
                this.getTargetDamage(context);
                this.resolveDamage(context);
                this.resolveNestedSubevents("hit", context);
            }
        }
    }

    @Override
    public String getAbility(RPGLContext context) {
        return this.json.getString("attack_ability");
    }

    /**
     * This helper method collects and stores the base damage dice and bonuses in the AttackRoll.
     *
     * @param context the context in which the base damage is being determined
     *
     * @throws Exception if an exception occurs
     */
    void getBaseDamage(RPGLContext context) throws Exception {
        // Collect base typed damage dice and bonuses
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
        baseDamageCollection.prepare(context);
        baseDamageCollection.setTarget(this.getSource());
        baseDamageCollection.invoke(context);

        String damageType = this.json.getJsonArray("damage").getJsonObject(0).getString("damage_type");

        // Add damage modifier from attack ability, if applicable
        if (!this.json.getBoolean("withhold_damage_modifier")) { // TODO make a function ond condition for this stuff...
            int attackAbilityModifier = this.getSource().getAbilityModifierFromAbilityName(this.getAbility(context), context);
            baseDamageCollection.addDamage(new JsonObject() {{
                this.putString("damage_type", damageType);
                this.putJsonArray("dice", new JsonArray());
                this.putInteger("bonus", attackAbilityModifier);
            }});
        }

        // Add origin item damage bonus, if applicable
        if (this.getOriginItem() != null) {
            baseDamageCollection.addDamage(new JsonObject() {{
                this.putString("damage_type", damageType);
                this.putJsonArray("dice", new JsonArray());
                this.putInteger("bonus", UUIDTable.getItem(getOriginItem()).getDamageBonus());
            }});
        }

        // Replace damage key with base damage collection
        this.json.putJsonArray("damage", baseDamageCollection.getDamageCollection());
    }

    /**
     * This helper method collects and stores the target damage dice and bonuses in the AttackRoll.
     *
     * @param context the context in which the target damage is being determined
     *
     * @throws Exception if an exception occurs
     */
    void getTargetDamage(RPGLContext context) throws Exception {
        // Collect target typed damage dice and bonuses
        DamageCollection targetDamageCollection = new DamageCollection();
        targetDamageCollection.joinSubeventData(new JsonObject() {{
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("target_damage_collection");
            }});
        }});
        targetDamageCollection.setOriginItem(this.getOriginItem());
        targetDamageCollection.setSource(this.getSource());
        targetDamageCollection.prepare(context);
        targetDamageCollection.setTarget(this.getTarget());
        targetDamageCollection.invoke(context);

        // add target damage collection to base damage collection
        this.json.getJsonArray("damage").asList().addAll(targetDamageCollection.getDamageCollection().asList());
    }

    /**
     * This helper method evaluates the effective armor class of the target to determine if the attack hits or misses.
     * This value can be influenced by the target after the attack roll is made to attempt to avoid the attack, and may
     * be different than the target's base armor class.
     *
     * @param context the context this Subevent takes place in
     * @return the target's effective (final) armor class
     *
     * @throws Exception if an exception occurs.
     */
    int getTargetArmorClass(RPGLContext context) throws Exception {
        CalculateEffectiveArmorClass calculateEffectiveArmorClass = new CalculateEffectiveArmorClass();
        calculateEffectiveArmorClass.joinSubeventData(new JsonObject() {{
            this.putJsonObject("base", new JsonObject() {{
                this.putString("base_formula", "number");
                this.putInteger("number", getTarget().getBaseArmorClass(context));
            }});
        }});
        calculateEffectiveArmorClass.setOriginItem(this.getOriginItem());
        calculateEffectiveArmorClass.setSource(this.getSource());
        calculateEffectiveArmorClass.prepare(context);
        calculateEffectiveArmorClass.setTarget(this.getTarget());
        calculateEffectiveArmorClass.invoke(context);
        return calculateEffectiveArmorClass.get();
    }

    /**
     * This method returns whether the attack roll is a critical hit.
     *
     * @param context the context this Subevent takes place in
     * @return true if the attack is a critical hit
     *
     * @throws Exception if an exception occurs.
     */
    public boolean isCriticalHit(RPGLContext context) throws Exception {
        CalculateCriticalHitThreshold calculateCriticalHitThreshold = new CalculateCriticalHitThreshold();
        calculateCriticalHitThreshold.joinSubeventData(new JsonObject() {{
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
            }});
        }});
        calculateCriticalHitThreshold.setOriginItem(this.getOriginItem());
        calculateCriticalHitThreshold.setSource(this.getSource());
        calculateCriticalHitThreshold.prepare(context);
        calculateCriticalHitThreshold.setTarget(this.getTarget());
        calculateCriticalHitThreshold.invoke(context);

        return this.getBase() >= calculateCriticalHitThreshold.get();
    }

    /**
     * This method returns whether the attack roll is a critical miss.
     *
     * @return true if the attack is a critical miss
     */
    public boolean isCriticalMiss() {
        return this.getBase() == 1;
    }

    /**
     * This helper method collects and stores the critical hit damage dice and bonuses in the AttackRoll.
     *
     * @param context the context in which the critical hit damage is being determined
     *
     * @throws Exception if an exception occurs
     */
    void getCriticalHitDamage(RPGLContext context) throws Exception {
        // Get a copy of the attack damage with twice the number of dice
        JsonArray damageArray = this.json.getJsonArray("damage").deepClone();
        for (int i = 0; i < damageArray.size(); i++) {
            JsonObject damageJson = damageArray.getJsonObject(i);
            JsonArray dice = damageJson.getJsonArray("dice");
            if (dice != null) {
                dice.asList().addAll(dice.deepClone().asList());
            }
        }

        // Collect any extra damage bonuses which aren't doubled
        CriticalHitDamageCollection criticalHitDamageCollection = new CriticalHitDamageCollection();
        criticalHitDamageCollection.joinSubeventData(new JsonObject() {{
            this.putJsonArray("damage", damageArray);
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
            }});
        }});
        criticalHitDamageCollection.setOriginItem(this.getOriginItem());
        criticalHitDamageCollection.setSource(this.getSource());
        criticalHitDamageCollection.prepare(context);
        criticalHitDamageCollection.setTarget(this.getTarget());
        criticalHitDamageCollection.invoke(context);

        // Set the attack damage to the critical hit damage collection
        this.json.putJsonArray("damage", criticalHitDamageCollection.getDamageCollection());
    }

    /**
     * This helper method rolls all damage dice for this attack and precipitates the delivery of that damage to the
     * target.
     *
     * @param context the context in which the damage is being rolled.
     *
     * @throws Exception if an exception occurs
     */
    void resolveDamage(RPGLContext context) throws Exception {
        DamageRoll damageRoll = new DamageRoll();
        damageRoll.joinSubeventData(new JsonObject() {{
            this.putJsonArray("damage", json.getJsonArray("damage"));
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("attack_damage_roll");
            }});
        }});
        damageRoll.setOriginItem(this.getOriginItem());
        damageRoll.setSource(this.getSource());
        damageRoll.prepare(context);
        damageRoll.setTarget(this.getTarget());
        damageRoll.invoke(context);

        // Store final damage by type to damage key
        this.json.putJsonArray("damage", damageRoll.getDamage());

        this.deliverDamage(context);
    }

    /**
     * This helper method resolves any additional subevents specified in the AttackRoll json according to whether the
     * attack hit or missed.
     *
     * @param resolution a String indicating the resolution of the Subevent (<code>"hit"</code> or <code>"miss"</code>)
     * @param context    the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void resolveNestedSubevents(String resolution, RPGLContext context) throws Exception {
        JsonArray subeventJsonArray = Objects.requireNonNullElse(this.json.getJsonArray(resolution), new JsonArray());
        for (int i = 0; i < subeventJsonArray.size(); i++) {
            JsonObject nestedSubeventJson = subeventJsonArray.getJsonObject(i);
            Subevent subevent = Subevent.SUBEVENTS.get(nestedSubeventJson.getString("subevent")).clone(nestedSubeventJson);
            subevent.setOriginItem(this.getOriginItem());
            subevent.setSource(this.getSource());
            subevent.prepare(context);
            subevent.setTarget(this.getTarget());
            subevent.invoke(context);
        }
    }

    /**
     * This helper method delivers the finalized damage of the attack to the target.
     *
     * @param context the context in which the damage is being delivered
     *
     * @throws Exception if an exception occurs
     */
    void deliverDamage(RPGLContext context) throws Exception {
        DamageDelivery damageDelivery = new DamageDelivery();
        damageDelivery.joinSubeventData(new JsonObject() {{
            this.putJsonArray("damage", json.getJsonArray("damage"));
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
            }});
        }});
        damageDelivery.setOriginItem(this.getOriginItem());
        damageDelivery.setSource(this.getSource());
        damageDelivery.prepare(context);
        damageDelivery.setTarget(this.getTarget());
        damageDelivery.invoke(context);
        damageDelivery.getTarget().receiveDamage(damageDelivery, context);
    }

    /**
     * Returns whether the AttackRoll deals any damage.
     *
     * @return true if the AttackRoll deals any damage
     */
    public boolean dealsDamage() {
        // TODO make condition for this
        return !Objects.requireNonNullElse(this.json.getJsonArray("damage"), new JsonArray()).asList().isEmpty();
    }

}
