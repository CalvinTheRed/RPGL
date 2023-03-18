package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

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
        this.addTag("heal");
        this.getBaseHealing(context);
    }

    @Override
    public void invoke(RPGLContext context) throws Exception {
        super.invoke(context);
        System.out.println(this.json);
        if (this.isNotCanceled()) {
            int baseHealing = Objects.requireNonNullElse(this.json.getInteger("healing"), 0);
            int targetHealing = this.getTargetHealing(context);
            this.deliverHealing(context, baseHealing + targetHealing);
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
            this.putString("subevent", "healing_collection");
            this.putJsonArray("healing", json.getJsonArray("healing").deepClone());
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("base_healing_collection");
            }});
        }});
        baseHealingCollection.setSource(this.getSource());
        baseHealingCollection.prepare(context);
        baseHealingCollection.setTarget(this.getSource());
        baseHealingCollection.invoke(context);
        System.out.println("Base Healing Collection:");
        System.out.println(baseHealingCollection);

        /*
         * Roll base healing dice
         */
        HealingRoll baseHealingRoll = new HealingRoll();
        baseHealingRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "healing_roll");
            this.putJsonArray("healing", baseHealingCollection.getHealingCollection());
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("base_healing_roll");
            }});
        }});
        baseHealingRoll.setSource(this.getSource());
        baseHealingRoll.prepare(context);
        baseHealingRoll.setTarget(this.getSource());
        baseHealingRoll.invoke(context);

        /*
         * Replace healing key with base healing calculation
         */
        this.json.putInteger("healing", baseHealingRoll.getHealing());
    }

    /**
     * This helper method returns all target-specific healing dice and bonuses involved in the heal's healing roll.
     *
     * @param context the context this Subevent takes place in
     * @return a quantity of target-specific healing
     *
     * @throws Exception if an exception occurs.
     */
    int getTargetHealing(RPGLContext context) throws Exception {
        /*
         * Collect target typed healing dice and bonuses
         */
        HealingCollection targetHealingCollection = new HealingCollection();
        targetHealingCollection.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "healing_collection");
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("target_healing_collection");
            }});
        }});
        targetHealingCollection.setSource(this.getSource());
        targetHealingCollection.prepare(context);
        targetHealingCollection.setTarget(this.getTarget());
        targetHealingCollection.invoke(context);

        /*
         * Roll target healing dice
         */
        HealingRoll targetHealingRoll = new HealingRoll();
        targetHealingRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "healing_roll");
            this.putJsonArray("healing", targetHealingCollection.getHealingCollection().deepClone());
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("target_healing_roll");
            }});
        }});
        targetHealingRoll.setSource(this.getSource());
        targetHealingRoll.prepare(context);
        targetHealingRoll.setTarget(this.getTarget());
        targetHealingRoll.invoke(context);

        return targetHealingRoll.getHealing();
    }

    /**
     * This helper method delivers the final quantity of healing determined by this Subevent to the target RPGLObject.
     *
     * @param context the context in which this Subevent was invoked
     * @param healing the final quantity of healing determined by this Subevent
     *
     * @throws Exception if an exception occurs
     */
    void deliverHealing(RPGLContext context, int healing) throws Exception {
        HealingDelivery healingDelivery = new HealingDelivery();
        healingDelivery.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "healing_delivery");
            this.putInteger("healing", healing);
            this.putJsonArray("tags", json.getJsonArray("tags").deepClone());
        }});
        healingDelivery.setSource(this.getSource());
        healingDelivery.prepare(context);
        healingDelivery.setTarget(this.getTarget());
        healingDelivery.invoke(context);
        this.getTarget().receiveHealing(healingDelivery, context);
    }

}
