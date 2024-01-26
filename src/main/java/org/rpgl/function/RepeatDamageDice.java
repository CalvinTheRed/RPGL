package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.CriticalHitDamageCollection;
import org.rpgl.subevent.DamageCollection;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * This function is dedicated to repeating the initial damage dice of a DamageCollection or a
 * CriticalHitDamageCollection a set number of times. If the initial damage has no dice or does not exist, this function
 * does nothing.
 *
 * @author Calvin Withun
 */
public class RepeatDamageDice extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepeatDamageDice.class);

    public RepeatDamageDice() {
        super("repeat_damage_dice");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context) throws Exception {
        if (subevent instanceof DamageCollection damageCollection) {
            repeatDamageDice(damageCollection.getDamageCollection(), functionJson);
        } else if (subevent instanceof CriticalHitDamageCollection criticalHitDamageCollection) {
            repeatDamageDice(criticalHitDamageCollection.getDamageCollection(), functionJson);
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

    /**
     * This helper method is responsible for adding any extra dice provided to eligible subevents by this function.
     *
     * @param damageArray the damage array of an eligible subevent
     * @param functionJson the function's JSON instructions
     */
    void repeatDamageDice(JsonArray damageArray, JsonObject functionJson) {
        if (!damageArray.asList().isEmpty()) {
            JsonObject damageElement = damageArray.getJsonObject(0);
            JsonArray dice = damageElement.getJsonArray("dice");
            if (!dice.asList().isEmpty()) {
                JsonObject die = dice.getJsonObject(0);
                for (int i = 0; i < Objects.requireNonNullElse(functionJson.getInteger("count"), 1); i++) {
                    dice.addJsonObject(die.deepClone());
                }
            }
        }
    }
}
