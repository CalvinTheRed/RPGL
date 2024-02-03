package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;
import org.rpgl.subevent.TemporaryHitPointRoll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This function is dedicated to re-rolling temporary hit point dice matching or below a certain threshold in
 * TemporaryHitPointRoll Subevents.
 *
 * @author Calvin Withun
 */
public class RerollTemporaryHitPointDiceMatchingOrBelow extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(RerollTemporaryHitPointDiceMatchingOrBelow.class);

    public RerollTemporaryHitPointDiceMatchingOrBelow() {
        super("reroll_temporary_hit_point_dice_matching_or_below");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context, JsonArray originPoint) {
        if (subevent instanceof TemporaryHitPointRoll temporaryHitPointRoll) {
            temporaryHitPointRoll.rerollTemporaryHitPointsDiceMatchingOrBelow(
                    functionJson.getInteger("threshold")
            );
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

}
