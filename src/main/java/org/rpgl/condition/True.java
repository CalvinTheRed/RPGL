package org.rpgl.condition;

import org.jsonutils.JsonObject;
import org.rpgl.exception.ConditionMismatchException;

public class True extends Condition {

    @Override
    public boolean evaluate(long sourceUuid, long targetUuid, JsonObject data) throws ConditionMismatchException {
        super.verifyCondition("true", data);
        return true;
    }

}
