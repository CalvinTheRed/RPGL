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
 * This Function is dedicated to adding a novel bonus to a new object created by a SpawnObject subevent.
 *
 * @author Calvin Withun
 */
public class AddSpawnObjectBonus extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddSpawnObjectBonus.class);

    public AddSpawnObjectBonus() {
        super("add_spawn_object_bonus");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context,
                    List<RPGLResource> resources) {
        if (subevent instanceof SpawnObject spawnObject) {
            JsonArray bonuses = functionJson.getJsonArray("bonus");
            for (int i = 0; i < bonuses.size(); i++) {
                spawnObject.addSpawnObjectBonus(bonuses.getJsonObject(i));
            }
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }
}
