package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

/**
 * This Condition is dedicated to comparing an RPGLObject's ability score against a particular value.
 *
 * @author Calvin Withun
 */
public class ObjectAbilityScoreComparison extends Condition {

    public ObjectAbilityScoreComparison() {
        super("object_ability_score_comparison");
    }

    @Override
    public boolean run(RPGLEffect effect, Subevent subevent, JsonObject conditionJson, RPGLContext context) throws Exception {
        RPGLObject object = RPGLEffect.getObject(effect, subevent, conditionJson.getJsonObject("object"));
        return super.compareValues(
                object.getAbilityScoreFromAbilityName(conditionJson.getString("ability"), context),
                conditionJson.getInteger("compare_to"),
                conditionJson.getString("comparison")
        );
    }

}
