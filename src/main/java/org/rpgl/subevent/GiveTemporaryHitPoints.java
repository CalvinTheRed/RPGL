package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.ArrayList;

/**
 * This Subevent is dedicated to giving temporary hit points to an RPGLObject. This Subevent also supports granting
 * RPGLEffects to the Subevent's target if the temporary hit points are successfully assigned to the target.
 * <br>
 * <br>
 * Source: an RPGLObject granting temporary hit points
 * <br>
 * Target: an RPGLObject being granted temporary hit points
 */
public class GiveTemporaryHitPoints extends Subevent implements CancelableSubevent {

    public GiveTemporaryHitPoints() {
        super("give_temporary_hit_points");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new GiveTemporaryHitPoints();
        clone.joinSubeventData(this.json);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new GiveTemporaryHitPoints();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        this.json.putBoolean("canceled", false);
        this.json.asMap().putIfAbsent("temporary_hit_points", new ArrayList<>());
        this.json.asMap().putIfAbsent("rider_effects", new ArrayList<>());
        this.getBaseTemporaryHitPoints(context, originPoint);
    }

    @Override
    public void run(RPGLContext context, JsonArray originPoint) throws Exception {
        if (this.isNotCanceled()) {
            this.getTargetTemporaryHitPoints(context, originPoint);
            this.deliverTemporaryHitPoints(context, originPoint);
        }
    }

    @Override
    public void cancel() {
        this.json.putBoolean("canceled", true);
    }

    @Override
    public boolean isNotCanceled() {
        return !this.json.getBoolean("canceled");
    }

    /**
     * This helper method collects the base temporary hit points of the Subevent. This includes all target-agnostic
     * temporary hit point dice and bonuses involved in the Subevent's temporary hit point roll.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void getBaseTemporaryHitPoints(RPGLContext context, JsonArray originPoint) throws Exception {
        /*
         * Collect base temporary hit point dice and bonuses
         */
        TemporaryHitPointCollection baseTemporaryHitPointCollection = new TemporaryHitPointCollection();
        baseTemporaryHitPointCollection.joinSubeventData(new JsonObject() {{
            this.putJsonArray("temporary_hit_points", json.removeJsonArray("temporary_hit_points"));
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("base_temporary_hit_point_collection");
            }});
        }});
        baseTemporaryHitPointCollection.setOriginItem(super.getOriginItem());
        baseTemporaryHitPointCollection.setSource(super.getSource());
        baseTemporaryHitPointCollection.prepare(context, originPoint);
        baseTemporaryHitPointCollection.setTarget(super.getSource());
        baseTemporaryHitPointCollection.invoke(context, originPoint);

        /*
         * Roll base temporary hit point dice
         */
        TemporaryHitPointRoll baseTemporaryHitPointRoll = new TemporaryHitPointRoll();
        baseTemporaryHitPointRoll.joinSubeventData(new JsonObject() {{
            this.putJsonArray("temporary_hit_points", baseTemporaryHitPointCollection.getTemporaryHitPointsCollection());
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("base_temporary_hit_points_roll");
            }});
        }});
        baseTemporaryHitPointRoll.setOriginItem(super.getOriginItem());
        baseTemporaryHitPointRoll.setSource(super.getSource());
        baseTemporaryHitPointRoll.prepare(context, originPoint);
        baseTemporaryHitPointRoll.setTarget(super.getSource());
        baseTemporaryHitPointRoll.invoke(context, originPoint);

        /*
         * Replace temporary hit points key with base temporary hit points calculation
         */
        this.json.putJsonArray("temporary_hit_points", baseTemporaryHitPointRoll.getTemporaryHitPoints());
    }

    /**
     * This helper method collects the target temporary hit points of the Subevent. This includes all target-specific
     * temporary hit point dice and bonuses involved in the Subevent's temporary hit point roll.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void getTargetTemporaryHitPoints(RPGLContext context, JsonArray originPoint) throws Exception {
        /*
         * Collect target typed temporary hit points dice and bonuses
         */
        TemporaryHitPointCollection targetTemporaryHitPointsCollection = new TemporaryHitPointCollection();
        targetTemporaryHitPointsCollection.joinSubeventData(new JsonObject() {{
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("target_temporary_hit_point_collection");
            }});
        }});
        targetTemporaryHitPointsCollection.setOriginItem(super.getOriginItem());
        targetTemporaryHitPointsCollection.setSource(super.getSource());
        targetTemporaryHitPointsCollection.prepare(context, originPoint);
        targetTemporaryHitPointsCollection.setTarget(super.getTarget());
        targetTemporaryHitPointsCollection.invoke(context, originPoint);

        /*
         * Roll target temporary hit points dice
         */
        TemporaryHitPointRoll targetTemporaryHitPointRoll = new TemporaryHitPointRoll();
        targetTemporaryHitPointRoll.joinSubeventData(new JsonObject() {{
            this.putJsonArray("temporary_hit_points", targetTemporaryHitPointsCollection.getTemporaryHitPointsCollection());
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("target_healing_roll");
            }});
        }});
        targetTemporaryHitPointRoll.setOriginItem(super.getOriginItem());
        targetTemporaryHitPointRoll.setSource(super.getSource());
        targetTemporaryHitPointRoll.prepare(context, originPoint);
        targetTemporaryHitPointRoll.setTarget(super.getTarget());
        targetTemporaryHitPointRoll.invoke(context, originPoint);

        /*
         * Add target temporary hit points to subevent temporary hit points
         */
        this.json.getJsonArray("temporary_hit_points").asList().addAll(targetTemporaryHitPointRoll.getTemporaryHitPoints().asList());
    }

    /**
     * This helper method provides the temporary hit points collected from this Subevent to the target.
     *
     * @param context the context in which target is being given temporary hit points
     *
     * @throws Exception if an exception occurs
     */
    void deliverTemporaryHitPoints(RPGLContext context, JsonArray originPoint) throws Exception {
        TemporaryHitPointsDelivery temporaryHitPointsDelivery = new TemporaryHitPointsDelivery();
        temporaryHitPointsDelivery.joinSubeventData(new JsonObject() {{
            this.putJsonArray("temporary_hit_points", json.getJsonArray("temporary_hit_points"));
            this.putJsonArray("tags", json.getJsonArray("tags").deepClone());
        }});
        temporaryHitPointsDelivery.setOriginItem(super.getOriginItem());
        temporaryHitPointsDelivery.setSource(super.getSource());
        temporaryHitPointsDelivery.prepare(context, originPoint);
        temporaryHitPointsDelivery.setTarget(super.getTarget());
        temporaryHitPointsDelivery.invoke(context, originPoint);
        super.getTarget().receiveTemporaryHitPoints(temporaryHitPointsDelivery, this.json.getJsonArray("rider_effects"));
    }

}
