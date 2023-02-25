package org.rpgl.condition;

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
    public boolean evaluate(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent, JsonObject conditionJson) throws Exception {
        super.verifyCondition(super.conditionId, conditionJson);
        return this.getObject(effectSource, effectTarget, subevent, conditionJson.getJsonObject("object_1")).equals(
                this.getObject(effectSource, effectTarget, subevent, conditionJson.getJsonObject("object_2"))
        );
    }

    RPGLObject getObject(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent, JsonObject instructions) throws Exception {
        String from = instructions.getString("from");
        String object = instructions.getString("object");
        if ("subevent".equals(from)) {
            if ("source".equals(object)) {
                return subevent.getSource();
            } else if ("target".equals(object)) {
                return subevent.getTarget();
            }
        } else if ("effect".equals(from)) {
            if ("source".equals(object)) {
                return effectSource;
            } else if ("target".equals(object)) {
                return effectTarget;
            }
        }

        Exception e = new Exception("could not isolate an RPGLObject to reference for comparison: " + instructions);
        LOGGER.error(e.getMessage());
        throw e;
    }

}
