package org.rpgl.core;

import org.jsonutils.JsonObject;

/**
 * This class contains a JSON template defining a particular type of RPGLItem. It is not intended to be used for any
 * purpose other than constructing new RPGLItem objects.
 *
 * @author Calvin Withun
 */
public class RPGLItemTemplate extends JsonObject {

    /**
     * A copy-constructor for the RPGLItemTemplate class.
     *
     * @param data the data to be copied to this object
     */
    public RPGLItemTemplate(JsonObject data) {
        this.join(data);
    }

    /**
     * This method returns a new RPGLItem object derived from the JSON template stored in the calling object.
     *
     * @return a new RPGLItem object
     */
    public RPGLItem newInstance() {
        // TODO write this method
        return null;
    }

}
