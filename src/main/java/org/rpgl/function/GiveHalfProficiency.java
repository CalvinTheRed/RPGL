package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.AbilityCheck;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This Function is dedicated to giving half proficiency to AbilityCheck Subevents.
 *
 * @author Calvin Withun
 */
public class GiveHalfProficiency extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(GiveHalfProficiency.class);

    public GiveHalfProficiency() {
        super("give_half_proficiency");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context,
                    List<RPGLResource> resources) {
        if (subevent instanceof AbilityCheck abilityCheck) {
            abilityCheck.giveHalfProficiency();
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

}
