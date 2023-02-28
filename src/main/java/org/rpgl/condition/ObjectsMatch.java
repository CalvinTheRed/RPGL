package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectsMatch extends Condition {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectsMatch.class);

    public ObjectsMatch() {
        super("objects_match");
    }

    @Override
    public boolean evaluate(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent, JsonObject conditionJson, RPGLContext context) throws Exception {
        super.verifyCondition(conditionJson);
        RPGLObject effectObject, subeventObject;
        String effectObjectAlias = conditionJson.getString("effect");
        String subeventObjectAlias = conditionJson.getString("subevent");
        if ("source".equals(effectObjectAlias)) {
            effectObject = effectSource;
        } else if ("target".equals(effectObjectAlias)) {
            effectObject = effectTarget;
        } else {
            Exception e = new Exception("Illegal value for ObjectMatch.effect: " + effectObjectAlias);
            LOGGER.error(e.getMessage());
            throw e;
        }
        if ("source".equals(subeventObjectAlias)) {
            subeventObject = subevent.getSource();
        } else if ("target".equals(subeventObjectAlias)) {
            subeventObject = subevent.getTarget();
        } else {
            Exception e = new Exception("Illegal value for ObjectMatch.subevent: " + subeventObjectAlias);
            LOGGER.error(e.getMessage());
            throw e;
        }
        return effectObject.equals(subeventObject);
    }

}
