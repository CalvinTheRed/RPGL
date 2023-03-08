package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.HealingRoll;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This function is dedicated to setting healing dice matching or below a certain threshold in HealingRoll Subevents to
 * a given value.
 *
 * @author Calvin Withun
 */
public class SetHealingDiceMatchingOrBelow extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetHealingDiceMatchingOrBelow.class);

    public SetHealingDiceMatchingOrBelow() {
        super("set_healing_dice_matching_or_below");
    }

    @Override
    public void execute(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent,
                        JsonObject functionJson, RPGLContext context) throws Exception {
        super.verifyFunction(functionJson);
        if (subevent instanceof HealingRoll healingRoll) {
            healingRoll.setHealingDiceMatchingOrBelow(
                    functionJson.getInteger("threshold"),
                    functionJson.getInteger("set")
            );
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

}
