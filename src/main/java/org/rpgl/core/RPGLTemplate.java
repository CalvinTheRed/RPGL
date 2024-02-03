package org.rpgl.core;

import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

/**
 * This abstract class represents the fundamental behavior of a template in RPGL.
 *
 * @author Calvin Withun
 */
public abstract class RPGLTemplate extends JsonObject {

    /**
     * Creates a new instance of the appropriate data type for the template.
     *
     * @return a new instance of the template
     */
    public abstract JsonObject newInstance();

    /**
     * This method copies the template data to an object being constructed. If the object is intended to have any
     * default values, this method should be overridden in order to specify them.
     *
     * @param other an object being prepared for creation
     */
    public void setup(JsonObject other) {
        other.join(this);
    }

    /**
     * Applies bonuses to the template.
     *
     * @param bonuses the bonuses to apply to the template
     * @return this template
     */
    public JsonObject applyBonuses(JsonArray bonuses) {
        JsonObject withBonuses = this.deepClone();
        for (int i = 0; i < bonuses.size(); i++) {
            JsonObject fieldBonus = bonuses.getJsonObject(i);
            String field = fieldBonus.getString("field");
            withBonuses.insertInteger(field, withBonuses.seekInteger(field) + fieldBonus.getInteger("bonus"));
        }
        return withBonuses;
    }

}
