package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
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

    public False() {
        super("false");
    }

    @Override
    public boolean evaluate(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent, JsonObject conditionJson, RPGLContext context) throws Exception {
        this.verifyCondition(super.conditionId, conditionJson);
        LOGGER.debug("false");
        return false;
    }

}
