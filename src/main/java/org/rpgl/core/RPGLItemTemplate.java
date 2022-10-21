package org.rpgl.core;

import org.jsonutils.JsonObject;

public class RPGLItemTemplate extends JsonObject {

    public RPGLItemTemplate(JsonObject templateData) {
        this.join(templateData);
    }

    public RPGLItem getInstance() {
        return null;
    }

}
