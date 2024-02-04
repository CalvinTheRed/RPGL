package org.rpgl.core;

import org.rpgl.datapack.RPGLItemTO;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * This class is used to contain a "template" to be used in the creation of new RPGLItem objects. Data stored in this
 * object is copied and then processed to create a specific RPGLItem defined somewhere in a datapack.
 *
 * @author Calvin Withun
 */
public class RPGLItemTemplate extends RPGLTemplate {

    public RPGLItemTemplate() {
        super();
    }

    public RPGLItemTemplate(JsonObject other) {
        this();
        this.join(other);
    }

    @Override
    public RPGLItem newInstance() {
        RPGLItem item = new RPGLItem();
        this.setup(item);
        UUIDTable.register(item);
        processEvents(item);
        processEquippedEffects(item);
        processEquippedResources(item);
        return item;
    }

    @Override
    public void setup(JsonObject item) {
        super.setup(item);
        item.asMap().putIfAbsent(RPGLItemTO.WEIGHT_ALIAS, 0);
        item.asMap().putIfAbsent(RPGLItemTO.ATTACK_BONUS_ALIAS, 0);
        item.asMap().putIfAbsent(RPGLItemTO.DAMAGE_BONUS_ALIAS, 0);
        item.asMap().putIfAbsent(RPGLItemTO.COST_ALIAS, 0);
        item.asMap().putIfAbsent(RPGLItemTO.EVENTS_ALIAS, new HashMap<String, Object>());
        item.asMap().putIfAbsent(RPGLItemTO.EQUIPPED_EFFECTS_ALIAS, new ArrayList<>());
        item.asMap().putIfAbsent(RPGLItemTO.EQUIPPED_RESOURCES_ALIAS, new ArrayList<>());
    }

    @Override
    public RPGLItemTemplate applyBonuses(JsonArray bonuses) {
        return new RPGLItemTemplate(super.applyBonuses(bonuses));
    }

    /**
     * This helper method defaults a new item's events to empty arrays if they are not specified.
     *
     * @param item a RPGLItem being created by this object
     */
    static void processEvents(RPGLItem item) {
        item.getEvents().asMap().putIfAbsent("multiple_hands", new ArrayList<>());
        item.getEvents().asMap().putIfAbsent("one_hand", new ArrayList<>());
        item.getEvents().asMap().putIfAbsent("special", new ArrayList<>());
    }

    /**
     * This helper method converts effectId's in an RPGLItemTemplate's equipped_effects array to RPGLEffects. The UUID's
     * of these new RPGLEffects replace the original array contents.
     *
     * @param item a RPGLItem being created by this object
     */
    static void processEquippedEffects(RPGLItem item) {
        JsonArray equippedEffectsIdArray = item.getEquippedEffects();
        JsonArray equippedEffectsUuidArray = new JsonArray();
        for (int i = 0; i < equippedEffectsIdArray.size(); i++) {
            String effectId = equippedEffectsIdArray.getString(i);
            RPGLEffect effect = RPGLFactory.newEffect(effectId).setOriginItem(item.getUuid());
            equippedEffectsUuidArray.addString(effect.getUuid());
        }
        item.setEquippedEffects(equippedEffectsUuidArray);
    }

    /**
     * This helper method converts resourceId's in an RPGLItemTemplate's equipped_resources array to RPGLResources. The
     * UUID's of these new RPGLResources replace the original array contents.
     *
     * @param item a RPGLItem being created by this object
     */
    static void processEquippedResources(RPGLItem item) {
        JsonArray processedEquippedResources = new JsonArray();
        JsonArray unprocessedEquippedResources = item.getEquippedResources();
        for (int i = 0; i < unprocessedEquippedResources.size(); i++) {
            String resourceId = unprocessedEquippedResources.getJsonObject(i).getString("resource");
            int count = Objects.requireNonNullElse(unprocessedEquippedResources.getJsonObject(i).getInteger("count"), 1);
            for (int j = 0; j < count; j++) {
                processedEquippedResources.addString(
                        RPGLFactory.newResource(resourceId).setOriginItem(item.getUuid()).getUuid()
                );
            }
        }
        item.setEquippedResources(processedEquippedResources);
    }

}
