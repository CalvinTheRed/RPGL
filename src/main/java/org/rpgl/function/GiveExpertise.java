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
 * This Function is dedicated to giving expertise to AbilityCheck Subevents.
 *
 * @author Calvin Withun
 */
public class GiveExpertise extends Function {

    // TODO make a generic solution to make use of scale objects?

    private static final Logger LOGGER = LoggerFactory.getLogger(GiveExpertise.class);

    public GiveExpertise() {
        super("give_expertise");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context, JsonArray originPoint) {
        if (subevent instanceof AbilityCheck abilityCheck) {
            abilityCheck.giveExpertise();
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

}
