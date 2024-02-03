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
public class RPGLEventTemplate extends RPGLTemplate {

    public RPGLEventTemplate() {
        super();
    }

    public RPGLEventTemplate(JsonObject other) {
        this();
        this.join(other);
    }

    @Override
    public RPGLEvent newInstance() {
        RPGLEvent event = new RPGLEvent();
        this.setup(event);
        processCost(event);
        return event;
    }

    @Override
    public void setup(JsonObject event) {
        super.setup(event);
        event.asMap().putIfAbsent(RPGLEventTO.COST_ALIAS, new ArrayList<>());
    }

    @Override
    public RPGLEventTemplate applyBonuses(JsonArray bonuses) {
        return new RPGLEventTemplate(super.applyBonuses(bonuses));
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
