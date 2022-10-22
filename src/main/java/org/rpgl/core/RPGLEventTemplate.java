package org.rpgl.core;

import org.jsonutils.JsonObject;

/**
 * This class contains a JSON template defining a particular type of RPGLEvent. It is not intended to be used for any
 * purpose other than constructing new RPGLEvent objects.
 *
 * @author Calvin Withun
 */
public class RPGLEventTemplate extends JsonObject {

    /**
     * A copy-constructor for the RPGLEventTemplate class.
     *
     * @param data the data to be copied to this object
     */
    public RPGLEventTemplate(JsonObject data) {
        this.join(data);
    }

    /**
     * This method returns a new RPGLEvent object derived from the JSON template stored in the calling object.
     *
     * @return a new RPGLEvent object
     */
    public RPGLEvent newInstance() {
        // TODO write this method
        return null;
    }

}
