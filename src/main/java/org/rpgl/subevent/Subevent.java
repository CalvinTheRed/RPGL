package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLObject;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.uuidtable.UUIDTable;

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
        // AbilityCheck
        // AttackRoll
        // Damage
        Subevent.SUBEVENTS.put("dummy_subevent", new DummySubevent());
        // GiveEffect
        Subevent.SUBEVENTS.put("saving_throw", new SavingThrow());
        // TakeEffect
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
     * @throws Exception when an exception occurs.
     */
    public void prepare(RPGLContext context) throws Exception {
        // This method has no behavior by default. It is left empty
        // here for ease of developing derived classes elsewhere.
    }

    public void invoke(RPGLContext context) throws Exception {
        this.verifySubevent(this.subeventId);
        context.processSubevent(this);
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

    public void setSource(RPGLObject source) {
        if (source == null) {
            this.subeventJson.put("source", null);
        } else {
            this.subeventJson.put("source", source.get("uuid"));
        }
    }

    public void setTarget(RPGLObject target) {
        if (target == null) {
            this.subeventJson.put("target", null);
        } else {
            this.subeventJson.put("target", target.get("uuid"));
        }
    }

    public RPGLObject getSource() {
        return UUIDTable.getObject((String) this.subeventJson.get("source"));
    }

    public RPGLObject getTarget() {
        return UUIDTable.getObject((String) this.subeventJson.get("target"));
    }

}
