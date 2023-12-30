package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonObject;

import java.util.List;

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
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new GiveResource();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context, List<RPGLResource> resources) throws Exception {
        super.prepare(context, resources);
        this.json.asMap().putIfAbsent("count", 1);
    }

    @Override
    public void run(RPGLContext context, List<RPGLResource> resources) throws Exception {
        int count = this.json.getInteger("count");
        String resourceId = this.json.getString("resource");
        Integer potency = this.json.getInteger("potency");
        for (int i = 0; i < count; i++) {
            RPGLResource resource = RPGLFactory.newResource(resourceId);
            resource.setOriginItem(this.getOriginItem());
            resource.addTag("temporary");
            if (potency != null) {
                resource.setPotency(potency);
            }
            super.getTarget().addResource(resource);
        }
    }

}
