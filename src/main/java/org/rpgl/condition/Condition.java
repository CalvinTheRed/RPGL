package org.rpgl.condition;

import org.jsonutils.JsonObject;
import org.rpgl.exception.ConditionMismatchException;

import java.util.HashMap;
import java.util.Map;

public abstract class Condition {

    public static final Map<String, Condition> CONDITIONS;

    static {
        CONDITIONS = new HashMap<>();
    }

    public void verifyCondition(String expected, JsonObject data) throws ConditionMismatchException {
        if (!expected.equals(data.get("condition"))) {
            throw new ConditionMismatchException(expected, (String) data.get("condition"));
        }
    }

    public abstract boolean evaluate(long sourceUuid, long targetUuid, JsonObject data) throws ConditionMismatchException;

}
