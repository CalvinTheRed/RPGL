package org.rpgl.condition;

import org.rpgl.core.JsonObject;
import org.rpgl.core.RPGLObject;
import org.rpgl.exception.ConditionMismatchException;

import java.util.Map;

/**
 * This Condition always evaluates true.
 *
 * @author Calvin Withun
 */
public class True extends Condition {

    @Override
    public boolean evaluate(RPGLObject source, RPGLObject target, Map<String, Object> data) throws ConditionMismatchException {
        super.verifyCondition("true", data);
        return true;
    }

}
