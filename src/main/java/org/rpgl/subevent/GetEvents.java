package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

/**
 * This subevent is dedicated to gathering a collection of additional RPGLEvent datapack IDs to which the subevent's
 * target is meant to have access. This allows for RPGLEffects to grant situational access to an RPGLEvent which target
 * might not otherwise have access to.
 * <br>
 * <br>
 * Source: an RPGLObject whose events are being listed
 * <br>
 * Target: should be the same as the source
 *
 * @author Calvin Withun
 */
public class GetEvents extends Subevent {

    public GetEvents() {
        super("get_events");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new GetEvents();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new GetEvents();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        this.json.putJsonArray("events", new JsonArray());
    }

    /**
     * Adds an RPGLEvent datapack ID to the subevent, to be later granted to target.
     *
     * @param eventId an RPGLEvent datapack ID
     */
    public void addEvent(String eventId) {
        this.getEvents().addString(eventId);
    }

    /**
     * Returns the list of RRPGLEvent datapack IDs gathered for target.
     *
     * @return a JsonArray of RPGLEvent datapack IDs
     */
    public JsonArray getEvents() {
        return this.json.getJsonArray("events");
    }

}
