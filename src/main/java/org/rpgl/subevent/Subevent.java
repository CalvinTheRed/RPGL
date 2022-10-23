package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLObject;
import org.rpgl.exception.SubeventMismatchException;

import java.util.HashMap;
import java.util.Map;

public abstract class Subevent {

    public static final Map<String, Subevent> SUBEVENTS;

    protected JsonObject subeventJson = new JsonObject();

    private final String subeventId;

    static {
        SUBEVENTS = new HashMap<>();
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
     * This method gives a Subevent the chance to modify itself before it is cloned and sent off to its targets (such as
     * rolling for damage or calculating a spell save DC).
     *
     * @param source the RPGLObject preparing to invoke the Subevent
     */
    public void prepare(RPGLObject source) throws Exception {

    }

    public void invoke(RPGLObject source, RPGLObject target) throws Exception {
        this.verifySubevent(this.subeventId);
        while (source.processSubevent(source, target, this) || target.processSubevent(source, target, this));
    }

}
