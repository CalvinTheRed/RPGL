package org.rpgl.condition;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLObject;
import org.rpgl.exception.ConditionMismatchException;

/**
 * This Condition always evaluates true.
 *
 * @author Calvin Withun
 */
public class True extends Condition {

    @Override
    public boolean evaluate(RPGLObject source, RPGLObject target, JsonObject data) throws ConditionMismatchException {
        super.verifyCondition("true", data);
        return true;
    }

}
