package org.rpgl.subevent;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLItem;
import org.rpgl.uuidtable.UUIDTable;

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
        long baseArmorClass;
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

        // Add shield bonus, if applicable
        baseArmorClass += this.getShieldBonus();

        // Set base armor class value in json
        this.subeventJson.put("base", baseArmorClass); // TODO what is base for as opposed to set?
    }

    /**
     * 	<p>
     * 	<b><i>prepareArmored</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * long prepareArmored(RPGLContext context, RPGLItem armor)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method prepares the subevent if <code>source</code> is wearing armor.
     * 	</p>
     *
     *  @param context the context this Subevent takes place in
     *  @param armor   the armor worn by <code>source</code>
     *  @return the base armor class of the armored <code>source</code>
     *
     * 	@throws Exception if an exception occurs.
     */
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

    /**
     * 	<p>
     * 	<b><i>prepareUnarmored</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * long prepareUnarmored(RPGLContext context)
     * 	throws Exception
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method prepares the subevent if <code>source</code> is not wearing armor.
     * 	</p>
     *
     *  @param context the context this Subevent takes place in
     *  @return the base armor class of the unarmored <code>source</code>
     *
     * 	@throws Exception if an exception occurs.
     */
    long prepareUnarmored(RPGLContext context) throws Exception {
        return 10L + this.getSource().getAbilityModifierFromAbilityScore(context, "dex");
    }

    /**
     * 	<p>
     * 	<b><i>getShieldBonus</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * long getShieldBonus()
     * 	throws JsonFormatException
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method returns the highest armor class bonus granted by a shield <code>source</code> is wielding.
     * 	</p>
     *
     *  @return the shield bonus for <code>source</code>.
     *
     * 	@throws JsonFormatException if <code>source</code> is missing <code>items.hand_1</code> or <code>items.hand_2</code>.
     */
    long getShieldBonus() throws JsonFormatException {
        // Get armor class bonus if wielding shield (you may only benefit from 1 shield at a time, larger bonus is used).
        String hand1ShieldUuid = (String) this.getSource().seek("items.hand_1");
        String hand2ShieldUuid = (String) this.getSource().seek("items.hand_2");
        long hand1ShieldBonus = 0L;
        long hand2ShieldBonus = 0L;

        if (hand1ShieldUuid != null) {
            RPGLItem shield = UUIDTable.getItem(hand1ShieldUuid);
            JsonArray shieldTags = (JsonArray) shield.get("tags");
            if (shieldTags.contains("shield")) {
                hand1ShieldBonus = (Long) shield.get("armor_class_bonus");
            }
        }
        if (hand2ShieldUuid != null) {
            RPGLItem shield = UUIDTable.getItem(hand2ShieldUuid);
            JsonArray shieldTags = (JsonArray) shield.get("tags");
            if (shieldTags.contains("shield")) {
                hand2ShieldBonus = (Long) shield.get("armor_class_bonus");
            }
        }

        return Math.max(hand1ShieldBonus, hand2ShieldBonus);
    }

}
