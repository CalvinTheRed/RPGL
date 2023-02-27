package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

public class ObjectsMatch extends Condition {

    public ObjectsMatch() {
        super("objects_match");
    }

    @Override
    public boolean evaluate(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent, JsonObject conditionJson, RPGLContext context) throws Exception {
        super.verifyCondition(super.conditionId, conditionJson);
        return this.getObject(effectSource, effectTarget, subevent, conditionJson.getJsonObject("object_1")).equals(
                this.getObject(effectSource, effectTarget, subevent, conditionJson.getJsonObject("object_2"))
        );
    }

}
