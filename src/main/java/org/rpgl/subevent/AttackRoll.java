package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLItem;
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

    @SuppressWarnings("all")
    private static final String ITEM_NAMESPACE_REGEX = "[\\w\\d]+:[\\w\\d]+";

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

        this.addTag("attack_roll");
        this.addTag(this.json.getString("attack_type"));

        this.json.asMap().putIfAbsent("damage", new ArrayList<>());

        String weapon = this.json.getString("weapon");
        if (weapon == null) {
            this.prepareNoWeapon(context);
        } else {
            if (weapon.matches(ITEM_NAMESPACE_REGEX)) {
                this.prepareNaturalWeapon(weapon, context);
            } else {
                this.prepareItemWeapon(weapon, context);
            }
        }
    }

    @Override
    public void invoke(RPGLContext context) throws Exception {
        super.invoke(context);
        if (this.isNotCanceled()) {
            this.roll();
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

            // Delete natural weapon if one was created at the end of invoke()
            if (this.json.getBoolean("natural_weapon_attack")) {
                String naturalWeaponUuid = this.json.getString("weapon");
                UUIDTable.unregister(naturalWeaponUuid);
            }
        }
    }

    @Override
    public String getAbility(RPGLContext context) {
        return this.json.getString("attack_ability");
    }

    void prepareNoWeapon(RPGLContext context) throws Exception {
        this.json.putBoolean("natural_weapon_attack", false);
        // Add attack ability score modifier (defined by the Subevent JSON) as a bonus to the roll.
        String attackAbility = this.json.getString("attack_ability");
        this.addBonus(new JsonObject() {{
            this.putInteger("bonus", getSource().getAbilityModifierFromAbilityName(attackAbility, context));
            this.putJsonArray("dice", new JsonArray());
        }});

        // No-weapon attacks have their damage formulas built into their JSON already.
    }

    void prepareNaturalWeapon(String weaponId, RPGLContext context) throws Exception {
        this.json.putBoolean("natural_weapon_attack", true);
        // Add attack ability score modifier (defined by the Item JSON) as a bonus to the roll.
        RPGLItem weapon = RPGLFactory.newItem(weaponId);
        String attackType = this.json.getString("attack_type");
        String attackAbility = weapon.getAttackAbility(attackType);
        this.addBonus(new JsonObject() {{
            this.putInteger("bonus", getSource().getAbilityModifierFromAbilityName(attackAbility, context));
            this.putJsonArray("dice", new JsonArray());
        }});
        this.applyWeaponAttackBonus(weapon);

        // Copy damage of natural weapon to Subevent JSON.
        this.json.putJsonArray("damage", weapon.getDamageForAttackType(attackType));

        // Add natural weapon tags to attack roll
        JsonArray naturalWeaponTags = weapon.getTags();
        for (int i = 0; i < naturalWeaponTags.size(); i++) {
            this.addTag(naturalWeaponTags.getString(i));
        }

        // Record natural weapon UUID
        this.json.putString("weapon", weapon.getUuid());

        // Record attack ability
        this.json.putString("attack_ability", attackAbility);
    }

    void prepareItemWeapon(String equipmentSlot, RPGLContext context) throws Exception {
        this.json.putBoolean("natural_weapon_attack", false);
        // Add attack ability score modifier (defined by the Item JSON) as a bonus to the roll.
        RPGLItem weapon = UUIDTable.getItem(this.getSource().getEquippedItems().getString(equipmentSlot));
        String attackType = this.json.getString("attack_type");
        String attackAbility = weapon.getAttackAbility(attackType);
        this.addBonus(new JsonObject() {{
            this.putInteger("bonus", getSource().getAbilityModifierFromAbilityName(attackAbility, context));
            this.putJsonArray("dice", new JsonArray());
        }});
        this.applyWeaponAttackBonus(weapon);

        // Copy damage of natural weapon to Subevent JSON.
        this.json.putJsonArray("damage", weapon.getDamageForAttackType(attackType));

        // Add item weapon tags to attack roll
        JsonArray itemWeaponTags = weapon.getTags();
        for (int i = 0; i < itemWeaponTags.size(); i++) {
            this.addTag(itemWeaponTags.getString(i));
        }

        // Record item weapon UUID
        this.json.putString("weapon", weapon.getUuid());

        // Record attack ability
        this.json.putString("attack_ability", attackAbility);
    }

    /**
     * This helper method applies any attack roll bonuses granted by the weapon's json data (typically this bonus is granted
     * by magical weapons).
     *
     * @param weapon the RPGLItem being used to deliver the attack
     */
    void applyWeaponAttackBonus(RPGLItem weapon) {
        this.addBonus(new JsonObject() {{
            this.putInteger("bonus", weapon.getAttackBonus());
            this.putJsonArray("dice", new JsonArray()); // TODO should this be made accessible?
        }});
    }

    void getBaseDamage(RPGLContext context) throws Exception {
        // Collect base typed damage dice and bonuses
        DamageCollection baseDamageCollection = new DamageCollection();
        baseDamageCollection.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "damage_collection");
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

        // If the attack is made with an item, add attack ability modifier to damage roll
        if (this.json.getString("weapon") != null) {
            RPGLItem weapon = UUIDTable.getItem(this.json.getString("weapon"));
            String attackAbility = weapon.getAttackAbility(this.json.getString("attack_type"));
            int attackAbilityModifier = this.getSource().getAbilityModifierFromAbilityName(attackAbility, context);
            String damageType = this.json.getJsonArray("damage").getJsonObject(0).getString("damage_type");
            baseDamageCollection.addDamage(new JsonObject() {{
                this.putString("damage_type", damageType);
                this.putJsonArray("dice", new JsonArray());
                this.putInteger("bonus", attackAbilityModifier);
            }});
        }

        // Replace damage key with base damage collection
        this.json.putJsonArray("damage", baseDamageCollection.getDamageCollection());
    }

    void getTargetDamage(RPGLContext context) throws Exception {
        // Collect target typed damage dice and bonuses
        DamageCollection targetDamageCollection = new DamageCollection();
        targetDamageCollection.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "damage_collection");
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("target_damage_collection");
            }});
        }});
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
            this.putString("subevent", "calculate_effective_armor_class");
            this.putJsonObject("base", new JsonObject() {{
                this.putString("base_formula", "number");
                this.putInteger("value", getTarget().getBaseArmorClass(context));
            }});
        }});
        calculateEffectiveArmorClass.setSource(this.getSource());
        calculateEffectiveArmorClass.prepare(context);
        calculateEffectiveArmorClass.setTarget(this.getTarget());
        calculateEffectiveArmorClass.invoke(context);
        return calculateEffectiveArmorClass.get();
    }

    /**
     * This helper method returns whether the attack roll is a critical hit.
     *
     * @param context the context this Subevent takes place in
     * @return true if the attack is a critical hit
     *
     * @throws Exception if an exception occurs.
     */
    public boolean isCriticalHit(RPGLContext context) throws Exception {
        CalculateCriticalHitThreshold calculateCriticalHitThreshold = new CalculateCriticalHitThreshold();
        calculateCriticalHitThreshold.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "calculate_critical_hit_threshold");
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
            }});
        }});
        calculateCriticalHitThreshold.setSource(this.getSource());
        calculateCriticalHitThreshold.prepare(context);
        calculateCriticalHitThreshold.setTarget(this.getTarget());
        calculateCriticalHitThreshold.invoke(context);

        return this.getBase().getInteger("value") >= calculateCriticalHitThreshold.get();
    }

    /**
     * This helper method returns whether the attack roll is a critical miss.
     *
     * @return true if the attack is a critical miss
     */
    public boolean isCriticalMiss() {
        return this.getBase().getInteger("value") == 1;
    }

    void getCriticalHitDamage(RPGLContext context) throws Exception {
        // Get a copy of the attack damage with twice the number of dice
        JsonArray damageArray = this.json.getJsonArray("damage").deepClone();
        for (int i = 0; i < damageArray.size(); i++) {
            JsonObject damageJson = damageArray.getJsonObject(i);
            JsonArray dice = damageJson.getJsonArray("dice");
            dice.asList().addAll(dice.deepClone().asList());
        }

        // Collect any extra damage bonuses which aren't doubled
        CriticalHitDamageCollection criticalHitDamageCollection = new CriticalHitDamageCollection();
        criticalHitDamageCollection.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "critical_hit_damage_collection");
            this.putJsonArray("damage", damageArray);
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
            }});
        }});
        criticalHitDamageCollection.setSource(this.getSource());
        criticalHitDamageCollection.prepare(context);
        criticalHitDamageCollection.setTarget(this.getTarget());
        criticalHitDamageCollection.invoke(context);

        // Set the attack damage to the critical hit damage collection
        this.json.putJsonArray("damage", criticalHitDamageCollection.getDamageCollection());
    }

    void resolveDamage(RPGLContext context) throws Exception {
        DamageRoll damageRoll = new DamageRoll();
        damageRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "damage_roll");
            this.putJsonArray("damage", json.getJsonArray("damage"));
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("attack_damage_roll");
            }});
        }});
        damageRoll.setSource(this.getSource());
        damageRoll.prepare(context);
        damageRoll.setTarget(this.getTarget());
        damageRoll.invoke(context);

        // Store final damage by type to damage key
        this.json.putJsonObject("damage", damageRoll.getDamage());

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
            subevent.setSource(this.getSource());
            subevent.prepare(context);
            subevent.setTarget(this.getTarget());
            subevent.invoke(context);
        }
    }

    void deliverDamage(RPGLContext context) throws Exception {
        DamageDelivery damageDelivery = new DamageDelivery();
        damageDelivery.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "damage_delivery");
            this.putJsonObject("damage", json.getJsonObject("damage"));
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
            }});
        }});
        damageDelivery.setSource(this.getSource());
        damageDelivery.prepare(context);
        damageDelivery.setTarget(this.getTarget());
        damageDelivery.invoke(context);
        damageDelivery.getTarget().receiveDamage(damageDelivery, context);
    }

    public boolean dealsDamage() {
        return !this.json.getJsonArray("damage").asList().isEmpty();
    }

}
