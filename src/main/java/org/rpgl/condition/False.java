package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

/**
 * This Condition always evaluates false.
 *
 * @author Calvin Withun
 */
public class False extends Condition {

    public False() {
        super("false");
    }

    @Override
    public boolean evaluate(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent,
                            JsonObject conditionJson, RPGLContext context) throws Exception {
        this.verifyCondition(conditionJson);
        return false;
    }

}
