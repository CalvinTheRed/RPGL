package org.rpgl.condition;

import org.rpgl.core.JsonObject;
import org.rpgl.core.RPGLObject;
import org.rpgl.exception.ConditionMismatchException;

import java.util.List;
import java.util.Map;

/**
 * This Condition evaluates true if all of its nested Conditions evaluate to true.
 *
 * @author Calvin Withun
 */
public class All extends Condition {

    @Override
    public boolean evaluate(RPGLObject source, RPGLObject target, Map<String, Object> conditionJson) throws ConditionMismatchException {
        super.verifyCondition("all", conditionJson);
        List<Object> nestedConditionList = (List<Object>) conditionJson.get("conditions");
        for (Object nestedConditionJsonElement : nestedConditionList) {
            if (nestedConditionJsonElement instanceof Map nestedConditionJson) {
                Condition nestedCondition = Condition.CONDITIONS.get((String) nestedConditionJson.get("condition"));
                // once a single nested condition returns false, iteration can short-circuit
                if (!nestedCondition.evaluate(source, target, nestedConditionJson)) {
                    return false;
                }
            }
        }
        return true;
    }

}
