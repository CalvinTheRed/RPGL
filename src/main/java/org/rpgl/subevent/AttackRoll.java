package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLItem;
import org.rpgl.datapack.RPGLObjectTO;
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

        String weapon = this.json.getString("weapon");
        if (weapon == null) {
            this.prepareAttackWithoutWeapon(context);
        } else {
            if (weapon.matches(ITEM_NAMESPACE_REGEX)) {
                this.prepareNaturalWeaponAttack(weapon, context);
            } else {
                this.prepareItemWeaponAttack(weapon, context);
            }
        }
    }

    @Override
    public void invoke(RPGLContext context) throws Exception {
        super.invoke(context);
        this.roll();
        int armorClass = this.getTargetArmorClass(context);

        if (this.isCriticalHit(context)) {
            this.resolveCriticalHitDamage(context);
            this.resolveNestedSubevents("hit", context);
        } else if (this.isCriticalMiss() || this.get() < armorClass) {
            this.resolveNestedSubevents("miss", context);
        } else {
            this.resolveDamage(context);
            this.resolveNestedSubevents("hit", context);
        }

        // Delete natural weapon if one was created at the end of invoke()
        if (this.json.getBoolean("natural_weapon_attack")) {
            String naturalWeaponUuid = this.json.getString("weapon");
            UUIDTable.unregister(naturalWeaponUuid);
        }
    }

    @Override
    public String getAbility(RPGLContext context) {
        String weapon = this.json.getString("weapon");
        if (weapon == null) {
            return this.json.getString("attack_ability");
        } else {
            if (weapon.matches(ITEM_NAMESPACE_REGEX)) {
                RPGLItem weaponItem = RPGLFactory.newItem(weapon);
                String attackType = this.json.getString("attack_type");
                String ability = weaponItem.getAttackAbility(attackType);
                UUIDTable.unregister(weaponItem.getUuid());
                return ability;
            } else {
                RPGLItem weaponItem = UUIDTable.getItem(this.getSource().getJsonObject(RPGLObjectTO.EQUIPPED_ITEMS_ALIAS).getString(weapon));
                String attackType = this.json.getString("attack_type");
                return weaponItem.getAttackAbility(attackType);
            }
        }
    }

    /**
     * This helper method prepares the AttackRoll under the assumption that the attack is being made using no weapon.
     * This typically is the case when the attack is a spell or other magical attack. Any damage dealt by the
     * AttackRoll must be specified in the RPGLEvent template when this prepare method is used, since no weapon will be
     * used to determine the damage.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void prepareAttackWithoutWeapon(RPGLContext context) throws Exception {
        this.json.putBoolean("natural_weapon_attack", false);

        // Add attack ability score modifier (defined by the Subevent JSON) as a bonus to the roll.
        String attackAbility = this.json.getString("attack_ability");
        this.addBonus(this.getSource().getAbilityModifierFromAbilityName(attackAbility, context));

        // Add proficiency bonus to the roll (all non-weapon attacks are made with proficiency).
        this.addBonus(this.getSource().getEffectiveProficiencyBonus(context));

        // Attacks made without weapons should already be defined in the event template so no changes are needed here

        // The damage field should already be populated for this type of attack. But in case it is not, set it to empty.
        this.json.asMap().computeIfAbsent("damage", k -> new ArrayList<>());
    }

    /**
     * This helper method prepares the AttackRoll under the assumption that the attack is being made using a natural
     * weapon such as a claw or fangs or a tail or a slam attack. The natural weapon will only exist during the attack,
     * and will be destroyed once the attack is resolved. Natural weapons should never exist as actual items.
     *
     * @param weaponId a natural weapon ID <code>("datapack:name")</code>
     * @param context  the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void prepareNaturalWeaponAttack(String weaponId, RPGLContext context) throws Exception {
        this.json.putBoolean("natural_weapon_attack", true);

        // Add attack ability score modifier (defined by the Item JSON) as a bonus to the roll.
        RPGLItem weapon = RPGLFactory.newItem(weaponId);
        String attackType = this.json.getString("attack_type");
        this.addBonus(this.getSource().getAbilityModifierFromAbilityName(weapon.getAttackAbility(attackType), context));
        this.applyWeaponAttackBonus(weapon);

        // Add proficiency bonus to the roll (all natural weapon attacks are made with proficiency).
        this.addBonus(this.getSource().getEffectiveProficiencyBonus(context));

        // Copy damage of natural weapon to Subevent JSON.
        this.json.putJsonArray("damage", weapon.getDamageForAttackType(attackType));

        // Add natural weapon tags to attack roll
        JsonArray naturalWeaponTags = weapon.getTags();
        for (int i = 0; i < naturalWeaponTags.size(); i++) {
            this.addTag(naturalWeaponTags.getString(i));
        }

        // Record natural weapon UUID
        this.json.putString("weapon", weapon.getUuid());
    }

    /**
     * This helper method prepares the AttackRoll under the assumption that the attack is being made using a weapon
     * such as a sword, shortbow, or even an improvised weapon such as a mug. The weapon being used is determined by
     * the content of the equipment slot specified in the method call.
     *
     * @param equipmentSlot an equipment slot (can be anything other than <code>"inventory"</code>)
     * @param context       the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void prepareItemWeaponAttack(String equipmentSlot, RPGLContext context) throws Exception {
        this.json.putBoolean("natural_weapon_attack", false);

        //Add attack ability score modifier (defined by the Item JSON) as a bonus to the roll.
        RPGLItem weapon = UUIDTable.getItem(this.getSource().getJsonObject(RPGLObjectTO.EQUIPPED_ITEMS_ALIAS).getString(equipmentSlot));
        String attackType = this.json.getString("attack_type");
        this.addBonus(this.getSource().getAbilityModifierFromAbilityName(weapon.getAttackAbility(attackType), context));
        this.applyWeaponAttackBonus(weapon);

        // Add proficiency bonus to the roll if source is proficient with weapon.
        if (getSource().isProficientWithWeapon(weapon, context)) {
            this.addBonus(this.getSource().getEffectiveProficiencyBonus(context));
        }

        // Copy damage of natural weapon to Subevent JSON.
        this.json.putJsonArray("damage", weapon.getDamageForAttackType(attackType));

        // Add item weapon tags to attack roll
        JsonArray itemWeaponTags = weapon.getTags();
        for (int i = 0; i < itemWeaponTags.size(); i++) {
            this.addTag(itemWeaponTags.getString(i));
        }

        // Record natural weapon UUID
        this.json.putString("weapon", weapon.getUuid());
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
        int baseArmorClass = this.getTarget().getBaseArmorClass(context);
        CalculateEffectiveArmorClass calculateEffectiveArmorClass = new CalculateEffectiveArmorClass();
        calculateEffectiveArmorClass.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "calculate_effective_armor_class");
            this.putInteger("base", baseArmorClass);
        }});
        calculateEffectiveArmorClass.setSource(this.getSource());
        calculateEffectiveArmorClass.prepare(context);
        calculateEffectiveArmorClass.setTarget(this.getTarget());
        calculateEffectiveArmorClass.invoke(context);
        return calculateEffectiveArmorClass.get();
    }

    /**
     * This helper method determines and delivers the damage dealt by the attack roll on a hit.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void resolveDamage(RPGLContext context) throws Exception {
        DamageCollection baseDamageCollection = this.getBaseDamageCollection(context);
        DamageCollection targetDamageCollection = this.getTargetDamageCollection(context);

        baseDamageCollection.addTypedDamage(targetDamageCollection.getDamageCollection());
        this.json.putJsonObject("damage", this.getAttackDamage(baseDamageCollection.getDamageCollection(), context));
        this.deliverDamage(context);
    }

    /**
     * This helper method increases the damage of the attack roll if the attack scored a critical hit.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void resolveCriticalHitDamage(RPGLContext context) throws Exception {
        DamageCollection baseDamageCollection = this.getBaseDamageCollection(context);
        DamageCollection targetDamageCollection = this.getTargetDamageCollection(context);
        CriticalHitDamageCollection criticalHitDamageCollection = this.getCriticalHitDamageCollection(
                baseDamageCollection,
                targetDamageCollection,
                context
        );

        this.json.putJsonObject("damage", this.getAttackDamage(criticalHitDamageCollection.getDamageCollection(), context));
        this.deliverDamage(context);
    }

    /**
     * This helper method determines the base damage dice and bonuses dealt by the attack roll on a hit.
     *
     * @param context the context this Subevent takes place in
     * @return the base damage dice collection for the attack
     *
     * @throws Exception if an exception occurs.
     */
    DamageCollection getBaseDamageCollection(RPGLContext context) throws Exception {
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

        // If the attack is made with an item, add attack ability modifier to damage roll
        if (this.json.getString("weapon") != null) {
            RPGLItem weapon = UUIDTable.getItem(this.json.getString("weapon"));
            String attackAbility = weapon.getAttackAbility(this.json.getString("attack_type"));
            int attackAbilityModifier = this.getSource().getAbilityModifierFromAbilityName(attackAbility, context);
            String damageType = this.json.getJsonArray("damage").getJsonObject(0).getString("type");
            baseDamageCollection.addTypedDamage(new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("type", damageType);
                    this.putInteger("bonus", attackAbilityModifier);
                }});
            }});
        }
        baseDamageCollection.setTarget(this.getSource());
        baseDamageCollection.invoke(context);
        return baseDamageCollection;
    }

    /**
     * This helper method determines any additional damage dice or bonuses dealt to a target on a hit beyond the base
     * damage.
     *
     * @param context the context this Subevent takes place in
     * @return the target damage dice collection of the attack
     *
     * @throws Exception if an exception occurs.
     */
    DamageCollection getTargetDamageCollection(RPGLContext context) throws Exception {
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
        return targetDamageCollection;
    }

    /**
     * This helper method determines the damage dice and bonuses dealt by an attack roll on a critical hit. This
     * doubles the number of dice involved with the base and target damage rolls, and allows for additional bonuses and
     * dice to be applied.
     *
     * @param baseDamageCollection   the base damage dice collection of the attack
     * @param targetDamageCollection the target damage dice collection of the attack
     * @param context                the context this Subevent takes place in
     * @return the critical hit damage dice collection for the attack
     *
     * @throws Exception if an exception occurs.
     */
    CriticalHitDamageCollection getCriticalHitDamageCollection(DamageCollection baseDamageCollection,
                                                               DamageCollection targetDamageCollection,
                                                               RPGLContext context) throws Exception {
        baseDamageCollection.addTypedDamage(targetDamageCollection.getDamageCollection());
        CriticalHitDamageCollection criticalHitDamageCollection = new CriticalHitDamageCollection();
        criticalHitDamageCollection.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "critical_hit_damage_collection");
            this.putJsonArray("damage", baseDamageCollection.getDamageCollection().deepClone());
        }});
        criticalHitDamageCollection.setSource(this.getSource());
        criticalHitDamageCollection.prepare(context);
        criticalHitDamageCollection.setTarget(this.getTarget());
        criticalHitDamageCollection.doubleDice();
        criticalHitDamageCollection.invoke(context);
        return criticalHitDamageCollection;
    }

    /**
     * This helper method rolls all dice involved in the damage roll of the attack on a hit and reports a collection of
     * damage types and amounts (individual dice rolls are not preserved).
     *
     * @param damageCollection a collection of typed damage dice and bonuses
     * @param context          the context this Subevent takes place in
     * @return the attack's final damage values by type
     *
     * @throws Exception if an exception occurs.
     */
    JsonObject getAttackDamage(JsonArray damageCollection, RPGLContext context) throws Exception {
        DamageRoll attackDamageRoll = new DamageRoll();
        attackDamageRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "damage_roll");
            this.putJsonArray("damage", damageCollection.deepClone());
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("attack_damage_roll");
            }});
        }});
        attackDamageRoll.setSource(this.getSource());
        attackDamageRoll.prepare(context);
        attackDamageRoll.setTarget(this.getTarget());
        attackDamageRoll.invoke(context);
        return attackDamageRoll.getDamage();
    }

    /**
     * This helper method passes the final damage quantities of the attack to the attack's target.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void deliverDamage(RPGLContext context) throws Exception {
        DamageDelivery damageDelivery = new DamageDelivery();
        JsonObject damage = this.json.getJsonObject("damage");
        damageDelivery.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "damage_delivery");
            this.putJsonObject("damage", damage.deepClone());
        }});
        damageDelivery.setSource(this.getSource());
        damageDelivery.prepare(context);
        damageDelivery.setTarget(this.getTarget());
        damageDelivery.invoke(context);
        this.getTarget().receiveDamage(damageDelivery, context);
    }

    /**
     * This helper method resolves any additional subevents specified in the AttackRoll json according to whether the
     * attack hit or missed.
     *
     * @param hitOrMiss a String indicating whether the attack hit <code>("hit")</code> or missed <code>("miss")</code>.
     * @param context   the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void resolveNestedSubevents(String hitOrMiss, RPGLContext context) throws Exception {
        JsonArray subeventJsonArray = Objects.requireNonNullElse(this.json.getJsonArray(hitOrMiss), new JsonArray());
        for (int i = 0; i < subeventJsonArray.size(); i++) {
            JsonObject nestedSubeventJson = subeventJsonArray.getJsonObject(i);
            Subevent subevent = Subevent.SUBEVENTS.get(nestedSubeventJson.getString("subevent")).clone(nestedSubeventJson);
            subevent.setSource(this.getSource());
            subevent.prepare(context);
            subevent.setTarget(this.getTarget());
            subevent.invoke(context);
        }
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
        }});
        calculateCriticalHitThreshold.setSource(this.getSource());
        calculateCriticalHitThreshold.prepare(context);
        calculateCriticalHitThreshold.setTarget(this.getTarget());
        calculateCriticalHitThreshold.invoke(context);

        return (this.get() - this.getBonus()) >= calculateCriticalHitThreshold.get();
    }

    /**
     * This helper method returns whether the attack roll is a critical miss.
     *
     * @return true if the attack is a critical miss
     */
    public boolean isCriticalMiss() {
        return this.getBase() == 1;
    }

    /**
     * This helper method applies any attack roll bonuses granted by the weapon's json data (typically this bonus is granted
     * by magical weapons).
     *
     * @param weapon the RPGLItem being used to deliver the attack
     */
    void applyWeaponAttackBonus(RPGLItem weapon) {
        this.addBonus(weapon.getAttackBonus());
    }

}
