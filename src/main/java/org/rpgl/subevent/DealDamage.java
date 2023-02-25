package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.Map;
import java.util.Objects;

/**
 * This Subevent is dedicated to directly dealing damage to an RPGLObject without first requiring an attack roll or
 * saving throw.
 *
 * @author Calvin Withun
 */
public class DealDamage extends Subevent {

    public DealDamage() {
        super("deal_damage");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new DealDamage();
        clone.joinSubeventData(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new DealDamage();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        this.addTag("deal_damage");
        this.getBaseDamage(context);
    }

    @Override
    public void invoke(RPGLContext context) throws Exception {
        super.invoke(context);
        JsonObject baseDamage = Objects.requireNonNullElse(this.subeventJson.getJsonObject("damage"), new JsonObject());
        JsonObject targetDamage = this.getTargetDamage(context);
        for (Map.Entry<String, Object> targetDamageEntry : targetDamage.asMap().entrySet()) {
            String damageType = targetDamageEntry.getKey();
            if (baseDamage.asMap().containsKey(damageType)) {
                Integer baseTypedDamage = baseDamage.getInteger(damageType);
                baseTypedDamage += targetDamage.getInteger(targetDamageEntry.getKey());
                baseDamage.putInteger(damageType, baseTypedDamage);
            } else {
                baseDamage.asMap().entrySet().add(targetDamageEntry);
            }
        }
        this.deliverDamage(context);
    }

    /**
     * This helper method collects the base damage of the subevent. This includes all target-agnostic damage dice and
     * bonuses involved in the subevent's damage roll.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void getBaseDamage(RPGLContext context) throws Exception {
        /*
         * Collect base typed damage dice and bonuses
         */
        DamageCollection baseDamageCollection = new DamageCollection();
        JsonArray damage = this.subeventJson.getJsonArray("damage");
        baseDamageCollection.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "damage_collection");
            this.putJsonArray("damage", damage.deepClone());
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(subeventJson.getJsonArray("tags").asList());
                this.addString("base_damage_collection");
            }});
        }});
        baseDamageCollection.setSource(this.getSource());
        baseDamageCollection.prepare(context);
        baseDamageCollection.setTarget(this.getSource());
        baseDamageCollection.invoke(context);

        /*
         * Roll base damage dice
         */
        DamageRoll baseDamageRoll = new DamageRoll();
        baseDamageRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "damage_roll");
            this.putJsonArray("damage", baseDamageCollection.getDamageCollection().deepClone());
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(subeventJson.getJsonArray("tags").asList());
                this.addString("base_damage_roll");
            }});
        }});
        baseDamageRoll.setSource(this.getSource());
        baseDamageRoll.prepare(context);
        baseDamageRoll.setTarget(this.getSource());
        baseDamageRoll.invoke(context);

        /*
         * Replace damage key with base damage calculation
         */
        this.subeventJson.putJsonObject("damage", baseDamageRoll.getDamage());
    }

    /**
     * This helper method returns all target-specific damage dice and bonuses involved in the DealDamage's damage roll.
     *
     * @param context the context this Subevent takes place in
     * @return a collection of rolled damage dice and bonuses
     *
     * @throws Exception if an exception occurs.
     */
    JsonObject getTargetDamage(RPGLContext context) throws Exception {
        /*
         * Collect target typed damage dice and bonuses
         */
        DamageCollection targetDamageCollection = new DamageCollection();
        targetDamageCollection.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "damage_collection");
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(subeventJson.getJsonArray("tags").asList());
                this.addString("target_damage_collection");
            }});
        }});
        targetDamageCollection.setSource(this.getSource());
        targetDamageCollection.prepare(context);
        targetDamageCollection.setTarget(this.getTarget());
        targetDamageCollection.invoke(context);

        /*
         * Roll target damage dice
         */
        DamageRoll targetDamageRoll = new DamageRoll();
        targetDamageRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "damage_roll");
            this.putJsonArray("damage", targetDamageCollection.getDamageCollection().deepClone());
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(subeventJson.getJsonArray("tags").asList());
                this.addString("target_damage_roll");
            }});
        }});
        targetDamageRoll.setSource(this.getSource());
        targetDamageRoll.prepare(context);
        targetDamageRoll.setTarget(this.getTarget());
        targetDamageRoll.invoke(context);

        return targetDamageRoll.getDamage();
    }

    /**
     * Delivers the finalized damage to the target.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void deliverDamage(RPGLContext context) throws Exception {
        DamageDelivery damageDelivery = new DamageDelivery();
        JsonObject damage = this.subeventJson.getJsonObject("damage");
        damageDelivery.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "damage_delivery");
            this.putJsonObject("damage", damage.deepClone());
        }});
        damageDelivery.setSource(this.getSource());
        damageDelivery.prepare(context);
        damageDelivery.setTarget(this.getTarget());
        damageDelivery.invoke(context);
        this.getTarget().receiveDamage(context, damageDelivery);
    }

}
