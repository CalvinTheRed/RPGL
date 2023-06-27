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
public class RPGLItemTemplate extends JsonObject {

    /**
     * Constructs a new RPGLItem object corresponding to the contents of the RPGLItemTemplate object. The new object is
     * registered to the UUIDTable class when it is constructed.
     *
     * @return a new RPGLItem object
     */
    public RPGLItem newInstance() {
        RPGLItem item = new RPGLItem();
        item.join(this);
        item.asMap().putIfAbsent(RPGLItemTO.WEIGHT_ALIAS, 0);
        item.asMap().putIfAbsent(RPGLItemTO.ATTACK_BONUS_ALIAS, 0);
        item.asMap().putIfAbsent(RPGLItemTO.DAMAGE_BONUS_ALIAS, 0);
        item.asMap().putIfAbsent(RPGLItemTO.COST_ALIAS, 0);
        item.asMap().putIfAbsent(RPGLItemTO.EVENTS_ALIAS, new HashMap<String, Object>());
        item.asMap().putIfAbsent(RPGLItemTO.EQUIPPED_EFFECTS_ALIAS, new ArrayList<>());
        processEvents(item);
        processEquippedEffects(item);
        UUIDTable.register(item);
        return item;
    }

    static void processEvents(RPGLItem item) {
        item.getEvents().asMap().putIfAbsent("multiple_hands", new ArrayList<>());
        item.getEvents().asMap().putIfAbsent("one_hand", new ArrayList<>());
        item.getEvents().asMap().putIfAbsent("special", new ArrayList<>());
    }

    /**
     * This helper method converts effectId's in an RPGLItemTemplate's while_equipped array to RPGLEffects. The UUID's
     * of these new RPGLEffects replace the original array contents.
     *
     * @param item a RPGLItem being created by this object
     */
    static void processEquippedEffects(RPGLItem item) {
        JsonArray equippedEffectsIdArray = Objects.requireNonNullElse(item.removeJsonArray(RPGLItemTO.EQUIPPED_EFFECTS_ALIAS), new JsonArray());
        JsonArray equippedEffectsUuidArray = new JsonArray();
        for (int i = 0; i < equippedEffectsIdArray.size(); i++) {
            String effectId = equippedEffectsIdArray.getString(i);
            RPGLEffect effect = RPGLFactory.newEffect(effectId);
            equippedEffectsUuidArray.addString(effect.getUuid());
        }
        item.putJsonArray(RPGLItemTO.EQUIPPED_EFFECTS_ALIAS, equippedEffectsUuidArray);
    }

}
