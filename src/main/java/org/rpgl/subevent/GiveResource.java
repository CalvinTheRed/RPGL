package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLFactory;
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
    public void prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        this.json.asMap().putIfAbsent("count", 1);
    }

    @Override
    public void run(RPGLContext context, JsonArray originPoint) throws Exception {
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
    }

}
