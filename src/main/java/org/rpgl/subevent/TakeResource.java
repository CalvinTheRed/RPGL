package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

/**
 * This Subevent is dedicated to taking one or more RPGLResource objects away from the target according to their
 * resource ID. An unspecified <code>"count"</code> field results in all matching resources to be removed.
 * <br>
 * <br>
 * NOTE: this Subevent can only remove an RPGLResource if it has the <code>"temporary"</code> tag.
 * <br>
 * <br>
 * Source: an RPGLObject taking away a RPGLResource
 * <br>
 * Target: an RPGLObject having its RPGLResource taken away
 *
 * @author Calvin Withun
 */
public class TakeResource extends Subevent {

    public TakeResource() {
        super("take_resource");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new TakeResource();
        clone.joinSubeventData(this.json);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new TakeResource();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public TakeResource invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (TakeResource) super.invoke(context, originPoint);
    }

    @Override
    public TakeResource joinSubeventData(JsonObject other) {
        return (TakeResource) super.joinSubeventData(other);
    }

    @Override
    public TakeResource prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        this.json.asMap().putIfAbsent("count", Integer.MAX_VALUE);
        return this;
    }

    @Override
    public TakeResource run(RPGLContext context, JsonArray originPoint) {
        String resourceTag = this.json.getString("resource_tag");
        int count = this.json.getInteger("count");
        RPGLObject target = super.getTarget();

        for (RPGLResource resource : target.getResourceObjects()) {
            if (count > 0 && resource.hasTag("temporary") && resource.hasTag(resourceTag)) {
                target.removeResource(resource.getUuid());
                count--;
            }
        }
        return this;
    }

    @Override
    public TakeResource setOriginItem(String originItem) {
        return (TakeResource) super.setOriginItem(originItem);
    }

    @Override
    public TakeResource setSource(RPGLObject source) {
        return (TakeResource) super.setSource(source);
    }

    @Override
    public TakeResource setTarget(RPGLObject target) {
        return (TakeResource) super.setTarget(target);
    }

}
