package org.rpgl.core;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;

/**
 * RPGLItems are objects which represent artifacts that RPGLObjects can use to perform RPGLEvents.
 *
 * @author Calvin Withun
 */
public class RPGLItem extends JsonObject {

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

}
