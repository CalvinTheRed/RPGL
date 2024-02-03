package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

/**
 * This Subevent is dedicated to giving a new RPGLResource to a RPGLObject. This Subevent allows for the specification
 * of the new resource's potency, with a default of 1 if not specified.
 * <br>
 * <br>
 * source: a RPGLObject granting a new resource
 * <br>
 * target: a RPGLObject receiving a new resource
 *
 * @author Calvin Withun
 */
public class GiveResource extends Subevent {

    public GiveResource() {
        super("give_resource");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new GiveResource();
        clone.joinSubeventData(this.json);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new GiveResource();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public GiveResource invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (GiveResource) super.invoke(context, originPoint);
    }

    @Override
    public GiveResource joinSubeventData(JsonObject other) {
        return (GiveResource) super.joinSubeventData(other);
    }

    @Override
    public GiveResource prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        this.json.asMap().putIfAbsent("count", 1);
        return this;
    }

    @Override
    public GiveResource run(RPGLContext context, JsonArray originPoint) throws Exception {
        int count = this.json.getInteger("count");
        String resourceId = this.json.getString("resource");
        Integer potency = this.json.getInteger("potency");
        for (int i = 0; i < count; i++) {
            RPGLResource resource = RPGLFactory.newResource(resourceId);
            resource.setOriginItem(super.getOriginItem());
            resource.addTag("temporary");
            if (potency != null) {
                resource.setPotency(potency);
            }
            super.getTarget().addResource(resource);
        }
        return this;
    }

    @Override
    public GiveResource setOriginItem(String originItem) {
        return (GiveResource) super.setOriginItem(originItem);
    }

    @Override
    public GiveResource setSource(RPGLObject source) {
        return (GiveResource) super.setSource(source);
    }

    @Override
    public GiveResource setTarget(RPGLObject target) {
        return (GiveResource) super.setTarget(target);
    }

}
