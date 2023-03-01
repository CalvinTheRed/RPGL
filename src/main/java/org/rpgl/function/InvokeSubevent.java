package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

public class InvokeSubevent extends Function {

    public InvokeSubevent() {
        super("invoke_subevent");
    }

    @Override
    public void execute(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent,
                        JsonObject functionJson, RPGLContext context) throws Exception {
        super.verifyFunction(functionJson);
        JsonObject nestedSubeventJson = functionJson.getJsonObject("subevent");
        Subevent nestedSubevent = Subevent.SUBEVENTS.get(nestedSubeventJson.getString("subevent")).clone(nestedSubeventJson);
        nestedSubevent.setSource(effectSource);
        nestedSubevent.prepare(context);
        nestedSubevent.setTarget(RPGLEffect.getObject(effectSource, effectTarget, subevent, functionJson.getJsonObject("target")));
        nestedSubevent.invoke(context);
    }

}
