package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.SpawnObject;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Function is dedicated to adding novel tags to a new object created by a SpawnObject subevent.
 *
 * @author Calvin Withun
 */
public class AddSpawnObjectTags extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddSpawnObjectTags.class);

    public AddSpawnObjectTags() {
        super("add_spawn_object_tags");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context, JsonArray originPoint) {
        if (subevent instanceof SpawnObject spawnObject) {
            JsonArray tags = functionJson.getJsonArray("tags");
            for (int i = 0; i < tags.size(); i++) {
                spawnObject.addSpawnObjectTag(tags.getString(i));
            }
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }
}
