package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.HealingDelivery;
import org.rpgl.subevent.HealingRoll;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Function is dedicated to maximizing the healing dice of HealingRoll and HealingDelivery Subevents.
 */
public class MaximizeHealing extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaximizeHealing.class);

    public MaximizeHealing() {
        super("maximize_healing");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context, JsonArray originPoint) {
        if (subevent instanceof HealingRoll healingRoll) {
            healingRoll.maximizeHealingDice();
        } else if (subevent instanceof HealingDelivery healingDelivery) {
            healingDelivery.maximizeHealingDice();
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

}
