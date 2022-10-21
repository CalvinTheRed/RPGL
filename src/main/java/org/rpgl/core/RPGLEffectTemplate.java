package org.rpgl.core;

import org.jsonutils.JsonObject;

public class RPGLEffectTemplate extends JsonObject {

    public RPGLEffectTemplate(JsonObject templateData) {
        this.join(templateData);
    }

    public RPGLEffect getInstance() {
        return null;
    }

}
