package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.ArrayList;
import java.util.Objects;

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
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new Heal();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        this.json.asMap().putIfAbsent("healing", new ArrayList<>());
        this.getBaseHealing(context);
    }

    @Override
    public void run(RPGLContext context) throws Exception {
        if (this.isNotCanceled()) {
            this.getTargetHealing(context);
            this.deliverHealing(context);
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
     * This helper method collects the base healing of the saving throw. This includes all target-agnostic healing dice
     * and bonuses involved in the heal's healing roll.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void getBaseHealing(RPGLContext context) throws Exception {
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
        baseHealingCollection.setOriginItem(this.getOriginItem());
        baseHealingCollection.setSource(this.getSource());
        baseHealingCollection.prepare(context);
        baseHealingCollection.setTarget(this.getSource());
        baseHealingCollection.invoke(context);

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
        baseHealingRoll.setOriginItem(this.getOriginItem());
        baseHealingRoll.setSource(this.getSource());
        baseHealingRoll.prepare(context);
        baseHealingRoll.setTarget(this.getSource());
        baseHealingRoll.invoke(context);

        /*
         * Replace healing key with base healing roll
         */
        this.json.putJsonArray("healing", baseHealingRoll.getHealing());
    }

    void getTargetHealing(RPGLContext context) throws Exception {
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
        targetHealingCollection.setOriginItem(this.getOriginItem());
        targetHealingCollection.setSource(this.getSource());
        targetHealingCollection.prepare(context);
        targetHealingCollection.setTarget(this.getTarget());
        targetHealingCollection.invoke(context);

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
        targetHealingRoll.setOriginItem(this.getOriginItem());
        targetHealingRoll.setSource(this.getSource());
        targetHealingRoll.prepare(context);
        targetHealingRoll.setTarget(this.getTarget());
        targetHealingRoll.invoke(context);

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
    void deliverHealing(RPGLContext context) throws Exception {
        HealingDelivery healingDelivery = new HealingDelivery();
        healingDelivery.joinSubeventData(new JsonObject() {{
            this.putJsonArray("healing", json.getJsonArray("healing"));
            this.putJsonArray("tags", json.getJsonArray("tags").deepClone());
        }});
        healingDelivery.setOriginItem(this.getOriginItem());
        healingDelivery.setSource(this.getSource());
        healingDelivery.prepare(context);
        healingDelivery.setTarget(this.getTarget());
        healingDelivery.invoke(context);
        this.getTarget().receiveHealing(healingDelivery, context);
    }

}
