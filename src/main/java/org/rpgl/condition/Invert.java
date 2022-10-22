package org.rpgl.condition;

import org.jsonutils.JsonObject;
import org.rpgl.exception.ConditionMismatchException;

public class Invert extends Condition {

    private static final String CONDITION_ID = "invert";

    static {
        Condition.CONDITIONS.put(CONDITION_ID, new Invert());
    }

    @Override
    public boolean evaluate(long sourceUuid, long targetUuid, JsonObject data) throws ConditionMismatchException {
        super.verifyCondition(CONDITION_ID, data);
        JsonObject nestedConditionData = (JsonObject) data.get("invert");
        Condition nestedCondition = Condition.CONDITIONS.get((String) nestedConditionData.get("condition"));
        return !nestedCondition.evaluate(sourceUuid, targetUuid, nestedConditionData);
    }
}
