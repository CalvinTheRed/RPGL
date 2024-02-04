package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
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
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new GetObjectTags();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public GetObjectTags invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (GetObjectTags) super.invoke(context, originPoint);
    }

    @Override
    public GetObjectTags joinSubeventData(JsonObject other) {
        return (GetObjectTags) super.joinSubeventData(other);
    }

    @Override
    public GetObjectTags prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        this.json.putJsonArray("object_tags", new JsonArray());
        return this;
    }

    @Override
    public GetObjectTags run(RPGLContext context, JsonArray originPoint) throws Exception {
        return this;
    }

    @Override
    public GetObjectTags setOriginItem(String originItem) {
        return (GetObjectTags) super.setOriginItem(originItem);
    }

    @Override
    public GetObjectTags setSource(RPGLObject source) {
        return (GetObjectTags) super.setSource(source);
    }

    @Override
    public GetObjectTags setTarget(RPGLObject target) {
        return (GetObjectTags) super.setTarget(target);
    }

    /**
     * Adds a tag to the Subevent, to be applied to a RPGLObject later.
     *
     * @param tag a tag to be applied to a RPGLObject
     * @return this GetObjectTags
     */
    public GetObjectTags addObjectTag(String tag) {
        this.getObjectTags().addString(tag);
        return this;
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
