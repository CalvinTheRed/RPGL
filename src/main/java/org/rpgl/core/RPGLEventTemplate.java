package org.rpgl.core;

import org.rpgl.json.JsonObject;

/**
 * This class is used to contain a "template" to be used in the creation of new RPGLEvent objects. Data stored in this
 * object is copied and then processed to create a specific RPGLEvent defined somewhere in a datapack.
 *
 * @author Calvin Withun
 */
public class RPGLEventTemplate extends JsonObject {

    /**
     * Constructs a new RPGLEvent object corresponding to the contents of the RPGLEventTemplate object. The new object
     * is registered to the UUIDTable class when it is constructed.
     *
     * @return a new RPGLEvent object
     */
    public RPGLEvent newInstance(String originItem) {
        RPGLEvent event = new RPGLEvent();
        event.join(this);
        event.setOriginItem(originItem);
        return event;
    }

    public RPGLEvent newInstance() {
        return this.newInstance(null);
    }

}
