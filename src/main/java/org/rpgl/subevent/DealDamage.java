package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.Objects;

/**
 * This Subevent is dedicated to directly dealing damage to an RPGLObject without first requiring an attack roll or
 * saving throw.
 *
 * @author Calvin Withun
 */
public class DealDamage extends Subevent implements CancelableSubevent, DamageTypeSubevent {

    public DealDamage() {
        super("deal_damage");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new DealDamage();
        clone.joinSubeventData(this.json);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new DealDamage();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public DealDamage invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (DealDamage) super.invoke(context, originPoint);
    }

    @Override
    public DealDamage joinSubeventData(JsonObject other) {
        return (DealDamage) super.joinSubeventData(other);
    }

    @Override
    public DealDamage prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        this.json.putBoolean("canceled", false);
        this.getBaseDamage(context, originPoint);
        return this;
    }

    @Override
    public DealDamage run(RPGLContext context, JsonArray originPoint) throws Exception {
        if (this.isNotCanceled()) {
            this.getTargetDamage(context, originPoint);
            this.deliverDamage(context, originPoint);
        }
        return this;
    }

    @Override
    public DealDamage setOriginItem(String originItem) {
        return (DealDamage) super.setOriginItem(originItem);
    }

    @Override
    public DealDamage setSource(RPGLObject source) {
        return (DealDamage) super.setSource(source);
    }

    @Override
    public DealDamage setTarget(RPGLObject target) {
        return (DealDamage) super.setTarget(target);
    }

    @Override
    public void cancel() {
        this.json.putBoolean("canceled", true);
    }

    @Override
    public boolean isNotCanceled() {
        return !this.json.getBoolean("canceled");
    }

    @Override
    public boolean includesDamageType(String damageType) {
        JsonArray damageArray = this.json.getJsonArray("damage");
        for (int i = 0; i < damageArray.size(); i++) {
            if (Objects.equals(damageType, damageArray.getJsonObject(i).getString("damage_type"))) {
                return true;
            }
        }
        return false;
    }

    /**
     * This helper method collects, rolls, and stores all target-agnostic damage bonuses for this Subevent.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void getBaseDamage(RPGLContext context, JsonArray originPoint) throws Exception {
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
        baseDamageCollection.setOriginItem(super.getOriginItem());
        baseDamageCollection.setSource(super.getSource());
        baseDamageCollection.prepare(context, originPoint);
        baseDamageCollection.setTarget(super.getSource());
        baseDamageCollection.invoke(context, originPoint);

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
        baseDamageRoll.setOriginItem(super.getOriginItem());
        baseDamageRoll.setSource(super.getSource());
        baseDamageRoll.prepare(context, originPoint);
        baseDamageRoll.setTarget(super.getSource());
        baseDamageRoll.invoke(context, originPoint);

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
    void getTargetDamage(RPGLContext context, JsonArray originPoint) throws Exception {
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
        targetDamageCollection.setOriginItem(super.getOriginItem());
        targetDamageCollection.setSource(super.getSource());
        targetDamageCollection.prepare(context, originPoint);
        targetDamageCollection.setTarget(super.getTarget());
        targetDamageCollection.invoke(context, originPoint);

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
        targetDamageRoll.setOriginItem(super.getOriginItem());
        targetDamageRoll.setSource(super.getSource());
        targetDamageRoll.prepare(context, originPoint);
        targetDamageRoll.setTarget(super.getTarget());
        targetDamageRoll.invoke(context, originPoint);

        this.json.getJsonArray("damage").asList().addAll(targetDamageRoll.getDamage().asList());
    }

    /**
     * Delivers the finalized damage to the target.
     *
     * @param context the context this Subevent takes place in
     *
     * @throws Exception if an exception occurs.
     */
    void deliverDamage(RPGLContext context, JsonArray originPoint) throws Exception {
        DamageDelivery damageDelivery = new DamageDelivery();
        damageDelivery.joinSubeventData(new JsonObject() {{
            this.putJsonArray("damage", json.getJsonArray("damage"));
        }});
        damageDelivery.setOriginItem(super.getOriginItem());
        damageDelivery.setSource(super.getSource());
        damageDelivery.prepare(context, originPoint);
        damageDelivery.setTarget(super.getTarget());
        damageDelivery.invoke(context, originPoint);

        JsonObject damageByType = damageDelivery.getDamage();
        if (this.json.asMap().containsKey("vampirism")) {
            VampiricSubevent.handleVampirism(this, damageByType, context, originPoint);
        }
    }

}
