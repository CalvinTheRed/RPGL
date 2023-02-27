package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Condition evaluates true if one or more of its nested Conditions evaluate to true.
 *
 * @author Calvin Withun
 */
public class Any extends Condition {

    private static final Logger LOGGER = LoggerFactory.getLogger(Any.class);

    public Any() {
        super("any");
    }

    @Override
    public boolean evaluate(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent, JsonObject conditionJson, RPGLContext context) throws Exception {
        this.verifyCondition(super.conditionId, conditionJson);
        JsonArray nestedConditionArray = conditionJson.getJsonArray("conditions");
        if (nestedConditionArray.size() == 0) {
            return true;
        }
        for (int i = 0; i < nestedConditionArray.size(); i++) {
            JsonObject nestedConditionJson = nestedConditionArray.getJsonObject(i);
            Condition nestedCondition = Condition.CONDITIONS.get(nestedConditionJson.getString("condition"));
            // once a single element returns true, iteration can short-circuit
            if (nestedCondition.evaluate(effectSource, effectTarget, subevent, nestedConditionJson, context)) {
                LOGGER.debug("true");
                return true;
            }
        }
        LOGGER.debug("false");
        return false;
    }

}
