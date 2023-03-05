package org.rpgl.core;

import org.rpgl.datapack.RPGLTaggableTO;
import org.rpgl.json.JsonArray;
import org.rpgl.uuidtable.UUIDTableElement;

/**
 * This class represents any UUIDTableElement object types which possesses tags.
 *
 * @author Calvin Withun
 */
public abstract class RPGLTaggable extends UUIDTableElement {

    public JsonArray getTags() {
        return this.getJsonArray(RPGLTaggableTO.TAGS_ALIAS);
    }

    public void addTag(String tag) {
        this.getTags().addString(tag);
    }

}
