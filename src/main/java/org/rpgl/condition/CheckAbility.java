package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.AbilitySubevent;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * This Condition is dedicated to checking the ability score being used for an AbilitySubevent.
 *
 * @author Calvin Withun
 */
public class CheckAbility extends Condition {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckAbility.class);

    public CheckAbility() {
        super("check_ability");
    }

    @Override
    public boolean run(RPGLEffect effect, Subevent subevent, JsonObject conditionJson, RPGLContext context) {
        if (subevent instanceof AbilitySubevent abilitySubevent) {
            return Objects.equals(abilitySubevent.getAbility(context), conditionJson.getString("ability"));
        }
        LOGGER.warn("Can not evaluate condition for " + subevent.getClass());
        return false;
    }

}
