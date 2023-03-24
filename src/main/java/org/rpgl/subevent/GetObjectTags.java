package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

/**
 * This Subevent is dedicated to collecting all tags which apply to a RPGLObject, beyond what appears on its template.
 * <br>
 * <br>
 * Source: an RPGLObject being queried for tags
 * <br>
 * Target: should be the same as the source
 *
 * @author Calvin Withun
 */
public class GetObjectTags extends Subevent {

    public GetObjectTags() {
        super("get_object_tags");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new GetObjectTags();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new GetObjectTags();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        this.json.putJsonArray("object_tags", new JsonArray());
    }

    /**
     * Adds a tag to the Subevent, to be applied to a RPGLObject later.
     *
     * @param tag a tag to be applied to a RPGLObject
     */
    public void addObjectTag(String tag) {
        this.getObjectTags().addString(tag);
    }

    /**
     * Returns the list of tags gathered for a RPGLObject.
     *
     * @return a JsonArray of tags
     */
    public JsonArray getObjectTags() {
        return this.json.getJsonArray("object_tags");
    }

}
