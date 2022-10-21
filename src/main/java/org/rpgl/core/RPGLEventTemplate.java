package org.rpgl.core;

import org.jsonutils.JsonObject;

public class RPGLEventTemplate extends JsonObject {

    public RPGLEventTemplate(JsonObject templateData) {
        this.join(templateData);
    }

    public RPGLEvent getInstance() {
        return null;
    }

}
