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
 * <br>
 * <br>
 * Source: an RPGLObject making an attack
 * <br>
 * Target: an RPGLObject being attacked
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
        long armorClass = this.getTargetArmorClass(context);

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
     *  @param context the context this Subevent takes place in
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
     * void prepareNaturalWeaponAttack(String weaponId, RPGLContext context)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method prepares the AttackRoll under the assumption that the attack is being made using a natural weapon
     * 	such as a claw or fangs or a tail or a slam attack. The natural weapon will only exist during the attack, and
     * 	will be destroyed once the attack is resolved. Natural weapons should never exist as actual items.
     * 	</p>
     *
     *  @param weaponId a natural weapon ID <code>("datapack:name")</code>
     *  @param context  the context this Subevent takes place in
     *
     * 	@throws Exception if an exception occurs.
     */
    void prepareNaturalWeaponAttack(String weaponId, RPGLContext context) throws Exception {
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
     * void prepareItemWeaponAttack(String equipmentSlot, RPGLContext context)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method prepares the AttackRoll under the assumption that the attack is being made using a weapon such as a
     * 	sword, shortbow, or even an improvised weapon such as a mug. The weapon being used is determined by the content
     * 	of the equipment slot specified in the method call.
     * 	</p>
     *
     *  @param equipmentSlot an equipment slot (can be anything other than <code>"inventory"</code>)
     *  @param context       the context this Subevent takes place in
     *
     * 	@throws Exception if an exception occurs.
     */
    void prepareItemWeaponAttack(String equipmentSlot, RPGLContext context) throws Exception {
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
     *  @param context the context this Subevent takes place in
     *  @return the target's effective (final) armor class
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

    /**
     * 	<p>
     * 	<b><i>resolveDamage</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void resolveDamage(RPGLContext context)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method determines and delivers the damage dealt by the attack roll on a hit.
     * 	</p>
     *
     *  @param context the context this Subevent takes place in
     *
     * 	@throws Exception if an exception occurs.
     */
    void resolveDamage(RPGLContext context) throws Exception {
        BaseDamageDiceCollection baseDamageDiceCollection = this.getBaseDamageDiceCollection(context);
        TargetDamageDiceCollection targetDamageDiceCollection = this.getTargetDamageDiceCollection(context);

        baseDamageDiceCollection.addTypedDamage(targetDamageDiceCollection.getDamageDiceCollection());
        this.subeventJson.put("damage", this.getAttackDamage(baseDamageDiceCollection.getDamageDiceCollection(), context));
        this.deliverDamage(context);
    }

    /**
     * 	<p>
     * 	<b><i>resolveCriticalHitDamage</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void resolveCriticalHitDamage(RPGLContext context)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method increases the damage of the attack roll if the attack scored a critical hit.
     * 	</p>
     *
     *  @param context the context this Subevent takes place in
     *
     * 	@throws Exception if an exception occurs.
     */
    void resolveCriticalHitDamage(RPGLContext context) throws Exception {
        BaseDamageDiceCollection baseDamageDiceCollection = this.getBaseDamageDiceCollection(context);
        TargetDamageDiceCollection targetDamageDiceCollection = this.getTargetDamageDiceCollection(context);
        CriticalHitDamageDiceCollection criticalHitDamageDiceCollection = this.getCriticalHitDamageDiceCollection(
                baseDamageDiceCollection,
                targetDamageDiceCollection,
                context
        );

        this.subeventJson.put("damage", this.getAttackDamage(criticalHitDamageDiceCollection.getDamageDiceCollection(), context));
        this.deliverDamage(context);
    }

    /**
     * 	<p>
     * 	<b><i>getBaseDamageDiceCollection</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * BaseDamageDiceCollection getBaseDamageDiceCollection(RPGLContext context)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method determines the base damage dice and bonuses dealt by the attack roll on a hit.
     * 	</p>
     *
     *  @param context the context this Subevent takes place in
     *  @return the base damage dice collection for the attack
     *
     * 	@throws Exception if an exception occurs.
     */
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
                    this.subeventJson.seek("damage[0].type"), // modifier applies to first damage type of the attack
                    attackAbilityModifier
            );
            JsonArray damageBonusArray = JsonParser.parseArrayString(damageBonusArrayString);
            baseDamageDiceCollection.addTypedDamage(damageBonusArray);
        }

        baseDamageDiceCollection.setTarget(this.getTarget());
        baseDamageDiceCollection.invoke(context);
        return baseDamageDiceCollection;
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
     * 	This method determines any additional damage dice or bonuses dealt to a target on a hit beyond the base damage.
     * 	</p>
     *
     *  @param context the context this Subevent takes place in
     *  @return the target damage dice collection of the attack
     *
     * 	@throws Exception if an exception occurs.
     */
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

    /**
     * 	<p>
     * 	<b><i>getCriticalHitDamageDiceCollection</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * CriticalHitDamageDiceCollection getCriticalHitDamageDiceCollection(
     *  BaseDamageDiceCollection baseDamageDiceCollection,
     *  TargetDamageDiceCollection targetDamageDiceCollection,
     *  RPGLContext context
     * ) throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method determines the damage dice and bonuses dealt by an attack roll on a critical hit. This doubles the
     * 	number of dice involved with the base and target damage rolls, and allows for additional bonuses and dice to be
     * 	applied.
     * 	</p>
     *
     *  @param baseDamageDiceCollection   the base damage dice collection of the attack
     *  @param targetDamageDiceCollection the target damage dice collection of the attack
     *  @param context                    the context this Subevent takes place in
     *  @return the critical hit damage dice collection for the attack
     *
     * 	@throws Exception if an exception occurs.
     */
    CriticalHitDamageDiceCollection getCriticalHitDamageDiceCollection(
            BaseDamageDiceCollection baseDamageDiceCollection,
            TargetDamageDiceCollection targetDamageDiceCollection,
            RPGLContext context
    ) throws Exception {
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

    /**
     * 	<p>
     * 	<b><i>getAttackDamage</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * JsonObject getAttackDamage(JsonArray damageDiceCollection, RPGLContext context)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method rolls all dice involved in the damage roll of the attack on a hit and reports a collection of damage
     * 	types and amounts (individual dice rolls are not preserved).
     * 	</p>
     *
     *  @param damageDiceCollection a collection of typed damage dice and bonuses
     *  @param context              the context this Subevent takes place in
     *  @return the attack's final damage values by type
     *
     * 	@throws Exception if an exception occurs.
     */
    JsonObject getAttackDamage(JsonArray damageDiceCollection, RPGLContext context) throws Exception {
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
     * 	This method passes the final damage quantities of the attack to the attack's target.
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

    /**
     * 	<p>
     * 	<b><i>resolveNestedSubevents</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void resolveNestedSubevents(String hitOrMiss, RPGLContext context)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method resolves any additional subevents specified in the AttackRoll json according to whether the attack
     * 	hit or missed.
     * 	</p>
     *
     *  @param hitOrMiss a String indicating whether the attack hit <code>("hit")</code> or missed <code>("miss")</code>.
     *  @param context   the context this Subevent takes place in
     *
     * 	@throws Exception if an exception occurs.
     */
    void resolveNestedSubevents(String hitOrMiss, RPGLContext context) throws Exception {
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

    /**
     * 	<p>
     * 	<b><i>isCriticalHit</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public boolean isCriticalHit(RPGLContext context)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns whether the attack roll is a critical hit.
     * 	</p>
     *
     *  @param context the context this Subevent takes place in
     *  @return true if the attack is a critical hit
     *
     * 	@throws Exception if an exception occurs.
     */
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

    /**
     * 	<p>
     * 	<b><i>isCriticalMiss</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public boolean isCriticalMiss()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns whether the attack roll is a critical miss.
     * 	</p>
     *
     *  @return true if the attack is a critical miss
     */
    public boolean isCriticalMiss() {
        return (this.get() - this.getBonus()) == 1L;
    }

    /**
     * 	<p>
     * 	<b><i>applyWeaponAttackBonus</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void applyWeaponAttackBonus(RPGLItem weapon)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method applies any attack roll bonuses granted by the weapon's json data (typically this bonus is granted
     * 	by magical weapons).
     * 	</p>
     *
     *  @param weapon the RPGLItem being used to deliver the attack
     */
    void applyWeaponAttackBonus(RPGLItem weapon) {
        this.addBonus(weapon.getAttackBonus());
    }

}
