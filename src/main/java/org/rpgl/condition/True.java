package org.rpgl.condition;

import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Condition always evaluates true.
 *
 * @author Calvin Withun
 */
public class True extends Condition {

    private static final Logger LOGGER = LoggerFactory.getLogger(True.class);

    public True() {
        super("true");
    }

    @Override
    public boolean evaluate(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent, JsonObject conditionJson) throws Exception {
        this.verifyCondition(super.conditionId, conditionJson);
        LOGGER.debug("true");
        return true;
    }

}
