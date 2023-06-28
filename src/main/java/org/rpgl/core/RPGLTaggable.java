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
     * Adds a single tag to the object. This method cannot be used to add a redundant tag to the object.
     *
     * @param tag a tag to be added to the object
     */
    public void addTag(String tag) {
        if (!this.hasTag(tag)) {
            this.getTags().addString(tag);
        }
    }

    public void removeTag(String tag) {
        this.getTags().asList().remove(tag);
    }

    /**
     * Returns whether the object has a specific tag.
     *
     * @param tag a tag
     * @return true if the object has the tag, false otherwise
     */
    public boolean hasTag(String tag) {
        return this.getTags().asList().contains(tag);
    }

}
