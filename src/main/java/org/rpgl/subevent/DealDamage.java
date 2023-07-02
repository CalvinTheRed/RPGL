package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.Objects;

/**
 * This Subevent is dedicated to directly dealing damage to an RPGLObject without first requiring an attack roll or
 * saving throw.
 *
 * @author Calvin Withun
 */
public class DealDamage extends Subevent implements CancelableSubevent {

    public DealDamage() {
        super("deal_damage");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new DealDamage();
        clone.joinSubeventData(this.json);
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
    public void run(RPGLContext context) throws Exception {
        if (this.isNotCanceled()) {
            this.getTargetDamage(context);
            this.deliverDamage(context);
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
     * This helper method collects, rolls, and stores all target-agnostic damage bonuses for this Subevent.
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
        JsonArray damage = this.json.getJsonArray("damage");
        baseDamageCollection.joinSubeventData(new JsonObject() {{
            this.putJsonArray("damage", damage.deepClone());
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("base_damage_collection");
            }});
        }});
        baseDamageCollection.setOriginItem(this.getOriginItem());
        baseDamageCollection.setSource(this.getSource());
        baseDamageCollection.prepare(context);
        baseDamageCollection.setTarget(this.getSource());
        baseDamageCollection.invoke(context);

        /*
         * Roll base damage dice
         */
        DamageRoll baseDamageRoll = new DamageRoll();
        baseDamageRoll.joinSubeventData(new JsonObject() {{
            this.putJsonArray("damage", baseDamageCollection.getDamageCollection().deepClone());
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("base_damage_roll");
            }});
        }});
        baseDamageRoll.setOriginItem(this.getOriginItem());
        baseDamageRoll.setSource(this.getSource());
        baseDamageRoll.prepare(context);
        baseDamageRoll.setTarget(this.getSource());
        baseDamageRoll.invoke(context);

        /*
         * Replace damage key with base damage calculation
         */
        this.json.putJsonArray("damage", baseDamageRoll.getDamage());
    }

    /**
     * This helper method collects, rolls, and stores all target-specific damage bonuses for this Subevent.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void getTargetDamage(RPGLContext context) throws Exception {
        /*
         * Collect target typed damage dice and bonuses
         */
        DamageCollection targetDamageCollection = new DamageCollection();
        targetDamageCollection.joinSubeventData(new JsonObject() {{
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("target_damage_collection");
            }});
        }});
        targetDamageCollection.setOriginItem(this.getOriginItem());
        targetDamageCollection.setSource(this.getSource());
        targetDamageCollection.prepare(context);
        targetDamageCollection.setTarget(this.getTarget());
        targetDamageCollection.invoke(context);

        /*
         * Roll target damage dice
         */
        DamageRoll targetDamageRoll = new DamageRoll();
        targetDamageRoll.joinSubeventData(new JsonObject() {{
            this.putJsonArray("damage", targetDamageCollection.getDamageCollection().deepClone());
            this.putJsonArray("tags", new JsonArray() {{
                this.asList().addAll(json.getJsonArray("tags").asList());
                this.addString("target_damage_roll");
            }});
        }});
        targetDamageRoll.setOriginItem(this.getOriginItem());
        targetDamageRoll.setSource(this.getSource());
        targetDamageRoll.prepare(context);
        targetDamageRoll.setTarget(this.getTarget());
        targetDamageRoll.invoke(context);

        this.json.getJsonArray("damage").asList().addAll(targetDamageRoll.getDamage().asList());
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
        damageDelivery.joinSubeventData(new JsonObject() {{
            this.putJsonArray("damage", json.getJsonArray("damage"));
        }});
        damageDelivery.setOriginItem(this.getOriginItem());
        damageDelivery.setSource(this.getSource());
        damageDelivery.prepare(context);
        damageDelivery.setTarget(this.getTarget());
        damageDelivery.invoke(context);
        this.getTarget().receiveDamage(damageDelivery, context);
    }

}
