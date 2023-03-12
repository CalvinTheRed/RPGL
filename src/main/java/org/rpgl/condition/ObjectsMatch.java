package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

/**
 * This Condition is dedicated to comparing two RPGLObjects to see if they are the same.
 *
 * @author Calvin Withun
 */
public class ObjectsMatch extends Condition {

    public ObjectsMatch() {
        super("objects_match");
    }

    @Override
    public boolean evaluate(RPGLEffect effect, Subevent subevent, JsonObject conditionJson, RPGLContext context) throws Exception {
        super.verifyCondition(conditionJson);
        RPGLObject effectObject = RPGLEffect.getObject(effect, subevent, new JsonObject() {{
            this.putString("from", "effect");
            this.putString("object", conditionJson.getString("effect"));
        }});
        RPGLObject subeventObject = RPGLEffect.getObject(effect, subevent, new JsonObject() {{
            this.putString("from", "subevent");
            this.putString("object", conditionJson.getString("subevent"));
        }});
        return effectObject.equals(subeventObject);
    }

}
