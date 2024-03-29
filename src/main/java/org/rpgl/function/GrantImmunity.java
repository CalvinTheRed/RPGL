package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonArray;
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
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context, JsonArray originPoint) {
        if (subevent instanceof DamageAffinity damageAffinity) {
            damageAffinity.grantImmunity(functionJson.getString("damage_type"));
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

}
