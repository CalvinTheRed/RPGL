package org.rpgl.condition;

import org.jsonutils.JsonObject;
import org.rpgl.exception.ConditionMismatchException;

public class False extends Condition {

    private static final String CONDITION_ID = "false";

    static {
        Condition.CONDITIONS.put(CONDITION_ID, new False());
    }

    @Override
    public boolean evaluate(long sourceUuid, long targetUuid, JsonObject data) throws ConditionMismatchException {
        super.verifyCondition(CONDITION_ID, data);
        return false;
    }

}
