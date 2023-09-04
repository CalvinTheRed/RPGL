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
 * This Function is dedicated to giving expertise to AbilityCheck Subevents.
 *
 * @author Calvin Withun
 */
public class GiveExpertise extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(GiveExpertise.class);

    public GiveExpertise() {
        super("give_expertise");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context,
                    List<RPGLResource> resources) {
        if (subevent instanceof AbilityCheck abilityCheck) {
            abilityCheck.giveExpertise();
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

}
