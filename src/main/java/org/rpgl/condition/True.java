package org.rpgl.condition;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLObject;
import org.rpgl.exception.ConditionMismatchException;

public class True extends Condition {

    @Override
    public boolean evaluate(RPGLObject source, RPGLObject target, JsonObject data) throws ConditionMismatchException {
        super.verifyCondition("true", data);
        return true;
    }

}
