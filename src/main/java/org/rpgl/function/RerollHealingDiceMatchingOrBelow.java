package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.HealingRoll;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This function is dedicated to re-rolling healing dice matching or below a certain threshold in HealingRoll Subevents.
 *
 * @author Calvin Withun
 */
public class RerollHealingDiceMatchingOrBelow extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(RerollHealingDiceMatchingOrBelow.class);

    public RerollHealingDiceMatchingOrBelow() {
        super("reroll_healing_dice_matching_or_below");
    }

    @Override
    public void execute(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context) throws Exception {
        super.verifyFunction(functionJson);
        if (subevent instanceof HealingRoll healingRoll) {
            healingRoll.rerollHealingDiceMatchingOrBelow(
                    functionJson.getInteger("threshold")
            );
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

}
