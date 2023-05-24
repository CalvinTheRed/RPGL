package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

/**
 * This Condition is dedicated to determining whether it is an RPGLObject's turn.
 *
 * @author Calvin Withun
 */
public class IsObjectsTurn extends Condition {

    public IsObjectsTurn() {
        super("is_objects_turn");
    }

    @Override
    public boolean run(RPGLEffect effect, Subevent subevent, JsonObject conditionJson, RPGLContext context) throws Exception {
        return context.isObjectsTurn(RPGLEffect.getObject(effect, subevent, conditionJson.getJsonObject("object")));
    }

}
