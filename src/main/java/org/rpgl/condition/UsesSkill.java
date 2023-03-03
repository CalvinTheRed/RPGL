package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.AbilityCheck;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Condition is dedicated to determining if an AbilityCheckSubevent Subevent uses a given skill.
 *
 * @author Calvin Withun
 */
public class UsesSkill extends Condition {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsesSkill.class);

    public UsesSkill() {
        super("uses_skill");
    }

    @Override
    public boolean evaluate(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent, JsonObject conditionJson, RPGLContext context) throws Exception {
        super.verifyCondition(conditionJson);
        if (subevent instanceof AbilityCheck abilityCheck) {
            return abilityCheck.getSkill().equals(conditionJson.getString("skill"));
        }
        LOGGER.warn("Can not evaluate condition for " + subevent.getClass());
        return false;
    }

}
