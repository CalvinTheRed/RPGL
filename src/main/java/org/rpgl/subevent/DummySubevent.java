package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLObject;

public class DummySubevent extends Subevent {

    public static int counter = 0;

    public DummySubevent() {
        super("dummy_subevent");
    }

    @Override
    public Subevent clone() {
        return new DummySubevent();
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new DummySubevent();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void invoke(RPGLObject source, RPGLObject target) throws Exception {
        super.invoke(source, target);
        DummySubevent.counter++;
    }

    public static void resetCounter() {
        DummySubevent.counter = 0;
    }

}