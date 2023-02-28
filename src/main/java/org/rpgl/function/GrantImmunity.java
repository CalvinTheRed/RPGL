package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DamageAffinity;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Function is dedicated to granting immunity to a damage type as indicated by a DamageAffinity Subevent.
 *
 * @author Calvin Withun
 */
public class GrantImmunity extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrantImmunity.class);

    public GrantImmunity() {
        super("grant_immunity");
    }

    @Override
    public void execute(RPGLObject source, RPGLObject target, Subevent subevent, JsonObject functionJson, RPGLContext context) throws FunctionMismatchException {
        super.verifyFunction(functionJson);
        if (subevent instanceof DamageAffinity damageAffinity) {
            damageAffinity.grantImmunity();
        } else {
            LOGGER.warn("Can not execute GrantImmunity function on " + subevent.getClass());
        }
    }

}
