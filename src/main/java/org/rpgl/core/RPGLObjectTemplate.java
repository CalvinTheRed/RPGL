package org.rpgl.core;

import org.jsonutils.JsonObject;

/**
 * This class contains a JSON template defining a particular type of RPGLObject. It is not intended to be used for any
 * purpose other than constructing new RPGLObject objects.
 *
 * @author Calvin Withun
 */
public class RPGLObjectTemplate extends JsonObject {

    /**
     * A copy-constructor for the RPGLObjectTemplate class.
     *
     * @param data the data to be copied to this object
     */
    public RPGLObjectTemplate(JsonObject data) {
        this.join(data);
    }

    /**
     * This method returns a new RPGLObject object derived from the JSON template stored in the calling object.
     *
     * @return a new RPGLObject object
     */
    public RPGLObject newInstance() {
        RPGLObject object = new RPGLObject(this);
        return object;
    }

}
