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
public class Heal extends Subevent {

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
        if (this.json.getJsonObject("healing") != null) {
            this.getBaseHealing(context);
        }
    }

    @Override
    public void invoke(RPGLContext context) throws Exception {
        super.invoke(context);
        JsonObject baseHealing = Objects.requireNonNullElse(this.json.getJsonObject("healing"), new JsonObject());
        JsonObject targetHealing = this.getTargetHealing(context);
        baseHealing.putInteger("bonus", baseHealing.getInteger("bonus") + targetHealing.getInteger("bonus"));
        baseHealing.getJsonArray("dice").asList().addAll(targetHealing.getJsonArray("dice").asList());
        int healing = baseHealing.getInteger("bonus");
        JsonArray healingDice = baseHealing.getJsonArray("dice");
        for (int i = 0; i < healingDice.size(); i++) {
            healing += healingDice.getJsonObject(i).getInteger("roll");
        }
        this.deliverHealing(context, healing);
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
        JsonObject healing = this.json.getJsonObject("healing");
        baseHealingCollection.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "healing_collection");
            this.putJsonArray("dice", healing.getJsonArray("dice").deepClone());
            this.putInteger("bonus", healing.getInteger("bonus"));
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("base_healing_collection");
            }});
        }});
        baseHealingCollection.setSource(this.getSource());
        baseHealingCollection.prepare(context);
        baseHealingCollection.setTarget(this.getSource());
        baseHealingCollection.invoke(context);

        /*
         * Roll base healing dice
         */
        HealingRoll baseHealingRoll = new HealingRoll();
        baseHealingRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "healing_roll");
            this.join(baseHealingCollection.getHealingCollection());
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
        this.json.putJsonObject("healing", baseHealingRoll.getHealing());
    }

    /**
     * This helper method returns all target-specific healing dice and bonuses involved in the heal's healing roll.
     *
     * @param context the context this Subevent takes place in
     * @return a collection of rolled healing dice and bonuses
     *
     * @throws Exception if an exception occurs.
     */
    JsonObject getTargetHealing(RPGLContext context) throws Exception {
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
            this.putJsonObject("healing", targetHealingCollection.getHealingCollection().deepClone());
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
