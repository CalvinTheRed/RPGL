package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

/**
 * This Condition evaluates true if all of its nested Conditions evaluate to true.
 *
 * @author Calvin Withun
 */
public class All extends Condition {

    public All() {
        super("all");
    }

    @Override
    public boolean run(RPGLEffect effect, Subevent subevent, JsonObject conditionJson, RPGLContext context, JsonArray originPoint) throws Exception {
        JsonArray nestedConditionList = conditionJson.getJsonArray("conditions");
        for (int i = 0; i < nestedConditionList.size(); i++) {
            JsonObject nestedConditionJson = nestedConditionList.getJsonObject(i);
            Condition nestedCondition = Condition.CONDITIONS.get(nestedConditionJson.getString("condition"));
            // once a single nested condition returns false, iteration can short-circuit
            if (!nestedCondition.evaluate(effect, subevent, nestedConditionJson, context, originPoint)) {
                return false;
            }
        }
        return true;
    }

}
