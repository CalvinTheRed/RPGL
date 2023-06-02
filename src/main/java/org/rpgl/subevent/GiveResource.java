package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonObject;

import java.util.Objects;

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
    public void run(RPGLContext context) throws Exception {
        int count = Objects.requireNonNullElse(this.json.getInteger("count"), 1);
        String resourceId = this.json.getString("resource");
        int potency = Objects.requireNonNullElse(this.json.getInteger("potency"), 1);
        for (int i = 0; i < count; i++) {
            RPGLResource resource = RPGLFactory.newResource(resourceId);
            resource.addTag("temporary");
            resource.setPotency(potency);
            this.getTarget().addResource(resource);
        }
    }

}
