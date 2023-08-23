package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This subevent is dedicated to destroying the event's origin item, if one exists. This is meant to be used to destroy
 * consumable items such as potions, or in the case that an Event breaks its origin item.
 *
 * @author Calvin Withun
 */
public class DestroyOriginItem extends Subevent {

    private static final Logger LOGGER = LoggerFactory.getLogger(DestroyOriginItem.class);

    public DestroyOriginItem() {
        super("destroy_origin_item");
    }


    @Override
    public Subevent clone() {
        Subevent clone = new DestroyOriginItem();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new DestroyOriginItem();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void run(RPGLContext context) {
        String originItemUuid = this.getOriginItem();
        RPGLItem originItem = UUIDTable.getItem(originItemUuid);
        if (originItem != null) {
            RPGLObject source = this.getSource();
            // remove origin item from equipped items
            List<String> originItemEquippedSlots = new ArrayList<>();
            JsonObject equippedItems = source.getEquippedItems();
            for (Map.Entry<String, Object> equippedItemEntry : equippedItems.asMap().entrySet()) {
                String equippedItemUuid = equippedItems.getString(equippedItemEntry.getKey());
                if (Objects.equals(equippedItemUuid, originItemUuid)) {
                    originItemEquippedSlots.add(equippedItemEntry.getKey());
                }
            }
            for (String equipmentSlot : originItemEquippedSlots) {
                equippedItems.removeString(equipmentSlot);
            }
            // remove origin item from inventory
            source.getInventory().asList().remove(originItemUuid);
            // remove origin item from UUIDTable
            UUIDTable.unregister(originItemUuid);
        } else {
            LOGGER.warn("Could not destroy origin item - subevent has no origin item!");
        }
    }
}
