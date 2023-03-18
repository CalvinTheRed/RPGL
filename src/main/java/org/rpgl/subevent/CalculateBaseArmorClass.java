package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLItem;
import org.rpgl.datapack.RPGLItemTO;
import org.rpgl.datapack.RPGLObjectTO;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.util.Map;

/**
 * This subevent is dedicated to calculating the armor class against which attack rolls are made. Once the attack roll
 * is made, the target will have an opportunity to raise its armor class further through the CalculateEffectiveArmorClass
 * subevent, but reactive changes to armor class are not accounted for in this subevent.
 * <br>
 * <br>
 * Source: the RPGLObject whose base armor class is being calculated
 * <br>
 * Target: should be the same as the source
 *
 * @author Calvin Withun
 */
public class CalculateBaseArmorClass extends Calculation {

    public CalculateBaseArmorClass() {
        super("calculate_base_armor_class");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new CalculateBaseArmorClass();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new CalculateBaseArmorClass();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        // Set base armor class from armor (or no armor)
        String armorUuid = this.getSource().getJsonObject(RPGLObjectTO.EQUIPPED_ITEMS_ALIAS).getString("armor");
        int baseArmorClass;
        if (armorUuid == null) {
            // if equipment slot is empty, you are unarmored
            baseArmorClass = this.prepareUnarmored(context);
        } else {
            RPGLItem armor = UUIDTable.getItem(armorUuid);
            JsonArray armorTags = armor.getJsonArray("tags");
            if (armorTags.asList().contains("armor")) {
                // equipment slot holds armor
                baseArmorClass = this.prepareArmored(armor, context);
            } else {
                // equipment slot holds non-armor (you are unarmored) (this is not intended to happen)
                baseArmorClass = this.prepareUnarmored(context);
            }
        }

        // Set base armor class value in json
        this.setBase(new JsonObject() {{
            this.putInteger("value", baseArmorClass + getShieldBonus());
        }});
    }

    /**
     * This helper method prepares the subevent if <code>source</code> is wearing armor.
     *
     * @param armor   the armor worn by <code>source</code>
     * @param context the context this Subevent takes place in
     * @return the base armor class of the armored <code>source</code>
     *
     * @throws Exception if an exception occurs.
     */
    int prepareArmored(RPGLItem armor, RPGLContext context) throws Exception {
        Integer baseArmorClass = armor.getInteger(RPGLItemTO.ARMOR_CLASS_BASE_ALIAS);

        // Add dexterity bonus, if not 0 (or lower)
        Integer dexterityBonusMaximum = armor.getInteger(RPGLItemTO.ARMOR_CLASS_DEX_LIMIT_ALIAS);
        if (dexterityBonusMaximum == null) {
            // no limit to dexterity bonus
            int dexterityBonus = this.getSource().getAbilityModifierFromAbilityName("dex", context);
            baseArmorClass += dexterityBonus;
        } else if (dexterityBonusMaximum > 0) {
            // non-zero, positive limit to dexterity bonus
            int dexterityBonus = this.getSource().getAbilityModifierFromAbilityName("dex", context);
            baseArmorClass += Math.min(dexterityBonus, dexterityBonusMaximum);
        }

        return baseArmorClass;
    }

    /**
     * This helper method prepares the subevent if <code>source</code> is not wearing armor.
     *
     * @param context the context this Subevent takes place in
     * @return the base armor class of the unarmored <code>source</code>
     *
     * @throws Exception if an exception occurs.
     */
    int prepareUnarmored(RPGLContext context) throws Exception {
        return 10 + this.getSource().getAbilityModifierFromAbilityName("dex", context);
    }

    /**
     * This helper method returns the highest armor class bonus granted by a shield <code>source</code> is wielding.
     *
     * @return the shield bonus for <code>source</code>.
     */
    int getShieldBonus() {
        int shieldBonus = 0;
        JsonObject equippedItems = this.getSource().getJsonObject(RPGLObjectTO.EQUIPPED_ITEMS_ALIAS);
        for (Map.Entry<String, Object> equippedItemsEntry : equippedItems.asMap().entrySet()) {
            String equipmentSlot = equippedItemsEntry.getKey();
            String itemUuid = equippedItems.getString(equipmentSlot);
            RPGLItem item = UUIDTable.getItem(itemUuid);
            if (item.getJsonArray(RPGLItemTO.TAGS_ALIAS).asList().contains("shield")) {
                int itemShieldBonus = item.getInteger(RPGLItemTO.ARMOR_CLASS_BONUS_ALIAS);
                if (itemShieldBonus > shieldBonus) {
                    shieldBonus = itemShieldBonus;
                }
            }
        }
        return shieldBonus;
    }

}
