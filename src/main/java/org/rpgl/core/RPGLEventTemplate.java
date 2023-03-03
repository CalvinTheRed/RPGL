package org.rpgl.core;

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
                    JsonArray templateDamageDiceArray = damageJson.removeJsonArray("dice");
                    JsonArray damageDiceArray = new JsonArray();
                    for (int k = 0; k < templateDamageDiceArray.size(); k++) {
                        JsonObject templateDamageDiceDefinition = templateDamageDiceArray.getJsonObject(k);
                        JsonObject damageDie = new JsonObject() {{
                            this.putInteger("size", templateDamageDiceDefinition.getInteger("size"));
                            this.putJsonArray("determined", templateDamageDiceDefinition.getJsonArray("determined"));
                        }};
                        for (int l = 0; l < templateDamageDiceDefinition.getInteger("count"); l++) {
                            damageDiceArray.addJsonObject(damageDie.deepClone());
                        }
                    }
                    damageJson.putJsonArray("dice", damageDiceArray);
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
            JsonArray healingArray = subeventJson.getJsonArray("heal");
            if (healingArray != null) {
                for (int j = 0; j < healingArray.size(); j++) {
                    JsonObject healingJson = healingArray.getJsonObject(j);
                    healingJson.asMap().putIfAbsent("dice", new ArrayList<>());
                    healingJson.asMap().putIfAbsent("bonus", 0);
                    JsonArray templateHealingDiceArray = healingJson.removeJsonArray("dice");
                    JsonArray healingDiceArray = new JsonArray();
                    for (int k = 0; k < templateHealingDiceArray.size(); k++) {
                        JsonObject templateHealingDiceDefinition = templateHealingDiceArray.getJsonObject(k);
                        JsonObject healingDie = new JsonObject() {{
                            this.putInteger("size", templateHealingDiceDefinition.getInteger("size"));
                            this.putJsonArray("determined", templateHealingDiceDefinition.getJsonArray("determined"));
                        }};
                        for (int l = 0; l < templateHealingDiceDefinition.getInteger("count"); l++) {
                            healingDiceArray.addJsonObject(healingDie.deepClone());
                        }
                    }
                    healingJson.putJsonArray("dice", healingDiceArray);
                }
            }
        }
    }

}
