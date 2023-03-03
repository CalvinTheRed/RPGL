package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DamageAffinity;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Function is dedicated to revoking immunity to a damage type as indicated by a DamageAffinity Subevent.
 *
 * @author Calvin Withun
 */
public class RevokeImmunity extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(RevokeImmunity.class);

    public RevokeImmunity() {
        super("revoke_immunity");
    }

    @Override
    public void execute(RPGLObject source, RPGLObject target, Subevent subevent,
                        JsonObject functionJson, RPGLContext context) throws Exception {
        super.verifyFunction(functionJson);
        if (subevent instanceof DamageAffinity damageAffinity) {
            damageAffinity.revokeImmunity();
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

}
