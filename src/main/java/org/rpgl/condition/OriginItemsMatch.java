package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

import java.util.Objects;

/**
 * This Condition is dedicated to comparing the origin items of an RPGLEvent and an RPGLEffect to determine if the
 * effect was produced by the same item being used to perform an event.
 *
 * @author Calvin Withun
 */
public class OriginItemsMatch extends Condition {

    public OriginItemsMatch() {
        super("origin_items_match");
    }

    @Override
    public boolean run(RPGLEffect effect, Subevent subevent, JsonObject conditionJson, RPGLContext context) throws Exception {
        String effectOriginItem = effect.getOriginItem();
        String eventOriginItem = subevent.getOriginItem();
        return effectOriginItem != null && Objects.equals(effectOriginItem, eventOriginItem);
    }

}
