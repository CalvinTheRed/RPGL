package org.rpgl.core;

import org.jsonutils.JsonObject;

/**
 * RPGLEvents are combinations of Subevents which define an emergent event which can be performed by a RPGLObject.
 *
 * @author Calvin Withun
 */
public class RPGLEvent extends JsonObject {

    /**
     * A copy-constructor for the RPGLEvent class.
     *
     * @param data the data to be copied to this object
     */
    RPGLEvent(JsonObject data) {
        this.join(data);
    }

}
