package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

import java.util.Objects;

/**
 * This Condition evaluates true if the specified objects share a common user id.
 *
 * @author Calvin Withun
 */
public class UserIdsMatch extends Condition {

    public UserIdsMatch() {
        super("user_ids_match");
    }

    @Override
    public boolean run(RPGLEffect effect, Subevent subevent, JsonObject conditionJson, RPGLContext context) throws Exception {
        RPGLObject effectObject = RPGLEffect.getObject(effect, subevent, new JsonObject() {{
            this.putString("from", "effect");
            this.putString("object", conditionJson.getString("effect"));
        }});
        RPGLObject subeventObject = RPGLEffect.getObject(effect, subevent, new JsonObject() {{
            this.putString("from", "subevent");
            this.putString("object", conditionJson.getString("subevent"));
        }});
        return Objects.equals(effectObject.getUserId(), subeventObject.getUserId());
    }

}
