package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.AbilityCheck;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Function is dedicated to giving proficiency to AbilityCheck Subevents.
 *
 * @author Calvin Withun
 */
public class GiveProficiency extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(GiveProficiency.class);

    public GiveProficiency() {
        super("give_proficiency");
    }

    // TODO consider shifting proficiency indicator to subevent tags? That way it canb be promoted downstream so something like Sneak Attack damage can be applied easier

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context, JsonArray originPoint) {
        if (subevent instanceof AbilityCheck abilityCheck) {
            abilityCheck.giveProficiency();
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

}
