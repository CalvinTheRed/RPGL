package org.rpgl.core;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

/**
 * This class contains a JSON template defining a particular type of RPGLObject. It is not intended to be used for any
 * purpose other than constructing new RPGLObject objects.
 *
 * @author Calvin Withun
 */
public class RPGLObjectTemplate extends JsonObject {

    /**
     * 	<p><b><i>RPGLObjectTemplate</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public RPGLObjectTemplate(JsonObject objectTemplateJson)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	The constructor for the RPGLObjectTemplate class.
     * 	</p>
     *
     * 	@param objectTemplateJson the JSON data to be joined to the new RPGLObjectTemplate object.
     */
    public RPGLObjectTemplate(JsonObject objectTemplateJson) {
        this.join(objectTemplateJson);
    }

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
        RPGLObject object = new RPGLObject(this);
        this.putIfAbsent("events", new JsonArray());
        processEffects(object);
        processItems(object);
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
        Object keyValue = object.remove("effects");
        JsonArray effectIdArray = (JsonArray) keyValue;
        JsonArray effectUuidArray = new JsonArray();
        for (Object effectIdElement : effectIdArray) {
            String effectId = (String) effectIdElement;
            RPGLEffect effect = RPGLFactory.newEffect(effectId);
            if (effect != null) {
                effectUuidArray.add(effect.get("uuid"));
            }
        }
        object.put("effects", effectUuidArray);
    }

    /**
     * 	<p><b><i>processItems</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * static void processItems(RPGLObject object)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method manages the conversion of item IDs in an RPGLObject's inventory to RPGLItem objects and their
     * 	UUID's.
     * 	</p>
     */
    static void processItems(RPGLObject object) {
        Object keyValue = object.get("items");
        if (keyValue instanceof JsonObject items) {

            // process inventory array (does not include equipped items)
            JsonArray inventoryUuids = new JsonArray();
            if (items.get("inventory") instanceof JsonArray) {
                inventoryUuids.addAll(processInventory((JsonArray) items.remove("inventory")));
            }

            // process equipped items (were not included in inventory array)
            JsonArray equippedItemUuids = new JsonArray();
            for (String equipmentSlotName : items.keySet()) {
                String itemId = (String) items.get(equipmentSlotName);
                RPGLItem item = RPGLFactory.newItem(itemId);
                assert item != null;
                String itemUuid = (String) item.get("uuid");
                items.put(equipmentSlotName, itemUuid);
                equippedItemUuids.add(itemUuid);
            }
            inventoryUuids.addAll(equippedItemUuids);

            // replace object inventory with array of item UUIDs
            items.put("inventory", inventoryUuids);
        }
    }

    /**
     * 	<p><b><i>processInventory</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * static JsonArray processInventory(RPGLObject object)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This helper method converts item IDs in an RPGLObjectTemplate's <code>items.inventory</code> array to RPGLItems.
     *  The UUID's of these new RPGLItems replace the original object contents.
     * 	</p>
     *
     *  @param inventoryIdArray the inventory being processed
     *  @return a JsonArray of RPGLItem UUID's
     */
    static JsonArray processInventory(JsonArray inventoryIdArray) {
        JsonArray inventoryUuidArray = new JsonArray();
        for (Object inventoryIdElement : inventoryIdArray) {
            String itemId = (String) inventoryIdElement;
            RPGLItem item = RPGLFactory.newItem(itemId);
            if (item != null) {
                inventoryUuidArray.add(item.get("uuid"));
            }
        }
        return inventoryUuidArray;
    }

}
