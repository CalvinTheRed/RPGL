package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

public class ObjectHealthDataComparison extends Condition {

    public ObjectHealthDataComparison() {
        super("object_health_data_comparison");
    }

    @Override
    public boolean evaluate(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent, JsonObject conditionJson, RPGLContext context) throws Exception {
        super.verifyCondition(this.conditionId, conditionJson);
        RPGLObject object = super.getObject(effectSource, effectTarget, subevent, conditionJson.getJsonObject("object"));
        return super.compare(
                object.getHealthData().getInteger(conditionJson.getString("data")),
                conditionJson.getInteger("compare_to"),
                conditionJson.getString("comparison")
        );
    }

}
