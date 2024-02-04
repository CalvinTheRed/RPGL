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
 * This Function is dedicated to assigning the base field of Calculation Subevents.
 *
 * @author Calvin Withun
 */
public class SetBase extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetBase.class);

    public SetBase() {
        super("set_base");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context, JsonArray originPoint) throws Exception {
        if (subevent instanceof Calculation calculation) {
            calculation.setBase(Calculation.processSetJson(effect, subevent, functionJson.getJsonObject("base"), context));
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }
}
