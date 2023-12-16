package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.SpawnObject;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This Function is dedicated to adding novel events to a new object created by a SpawnObject subevent.
 *
 * @author Calvin Withun
 */
public class AddSpawnObjectEvents extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddSpawnObjectEvents.class);

    public AddSpawnObjectEvents() {
        super("add_spawn_object_events");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context,
                    List<RPGLResource> resources) {
        if (subevent instanceof SpawnObject spawnObject) {
            JsonArray events = functionJson.getJsonArray("events");
            for (int i = 0; i < events.size(); i++) {
                spawnObject.addSpawnObjectEvent(events.getString(i));
            }
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }
}
