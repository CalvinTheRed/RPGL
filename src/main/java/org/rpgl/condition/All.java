package org.rpgl.condition;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonObject;
import org.rpgl.exception.ConditionMismatchException;

public class All extends Condition {

    @Override
    public boolean evaluate(long sourceUuid, long targetUuid, JsonObject conditionJson) throws ConditionMismatchException {
        super.verifyCondition("all", conditionJson);
        JsonArray nestedConditionJsonArray = (JsonArray) conditionJson.get("conditions");
        for (Object nestedConditionJsonElement : nestedConditionJsonArray) {
            if (nestedConditionJsonElement instanceof JsonObject nestedConditionJson) {
                Condition nestedCondition = Condition.CONDITIONS.get((String) nestedConditionJson.get("condition"));
                // once a single nested condition returns false, iteration can stop
                if (!nestedCondition.evaluate(sourceUuid, targetUuid, nestedConditionJson)) {
                    return false;
                }
            }
        }
        return true;
    }

}
