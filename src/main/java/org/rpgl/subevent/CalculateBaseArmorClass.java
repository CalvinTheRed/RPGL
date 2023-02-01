package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLItem;
import org.rpgl.uuidtable.UUIDTable;

import java.util.List;
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
        clone.joinSubeventData(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(Map<String, Object> subeventDataMap) {
        Subevent clone = new CalculateBaseArmorClass();
        clone.joinSubeventData(subeventDataMap);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        // NOTE: you do not need to be proficient in armor or shields to receive their AC benefits

        // Set base armor class from armor (or no armor)
        String armorUuid = (String) this.getSource().seek("items.armor");
        int baseArmorClass;
        if (armorUuid == null) {
            // if equipment slot is empty, you are unarmored
            baseArmorClass = this.prepareUnarmored(context);
        } else {
            RPGLItem armor = UUIDTable.getItem(armorUuid);
            List<Object> armorTags = armor.getList("tags");
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
     * int prepareArmored(RPGLContext context, RPGLItem armor)
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
    int prepareArmored(RPGLContext context, RPGLItem armor) throws Exception {
        Integer baseArmorClass = armor.getInteger("base_armor_class");

        // Add dexterity bonus, if not 0 (or lower)
        Integer dexterityBonusMaximum = armor.getInteger("dex_bonus_max");
        if (dexterityBonusMaximum == null) {
            // no limit to dexterity bonus
            int dexterityBonus = this.getSource().getAbilityModifierFromAbilityScore(context, "dex");
            baseArmorClass += dexterityBonus;
        } else if (dexterityBonusMaximum > 0) {
            // non-zero, positive limit to dexterity bonus
            int dexterityBonus = this.getSource().getAbilityModifierFromAbilityScore(context, "dex");
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
     * int prepareUnarmored(RPGLContext context)
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
    int prepareUnarmored(RPGLContext context) throws Exception {
        return 10 + this.getSource().getAbilityModifierFromAbilityScore(context, "dex");
    }

    /**
     * 	<p>
     * 	<b><i>getShieldBonus</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     * int getShieldBonus()
     * 	throws JsonFormatException
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method returns the highest armor class bonus granted by a shield <code>source</code> is wielding.
     * 	</p>
     *
     *  @return the shield bonus for <code>source</code>.
     */
    int getShieldBonus() {
        // Get armor class bonus if wielding shield (you may only benefit from 1 shield at a time, larger bonus is used).
        String hand1ShieldUuid = this.getSource().seekString("items.hand_1");
        String hand2ShieldUuid = this.getSource().seekString("items.hand_2");
        int hand1ShieldBonus = 0;
        int hand2ShieldBonus = 0;

        if (hand1ShieldUuid != null) {
            RPGLItem shield = UUIDTable.getItem(hand1ShieldUuid);
            List<Object> shieldTags = shield.getList("tags");
            if (shieldTags.contains("shield")) {
                hand1ShieldBonus = (Integer) shield.get("armor_class_bonus");
            }
        }
        if (hand2ShieldUuid != null) {
            RPGLItem shield = UUIDTable.getItem(hand2ShieldUuid);
            List<Object> shieldTags = shield.getList("tags");
            if (shieldTags.contains("shield")) {
                hand2ShieldBonus = (Integer) shield.get("armor_class_bonus");
            }
        }

        return Math.max(hand1ShieldBonus, hand2ShieldBonus);
    }

}
