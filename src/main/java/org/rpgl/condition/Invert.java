package org.rpgl.condition;

import org.rpgl.core.JsonObject;
import org.rpgl.core.RPGLObject;
import org.rpgl.exception.ConditionMismatchException;

import java.util.Map;

/**
 * This Condition evaluates true if its nested Condition evaluates false.
 *
 * @author Calvin Withun
 */
public class Invert extends Condition {

    @Override
    public boolean evaluate(RPGLObject source, RPGLObject target, Map<String, Object> conditionJson) throws ConditionMismatchException {
        super.verifyCondition("invert", conditionJson);
        JsonObject nestedConditionJson = (JsonObject) conditionJson.get("invert");
        Condition nestedCondition = Condition.CONDITIONS.get((String) nestedConditionJson.get("condition"));
        return !nestedCondition.evaluate(source, target, nestedConditionJson);
    }
}
