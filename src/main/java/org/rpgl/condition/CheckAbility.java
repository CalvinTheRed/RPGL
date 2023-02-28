package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.CalculateAbilityScore;
import org.rpgl.subevent.Roll;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Condition is dedicated to checking the ability score being used for a CalculateAbilityScore Subevent or a Roll
 * Subevent.
 *
 * @author Calvin Withun
 */
public class CheckAbility extends Condition {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckAbility.class);

    public CheckAbility() {
        super("check_ability");
    }

    @Override
    public boolean evaluate(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent, JsonObject conditionJson, RPGLContext context) throws Exception {
        super.verifyCondition(conditionJson);
        if (subevent instanceof CalculateAbilityScore calculateAbilityScore) {
            return calculateAbilityScore.getAbility().equals(conditionJson.getString("ability"));
        } else if (subevent instanceof Roll roll) {
            return roll.getAbility(context).equals(conditionJson.getString("ability"));
        }
        LOGGER.warn("Can not evaluate CheckAbility condition on " + subevent.getClass());
        return false;
    }

}
