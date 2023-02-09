package org.rpgl.condition;

import org.rpgl.core.RPGLObject;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

/**
 * This Condition evaluates true if its nested Condition evaluates false.
 *
 * @author Calvin Withun
 */
public class Invert extends Condition {

    @Override
    public boolean evaluate(RPGLObject source, RPGLObject target, Subevent subevent, JsonObject conditionJson) throws ConditionMismatchException {
        super.verifyCondition("invert", conditionJson);
        JsonObject nestedConditionJson = conditionJson.getJsonObject("invert");
        Condition nestedCondition = Condition.CONDITIONS.get(nestedConditionJson.getString("condition"));
        return !nestedCondition.evaluate(source, target, subevent, nestedConditionJson);
    }
}
