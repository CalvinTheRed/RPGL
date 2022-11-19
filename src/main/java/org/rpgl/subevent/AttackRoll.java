package org.rpgl.subevent;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLItem;
import org.rpgl.uuidtable.UUIDTable;

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
        if (this.get() < armorClass) {
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

    void prepareAttackWithoutWeapon(RPGLContext context) throws Exception {
        this.subeventJson.put("natural_weapon_attack", false);

        // Add attack ability score modifier (defined by the Subevent JSON) as a bonus to the roll.
        String attackAbility = (String) this.subeventJson.get("attack_ability");
        this.addBonus(this.getSource().getAbilityModifier(context, attackAbility));

        // Add proficiency bonus to the roll (all non-weapon attacks are made with proficiency).
        this.addBonus(this.getSource().getProficiencyBonus(context));

        // The damage field should already be populated for this type of attack. But in case it is not, set it to empty.
        if (this.subeventJson.get("damage") == null) {
            this.subeventJson.put("damage", new JsonArray());
        }
    }

    void prepareNaturalWeaponAttack(RPGLContext context, String weaponId) throws Exception {
        this.subeventJson.put("natural_weapon_attack", true);

        // Add attack ability score modifier (defined by the Item JSON) as a bonus to the roll.
        RPGLItem weapon = RPGLFactory.newItem(weaponId);
        assert weapon != null; // TODO is there a better way to do this?
        String attackType = (String) this.subeventJson.get("attack_type");
        this.addBonus(this.getSource().getAbilityModifier(context, weapon.getAttackAbility(attackType)));

        // Add proficiency bonus to the roll (all natural weapon attacks are made with proficiency).
        this.addBonus(this.getSource().getProficiencyBonus(context));

        // Copy damage of natural weapon to Subevent JSON.
        this.subeventJson.put("damage", weapon.getDamage(attackType));

        // Record natural weapon UUID
        this.subeventJson.put("weapon", weapon.get("uuid"));
    }

    void prepareItemWeaponAttack(RPGLContext context, String equipmentSlot) throws Exception {
        this.subeventJson.put("natural_weapon_attack", false);

        //Add attack ability score modifier (defined by the Item JSON) as a bonus to the roll.
        RPGLItem weapon = UUIDTable.getItem((String) this.getSource().seek("items." + equipmentSlot));
        String attackType = (String) this.subeventJson.get("attack_type");
        this.addBonus(this.getSource().getAbilityModifier(context, weapon.getAttackAbility(attackType)));

        // Add proficiency bonus to the roll (not all natural weapon attacks are made with proficiency).
        // TODO make a Subevent to check if a RPGLObject is proficient with a RPGLItem before applying bonus
        this.addBonus(this.getSource().getProficiencyBonus(context));

        // Copy damage of natural weapon to Subevent JSON.
        this.subeventJson.put("damage", weapon.getDamage(attackType));

        // Record natural weapon UUID
        this.subeventJson.put("weapon", weapon.get("uuid"));
    }

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

}
