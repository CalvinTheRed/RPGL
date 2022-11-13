package org.rpgl.core;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.rpgl.uuidtable.UUIDTable;

import java.util.Collections;

/**
 * This class contains a JSON template defining a particular type of RPGLItem. It is not intended to be used for any
 * purpose other than constructing new RPGLItem objects.
 *
 * @author Calvin Withun
 */
public class RPGLItemTemplate extends JsonObject {

    /**
     * 	<p><b><i>RPGLItemTemplate</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public RPGLItemTemplate(JsonObject itemTemplateJson)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	The constructor for the RPGLItemTemplate class.
     * 	</p>
     *
     * 	@param itemTemplateJson the JSON data to be joined to the new RPGLItemTemplate object.
     */
    public RPGLItemTemplate(JsonObject itemTemplateJson) {
        this.join(itemTemplateJson);
    }

    /**
     * 	<p><b><i>newInstance</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public RPGLItem newInstance()
     * 	throws JsonFormatException
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Constructs a new RPGLItem object corresponding to the contents of the RPGLItemTemplate object. The new object is
     * 	registered to the UUIDTable class when it is constructed.
     * 	</p>
     *
     * 	@return a new RPGLItem object
     * 	@throws JsonFormatException if an error occurs while assigning improvised weapon damage to the RPGLItem
     */
    public RPGLItem newInstance() throws JsonFormatException {
        RPGLItem item = new RPGLItem(this);
        processWhileEquipped(item);
        processOptionalFields(item);
        processDefaultAttackAbilities(item);
        UUIDTable.register(item);
        return item;
    }

    /**
     * 	<p><b><i>processWhileEquipped</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * static void processWhileEquipped(RPGLItem item)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method converts effectId's in an RPGLItemTemplate's while_equipped array to RPGLEffects. The UUID's
     * 	of these new RPGLEffects replace the original array contents.
     * 	</p>
     *
     *  @param item an RPGLItem
     */
    static void processWhileEquipped(RPGLItem item) {
        Object keyValue = item.remove("while_equipped");
        JsonArray whileEquippedIdArray = (JsonArray) keyValue;
        JsonArray whileEquippedUuidArray = new JsonArray();
        for (Object whileEquippedIdElement : whileEquippedIdArray) {
            String effectId = (String) whileEquippedIdElement;
            RPGLEffect effect = RPGLFactory.newEffect(effectId);
            assert effect != null;
            whileEquippedUuidArray.add(effect.get("uuid"));
        }
        item.put("while_equipped", whileEquippedUuidArray);
    }

    /**
     * 	<p><b><i>processOptionalFields</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * static void processOptionalFields(RPGLItem item)
     * 	throws JsonFormatException
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method ensures that all optional fields in the RPGLItem JSON data are defined and present.
     * 	</p>
     *
     *  @param item an RPGLItem
     *  @throws JsonFormatException if an error occurs while assigning improvised weapon damage to the RPGLItem
     */
    static void processOptionalFields(RPGLItem item) throws JsonFormatException {
        if (item.get("weapon_properties") == null) {
            item.put("weapon_properties", new JsonArray(Collections.singleton("improvised")));
        }
        if (item.get("proficiency_tags") == null) {
            item.put("proficiency_tags", new JsonArray(Collections.singleton("improvised")));
        }
        processItemDamage(item);
    }

    /**
     * 	<p><b><i>processDefaultAttackAbilities</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * static void processDefaultAttackAbilities(RPGLItem item)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method defines the default attack abilities for RPGLItems. <i>Melee</i> and <i>thrown</i> attacks
     * 	default to using <i>str</i>, unless they have the <i>finesse</i> property, in which case they default to
     * 	<i>dex</i>. <i>Ranged</i> attacks always default to <i>dex</i>.
     * 	</p>
     *
     *  @param item an RPGLItem
     */
    static void processDefaultAttackAbilities(RPGLItem item) {
        // TODO move this into RPGLItem to make it more accessible to the client?
        JsonObject attackAbilities = new JsonObject();
        if (item.getWeaponProperties().contains("ranged")) {
            attackAbilities.put("ranged", "dex");
        }
        if (item.getWeaponProperties().contains("finesse")) {
            attackAbilities.put("melee", "dex");
            attackAbilities.put("thrown", "dex");
        } else {
            attackAbilities.put("melee", "str");
            attackAbilities.put("thrown", "str");
        }
        item.put("attack_abilities", attackAbilities);
    }

    /**
     * 	<p><b><i>processItemDamage</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * static void processItemDamage(RPGLItem item)
     * 	throws JsonFormatException
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method ensures that each RPGLItem object has values assigned for melee and thrown damage. If no
     *  values are assigned, the default improvised weapon damage die (1d4 bludgeoning) will be assigned.
     * 	</p>
     *
     *  @param item an RPGLItem
     * 	@throws JsonFormatException if an error occurs while assigning improvised weapon damage to the RPGLItem
     */
    static void processItemDamage(RPGLItem item) throws JsonFormatException {
        JsonObject damage = (JsonObject) item.get("damage");
        if (damage == null) {
            damage = new JsonObject();
            item.put("damage", damage);
        }

        if (damage.isEmpty()) {
            setImprovisedItemDamage(item, "melee");
            setImprovisedItemDamage(item, "thrown");
        } else {
            JsonArray melee = (JsonArray) damage.get("melee");
            JsonArray thrown = (JsonArray) damage.get("thrown");
            if (melee == null || melee.isEmpty()) {
                setImprovisedItemDamage(item, "melee");
            }
            if (thrown == null || thrown.isEmpty()) {
                setImprovisedItemDamage(item, "thrown");
            }
        }
    }

    /**
     * 	<p><b><i>setImprovisedItemDamage</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * static void setImprovisedItemDamage(RPGLItem item, String attackType)
     * 	throws JsonFormatException
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method assigns the improvised weapon damage die 1d4 to the passed attack type for the RPGLItem.
     * 	</p>
     *
     *  @param item       an RPGLItem
     *  @param attackType a type of weapon attack <code>("melee", "ranged", "thrown")</code>
     * 	@throws JsonFormatException if an error occurs while assigning improvised weapon damage to the RPGLItem
     */
    static void setImprovisedItemDamage(RPGLItem item, String attackType) throws JsonFormatException {
        String damageArrayString = """
                [
                    {
                        "type": "bludgeoning",
                        "dice": [
                            { "size": 4, "determined": 1 }
                        ],
                        "bonus": 0
                    }
                ]
                """;
        JsonArray damageArray = JsonParser.parseArrayString(damageArrayString);
        JsonObject damage = (JsonObject) item.get("damage");
        damage.put(attackType, damageArray);
    }

}
