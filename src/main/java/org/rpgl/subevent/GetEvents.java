package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEvent;
import org.rpgl.core.RPGLFactory;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

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

    private List<RPGLEvent> events = new ArrayList<>();

    @Override
    public GetEvents clone() {
        GetEvents clone = new GetEvents();
        clone.joinSubeventData(this.json);
        clone.events = new ArrayList<>(this.events);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public GetEvents clone(JsonObject jsonData) {
        GetEvents clone = new GetEvents();
        clone.joinSubeventData(jsonData);
        clone.events = new ArrayList<>(this.events);
        clone.appliedEffects.addAll(this.appliedEffects);
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
     * @param originItem a UUID for a RPGLItem if the event was provided by an item, or null otherwise
     * @param sourceUuid a UUID for the source of the event if it should differ from the object which invokes it, or
     *                   null otherwise
     */
    public void addEvent(String eventId, String originItem, String sourceUuid) {
        this.json.getJsonArray("events").addJsonObject(RPGLFactory
                .newEvent(eventId).setOriginItem(originItem).setSource(sourceUuid));
    }

    /**
     * Returns the list of RRPGLEvent datapack IDs gathered for target.
     *
     * @return a JsonArray of RPGLEvent datapack IDs
     */
    public List<RPGLEvent> getEvents() {
        JsonArray eventsRaw = this.json.getJsonArray("events");
        List<RPGLEvent> events = new ArrayList<>();
        for (int i = 0; i < eventsRaw.size(); i++) {
            RPGLEvent event = new RPGLEvent();
            event.join(eventsRaw.getJsonObject(i));
            events.add(event);
        }
        return events;
    }

}
