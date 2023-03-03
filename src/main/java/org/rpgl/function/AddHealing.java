package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
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
    public void execute(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent, JsonObject functionJson, RPGLContext context) throws Exception {
        super.verifyFunction(functionJson);
        if (subevent instanceof HealingCollection healingCollection) {
            healingCollection.addHealing(functionJson.getJsonObject("healing"));
        } else {
            LOGGER.warn("Can not execute AddHealing function on " + subevent.getClass());
        }
    }

}
