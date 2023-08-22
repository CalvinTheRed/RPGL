package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DamageDelivery;
import org.rpgl.subevent.DamageRoll;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Function is dedicated to maximizing the damage dice of DamageRoll and DamageDelivery Subevents. If a damage type
 * is specified, only damage dice of that type will be maximized.
 *
 * @author Calvin Withun
 */
public class MaximizeDamage extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaximizeDamage.class);

    public MaximizeDamage() {
        super("maximize_damage");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context) {
        if (subevent instanceof DamageRoll damageRoll) {
            damageRoll.maximizeTypedDamageDice(functionJson.getString("damage_type"));
        } else if (subevent instanceof DamageDelivery damageDelivery) {
            damageDelivery.maximizeTypedDamageDice(functionJson.getString("damage_type"));
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

}
