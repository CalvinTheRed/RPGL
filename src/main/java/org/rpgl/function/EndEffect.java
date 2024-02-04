package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

/**
 * This Function is dedicated to ending the RPGLEffect containing it.
 *
 * @author Calvin Withun
 */
public class EndEffect extends Function {

    // TODO should this exist now that RemoveEffect is a Subevent?

    public EndEffect() {
        super("end_effect");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context, JsonArray originPoint) {
        effect.getTarget().removeEffect(effect.getUuid());
    }

}
