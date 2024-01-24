package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

/**
 * This Condition is dedicated to checking the level of an RPGLObject.
 *
 * @author Calvin Withun
 */
public class CheckLevel extends Condition {

    public CheckLevel() {
        super("check_level");
    }

    @Override
    public boolean run(RPGLEffect effect, Subevent subevent, JsonObject conditionJson, RPGLContext context) throws Exception {
        RPGLObject object = RPGLEffect.getObject(effect, subevent, conditionJson.getJsonObject("object"));
        String classId = conditionJson.getString("class");
        return Condition.compareValues(
                classId == null ? object.getLevel() : object.getLevel(classId),
                conditionJson.getInteger("compare_to"),
                conditionJson.getString("comparison")
        );
    }

}
