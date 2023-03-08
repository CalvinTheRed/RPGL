package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Roll;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Function is dedicated to granting disadvantage to Roll Subevents.
 *
 * @author Calvin Withun
 */
public class GrantDisadvantage extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrantDisadvantage.class);

    public GrantDisadvantage() {
        super("grant_disadvantage");
    }

    @Override
    public void execute(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent,
                        JsonObject functionJson, RPGLContext context) throws Exception {
        super.verifyFunction(functionJson);
        if (subevent instanceof Roll roll) {
            roll.grantDisadvantage();
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

}
