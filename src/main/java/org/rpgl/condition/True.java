package org.rpgl.condition;

import org.rpgl.core.RPGLObject;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

/**
 * This Condition always evaluates true.
 *
 * @author Calvin Withun
 */
public class True extends Condition {

    @Override
    public boolean evaluate(RPGLObject source, RPGLObject target, Subevent subevent, JsonObject conditionJson) throws ConditionMismatchException {
        super.verifyCondition("true", conditionJson);
        return true;
    }

}
