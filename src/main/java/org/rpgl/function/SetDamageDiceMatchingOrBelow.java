package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DamageRoll;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This function is dedicated to setting typed damage dice matching or below a certain threshold in DamageRoll
 * Subevents to a given value.
 *
 * @author Calvin Withun
 */
public class SetDamageDiceMatchingOrBelow extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetDamageDiceMatchingOrBelow.class);

    public SetDamageDiceMatchingOrBelow() {
        super("set_damage_dice_matching_or_below");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context) {
        if (subevent instanceof DamageRoll damageRoll) {
            damageRoll.setDamageDiceMatchingOrBelow(
                    functionJson.getInteger("threshold"),
                    functionJson.getInteger("set"),
                    functionJson.getString("damage_type")
            );
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

}
