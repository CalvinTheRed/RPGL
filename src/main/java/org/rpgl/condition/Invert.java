package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

/**
 * This Condition evaluates true if its nested Condition evaluates false.
 *
 * @author Calvin Withun
 */
public class Invert extends Condition {

    public Invert() {
        super("invert");
    }

    @Override
    public boolean run(RPGLEffect effect, Subevent subevent, JsonObject conditionJson, RPGLContext context) throws Exception {
        JsonObject nestedConditionJson = conditionJson.getJsonObject("invert");
        return !Condition.CONDITIONS
                .get(nestedConditionJson.getString("condition"))
                .evaluate(effect, subevent, nestedConditionJson, context);
    }
}
