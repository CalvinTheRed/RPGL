package org.rpgl.core;

import org.jsonutils.JsonObject;
import org.rpgl.subevent.Subevent;

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

    public boolean processSubevent(RPGLObject source, RPGLObject target, Subevent subevent) {
        // TODO...
        return false;
    }

}
