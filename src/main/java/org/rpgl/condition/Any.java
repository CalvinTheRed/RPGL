package org.rpgl.condition;

import org.rpgl.core.RPGLObject;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

/**
 * This Condition evaluates true if one or more of its nested Conditions evaluate to true.
 *
 * @author Calvin Withun
 */
public class Any extends Condition {

    @Override
    public boolean evaluate(RPGLObject source, RPGLObject target, JsonObject conditionJson) throws ConditionMismatchException {
        super.verifyCondition("any", conditionJson);
        JsonArray nestedConditionArray = conditionJson.getJsonArray("conditions");
        if (nestedConditionArray.size() == 0) {
            return true;
        }
        for (int i = 0; i < nestedConditionArray.size(); i++) {
            JsonObject nestedConditionJson = nestedConditionArray.getJsonObject(i);
            Condition nestedCondition = Condition.CONDITIONS.get(nestedConditionJson.getString("condition"));
            // once a single element returns true, iteration can short-circuit
            if (nestedCondition.evaluate(source, target, nestedConditionJson)) {
                return true;
            }
        }
        return false;
    }

}
