package org.rpgl.condition;

import org.rpgl.core.RPGLObject;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Condition always evaluates false.
 *
 * @author Calvin Withun
 */
public class False extends Condition {

    private static final Logger LOGGER = LoggerFactory.getLogger(False.class);

    @Override
    public boolean evaluate(RPGLObject source, RPGLObject target, Subevent subevent, JsonObject conditionJson) throws ConditionMismatchException {
        super.verifyCondition("false", conditionJson);
        LOGGER.debug("false");
        return false;
    }

}
