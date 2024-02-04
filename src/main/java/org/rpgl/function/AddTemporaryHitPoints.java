package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Calculation;
import org.rpgl.subevent.Subevent;
import org.rpgl.subevent.TemporaryHitPointCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Function is dedicated to adding some amount of temporary hit points to a TemporaryHitPointsCollection Subevent.
 *
 * @author Calvin Withun
 */
public class AddTemporaryHitPoints extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddTemporaryHitPoints.class);

    public AddTemporaryHitPoints() {
        super("add_temporary_hit_points");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context, JsonArray originPoint) throws Exception {
        if (subevent instanceof TemporaryHitPointCollection temporaryHitPointCollection) {
            JsonArray temporaryHitPointsArray = functionJson.getJsonArray("temporary_hit_points");
            for (int i = 0; i < temporaryHitPointsArray.size(); i++) {
                temporaryHitPointCollection.addTemporaryHitPoints(Calculation.processBonusJson(effect, subevent, temporaryHitPointsArray.getJsonObject(i), context));
            }
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }
}
