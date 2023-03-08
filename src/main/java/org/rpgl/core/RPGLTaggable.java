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

    /**
     * Returns the tags assigned to the object.
     *
     * @return a JsonArray of String tags
     */
    public JsonArray getTags() {
        return this.getJsonArray(RPGLTaggableTO.TAGS_ALIAS);
    }

    /**
     * Setter for tags.
     *
     * @param tags a new tags JsonArray
     */
    public void setTags(JsonArray tags) {
        this.putJsonArray(RPGLTaggableTO.TAGS_ALIAS, tags);
    }

    /**
     * Adds a single tag to the object.
     *
     * @param tag a tag to be added to the object
     */
    public void addTag(String tag) {
        this.getTags().addString(tag);
    }

}
