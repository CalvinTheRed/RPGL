package org.rpgl.subevent;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLItem;
import org.rpgl.uuidtable.UUIDTable;

/**
 * This Subevent is dedicated to making an attack roll and resolving all fallout from making the attack. This is a
 * high-level Subevent which can be referenced in an RPGLEvent template.
 *
 * @author Calvin Withun
 */
public class AttackRoll extends ContestRoll {

    private static final String ITEM_NAMESPACE_REGEX = "[\\w\\d]+:[\\w\\d]+";

    public AttackRoll() {
        super("attack_roll");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new AttackRoll();
        clone.joinSubeventJson(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new AttackRoll();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        String weapon = (String) this.subeventJson.get("weapon");

        if (weapon == null) {
            this.prepareAttackWithoutWeapon(context);
        } else {
            if (weapon.matches(ITEM_NAMESPACE_REGEX)) {
                this.prepareNaturalWeaponAttack(context, weapon);
            } else {
                this.prepareItemWeaponAttack(context, weapon);
            }
        }
    }

    @Override
    public void invoke(RPGLContext context) throws Exception {
        super.invoke(context);
        this.roll();
        long armorClass = this.getTargetArmorClass(context);

        if (this.isCriticalHit(context)) {
            this.resolveCriticalHitDamage(context);
            this.resolveNestedSubevents(context, "hit");
        } else if (this.isCriticalMiss() || this.get() < armorClass) {
            this.resolveNestedSubevents(context, "miss");
        } else {
            this.resolveDamage(context);
            this.resolveNestedSubevents(context, "hit");
        }

        // Delete natural weapon if one was created at the end of invoke()
        if ((Boolean) this.subeventJson.get("natural_weapon_attack")) {
            String naturalWeaponUuid = (String) this.subeventJson.get("weapon");
            UUIDTable.unregister(naturalWeaponUuid);
        }
    }

    /**
     * 	<p>
     * 	<b><i>prepareAttackWithoutWeapon</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void prepareAttackWithoutWeapon(RPGLContext context)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method prepares the AttackRoll under the assumption that the attack is being made using no weapon. This
     * 	typically is the case when the attack is a spell or other magical attack. Any damage dealt by the AttackRoll
     * 	must be specified in the RPGLEvent template when this prepare method is used, since no weapon will be used to
     * 	determine the damage.
     * 	</p>
     *
     *  @param context the RPGLContext in which the AttackRoll is being prepared
     *
     * 	@throws Exception if an exception occurs.
     */
    void prepareAttackWithoutWeapon(RPGLContext context) throws Exception {
        this.subeventJson.put("natural_weapon_attack", false);

        // Add attack ability score modifier (defined by the Subevent JSON) as a bonus to the roll.
        String attackAbility = (String) this.subeventJson.get("attack_ability");
        this.addBonus(this.getSource().getAbilityModifierFromAbilityScore(context, attackAbility));

        // Add proficiency bonus to the roll (all non-weapon attacks are made with proficiency).
        this.addBonus(this.getSource().getProficiencyBonus(context));

        // The damage field should already be populated for this type of attack. But in case it is not, set it to empty.
        if (this.subeventJson.get("damage") == null) {
            this.subeventJson.put("damage", new JsonArray());
        }
    }

    /**
     * 	<p>
     * 	<b><i>prepareNaturalWeaponAttack</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void prepareNaturalWeaponAttack(RPGLContext context, String weaponId)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method prepares the AttackRoll under the assumption that the attack is being made using a natural weapon
     * 	such as a claw or fangs or a tail or a slam attack. The natural weapon will only exist during the attack, and
     * 	will be destroyed once the attack is resolved. Natural weapons should never exist as actual items.
     * 	</p>
     *
     *  @param context  the RPGLContext in which the AttackRoll is being prepared
     *  @param weaponId a natural weapon ID <code>("datapack:name")</code>
     *
     * 	@throws Exception if an exception occurs.
     */
    void prepareNaturalWeaponAttack(RPGLContext context, String weaponId) throws Exception {
        this.subeventJson.put("natural_weapon_attack", true);

        // Add attack ability score modifier (defined by the Item JSON) as a bonus to the roll.
        RPGLItem weapon = RPGLFactory.newItem(weaponId);
        assert weapon != null; // TODO is there a better way to do this?
        String attackType = (String) this.subeventJson.get("attack_type");
        this.addBonus(this.getSource().getAbilityModifierFromAbilityScore(context, weapon.getAttackAbility(attackType)));
        this.applyWeaponAttackBonus(weapon);

        // Add proficiency bonus to the roll (all natural weapon attacks are made with proficiency).
        this.addBonus(this.getSource().getProficiencyBonus(context));

        // Copy damage of natural weapon to Subevent JSON.
        this.subeventJson.put("damage", weapon.getDamage(attackType));

        // Record natural weapon UUID
        this.subeventJson.put("weapon", weapon.getUuid());
    }

    /**
     * 	<p>
     * 	<b><i>prepareItemWeaponAttack</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void prepareItemWeaponAttack(RPGLContext context, String equipmentSlot)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method prepares the AttackRoll under the assumption that the attack is being made using a weapon such as a
     * 	sword, shortbow, or even an improvised weapon such as a mug. The weapon being used is determined by the content
     * 	of the equipment slot specified in the method call.
     * 	</p>
     *
     *  @param context       the RPGLContext in which the AttackRoll is being prepared
     *  @param equipmentSlot an equipment slot (can be anything other than <code>"inventory"</code>)
     *
     * 	@throws Exception if an exception occurs.
     */
    void prepareItemWeaponAttack(RPGLContext context, String equipmentSlot) throws Exception {
        this.subeventJson.put("natural_weapon_attack", false);

        //Add attack ability score modifier (defined by the Item JSON) as a bonus to the roll.
        RPGLItem weapon = UUIDTable.getItem((String) this.getSource().seek("items." + equipmentSlot));
        String attackType = (String) this.subeventJson.get("attack_type");
        this.addBonus(this.getSource().getAbilityModifierFromAbilityScore(context, weapon.getAttackAbility(attackType)));
        this.applyWeaponAttackBonus(weapon);

        // Add proficiency bonus to the roll if source is proficient with weapon.
        if (getSource().isProficientWithWeapon(context, weapon.getUuid())) {
            this.addBonus(this.getSource().getProficiencyBonus(context));
        }

        // Copy damage of natural weapon to Subevent JSON.
        this.subeventJson.put("damage", weapon.getDamage(attackType));

        // Record natural weapon UUID
        this.subeventJson.put("weapon", weapon.getUuid());
    }

    /**
     * 	<p>
     * 	<b><i>getTargetArmorClass</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * long getTargetArmorClass(RPGLContext context)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method evaluates the effective armor class of the target to determine if the attack hits or misses. This
     * 	value can be influenced by the target after the attack roll is made to attempt to avoid the attack, and may be
     * 	different than the target's base armor class.
     * 	</p>
     *
     *  @param context the RPGLContext in which the target's armor class is being evaluated
     *
     * 	@throws Exception if an exception occurs.
     */
    long getTargetArmorClass(RPGLContext context) throws Exception {
        long baseArmorClass = this.getTarget().getBaseArmorClass(context);
        CalculateEffectiveArmorClass calculateEffectiveArmorClass = new CalculateEffectiveArmorClass();
        String calculateEffectiveArmorClassJsonString = String.format("""
                        {
                            "subevent": "calculate_effective_armor_class",
                            "base": %d
                        }
                        """,
                baseArmorClass
        );
        JsonObject calculateEffectiveArmorClassJson = JsonParser.parseObjectString(calculateEffectiveArmorClassJsonString);
        calculateEffectiveArmorClass.joinSubeventJson(calculateEffectiveArmorClassJson);
        calculateEffectiveArmorClass.setSource(this.getSource());
        calculateEffectiveArmorClass.prepare(context);
        calculateEffectiveArmorClass.setTarget(this.getTarget());
        calculateEffectiveArmorClass.invoke(context);
        return calculateEffectiveArmorClass.get();
    }

    void resolveDamage(RPGLContext context) throws Exception {
        BaseDamageDiceCollection baseDamageDiceCollection = this.getBaseDamageDiceCollection(context);
        TargetDamageDiceCollection targetDamageDiceCollection = this.getTargetDamageDiceCollection(context);

        baseDamageDiceCollection.addTypedDamage(targetDamageDiceCollection.getDamageDiceCollection());
        this.subeventJson.put("damage", this.getAttackDamage(context, baseDamageDiceCollection.getDamageDiceCollection()));
        this.deliverDamage(context);
    }

    void resolveCriticalHitDamage(RPGLContext context) throws Exception {
        BaseDamageDiceCollection baseDamageDiceCollection = this.getBaseDamageDiceCollection(context);
        TargetDamageDiceCollection targetDamageDiceCollection = this.getTargetDamageDiceCollection(context);
        CriticalHitDamageDiceCollection criticalHitDamageDiceCollection = this.getCriticalHitDamageDiceCollection(
                context,
                baseDamageDiceCollection,
                targetDamageDiceCollection
        );

        this.subeventJson.put("damage", this.getAttackDamage(context, criticalHitDamageDiceCollection.getDamageDiceCollection()));
        this.deliverDamage(context);
    }

    BaseDamageDiceCollection getBaseDamageDiceCollection(RPGLContext context) throws Exception {
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
        baseDamageDiceCollection.setSource(this.getSource());
        baseDamageDiceCollection.prepare(context);

        // If the attack is made with an item, add attack modifier to damage roll
        if (this.subeventJson.get("weapon") != null) {
            RPGLItem weapon = UUIDTable.getItem((String) this.subeventJson.get("weapon"));
            String attackAbility = weapon.getAttackAbility((String) this.subeventJson.get("attack_type"));
            long attackAbilityModifier = this.getSource().getAbilityModifierFromAbilityScore(context, attackAbility);

            String damageBonusArrayString = String.format("""
                    [
                        {
                            "type": "%s",
                            "bonus": %d
                        }
                    ]
                    """,
                    this.subeventJson.seek("damage[0].type"), // modifier applies to first damage type?
                    attackAbilityModifier
            );
            JsonArray damageBonusArray = JsonParser.parseArrayString(damageBonusArrayString);
            baseDamageDiceCollection.addTypedDamage(damageBonusArray);
        }

        baseDamageDiceCollection.setTarget(this.getTarget());
        baseDamageDiceCollection.invoke(context);
        return baseDamageDiceCollection;
    }

    TargetDamageDiceCollection getTargetDamageDiceCollection(RPGLContext context) throws Exception {
        TargetDamageDiceCollection targetDamageDiceCollection = new TargetDamageDiceCollection();
        String targetDamageDiceCollectionJsonString = """
                {
                    "subevent": "target_damage_dice_collection",
                    "damage": [ ]
                }
                """; // TODO can the empty array be moved to prepare() ?
        JsonObject targetDamageDiceCollectionJson = JsonParser.parseObjectString(targetDamageDiceCollectionJsonString);
        targetDamageDiceCollection.joinSubeventJson(targetDamageDiceCollectionJson);
        targetDamageDiceCollection.prepare(context);
        targetDamageDiceCollection.invoke(context);
        return targetDamageDiceCollection;
    }

    CriticalHitDamageDiceCollection getCriticalHitDamageDiceCollection(RPGLContext context, BaseDamageDiceCollection baseDamageDiceCollection, TargetDamageDiceCollection targetDamageDiceCollection) throws Exception {
        // TODO can this method signature be made any cleaner?
        baseDamageDiceCollection.addTypedDamage(targetDamageDiceCollection.getDamageDiceCollection());

        CriticalHitDamageDiceCollection criticalHitDamageDiceCollection = new CriticalHitDamageDiceCollection();
        String criticalHitDamageDiceCollectionJsonString = String.format("""
                {
                    "subevent": "critical_hit_damage_dice_collection",
                    "damage": %s
                }
                """,
                baseDamageDiceCollection.getDamageDiceCollection().toString()
        );
        JsonObject criticalHitDamageDiceCollectionJson = JsonParser.parseObjectString(criticalHitDamageDiceCollectionJsonString);
        criticalHitDamageDiceCollection.joinSubeventJson(criticalHitDamageDiceCollectionJson);
        criticalHitDamageDiceCollection.setSource(this.getSource());
        criticalHitDamageDiceCollection.prepare(context);
        criticalHitDamageDiceCollection.setTarget(this.getTarget());
        criticalHitDamageDiceCollection.doubleDice();
        criticalHitDamageDiceCollection.invoke(context);
        return criticalHitDamageDiceCollection;
    }

    JsonObject getAttackDamage(RPGLContext context, JsonArray damageDiceCollection) throws Exception {
        AttackDamageRoll attackDamageRoll = new AttackDamageRoll();
        String attackDamageRollJsonString = String.format("""
                        {
                            "subevent": "attack_damage_roll",
                            "damage": %s
                        }
                        """,
                damageDiceCollection.toString()
        );
        JsonObject attackDamageRollJson = JsonParser.parseObjectString(attackDamageRollJsonString);
        attackDamageRoll.joinSubeventJson(attackDamageRollJson);
        attackDamageRoll.setSource(this.getSource());
        attackDamageRoll.prepare(context);
        attackDamageRoll.setTarget(this.getTarget());
        attackDamageRoll.invoke(context);
        return attackDamageRoll.getDamage();
    }

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

    void resolveNestedSubevents(RPGLContext context, String hitOrMiss) throws Exception {
        JsonArray subeventJsonArray = (JsonArray) this.subeventJson.get(hitOrMiss);
        if (subeventJsonArray != null) {
            for (Object subeventJsonElement : subeventJsonArray) {
                JsonObject subeventJson = (JsonObject) subeventJsonElement;
                Subevent subevent = Subevent.SUBEVENTS.get((String) subeventJson.get("subevent")).clone(subeventJson);
                subevent.prepare(context);
                subevent.invoke(context);
            }
        }
    }

    public boolean isCriticalHit(RPGLContext context) throws Exception {
        CalculateCriticalHitThreshold calculateCriticalHitThreshold = new CalculateCriticalHitThreshold();
        String calculateCriticalHitThresholdJsonString = """
                        {
                            "subevent": "calculate_critical_hit_threshold"
                        }
                        """;
        JsonObject calculateCriticalHitThresholdJson = JsonParser.parseObjectString(calculateCriticalHitThresholdJsonString);
        calculateCriticalHitThreshold.joinSubeventJson(calculateCriticalHitThresholdJson);
        calculateCriticalHitThreshold.setSource(this.getSource());
        calculateCriticalHitThreshold.prepare(context);
        calculateCriticalHitThreshold.setTarget(this.getTarget());
        calculateCriticalHitThreshold.invoke(context);

        return (this.get() - this.getBonus()) >= calculateCriticalHitThreshold.get();
    }

    public boolean isCriticalMiss() {
        return (this.get() - this.getBonus()) == 1L;
    }

    void applyWeaponAttackBonus(RPGLItem weapon) {
        this.addBonus(weapon.getAttackBonus());
    }

}
