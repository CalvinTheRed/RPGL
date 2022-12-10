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
        item.putIfAbsent("weight",            0L);
        item.putIfAbsent("attack_bonus",      0L);
        item.putIfAbsent("cost",              "0g");
        item.putIfAbsent("weapon_properties", new JsonArray(Collections.singleton("improvised")));
        item.putIfAbsent("proficiency_tags",  new JsonArray(Collections.singleton("improvised")));
        item.putIfAbsent("tags",              new JsonArray(Collections.singleton("improvised")));
        item.putIfAbsent("thrown_range",      JsonParser.parseObjectString("""
                    {
                        "normal": 20,
                        "long": 60
                    }
                    """
        ));
        item.defaultAttackAbilities();
        processEquippedEffects(item);
        processItemDamage(item);
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
    static void processEquippedEffects(RPGLItem item) {
        JsonArray equippedEffectsIdArray = (JsonArray) item.remove("equipped_effects");
        if (equippedEffectsIdArray == null) {
            item.put("equipped_effects", new JsonArray());
        } else {
            JsonArray equippedEffectsUuidArray = new JsonArray();
            for (Object equippedEffectIdElement : equippedEffectsIdArray) {
                String effectId = (String) equippedEffectIdElement;
                RPGLEffect effect = RPGLFactory.newEffect(effectId);
                assert effect != null;
                equippedEffectsUuidArray.add(effect.getUuid());
            }
            item.put("equipped_effects", equippedEffectsUuidArray);
        }
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
