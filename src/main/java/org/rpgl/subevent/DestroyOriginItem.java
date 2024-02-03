package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
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
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new DestroyOriginItem();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public DestroyOriginItem invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (DestroyOriginItem) super.invoke(context, originPoint);
    }

    @Override
    public DestroyOriginItem joinSubeventData(JsonObject other) {
        return (DestroyOriginItem) super.joinSubeventData(other);
    }

    @Override
    public DestroyOriginItem run(RPGLContext context, JsonArray originPoint) {
        String originItemUuid = super.getOriginItem();
        if (UUIDTable.getItem(originItemUuid) != null) {
            RPGLObject source = super.getSource();
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
        return this;
    }

    @Override
    public DestroyOriginItem setOriginItem(String originItem) {
        return (DestroyOriginItem) super.setOriginItem(originItem);
    }

    @Override
    public DestroyOriginItem setSource(RPGLObject source) {
        return (DestroyOriginItem) super.setSource(source);
    }

    @Override
    public DestroyOriginItem setTarget(RPGLObject target) {
        return (DestroyOriginItem) super.setTarget(target);
    }

}
