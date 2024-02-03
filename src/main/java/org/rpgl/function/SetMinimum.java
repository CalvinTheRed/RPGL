package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Calculation;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Function is dedicated to assigning the minimum field of Calculation Subevents.
 * TODO this is insufficient to support features such as Reliable Talent or Silver Tongue because they require a minimum base
 *
 * @author Calvin Withun
 */
public class SetMinimum extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetMinimum.class);

    public SetMinimum() {
        super("set_minimum");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context, JsonArray originPoint) throws Exception {
        if (subevent instanceof Calculation calculation) {
            calculation.setMinimum(Calculation.processSetJson(effect, subevent, functionJson.getJsonObject("minimum"), context));
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }
}
