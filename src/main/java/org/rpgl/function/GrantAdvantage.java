package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Roll;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This Function is dedicated to granting advantage to Roll Subevents.
 *
 * @author Calvin Withun
 */
public class GrantAdvantage extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrantAdvantage.class);

    public GrantAdvantage() {
        super("grant_advantage");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context,
                    List<RPGLResource> resources) {
        if (subevent instanceof Roll roll) {
            roll.grantAdvantage();
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

}
