package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

/**
 * This Function is dedicated to ending the RPGLEffect containing it.
 *
 * @author Calvin Withun
 */
public class EndEffect extends Function {

    public EndEffect() {
        super("end_effect");
    }

    @Override
    public void execute(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context) throws Exception {
        super.verifyFunction(functionJson);
        effect.getTarget().removeEffect(effect.getUuid());
    }

}