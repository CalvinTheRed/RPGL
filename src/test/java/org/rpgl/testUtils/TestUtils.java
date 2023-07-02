package org.rpgl.testUtils;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEvent;
import org.rpgl.core.RPGLObject;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonObject;

import java.util.List;
import java.util.Objects;

/**
 * A class written to assist with running unit tests
 *
 * @author Calvin Withun
 */
public final class TestUtils {

    /**
     * Returns an event by ID from a list (first match)
     *
     * @param events a list of events
     * @param eventId an event ID being searched for
     * @return an RPGLEvent, or null if none match the event ID
     */
    public static RPGLEvent getEventById(List<RPGLEvent> events, String eventId) {
        for (RPGLEvent event : events) {
            if (Objects.equals(event.getId(), eventId)) {
                return event;
            }
        }
        return null;
    }

    /**
     * Returns a resource by ID from a list (first match)
     *
     * @param resources a list of resources
     * @param resourceId a resource ID being searched for
     * @return an RPGLResource, or null if none match the resource ID
     */
    public static RPGLResource getResourceById(List<RPGLResource> resources, String resourceId) {
        for (RPGLResource resource : resources) {
            if (Objects.equals(resource.getId(), resourceId)) {
                return resource;
            }
        }
        return null;
    }

    /**
     * Sets an RPGLObject's current health to its max health as determined by its base, hit dice count, and constitution
     * modifier.
     *
     * @param object an RPGLObject
     * @param context the context in which the object is being restored to full health
     *
     * @throws Exception if en exception occurs
     */
    public static void resetObjectHealth(RPGLObject object, RPGLContext context) throws Exception {
        JsonObject healthData = object.getHealthData();
        int base = healthData.getInteger("base");
        int hitDiceCount = healthData.getJsonArray("hit_dice").size();
        int conModifier = object.getAbilityModifierFromAbilityName("con", context);
        healthData.putInteger("current", base + hitDiceCount * conModifier);
    }

}
