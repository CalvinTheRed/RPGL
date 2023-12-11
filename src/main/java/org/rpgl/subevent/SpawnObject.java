package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.List;
import java.util.Objects;

/**
 * This Subevent is dedicated to spawning a new RPGLObject into the game.
 * <br>
 * <br>
 * Source: an RPGLObject causing a new RPGLObject to spawn
 * <br>
 * Target: any
 *
 * @author Calvin Withun
 */
public class SpawnObject extends Subevent {

    public SpawnObject() {
        super("spawn_object");
        this.addTag("spawn_object");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new SpawnObject();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new SpawnObject();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void run(RPGLContext context, List<RPGLResource> resources) throws Exception {
        RPGLObject spawnedObject = RPGLFactory.newObject(
                this.json.getString("object_id"),
                RPGLEffect.getObject(null, this, new JsonObject() {{
                    this.putString("from", "subevent");
                    this.putString("object", Objects.requireNonNullElse(json.getString("controlled_by"), "source"));
                }}).getUserId(),
                Objects.requireNonNullElse(this.json.getJsonArray("object_bonuses"), new JsonArray())
        );

        JsonArray extraTags = Objects.requireNonNullElse(this.json.getJsonArray("extra_tags"), new JsonArray());
        for (int i = 0; i < extraTags.size(); i++) {
            spawnedObject.addTag(extraTags.getString(i));
        }

        if (Objects.requireNonNullElse(this.json.getBoolean("extend_proficiency_bonus"), false)) {
            spawnedObject.setProficiencyBonus(this.getSource().getEffectiveProficiencyBonus(context));
        }

        context.add(spawnedObject);
    }
}
