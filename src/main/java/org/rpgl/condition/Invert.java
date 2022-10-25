package org.rpgl.condition;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLObject;
import org.rpgl.exception.ConditionMismatchException;

public class Invert extends Condition {

    @Override
    public boolean evaluate(RPGLObject source, RPGLObject target, JsonObject conditionJson) throws ConditionMismatchException {
        super.verifyCondition("invert", conditionJson);
        JsonObject nestedConditionJson = (JsonObject) conditionJson.get("invert");
        Condition nestedCondition = Condition.CONDITIONS.get((String) nestedConditionJson.get("condition"));
        return !nestedCondition.evaluate(source, target, nestedConditionJson);
    }
}
