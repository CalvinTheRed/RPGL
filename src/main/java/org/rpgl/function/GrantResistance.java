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
 * This Function is dedicated to granting resistance to a damage type as indicated by a DamageAffinity Subevent.
 *
 * @author Calvin Withun
 */
public class GrantResistance extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrantResistance.class);

    public GrantResistance() {
        super("grant_resistance");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context, JsonArray originPoint) {
        if (subevent instanceof DamageAffinity damageAffinity) {
            damageAffinity.grantResistance(functionJson.getString("damage_type"));
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

}
