package org.rpgl.core;

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

}
