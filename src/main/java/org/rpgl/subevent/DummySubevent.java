package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

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

    private String ability = "";

    public DummySubevent() {
        super("dummy_subevent");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new DummySubevent();
        clone.joinSubeventData(this.json);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new DummySubevent();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public DummySubevent invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (DummySubevent) super.invoke(context, originPoint);
    }

    @Override
    public DummySubevent joinSubeventData(JsonObject other) {
        return (DummySubevent) super.joinSubeventData(other);
    }

    @Override
    public DummySubevent prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        return (DummySubevent) super.prepare(context, originPoint);
    }

    @Override
    public DummySubevent run(RPGLContext context, JsonArray originPoint) {
        DummySubevent.counter++;
        return this;
    }

    @Override
    public DummySubevent setOriginItem(String originItem) {
        return (DummySubevent) super.setOriginItem(originItem);
    }

    @Override
    public DummySubevent setSource(RPGLObject source) {
        return (DummySubevent) super.setSource(source);
    }

    @Override
    public DummySubevent setTarget(RPGLObject target) {
        return (DummySubevent) super.setTarget(target);
    }

    /**
     * Resets this class's counter used during testing.
     */
    public static void resetCounter() {
        DummySubevent.counter = 0;
    }

    @Override
    public String getAbility(RPGLContext context) {
        return ability;
    }

    /**
     * Setter for the Subevent's ability
     *
     * @param ability a String representing an ability score
     * @return the Subevent
     */
    public DummySubevent setAbility(String ability) {
        this.ability = ability;
        return this;
    }
}
