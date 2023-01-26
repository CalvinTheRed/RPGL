package org.rpgl.core;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.rpgl.uuidtable.UUIDTable;
import org.rpgl.uuidtable.UUIDTableElement;

/**
 * RPGLItems are objects which represent artifacts that RPGLObjects can use to perform RPGLEvents.
 *
 * @author Calvin Withun
 */
public class RPGLItem extends UUIDTableElement {

    /**
     * 	<p><b><i>RPGLItem</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * RPGLItem(String itemJson)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	A copy-constructor for the RPGLItem class.
     * 	</p>
     *
     * 	@param itemJson the data to be joined to the new RPGLItem object
     */
    RPGLItem(JsonObject itemJson) {
        this.join(itemJson);
    }

    /**
     * 	<p><b><i>getAttackAbility</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public String getAttackAbility(String attackType)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Returns the ability score the weapon is currently set to use for attacks of the given type.
     * 	</p>
     *
     * 	@param attackType a type of weapon attack <code>("melee", "ranged", "thrown")</code>
     * 	@return an ability score, or <code>null</code> if an invalid attackType for the RPGLItem was passed
     */
    public String getAttackAbility(String attackType) {
        try {
            return (String) this.seek("attack_abilities." + attackType);
        } catch (JsonFormatException e) {
            // return null if an invalid attackType is provided, rather than throwing an error
            return null;
        }
    }

    /**
     * 	<p><b><i>defaultAttackAbilities</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * static void defaultAttackAbilities(RPGLItem item)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method defines the default attack abilities for RPGLItems. <i>Melee</i> and <i>thrown</i> attacks default
     * 	to using <i>str</i>, unless they have the <i>finesse</i> property, in which case they default to <i>dex</i>.
     * 	<i>Ranged</i> attacks always default to <i>dex</i>.
     * 	</p>
     */
    public void defaultAttackAbilities() {
        JsonObject attackAbilities = new JsonObject();
        if (this.getWeaponProperties().contains("ranged")) {
            attackAbilities.put("ranged", "dex");
        }
        if (this.getWeaponProperties().contains("finesse")) {
            attackAbilities.put("melee", "dex");
            attackAbilities.put("thrown", "dex");
        } else {
            attackAbilities.put("melee", "str");
            attackAbilities.put("thrown", "str");
        }
        this.put("attack_abilities", attackAbilities);
    }

    /**
     * 	<p><b><i>setAttackAbility</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void setAttackAbility(String attackType, String ability)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method assigns an ability score to be used for attack and damage rolls made using this item for a given
     * 	attack type.
     * 	</p>
     *
     * 	@param attackType a type of weapon attack <code>("melee", "ranged", "thrown")</code>
     *  @param ability    an ability score reference <code>("str", "dex", etc.)</code>
     */
    public void setAttackAbility(String attackType, String ability) {
        JsonObject attackAbilities = (JsonObject) this.get("attack_abilities");
        attackAbilities.put(attackType, ability);
    }

    /**
     * 	<p><b><i>getDamage</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public JsonArray getDamage()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Returns the damage dice associated with the weapon for attacks of the given type, or <code>null</code> if the
     * 	passed attack type does not apply to the RPGLItem.
     * 	</p>
     *
     * 	@param attackType a type of weapon attack <code>("melee", "ranged", "thrown")</code>
     * 	@return the damage associated with the RPGLItem for the given attackType
     */
    public JsonArray getDamage(String attackType) {
        try {
            return (JsonArray) this.seek("damage." + attackType);
        } catch (JsonFormatException e) {
            // return null if the attackType is invalid for the RPGLItem
            return null;
        }
    }

    /**
     * 	<p><b><i>getWeaponProperties</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public JsonArray getWeaponProperties()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Returns the object's weapon properties as a JsonArray.
     * 	</p>
     *
     * 	@return a JsonArray of weapon properties
     */
    public JsonArray getWeaponProperties() {
        return (JsonArray) this.get("weapon_properties");
    }

    /**
     * 	<p><b><i>getAttackBonus</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public long getAttackBonus()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Returns the item's attack bonus (typically reflective of a +N magic weapon's bonus to attack rolls).
     * 	</p>
     *
     * 	@return the item's attack bonus
     */
    public long getAttackBonus() {
        Long attackBonus = (Long) this.get("attack_bonus");
        return attackBonus != null ? attackBonus : 0L;
    }

    /**
     * 	<p><b><i>getEquippedEffects</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public RPGLEffect[] getEquippedEffects()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Returns an array of RPGLEffect objects which are to be applied to anyone who equips this RPGLItem.
     * 	</p>
     *
     * 	@return an array of RPGLEffect objects
     */
    public RPGLEffect[] getEquippedEffects() {
        JsonArray equippedEffectsUuidArray = (JsonArray) this.get("equipped_effects");
        RPGLEffect[] effects = new RPGLEffect[equippedEffectsUuidArray.size()];
        int i = 0;
        for (Object equippedEffectUuidElement : equippedEffectsUuidArray) {
            String equippedEffectUuid = (String) equippedEffectUuidElement;
            RPGLEffect effect = UUIDTable.getEffect(equippedEffectUuid);
            effects[i] = effect;
            i++;
        }
        return effects;
    }

    /**
     * 	<p><b><i>updateEquippedEffects</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void updateEquippedEffects()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Updates the source and target of RPGLEffect objects associated with this RPGLItem. This is to be used whenever a
     * 	RPGLObject equips the RPGLItem.
     * 	</p>
     *
     *  @param object an RPGLObject which has equipped this RPGLItem
     */
    public void updateEquippedEffects(RPGLObject object) {
        for (RPGLEffect effect : this.getEquippedEffects()) {
            effect.setSource(object.getUuid());
            effect.setTarget(object.getUuid());
        }
    }

}
