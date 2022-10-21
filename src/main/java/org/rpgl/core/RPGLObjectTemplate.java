package org.rpgl.core;

import org.jsonutils.JsonObject;

public class RPGLObjectTemplate extends JsonObject {

    public RPGLObjectTemplate(JsonObject templateData) {
        this.join(templateData);
    }

    public RPGLObject getInstance() {
        return null;
    }

}
