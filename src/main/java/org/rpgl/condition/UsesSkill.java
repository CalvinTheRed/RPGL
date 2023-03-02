package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.AbilityChecck;
import org.rpgl.subevent.Subevent;

/**
 * This Condition is dedicated to determining if an AbilityCheckSubevent Subevent uses a given skill.
 *
 * @author Calvin Withun
 */
public class UsesSkill extends Condition {

    public UsesSkill() {
        super("uses_skill");
    }

    @Override
    public boolean evaluate(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent, JsonObject conditionJson, RPGLContext context) throws Exception {
        super.verifyCondition(conditionJson);
        if (subevent instanceof AbilityChecck abilityChecck) {
            return abilityChecck.getSkill().equals(conditionJson.getString("skill"));
        }
        return false;
    }

}
