package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.GetProficiency;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Function is dedicated to granting expertise to GetProficiency Subevents.
 *
 * @author Calvin Withun
 */
public class GrantExpertise extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrantExpertise.class);

    public GrantExpertise() {
        super("grant_expertise");
    }

    @Override
    public void execute(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent,
                        JsonObject functionJson, RPGLContext context) throws Exception {
        super.verifyFunction(functionJson);
        if (subevent instanceof GetProficiency getProficiency) {
            getProficiency.grantExpertise();
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

}
