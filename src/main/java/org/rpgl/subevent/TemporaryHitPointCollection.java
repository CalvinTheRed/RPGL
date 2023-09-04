package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLResource;
import org.rpgl.function.AddTemporaryHitPoints;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.List;

/**
 * This Subevent is dedicated to collecting unrolled temporary hit point dice and bonuses.
 * <br>
 * <br>
 * Source: an RPGLObject preparing to give temporary hit points
 * <br>
 * Target: an RPGLObject which will later receive the collected temporary hit points
 *
 * @author Calvin Withun
 */
public class TemporaryHitPointCollection extends Subevent {

    public TemporaryHitPointCollection() {
        super("temporary_hit_point_collection");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new TemporaryHitPointCollection();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new TemporaryHitPointCollection();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context, List<RPGLResource> resources) throws Exception {
        super.prepare(context, resources);
        this.prepareTemporaryHitPoints(context);
    }

    /**
     * This helper method interprets the temporary hit point formulas provided in the Subevent JSON, and then stores
     * those values in the Subevent.
     *
     * @param context the context in which the Subevent is being prepared
     *
     * @throws Exception if an exception occurs
     */
    void prepareTemporaryHitPoints(RPGLContext context) throws Exception {
        JsonArray temporaryHitPointsArray = this.json.removeJsonArray("temporary_hit_points");
        this.json.putJsonArray("temporary_hit_points", new JsonArray());
        if (temporaryHitPointsArray != null) {
            RPGLEffect effect = new RPGLEffect();
            effect.setSource(this.getSource());
            effect.setTarget(this.getSource());
            for (int i = 0; i < temporaryHitPointsArray.size(); i++) {
                JsonObject temporaryHitPointsJson = temporaryHitPointsArray.getJsonObject(i);
                this.addTemporaryHitPoints(AddTemporaryHitPoints.processJson(effect, this, temporaryHitPointsJson, context));
            }
        }
    }

    /**
     * Adds dice and/or a bonus to the healing collected by this Subevent.
     *
     * @param temporaryHitPointsJson healing data to be added to the collection
     */
    public void addTemporaryHitPoints(JsonObject temporaryHitPointsJson) {
        this.getTemporaryHitPointsCollection().addJsonObject(temporaryHitPointsJson);
    }

    /**
     * Returns the collection of healing gathered by this Subevent.
     *
     * @return a JsonArray storing healing dice and a healing bonus
     */
    public JsonArray getTemporaryHitPointsCollection() {
        return this.json.getJsonArray("temporary_hit_points");
    }

}
