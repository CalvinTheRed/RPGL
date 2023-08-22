package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;
import org.rpgl.subevent.TemporaryHitPointRoll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Function is dedicated to maximizing the temporary hit point dice of TemporaryHitPointRoll Subevents.
 */
public class MaximizeTemporaryHitPoints extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaximizeTemporaryHitPoints.class);

    public MaximizeTemporaryHitPoints() {
        super("maximize_temporary_hit_points");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context) {
        if (subevent instanceof TemporaryHitPointRoll temporaryHitPointRoll) {
            temporaryHitPointRoll.maximizeTemporaryHitPointsDice();
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

}
