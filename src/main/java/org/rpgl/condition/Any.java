package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

/**
 * This Condition evaluates true if one or more of its nested Conditions evaluate to true.
 *
 * @author Calvin Withun
 */
public class Any extends Condition {

    public Any() {
        super("any");
    }

    @Override
    public boolean run(RPGLEffect effect, Subevent subevent, JsonObject conditionJson, RPGLContext context, JsonArray originPoint) throws Exception {
        JsonArray nestedConditionArray = conditionJson.getJsonArray("conditions");
        if (nestedConditionArray.size() == 0) {
            return true;
        }
        for (int i = 0; i < nestedConditionArray.size(); i++) {
            JsonObject nestedConditionJson = nestedConditionArray.getJsonObject(i);
            Condition nestedCondition = Condition.CONDITIONS.get(nestedConditionJson.getString("condition"));
            // once a single element returns true, iteration can short-circuit
            if (nestedCondition.evaluate(effect, subevent, nestedConditionJson, context, originPoint)) {
                return true;
            }
        }
        return false;
    }

}
