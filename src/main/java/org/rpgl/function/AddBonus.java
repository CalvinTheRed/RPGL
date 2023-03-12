package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Calculation;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Function is dedicated to adding a bonus to Calculation Subevents.
 *
 * @author Calvin Withun
 */
public class AddBonus extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddBonus.class);

    public AddBonus() {
        super("add_bonus");
    }

    @Override
    public void execute(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context) throws Exception {
        super.verifyFunction(functionJson);
        if (subevent instanceof Calculation calculation) {
            calculation.addBonus(functionJson.getInteger("bonus"));
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

}
