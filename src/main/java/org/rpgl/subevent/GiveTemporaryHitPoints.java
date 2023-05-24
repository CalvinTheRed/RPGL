package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.ArrayList;
import java.util.Objects;

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
        return null;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        return null;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        this.json.asMap().putIfAbsent("temporary_hit_points", new ArrayList<>());
        this.json.asMap().putIfAbsent("rider_effects", new ArrayList<>());
        this.getBaseTemporaryHitPoints(context);
    }

    @Override
    public void run(RPGLContext context) throws Exception {
        if (this.isNotCanceled()) {
            this.getTargetTemporaryHitPoints(context);
            this.deliverTemporaryHitPoints(context);
        }
    }

    @Override
    public void cancel() {
        this.json.putBoolean("cancel", true);
    }

    @Override
    public boolean isNotCanceled() {
        return !Objects.requireNonNullElse(this.json.getBoolean("cancel"), false);
    }

    /**
     * This helper method collects the base temporary hit points of the Subevent. This includes all target-agnostic
     * temporary hit point dice and bonuses involved in the Subevent's temporary hit point roll.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void getBaseTemporaryHitPoints(RPGLContext context) throws Exception {
        /*
         * Collect base temporary hit point dice and bonuses
         */
        TemporaryHitPointCollection baseTemporaryHitPointCollection = new TemporaryHitPointCollection();
        baseTemporaryHitPointCollection.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "temporary_hit_point_collection");
            this.putJsonArray("temporary_hit_points", json.removeJsonArray("temporary_hit_points"));
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("base_temporary_hit_point_collection");
            }});
        }});
        baseTemporaryHitPointCollection.setSource(this.getSource());
        baseTemporaryHitPointCollection.prepare(context);
        baseTemporaryHitPointCollection.setTarget(this.getSource());
        baseTemporaryHitPointCollection.invoke(context);

        /*
         * Roll base temporary hit point dice
         */
        TemporaryHitPointRoll baseTemporaryHitPointRoll = new TemporaryHitPointRoll();
        baseTemporaryHitPointRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "temporary_hit_points_roll");
            this.putJsonArray("temporary_hit_points", baseTemporaryHitPointCollection.getTemporaryHitPointsCollection());
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("base_temporary_hit_points_roll");
            }});
        }});
        baseTemporaryHitPointRoll.setSource(this.getSource());
        baseTemporaryHitPointRoll.prepare(context);
        baseTemporaryHitPointRoll.setTarget(this.getSource());
        baseTemporaryHitPointRoll.invoke(context);

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
    void getTargetTemporaryHitPoints(RPGLContext context) throws Exception {
        /*
         * Collect target typed temporary hit points dice and bonuses
         */
        TemporaryHitPointCollection targetTemporaryHitPointsCollection = new TemporaryHitPointCollection();
        targetTemporaryHitPointsCollection.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "temporary_hit_point_collection");
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("target_temporary_hit_point_collection");
            }});
        }});
        targetTemporaryHitPointsCollection.setSource(this.getSource());
        targetTemporaryHitPointsCollection.prepare(context);
        targetTemporaryHitPointsCollection.setTarget(this.getTarget());
        targetTemporaryHitPointsCollection.invoke(context);

        /*
         * Roll target temporary hit points dice
         */
        TemporaryHitPointRoll targetTemporaryHitPointRoll = new TemporaryHitPointRoll();
        targetTemporaryHitPointRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "temporary_hit_points_roll");
            this.putJsonArray("temporary_hit_points", targetTemporaryHitPointsCollection.getTemporaryHitPointsCollection());
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("target_healing_roll");
            }});
        }});
        targetTemporaryHitPointRoll.setSource(this.getSource());
        targetTemporaryHitPointRoll.prepare(context);
        targetTemporaryHitPointRoll.setTarget(this.getTarget());
        targetTemporaryHitPointRoll.invoke(context);

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
    void deliverTemporaryHitPoints(RPGLContext context) throws Exception {
        TemporaryHitPointsDelivery temporaryHitPointsDelivery = new TemporaryHitPointsDelivery();
        temporaryHitPointsDelivery.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "temporary_hit_points_delivery");
            this.putJsonArray("temporary_hit_points", json.getJsonArray("temporary_hit_points"));
            this.putJsonArray("tags", json.getJsonArray("tags").deepClone());
        }});
        temporaryHitPointsDelivery.setSource(this.getSource());
        temporaryHitPointsDelivery.prepare(context);
        temporaryHitPointsDelivery.setTarget(this.getTarget());
        temporaryHitPointsDelivery.invoke(context);
        this.getTarget().receiveTemporaryHitPoints(temporaryHitPointsDelivery, this.json.getJsonArray("rider_effects"));
    }

}
