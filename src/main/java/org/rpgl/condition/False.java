package org.rpgl.condition;

import org.jsonutils.JsonObject;
import org.rpgl.exception.ConditionMismatchException;

public class False extends Condition {

    @Override
    public boolean evaluate(long sourceUuid, long targetUuid, JsonObject conditionJson) throws ConditionMismatchException {
        super.verifyCondition("false", conditionJson);
        return false;
    }

}
