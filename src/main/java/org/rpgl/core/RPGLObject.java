package org.rpgl.core;

import org.jsonutils.JsonObject;

/**
 * RPGLObjects are objects which represent anything which might be placed on a game board (not including the game board
 * or terrain itself).
 *
 * @author Calvin Withun
 */
public class RPGLObject extends JsonObject {

    /**
     * A copy-constructor for the RPGLObject class.
     *
     * @param data the data to be copied to this object
     */
    RPGLObject(JsonObject data) {
        this.join(data);
    }

}
