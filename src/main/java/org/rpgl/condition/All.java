package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Condition evaluates true if all of its nested Conditions evaluate to true.
 *
 * @author Calvin Withun
 */
public class All extends Condition {

    private static final Logger LOGGER = LoggerFactory.getLogger(All.class);

    public All() {
        super("all");
    }

    @Override
    public boolean evaluate(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent, JsonObject conditionJson, RPGLContext context) throws Exception {
        this.verifyCondition(super.conditionId, conditionJson);
        JsonArray nestedConditionList = conditionJson.getJsonArray("conditions");
        for (int i = 0; i < nestedConditionList.size(); i++) {
            JsonObject nestedConditionJson = nestedConditionList.getJsonObject(i);
            Condition nestedCondition = Condition.CONDITIONS.get(nestedConditionJson.getString("condition"));
            // once a single nested condition returns false, iteration can short-circuit
            if (!nestedCondition.evaluate(effectSource, effectTarget, subevent, nestedConditionJson, context)) {
                LOGGER.debug("false");
                return false;
            }
        }
        LOGGER.debug("true");
        return true;
    }

}
