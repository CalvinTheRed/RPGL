package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Calculation;
import org.rpgl.subevent.HealingCollection;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Function is dedicated to adding some amount of healing to a HealingCollection Subevent.
 *
 * @author Calvin Withun
 */
public class AddHealing extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddHealing.class);

    public AddHealing() {
        super("add_healing");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context, JsonArray originPoint) throws Exception {
        if (subevent instanceof HealingCollection healingCollection) {
            JsonArray healingArray = functionJson.getJsonArray("healing");
            for (int i = 0; i < healingArray.size(); i++) {
                healingCollection.addHealing(Calculation.processBonusJson(effect, subevent, healingArray.getJsonObject(i), context));
            }
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }
}
