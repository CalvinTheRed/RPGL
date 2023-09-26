package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonObject;

import java.util.List;
import java.util.Objects;

/**
 * This Subevent is dedicated to giving a new RPGLResource to a RPGLObject.
 * <br>
 * <br>
 * Source: a RPGLObject granting a new resource
 * <br>
 * Target: a RPGLObject receiving a new resource
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
    public void run(RPGLContext context, List<RPGLResource> resources) throws Exception {
        int count = Objects.requireNonNullElse(this.json.getInteger("count"), 1);
        String resourceId = this.json.getString("resource");
        for (int i = 0; i < count; i++) {
            RPGLResource resource = RPGLFactory.newResource(resourceId);
            resource.setOriginItem(this.getOriginItem());
            resource.addTag("temporary");
            this.getTarget().addResource(resource);
        }
    }

}
