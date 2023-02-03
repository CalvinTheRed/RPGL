package org.rpgl.core;

import org.rpgl.datapack.RPGLObjectTO;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.util.HashMap;
import java.util.Map;

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
        this.asMap().putIfAbsent("events", new HashMap<String, Object>());
        processEffects(object);
        processInventory(object);
        processEquippedItems(object);
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
        JsonArray effectIdArray = object.removeJsonArray("effects");
        JsonArray effectUuidArray = new JsonArray();
        for (int i = 0; i < effectIdArray.size(); i++) {
            String effectId = effectIdArray.getString(i);
            RPGLEffect effect = RPGLFactory.newEffect(effectId);
            if (effect != null) {
                effectUuidArray.addString(effect.getUuid());
            }
        }
        object.putJsonArray("effects", effectUuidArray);
    }

    static void processEquippedItems(RPGLObject object) {
        JsonObject equippedItemIds = object.getJsonObject(RPGLObjectTO.EQUIPPED_ITEMS_ALIAS);
        JsonArray inventoryUuids = object.getJsonArray(RPGLObjectTO.INVENTORY_ALIAS);
        JsonObject equippedItemUuids = new JsonObject();
        for (Map.Entry<String, Object> equippedItemEntry : equippedItemIds.asMap().entrySet()) {
            String equippedItemId = equippedItemIds.getString(equippedItemEntry.getKey());
            RPGLItem item = RPGLFactory.newItem(equippedItemId);
            equippedItemUuids.putString(equippedItemEntry.getKey(), item.getUuid());
            inventoryUuids.addString(item.getUuid());
        }
        object.putJsonObject(RPGLObjectTO.EQUIPPED_ITEMS_ALIAS, equippedItemUuids);
    }

    static void processInventory(RPGLObject object) {
        JsonArray inventoryItemIds = object.removeJsonArray(RPGLObjectTO.INVENTORY_ALIAS);
        JsonArray inventoryItemUuids = new JsonArray();
        for (int i = 0; i < inventoryItemIds.size(); i++) {
            RPGLItem item = RPGLFactory.newItem(inventoryItemIds.getString(i));
            inventoryItemUuids.addString(item.getUuid());
        }
        object.putJsonArray(RPGLObjectTO.INVENTORY_ALIAS, inventoryItemUuids);
    }

}
