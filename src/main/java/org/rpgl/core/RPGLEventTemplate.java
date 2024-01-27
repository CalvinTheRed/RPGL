package org.rpgl.core;

import org.rpgl.datapack.RPGLEventTO;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.ArrayList;

/**
 * This class is used to contain a "template" to be used in the creation of new RPGLEvent objects. Data stored in this
 * object is copied and then processed to create a specific RPGLEvent defined somewhere in a datapack.
 *
 * @author Calvin Withun
 */
public class RPGLEventTemplate extends JsonObject {

    /**
     * Constructs a new RPGLEvent object corresponding to the contents of the RPGLEventTemplate object. The new object
     * is registered to the UUIDTable class when it is constructed.
     *
     * @param originItem an item UUID to be stored for the new event's origin item
     * @return a new RPGLEvent object
     */
    public RPGLEvent newInstance(String originItem) {
        RPGLEvent event = new RPGLEvent();
        this.setup(event);
        event.setOriginItem(originItem);
        processCost(event);
        return event;
    }

    void setup(RPGLEvent event) {
        event.join(this);
        event.asMap().putIfAbsent(RPGLEventTO.COST_ALIAS, new ArrayList<>());
    }

    /**
     * Constructs a new RPGLEvent object corresponding to the contents of the RPGLEventTemplate object. The new object
     * is registered to the UUIDTable class when it is constructed.
     *
     * @return a new RPGLEvent object
     */
    public RPGLEvent newInstance() {
        return this.newInstance(null);
    }

    /**
     * This helper method processed the cost field of a new event being constructed. It will default the default count,
     * minimum potency, and scale fields if none are specified.
     *
     * @param event a new RPGLEvent being created by this object
     */
    static void processCost(RPGLEvent event) {
        JsonArray rawCost = event.getCost();
        JsonArray processedCost = new JsonArray();
        for (int i = 0; i < rawCost.size(); i++) {
            JsonObject rawCostElement = rawCost.getJsonObject(i);
            rawCostElement.asMap().putIfAbsent("count", 1);
            rawCostElement.asMap().putIfAbsent("minimum_potency", 1);
            rawCostElement.asMap().putIfAbsent("scale", new ArrayList<>());
            int count = rawCostElement.removeInteger("count");
            for (int j = 0; j < count; j++) {
                processedCost.addJsonObject(rawCostElement.deepClone());
            }
        }
        event.setCost(processedCost);
    }

}
