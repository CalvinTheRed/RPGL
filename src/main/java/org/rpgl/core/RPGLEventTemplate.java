package org.rpgl.core;

import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.math.Die;

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
     * @return a new RPGLEvent object
     */
    public RPGLEvent newInstance() {
        RPGLEvent event = new RPGLEvent();
        event.join(this);
        processSubeventDamage(event);
        processSubeventHealing(event);
        // TODO how to deal with life-stealing?
        return event ;
    }

    /**
     * This helper method unpacks the condensed representation of damage dice in a RPGLEventTemplate into multiple dice
     * objects in accordance with the <code>count</code> field.
     *
     * @param event a RPGLEvent being created by this object
     */
    static void processSubeventDamage(RPGLEvent event) {
        JsonArray subevents = event.getSubevents();
        for (int i = 0; i < subevents.size(); i++) {
            JsonObject subeventJson = subevents.getJsonObject(i);
            JsonArray damageArray = subeventJson.getJsonArray("damage");
            if (damageArray != null) {
                for (int j = 0; j < damageArray.size(); j++) {
                    JsonObject damageJson = damageArray.getJsonObject(j);
                    damageJson.asMap().putIfAbsent("dice", new ArrayList<>());
                    damageJson.asMap().putIfAbsent("bonus", 0);
                    damageJson.putJsonArray("dice", Die.unpack(damageJson.removeJsonArray("dice")));
                }
            }
        }
    }

    /**
     * This helper method unpacks the condensed representation of healing dice in a RPGLEventTemplate into multiple dice
     * objects in accordance with the <code>count</code> field.
     *
     * @param event a RPGLEvent being created by this object
     */
    static void processSubeventHealing(RPGLEvent event) {
        JsonArray subevents = event.getSubevents();
        for (int i = 0; i < subevents.size(); i++) {
            JsonObject subeventJson = subevents.getJsonObject(i);
            JsonObject healing = subeventJson.getJsonObject("healing");
            if (healing != null) {
                healing.asMap().putIfAbsent("dice", new ArrayList<>());
                healing.asMap().putIfAbsent("bonus", 0);
                healing.putJsonArray("dice", Die.unpack(healing.removeJsonArray("dice")));
            }
        }
    }

}
