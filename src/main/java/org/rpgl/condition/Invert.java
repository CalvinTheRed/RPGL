package org.rpgl.condition;

import org.rpgl.core.RPGLObject;
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

    public Invert() {
        super("invert");
    }

    @Override
    public boolean evaluate(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent, JsonObject conditionJson) throws Exception {
        this.verifyCondition(super.conditionId, conditionJson);
        JsonObject nestedConditionJson = conditionJson.getJsonObject("invert");
        Condition nestedCondition = Condition.CONDITIONS.get(nestedConditionJson.getString("condition"));
        boolean result = !nestedCondition.evaluate(effectSource, effectTarget, subevent, nestedConditionJson);
        LOGGER.debug(Boolean.toString(result));
        return result;
    }
}
