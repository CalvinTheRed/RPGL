package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.GetProficiency;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Function is dedicated to granting half proficiency to GetProficiency Subevents.
 *
 * @author Calvin Withun
 */
public class GrantHalfProficiency extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrantHalfProficiency.class);

    public GrantHalfProficiency() {
        super("grant_half_proficiency");
    }

    @Override
    public void execute(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent, JsonObject functionJson, RPGLContext context) throws Exception {
        super.verifyFunction(functionJson);
        if (subevent instanceof GetProficiency getProficiency) {
            getProficiency.grantHalfProficiency();
        } else {
            LOGGER.warn("Can not execute GrantHalfProficiency function on " + subevent.getClass());
        }
    }

}
