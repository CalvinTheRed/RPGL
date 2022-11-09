package org.rpgl.core;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;

/**
 * RPGLItems are objects which represent artifacts that RPGLObjects can use to perform RPGLEvents.
 *
 * @author Calvin Withun
 */
public class RPGLItem extends JsonObject {

    /**
     * A copy-constructor for the RPGLItem class.
     *
     * @param data the data to be copied to this object
     */
    RPGLItem(JsonObject data) {
        this.join(data);
    }

    public String getAttackAbility(String attackType) throws JsonFormatException {
        return (String) this.seek("attack_abilities." + attackType);
    }

    public JsonArray getDamage() {
        return (JsonArray) this.get("damage");
    }

    public JsonArray getWeaponProperties() {
        return (JsonArray) this.get("weapon_properties");
    }

}
