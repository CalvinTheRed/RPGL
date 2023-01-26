package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLContext;

/**
 * This Subevent is a dummy Subevent only meant to be used during testing. It has no practical application.
 * <br>
 * <br>
 * Source: any
 * <br>
 * Target: any
 *
 * @author Calvin Withun
 */
public class DummySubevent extends Subevent {

    public static int counter = 0;

    public DummySubevent() {
        super("dummy_subevent");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new DummySubevent();
        clone.joinSubeventJson(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new DummySubevent();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void invoke(RPGLContext context) throws Exception {
        super.invoke(context);
        DummySubevent.counter++;
    }

    public static void resetCounter() {
        DummySubevent.counter = 0;
    }

}
