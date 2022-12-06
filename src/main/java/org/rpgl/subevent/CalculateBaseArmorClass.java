package org.rpgl.subevent;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLItem;
import org.rpgl.uuidtable.UUIDTable;

public class CalculateBaseArmorClass extends Calculation {

    public CalculateBaseArmorClass() {
        super("calculate_base_armor_class");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new CalculateBaseArmorClass();
        clone.joinSubeventJson(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new CalculateBaseArmorClass();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        // NOTE: you do not need to be proficient in armor or shields to receive their AC benefits

        // Set base armor class from armor (or no armor)
        String armorUuid = (String) this.getSource().seek("items.armor");
        Long baseArmorClass;
        if (armorUuid == null) {
            // if equipment slot is empty, you are unarmored
            baseArmorClass = this.prepareUnarmored(context);
        } else {
            RPGLItem armor = UUIDTable.getItem(armorUuid);
            JsonArray armorTags = (JsonArray) armor.get("tags");
            if (armorTags.contains("armor")) {
                // equipment slot holds armor
                baseArmorClass = this.prepareArmored(context, armor);
            } else {
                // equipment slot holds non-armor (you are unarmored) (this is not intended to happen)
                baseArmorClass = this.prepareUnarmored(context);
            }
        }

        // Add armor class bonus if wielding shield
        String shieldUuid = (String) this.getSource().seek("items.hand_2");
        if (shieldUuid != null) {
            RPGLItem shield = UUIDTable.getItem(shieldUuid);
            JsonArray shieldTags = (JsonArray) shield.get("tags");
            if (shieldTags.contains("shield")) {
                baseArmorClass += (Long) shield.get("armor_class_bonus");
            }
        }

        // Set base armor class
        this.subeventJson.put("base", baseArmorClass);
    }

    long prepareArmored(RPGLContext context, RPGLItem armor) throws Exception {
        Long baseArmorClass = (Long) armor.get("base_armor_class");

        // Add dexterity bonus, if not 0 (or lower)
        Long dexterityBonusMaximum = (Long) armor.get("dex_bonus_max");
        if (dexterityBonusMaximum == null) {
            // no limit to dexterity bonus
            long dexterityBonus = this.getSource().getAbilityModifierFromAbilityScore(context, "dex");
            baseArmorClass += dexterityBonus;
        } else if (dexterityBonusMaximum > 0L) {
            // non-zero, positive limit to dexterity bonus
            long dexterityBonus = this.getSource().getAbilityModifierFromAbilityScore(context, "dex");
            baseArmorClass += Math.min(dexterityBonus, dexterityBonusMaximum);
        }

        return baseArmorClass;
    }

    long prepareUnarmored(RPGLContext context) throws Exception {
        return 10L + this.getSource().getAbilityModifierFromAbilityScore(context, "dex");
    }

}
