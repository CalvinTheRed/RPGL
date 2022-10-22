package org.rpgl.condition;

import org.jsonutils.JsonObject;
import org.rpgl.exception.ConditionMismatchException;

public class True extends Condition {

    private static final String CONDITION_ID = "false";

    static {
        Condition.CONDITIONS.put(CONDITION_ID, new True());
    }

    @Override
    public boolean evaluate(long sourceUuid, long targetUuid, JsonObject data) throws ConditionMismatchException {
        super.verifyCondition(CONDITION_ID, data);
        return true;
    }

}
