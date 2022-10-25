package org.rpgl.condition;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLObject;
import org.rpgl.exception.ConditionMismatchException;

import java.util.HashMap;
import java.util.Map;

public abstract class Condition {

    public static final Map<String, Condition> CONDITIONS;

    static {
        CONDITIONS = new HashMap<>();
        Condition.CONDITIONS.put("all", new All());
        Condition.CONDITIONS.put("any", new Any());
        Condition.CONDITIONS.put("false", new False());
        Condition.CONDITIONS.put("invert", new Invert());
        Condition.CONDITIONS.put("true", new True());
    }

    public void verifyCondition(String expected, JsonObject conditionJson) throws ConditionMismatchException {
        if (!expected.equals(conditionJson.get("condition"))) {
            throw new ConditionMismatchException(expected, (String) conditionJson.get("condition"));
        }
    }

    public abstract boolean evaluate(RPGLObject source, RPGLObject target, JsonObject conditionJson) throws ConditionMismatchException;

}
