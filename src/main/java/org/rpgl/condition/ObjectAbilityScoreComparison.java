package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

public class ObjectAbilityScoreComparison extends Condition {

    public ObjectAbilityScoreComparison() {
        super("object_ability_score_comparison");
    }

    @Override
    public boolean evaluate(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent, JsonObject conditionJson, RPGLContext context) throws Exception {
        super.verifyCondition(super.conditionId, conditionJson);
        RPGLObject object = super.getObject(effectSource, effectTarget, subevent, conditionJson.getJsonObject("object"));
        return super.compare(
                object.getAbilityScoreFromAbilityName(context, conditionJson.getString("ability")),
                conditionJson.getInteger("compare_to"),
                conditionJson.getString("comparison")
        );
    }

}
