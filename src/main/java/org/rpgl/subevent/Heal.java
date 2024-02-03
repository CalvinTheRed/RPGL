package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.ArrayList;

/**
 * This Subevent is dedicated to performing healing on an RPGLObject.
 * <br>
 * <br>
 * Source: an RPGLObject performing healing
 * <br>
 * Target: an RPGLObject being targeted by the healing
 *
 * @author Calvin Withun
 */
public class Heal extends Subevent implements CancelableSubevent {

    public Heal() {
        super("heal");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new Heal();
        clone.joinSubeventData(this.json);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new Heal();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Heal invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (Heal) super.invoke(context, originPoint);
    }

    @Override
    public Heal joinSubeventData(JsonObject other) {
        return (Heal) super.joinSubeventData(other);
    }

    @Override
    public Heal prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        this.json.putBoolean("canceled", false);
        this.json.asMap().putIfAbsent("healing", new ArrayList<>());
        this.getBaseHealing(context, originPoint);
        return this;
    }

    @Override
    public Heal run(RPGLContext context, JsonArray originPoint) throws Exception {
        if (this.isNotCanceled()) {
            this.getTargetHealing(context, originPoint);
            this.deliverHealing(context, originPoint);
        }
        return this;
    }

    @Override
    public Heal setOriginItem(String originItem) {
        return (Heal) super.setOriginItem(originItem);
    }

    @Override
    public Heal setSource(RPGLObject source) {
        return (Heal) super.setSource(source);
    }

    @Override
    public Heal setTarget(RPGLObject target) {
        return (Heal) super.setTarget(target);
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
     * This helper method collects the base healing of the saving throw. This includes all target-agnostic healing dice
     * and bonuses involved in the heal's healing roll.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void getBaseHealing(RPGLContext context, JsonArray originPoint) throws Exception {
        /*
         * Collect base typed healing dice and bonuses
         */
        HealingCollection baseHealingCollection = new HealingCollection();
        baseHealingCollection.joinSubeventData(new JsonObject() {{
            this.putJsonArray("healing", json.removeJsonArray("healing"));
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("base_healing_collection");
            }});
        }});
        baseHealingCollection.setOriginItem(super.getOriginItem());
        baseHealingCollection.setSource(super.getSource());
        baseHealingCollection.prepare(context, originPoint);
        baseHealingCollection.setTarget(super.getSource());
        baseHealingCollection.invoke(context, originPoint);

        /*
         * Roll base healing dice
         */
        HealingRoll baseHealingRoll = new HealingRoll();
        baseHealingRoll.joinSubeventData(new JsonObject() {{
            this.putJsonArray("healing", baseHealingCollection.getHealingCollection());
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("base_healing_roll");
            }});
        }});
        baseHealingRoll.setOriginItem(super.getOriginItem());
        baseHealingRoll.setSource(super.getSource());
        baseHealingRoll.prepare(context, originPoint);
        baseHealingRoll.setTarget(super.getSource());
        baseHealingRoll.invoke(context, originPoint);

        /*
         * Replace healing key with base healing roll
         */
        this.json.putJsonArray("healing", baseHealingRoll.getHealing());
    }

    void getTargetHealing(RPGLContext context, JsonArray originPoint) throws Exception {
        /*
         * Collect target typed healing dice and bonuses
         */
        HealingCollection targetHealingCollection = new HealingCollection();
        targetHealingCollection.joinSubeventData(new JsonObject() {{
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("target_healing_collection");
            }});
        }});
        targetHealingCollection.setOriginItem(super.getOriginItem());
        targetHealingCollection.setSource(super.getSource());
        targetHealingCollection.prepare(context, originPoint);
        targetHealingCollection.setTarget(super.getTarget());
        targetHealingCollection.invoke(context, originPoint);

        /*
         * Roll target healing dice
         */
        HealingRoll targetHealingRoll = new HealingRoll();
        targetHealingRoll.joinSubeventData(new JsonObject() {{
            this.putJsonArray("healing", targetHealingCollection.getHealingCollection());
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("target_healing_roll");
            }});
        }});
        targetHealingRoll.setOriginItem(super.getOriginItem());
        targetHealingRoll.setSource(super.getSource());
        targetHealingRoll.prepare(context, originPoint);
        targetHealingRoll.setTarget(super.getTarget());
        targetHealingRoll.invoke(context, originPoint);

        /*
         * Add target healing roll to Subevent JSON
         */
        this.json.getJsonArray("healing").asList().addAll(targetHealingRoll.getHealing().asList());
    }

    /**
     * This helper method delivers the final collection of healing determined by this Subevent to the target RPGLObject.
     *
     * @param context the context in which this Subevent was invoked
     *
     * @throws Exception if an exception occurs
     */
    void deliverHealing(RPGLContext context, JsonArray originPoint) throws Exception {
        HealingDelivery healingDelivery = new HealingDelivery();
        healingDelivery.joinSubeventData(new JsonObject() {{
            this.putJsonArray("healing", json.getJsonArray("healing"));
            this.putJsonArray("tags", json.getJsonArray("tags").deepClone());
        }});
        healingDelivery.setOriginItem(super.getOriginItem());
        healingDelivery.setSource(super.getSource());
        healingDelivery.prepare(context, originPoint);
        healingDelivery.setTarget(super.getTarget());
        healingDelivery.invoke(context, originPoint);
        super.getTarget().receiveHealing(healingDelivery, context);
    }

}
