package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Calculation;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Function is dedicated to assigning the set field of Calculation Subevents.
 *
 * @author Calvin Withun
 */
public class SetSet extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetSet.class);

    public SetSet() {
        super("set_set");
    }

    @Override
    public void execute(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent,
                        JsonObject functionJson, RPGLContext context) throws Exception {
        super.verifyFunction(functionJson);
        if (subevent instanceof Calculation calculation) {
            calculation.setSet(functionJson.getInteger("set"));
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

}
