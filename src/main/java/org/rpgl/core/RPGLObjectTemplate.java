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
     * A copy-constructor for the RPGLObjectTemplate class.
     *
     * @param data the data to be copied to this object
     */
    public RPGLObjectTemplate(JsonObject data) {
        this.join(data);
    }

    /**
     * This method returns a new RPGLObject object derived from the JSON template stored in the calling object.
     *
     * @return a new RPGLObject object
     */
    public RPGLObject newInstance() {
        RPGLObject object = new RPGLObject(this);
        processItems(object);
        processEffects(object);
        UUIDTable.register(object);
        return object;
    }

    /**
     * This helper method converts itemId's in an RPGLObjectTemplate's items object to RPGLItems. The UUID's of these
     * new RPGLItems replace the original object contents.
     *
     * @param object the object being processed.
     */
    private static void processItems(RPGLObject object) {
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
                RPGLItem item = RPGLFactory.newItem((String) items.remove(equipmentSlotName));
                items.put(equipmentSlotName, item.get("uuid"));
                equippedItemUuids.add(item.get("uuid"));
            }
            inventoryUuids.addAll(equippedItemUuids);

            // replace object inventory with array of item UUIDs
            items.put("inventory", inventoryUuids);
        }
    }

    /**
     * This helper method converts itemId's in an RPGLObjectTemplate's <code>items.inventory</code> array to RPGLItems.
     * The UUID's of these new RPGLItems replace the original object contents.
     *
     * @param inventoryIdArray the inventory being processed.
     */
    private static JsonArray processInventory(JsonArray inventoryIdArray) {
        JsonArray inventoryUuidArray = new JsonArray();
        for (Object inventoryIdElement : inventoryIdArray) {
            String itemId = (String) inventoryIdElement;
            RPGLItem item = RPGLFactory.newItem(itemId);
            inventoryUuidArray.add(item.get("uuid"));
        }
        return inventoryUuidArray;
    }

    /**
     * This helper method converts effectId's in an RPGLObjectTemplate's effects array to RPGLEffects. The UUID's of
     * these new RPGLEffects replace the original array contents.
     *
     * @param object the object being processed.
     */
    private static void processEffects(RPGLObject object) {
        Object keyValue = object.remove("effects");
        JsonArray effectIdArray = (JsonArray) keyValue;
        JsonArray effectUuidArray = new JsonArray();
        for (Object effectIdElement : effectIdArray) {
            String effectId = (String) effectIdElement;
            RPGLEffect effect = RPGLFactory.newEffect(effectId);
            effectUuidArray.add(effect.get("uuid"));
        }
        object.put("effects", effectUuidArray);
    }

}
