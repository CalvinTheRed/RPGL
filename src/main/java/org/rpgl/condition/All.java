package org.rpgl.condition;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonObject;
import org.rpgl.exception.ConditionMismatchException;

public class All extends Condition {

    private static final String CONDITION_ID = "all";

    static {
        Condition.CONDITIONS.put(CONDITION_ID, new All());
    }

    @Override
    public boolean evaluate(long sourceUuid, long targetUuid, JsonObject data) throws ConditionMismatchException {
        super.verifyCondition(CONDITION_ID, data);
        JsonArray nestedConditionsArray = (JsonArray) data.get("conditions");
        for (Object element : nestedConditionsArray) {
            if (element instanceof JsonObject conditionData) {
                Condition condition = Condition.CONDITIONS.get((String) conditionData.get("condition"));
                // once a single element returns false, iteration can stop
                if (!condition.evaluate(sourceUuid, targetUuid, conditionData)) {
                    return false;
                }
            }
        }
        return true;
    }

}
