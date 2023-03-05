package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

public class InvokeEvent extends Function {

    public InvokeEvent() {
        super("invoke_event");
    }

    @Override
    public void execute(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent,
                        JsonObject functionJson, RPGLContext context) throws Exception {
        super.verifyFunction(functionJson);
        effectTarget.invokeEvent(
                new RPGLObject[] {}, // TODO this needs to be supplied somehow
                RPGLFactory.newEvent(functionJson.getString("event")),
                context
        );
    }

}
