package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DamageRoll;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context,
                    List<RPGLResource> resources) {
        if (subevent instanceof DamageRoll damageRoll) {
            damageRoll.setTypedDiceMatchingOrBelow(
                    functionJson.getInteger("threshold"),
                    functionJson.getInteger("set"),
                    functionJson.getString("damage_type")
            );
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

}
