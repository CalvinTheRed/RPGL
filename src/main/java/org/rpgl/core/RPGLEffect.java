package org.rpgl.core;

import org.jsonutils.JsonObject;

/**
 * RPGLEffects are objects assigned to RPGLObjects which influence the final results of Subevents executed by or upon
 * those RPGLObjects.
 *
 * @author Calvin Withun
 */
public class RPGLEffect extends JsonObject {

    /**
     * A copy-constructor for the RPGLEffect class.
     *
     * @param data the data to be copied to this object
     */
    RPGLEffect(JsonObject data) {
        this.join(data);
    }

}
