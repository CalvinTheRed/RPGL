package org.rpgl.core;

import org.jsonutils.JsonObject;

/**
 * This class contains a JSON template defining a particular type of RPGLEffect. It is not intended to be used for any
 * purpose other than constructing new RPGLEffect objects.
 *
 * @author Calvin Withun
 */
public class RPGLEffectTemplate extends JsonObject {

    /**
     * A copy-constructor for the RPGLEffectTemplate class.
     *
     * @param data the data to be copied to this object
     */
    public RPGLEffectTemplate(JsonObject data) {
        this.join(data);
    }

    /**
     * This method returns a new RPGLEffect object derived from the JSON template stored in the calling object.
     *
     * @return a new RPGLEffect object
     */
    public RPGLEffect newInstance() {
        RPGLEffect effect = new RPGLEffect(this);
        return effect;
    }

}
