package org.rpgl.core;

import org.rpgl.datapack.DatapackContent;
import org.rpgl.datapack.RPGLEventTO;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

/**
 * This class represents any high-level verbs which occur in RPGL. Examples of this include actions such as casting
 * Fireball, swinging a Longsword, or taking the Dodge action.
 *
 * @author Calvin Withun
 */
public class RPGLEvent extends DatapackContent {

    /**
     * Returns the RPGLEvent's area of effect.
     *
     * @return a JsonObject representing the area of effect of the RPGLEvent
     */
    public JsonObject getAreaOfEffect() {
        return this.getJsonObject(RPGLEventTO.AREA_OF_EFFECT_ALIAS);
    }

    /**
     * Setter for area of effect.
     *
     * @param areaOfEffect a new area of effect JsonObject
     */
    public void setAreaOfEffect(JsonObject areaOfEffect) {
        this.putJsonObject(RPGLEventTO.AREA_OF_EFFECT_ALIAS, areaOfEffect);
    }

    /**
     * Returns the Subevents composing the RPGLEvent.
     *
     * @return a JsonArray containing Subevent instructions
     */
    public JsonArray getSubevents() {
        return this.getJsonArray(RPGLEventTO.SUBEVENTS_ALIAS);
    }

    /**
     * Setter for subevents.
     *
     * @param subevents a new subevents JsonArray
     */
    public void setSubevents(JsonArray subevents) {
        this.putJsonArray(RPGLEventTO.SUBEVENTS_ALIAS, subevents);
    }

    // =================================================================================================================
    // Methods not derived directly from transfer objects
    // =================================================================================================================

}
