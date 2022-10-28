package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLObject;
import org.rpgl.exception.SubeventMismatchException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public abstract class Subevent {

    public static final Map<String, Subevent> SUBEVENTS;

    protected JsonObject subeventJson = new JsonObject();
    protected LinkedList<RPGLEffect> modifyingEffects = new LinkedList<>();

    private final String subeventId;

    static {
        SUBEVENTS = new HashMap<>();
        Subevent.SUBEVENTS.put("attack_roll", new AttackRoll());
        Subevent.SUBEVENTS.put("dummy_subevent", new DummySubevent());
        Subevent.SUBEVENTS.put("saving_throw", new SavingThrow());
    }

    public Subevent(String subeventId) {
        this.subeventId = subeventId;
    }

    public void verifySubevent(String expected) throws SubeventMismatchException {
        if (!expected.equals(this.subeventJson.get("subevent"))) {
            throw new SubeventMismatchException(expected, (String) this.subeventJson.get("subevent"));
        }
    }

    public void joinSubeventJson(JsonObject subeventJson) {
        this.subeventJson.join(subeventJson);
    }

    @Override
    public abstract Subevent clone();

    public abstract Subevent clone(JsonObject subeventJson);

    /**
     * This method gives a Subevent the chance to modify itself before it is cloned and sent off to its targets. This is
     * typically only necessary when a single Subevent has several targets.
     *
     * @param source the RPGLObject preparing to invoke the Subevent
     * @throws Exception when an exception occurs.
     */
    public void prepare(RPGLObject source) throws Exception {
        // This method has no behavior by default. It is left empty
        // here for ease of developing derived classes elsewhere.
    }

    public void invoke(RPGLObject source, RPGLObject target) throws Exception {
        this.verifySubevent(this.subeventId);
        while (source.processSubevent(source, target, this) | target.processSubevent(source, target, this));
    }

    public void addModifyingEffect(RPGLEffect effect) {
        this.modifyingEffects.add(effect);
    }

    public boolean hasModifyingEffect(RPGLEffect effect) {
        String effectId = (String) effect.get("id");
        for (RPGLEffect modifyingEffect : modifyingEffects) {
            if (effectId.equals(modifyingEffect.get("id"))) {
                return true;
            }
        }
        return false;
    }

}
