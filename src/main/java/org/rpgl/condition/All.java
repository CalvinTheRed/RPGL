package org.rpgl.condition;

import org.rpgl.core.RPGLObject;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

/**
 * This Condition evaluates true if all of its nested Conditions evaluate to true.
 *
 * @author Calvin Withun
 */
public class All extends Condition {

    @Override
    public boolean evaluate(RPGLObject source, RPGLObject target, Subevent subevent, JsonObject conditionJson) throws ConditionMismatchException {
        super.verifyCondition("all", conditionJson);
        JsonArray nestedConditionList = conditionJson.getJsonArray("conditions");
        for (int i = 0; i < nestedConditionList.size(); i++) {
            JsonObject nestedConditionJson = nestedConditionList.getJsonObject(i);
            Condition nestedCondition = Condition.CONDITIONS.get(nestedConditionJson.getString("condition"));
            // once a single nested condition returns false, iteration can short-circuit
            if (!nestedCondition.evaluate(source, target, subevent, nestedConditionJson)) {
                return false;
            }
        }
        return true;
    }

}
