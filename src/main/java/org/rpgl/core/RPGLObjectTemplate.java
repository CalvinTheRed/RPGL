package org.rpgl.core;

import org.rpgl.datapack.RPGLObjectTO;
import org.rpgl.uuidtable.UUIDTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        this.putIfAbsent("events", new HashMap<String, Object>());
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
        List<Object> effectIdArray = (List) object.remove("effects");
        List<Object> effectUuidArray = new ArrayList<>();
        for (Object effectIdElement : effectIdArray) {
            String effectId = (String) effectIdElement;
            RPGLEffect effect = RPGLFactory.newEffect(effectId);
            if (effect != null) {
                effectUuidArray.add(effect.getUuid());
            }
        }
        object.put("effects", effectUuidArray);
    }

    static void processEquippedItems(RPGLObject object) {
        Map<String, Object> equippedItemIds = object.getMap(RPGLObjectTO.EQUIPPED_ITEMS_ALIAS);
        List<Object> inventoryUuids = object.getList(RPGLObjectTO.INVENTORY_ALIAS);
        Map<String, Object> equippedItemUuids = new HashMap<>();
        for (Map.Entry<String, Object> equippedItemEntry : equippedItemIds.entrySet()) {
            String equippedItemId = (String) equippedItemEntry.getValue();
            RPGLItem item = RPGLFactory.newItem(equippedItemId);
            equippedItemUuids.put(equippedItemEntry.getKey(), item.getUuid());
            inventoryUuids.add(item.getUuid());
        }
        object.put(RPGLObjectTO.EQUIPPED_ITEMS_ALIAS, equippedItemUuids);
    }

    static void processInventory(RPGLObject object) {
        List<Object> inventoryItemIds = object.getList(RPGLObjectTO.INVENTORY_ALIAS);
        List<Object> inventoryItemUuids = new ArrayList<>();
        for (Object itemId : inventoryItemIds) {
            RPGLItem item = RPGLFactory.newItem((String) itemId);
            inventoryItemUuids.add(item.getUuid());
        }
        object.put(RPGLObjectTO.INVENTORY_ALIAS, inventoryItemUuids);
    }

}
