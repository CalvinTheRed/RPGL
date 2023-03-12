package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.GetProficiency;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Function is dedicated to granting proficiency to GetProficiency Subevents.
 *
 * @author Calvin Withun
 */
public class GrantProficiency extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrantProficiency.class);

    public GrantProficiency() {
        super("grant_proficiency");
    }

    @Override
    public void execute(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context) throws Exception {
        super.verifyFunction(functionJson);
        if (subevent instanceof GetProficiency getProficiency) {
            getProficiency.grantProficiency();
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

}
