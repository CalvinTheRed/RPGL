package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.ArrayList;

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
    }

    @Override
    public Subevent clone() {
        Subevent clone = new SpawnObject();
        clone.joinSubeventData(this.json);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new SpawnObject();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public SpawnObject invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (SpawnObject) super.invoke(context, originPoint);
    }

    @Override
    public SpawnObject joinSubeventData(JsonObject other) {
        return (SpawnObject) super.joinSubeventData(other);
    }

    @Override
    public SpawnObject prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        this.json.asMap().putIfAbsent("controlled_by", "source");
        this.json.asMap().putIfAbsent("object_bonuses", new ArrayList<>());
        this.json.asMap().putIfAbsent("extra_effects", new ArrayList<>());
        this.json.asMap().putIfAbsent("extra_events", new ArrayList<>());
        this.json.asMap().putIfAbsent("extra_tags", new ArrayList<>());
        this.json.asMap().putIfAbsent("extend_proficiency_bonus", false);
        this.json.asMap().putIfAbsent("proxy", false);
        return this;
    }

    @Override
    public SpawnObject run(RPGLContext context, JsonArray originPoint) throws Exception {
        RPGLObject spawnedObject = RPGLFactory.newObject(
                this.json.getString("object_id"),
                RPGLEffect.getObject(null, this, new JsonObject() {{
                    this.putString("from", "subevent");
                    this.putString("object", json.getString("controlled_by"));
                }}).getUserId(),
                originPoint.deepClone(),
                this.getSource().getRotation().deepClone(),
                this.json.getJsonArray("object_bonuses")
        )
                .setOriginObject(super.getSource().getUuid())
                .setProxy(this.json.getBoolean("proxy"));

        JsonArray extraEffects = this.json.getJsonArray("extra_effects");
        for (int i = 0; i < extraEffects.size(); i++) {
            spawnedObject.addEffect(RPGLFactory.newEffect(extraEffects.getString(i))
                    .setOriginItem(super.getOriginItem())
                    .setSource(super.getSource())
                    .setTarget(spawnedObject)
            );
        }

        spawnedObject.getEvents().asList().addAll(this.json.getJsonArray("extra_events").asList());

        JsonArray extraTags = this.json.getJsonArray("extra_tags");
        for (int i = 0; i < extraTags.size(); i++) {
            spawnedObject.addTag(extraTags.getString(i));
        }

        if (this.json.getBoolean("extend_proficiency_bonus")) {
            spawnedObject.setProficiencyBonus(super.getSource().getEffectiveProficiencyBonus(context, originPoint));
        }

        context.add(spawnedObject);

        return this;
    }

    @Override
    public SpawnObject setOriginItem(String originItem) {
        return (SpawnObject) super.setOriginItem(originItem);
    }

    @Override
    public SpawnObject setSource(RPGLObject source) {
        return (SpawnObject) super.setSource(source);
    }

    @Override
    public SpawnObject setTarget(RPGLObject target) {
        return (SpawnObject) super.setTarget(target);
    }

    /**
     * This method adds a custom bonus to the object spawned by this subevent.
     *
     * @param bonus an array of bonuses to be applied to the object
     */
    public void addSpawnObjectBonus(JsonObject bonus) {
        this.json.getJsonArray("object_bonuses").addJsonObject(bonus);
    }

    /**
     * This method adds a custom effect to the object spawned by this subevent.
     *
     * @param effectId the datapack ID of an effect to be added to the spawned object
     */
    public void addSpawnObjectEffect(String effectId) {
        this.json.getJsonArray("extra_effects").addString(effectId);
    }

    /**
     * This method adds a custom event to the object spawned by this subevent.
     *
     * @param eventId the datapack ID of an event to be added to the spawned object
     */
    public void addSpawnObjectEvent(String eventId) {
        this.json.getJsonArray("extra_events").addString(eventId);
    }

    /**
     * This method adds a custom tag to the object spawned by this subevent.
     *
     * @param tag a tag to be added to the spawned object
     */
    public void addSpawnObjectTag(String tag) {
        this.json.getJsonArray("extra_tags").addString(tag);
    }
}
