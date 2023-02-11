package org.rpgl.core;

import org.rpgl.datapack.RPGLObjectTO;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to contain a "template" to be used in the creation of new RPGLObject objects. Data stored in this
 * object is copied and then processed to create a specific RPGLObject defined somewhere in a datapack.
 *
 * @author Calvin Withun
 */
public class RPGLObjectTemplate extends JsonObject {

    /**
     * 	<p><b><i>newInstance</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public RPGLObject newInstance()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Constructs a new RPGLObject object corresponding to the contents of the RPGLObjectTemplate object. The new
     * 	object is registered to the UUIDTable class when it is constructed.
     * 	</p>
     *
     * 	@return a new RPGLObject object
     */
    public RPGLObject newInstance() {
        RPGLObject object = new RPGLObject();
        object.join(this);
        this.asMap().putIfAbsent(RPGLObjectTO.EQUIPPED_ITEMS_ALIAS, new HashMap<String, Object>());
        this.asMap().putIfAbsent(RPGLObjectTO.INVENTORY_ALIAS, new ArrayList<>());
        this.asMap().putIfAbsent(RPGLObjectTO.EVENTS_ALIAS, new ArrayList<>());
        this.asMap().putIfAbsent(RPGLObjectTO.EFFECTS_ALIAS, new ArrayList<>());
        processEffects(object);
        processInventory(object);
        processEquippedItems(object);
        processHealthData(object);
        UUIDTable.register(object);
        return object;
    }

    /**
     * 	<p><b><i>processEffects</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * static void processEffects(RPGLObject object)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method converts effect IDs in an RPGLObjectTemplate's effects array to RPGLEffects. The UUID's of
     *  these new RPGLEffects replace the original array contents.
     * 	</p>
     *
     *  @param object an RPGLObject
     */
    static void processEffects(RPGLObject object) {
        JsonArray effectIdArray = object.removeJsonArray(RPGLObjectTO.EFFECTS_ALIAS);
        JsonArray effectUuidArray = new JsonArray();
        for (int i = 0; i < effectIdArray.size(); i++) {
            String effectId = effectIdArray.getString(i);
            RPGLEffect effect = RPGLFactory.newEffect(effectId);
            if (effect != null) {
                effectUuidArray.addString(effect.getUuid());
            }
        }
        object.putJsonArray(RPGLObjectTO.EFFECTS_ALIAS, effectUuidArray);
    }

    /**
     * 	<p><b><i>processEquippedItems</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * static void processEquippedItems(RPGLObject object)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method processes the equipped items as defined by an RPGLObjectTemplate. All items are created,
     * 	loaded into UUIDTable, and then assigned to the corresponding equipment slots in the object being created and
     * 	are appended to the inventory list. Note that this method must be called AFTER the <code>processInventory()</code>
     * 	method to work properly.
     * 	</p>
     *
     * 	@param object a RPGLObject being created by this object
     */
    static void processEquippedItems(RPGLObject object) {
        JsonObject equippedItemIds = object.getJsonObject(RPGLObjectTO.EQUIPPED_ITEMS_ALIAS);
        JsonArray inventoryUuids = object.getJsonArray(RPGLObjectTO.INVENTORY_ALIAS);
        JsonObject equippedItemUuids = new JsonObject();
        for (Map.Entry<String, ?> equippedItemEntry : equippedItemIds.asMap().entrySet()) {
            String equippedItemId = equippedItemIds.getString(equippedItemEntry.getKey());
            RPGLItem item = RPGLFactory.newItem(equippedItemId);
            equippedItemUuids.putString(equippedItemEntry.getKey(), item.getUuid());
            inventoryUuids.addString(item.getUuid());
        }
        object.putJsonObject(RPGLObjectTO.EQUIPPED_ITEMS_ALIAS, equippedItemUuids);
    }

    /**
     * 	<p><b><i>processInventory</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * static void processInventory(RPGLObject object)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method processes the equipment as defined by an RPGLObjectTemplate. All items are created and added
     * 	to the inventory list. Note that this method must be called BEFORE the <code>processEquippedItems()</code>
     * 	method for that method to work properly.
     * 	</p>
     *
     * 	@param object a RPGLObject being created by this object
     */
    static void processInventory(RPGLObject object) {
        JsonArray inventoryItemIds = object.removeJsonArray(RPGLObjectTO.INVENTORY_ALIAS);
        JsonArray inventoryItemUuids = new JsonArray();
        for (int i = 0; i < inventoryItemIds.size(); i++) {
            String itemId = inventoryItemIds.getString(i);
            System.out.println(itemId);
            RPGLItem item = RPGLFactory.newItem(itemId);
            inventoryItemUuids.addString(item.getUuid());
        }
        object.putJsonArray(RPGLObjectTO.INVENTORY_ALIAS, inventoryItemUuids);
    }

    /**
     * 	<p><b><i>processHealthData</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * static void processHealthData(RPGLObject object)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method unpacks the condensed representation of hit dice in a RPGLObjectTemplate into multiple dice
     * 	objects in accordance with the <code>count</code> field.
     * 	</p>
     *
     * 	@param object a RPGLObject being created by this object
     */
    static void processHealthData(RPGLObject object) {
        JsonObject healthData = object.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS);
        JsonArray templateHitDice = healthData.removeJsonArray("hit_dice");
        JsonArray hitDice = new JsonArray();
        for (int i = 0; i < templateHitDice.size(); i++) {
            JsonObject templateHitDieDefinition = templateHitDice.getJsonObject(i);
            JsonObject hitDie = new JsonObject() {{
                this.putInteger("size", templateHitDieDefinition.getInteger("size"));
                this.putJsonArray("determined", templateHitDieDefinition.getJsonArray("determined"));
                this.putBoolean("spent", false);
            }};
            for (int j = 0; j < templateHitDieDefinition.getInteger("count"); j++) {
                hitDice.addJsonObject(hitDie.deepClone());
            }
        }
        healthData.putJsonArray("hit_dice", hitDice);
    }

}
