package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonObject;

import java.util.List;
import java.util.Objects;

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
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new TakeResource();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void run(RPGLContext context, List<RPGLResource> resources) {
        String resourceTag = this.json.getString("resource_tag");
        int count = Objects.requireNonNullElse(this.json.getInteger("count"), Integer.MAX_VALUE);
        RPGLObject target = this.getTarget();

        for (RPGLResource resource : target.getResourceObjects()) {
            if (count > 0 && resource.hasTag("temporary") && resource.hasTag(resourceTag)) {
                target.removeResource(resource.getUuid());
                count--;
            }
        }
    }

}
