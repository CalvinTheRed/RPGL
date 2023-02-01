package org.rpgl.condition;

import org.rpgl.core.JsonObject;
import org.rpgl.core.RPGLObject;
import org.rpgl.exception.ConditionMismatchException;

import java.util.Map;

/**
 * This Condition always evaluates false.
 *
 * @author Calvin Withun
 */
public class False extends Condition {

    @Override
    public boolean evaluate(RPGLObject source, RPGLObject target, Map<String, Object> conditionJson) throws ConditionMismatchException {
        super.verifyCondition("false", conditionJson);
        return false;
    }

}
