package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

import java.util.List;

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
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context,
                    List<RPGLResource> resources) {
        effect.getTarget().removeEffect(effect.getUuid());
    }

}
