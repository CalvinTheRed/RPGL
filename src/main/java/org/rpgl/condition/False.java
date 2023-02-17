package org.rpgl.condition;

import org.rpgl.core.RPGLObject;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

/**
 * This Condition always evaluates false.
 *
 * @author Calvin Withun
 */
public class False extends Condition {

    @Override
    public boolean evaluate(RPGLObject source, RPGLObject target, Subevent subevent, JsonObject conditionJson) throws ConditionMismatchException {
        super.verifyCondition("false", conditionJson);
        return false;
    }

}
