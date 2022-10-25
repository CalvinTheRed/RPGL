package org.rpgl.condition;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLObject;
import org.rpgl.exception.ConditionMismatchException;

public class Any extends Condition {

    @Override
    public boolean evaluate(RPGLObject source, RPGLObject target, JsonObject conditionJson) throws ConditionMismatchException {
        super.verifyCondition("any", conditionJson);
        JsonArray nestedConditionJsonArray = (JsonArray) conditionJson.get("conditions");
        if (nestedConditionJsonArray.size() == 0) {
            return true;
        }
        for (Object nestedConditionJsonElement : nestedConditionJsonArray) {
            if (nestedConditionJsonElement instanceof JsonObject nestedConditionJson) {
                Condition nestedCondition = Condition.CONDITIONS.get((String) nestedConditionJson.get("condition"));
                // once a single element returns true, iteration can stop
                if (nestedCondition.evaluate(source, target, nestedConditionJson)) {
                    return true;
                }
            }
        }
        return false;
    }

}
