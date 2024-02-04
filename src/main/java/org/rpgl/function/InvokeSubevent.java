package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

/**
 * This Function is dedicated to invoking a particular Subevent. This Function allows for the fine control of the
 * Subevent's source and targets.
 *
 * @author Calvin Withun
 */
public class InvokeSubevent extends Function {

    public InvokeSubevent() {
        super("invoke_subevent");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context, JsonArray originPoint) throws Exception {
        RPGLObject source = RPGLEffect.getObject(effect, subevent, functionJson.getJsonObject("source"));
        JsonArray targets = functionJson.getJsonArray("targets");
        JsonObject nestedSubeventJson = functionJson.getJsonObject("subevent");
        Subevent nestedSubevent = Subevent.SUBEVENTS
                .get(nestedSubeventJson.getString("subevent"))
                .clone(nestedSubeventJson);
        nestedSubevent.setOriginItem(subevent.getOriginItem());
        nestedSubevent.setSource(source);
        nestedSubevent.prepare(context, originPoint);
        for (int i = 0; i < targets.size(); i++) {
            Subevent subeventClone = nestedSubevent.clone();
            subeventClone.setTarget(RPGLEffect.getObject(effect, subevent, targets.getJsonObject(i)));
            subeventClone.invoke(context, originPoint);
        }
    }

}
