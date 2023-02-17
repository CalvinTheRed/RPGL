package org.rpgl.condition;

import org.rpgl.core.RPGLObject;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Condition evaluates true if its nested Condition evaluates false.
 *
 * @author Calvin Withun
 */
public class Invert extends Condition {

    private static final Logger LOGGER = LoggerFactory.getLogger(Invert.class);

    @Override
    public boolean evaluate(RPGLObject source, RPGLObject target, Subevent subevent, JsonObject conditionJson) throws ConditionMismatchException {
        super.verifyCondition("invert", conditionJson);
        JsonObject nestedConditionJson = conditionJson.getJsonObject("invert");
        Condition nestedCondition = Condition.CONDITIONS.get(nestedConditionJson.getString("condition"));
        boolean result = !nestedCondition.evaluate(source, target, subevent, nestedConditionJson);
        LOGGER.debug("" + result);
        return result;
    }
}
