package org.rpgl.core;

import org.rpgl.datapack.RPGLItemTO;
import org.rpgl.datapack.UUIDTableElementTO;
import org.rpgl.uuidtable.UUIDTable;
import org.rpgl.uuidtable.UUIDTableElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RPGLItem extends JsonObject implements UUIDTableElement {

    @Override
    public String getUuid() {
        return this.getString(UUIDTableElementTO.UUID_ALIAS);
    }

    @Override
    public void setUuid(String uuid) {
        this.put(UUIDTableElementTO.UUID_ALIAS, uuid);
    }

    @Override
    public void deleteUuid() {
        this.put(UUIDTableElementTO.UUID_ALIAS, null);
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
        return this.seekString("attack_abilities." + attackType);
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
        Map<String, Object> attackAbilities = new HashMap<>();
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
        this.put(RPGLItemTO.ATTACK_ABILITIES_ALIAS, attackAbilities);
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
        Map<String, Object> attackAbilities = this.getMap("attack_abilities");
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
    public List<Object> getDamage(String attackType) {
        return this.seekList("damage." + attackType);
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
    public List<Object> getWeaponProperties() {
        return this.getList(RPGLItemTO.WEAPON_PROPERTIES_ALIAS);
    }

    /**
     * 	<p><b><i>getAttackBonus</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public int getAttackBonus()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Returns the item's attack bonus (typically reflective of a +N magic weapon's bonus to attack rolls).
     * 	</p>
     *
     * 	@return the item's attack bonus
     */
    public int getAttackBonus() {
        Integer attackBonus = (Integer) this.get(RPGLItemTO.ATTACK_BONUS_ALIAS);
        return attackBonus != null ? attackBonus : 0;
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
        List<Object> equippedEffectsUuidArray = this.getList("equipped_effects");
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
