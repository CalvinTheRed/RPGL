package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonObject;

import java.util.List;

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
public class DummySubevent extends Subevent implements AbilitySubevent {

    public static int counter = 0;

    public DummySubevent() {
        super("dummy_subevent");
        this.addTag("dummy_subevent");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new DummySubevent();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new DummySubevent();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void run(RPGLContext context, List<RPGLResource> resources) {
        DummySubevent.counter++;
    }

    /**
     * Resets this class's counter used during testing.
     */
    public static void resetCounter() {
        DummySubevent.counter = 0;
    }

    @Override
    public String getAbility(RPGLContext context) {
        return "str";
    }
}
