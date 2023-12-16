package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.List;
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
    public void prepare(RPGLContext context, List<RPGLResource> resources) throws Exception {
        super.prepare(context, resources);
        this.json.putBoolean("canceled", false);
        this.getBaseDamage(context, resources);
    }

    @Override
    public void run(RPGLContext context, List<RPGLResource> resources) throws Exception {
        if (this.isNotCanceled()) {
            this.getTargetDamage(context, resources);
            this.deliverDamage(context, resources);
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
     * @param resources a list of resources used to produce this subevent
     *
     * @throws Exception if an exception occurs.
     */
    void getBaseDamage(RPGLContext context, List<RPGLResource> resources) throws Exception {
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
        baseDamageCollection.prepare(context, resources);
        baseDamageCollection.setTarget(this.getSource());
        baseDamageCollection.invoke(context, resources);

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
        baseDamageRoll.prepare(context, resources);
        baseDamageRoll.setTarget(this.getSource());
        baseDamageRoll.invoke(context, resources);

        /*
         * Replace damage key with base damage calculation
         */
        this.json.putJsonArray("damage", baseDamageRoll.getDamage());
    }

    /**
     * This helper method collects, rolls, and stores all target-specific damage bonuses for this Subevent.
     *
     * @param context the context this Subevent takes place in
     * @param resources a list of resources used to produce this subevent
     *
     * @throws Exception if an exception occurs.
     */
    void getTargetDamage(RPGLContext context, List<RPGLResource> resources) throws Exception {
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
        targetDamageCollection.prepare(context, resources);
        targetDamageCollection.setTarget(this.getTarget());
        targetDamageCollection.invoke(context, resources);

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
        targetDamageRoll.prepare(context, resources);
        targetDamageRoll.setTarget(this.getTarget());
        targetDamageRoll.invoke(context, resources);

        this.json.getJsonArray("damage").asList().addAll(targetDamageRoll.getDamage().asList());
    }

    /**
     * Delivers the finalized damage to the target.
     *
     * @param context the context this Subevent takes place in
     * @param resources a listof resources used to produce this subevent
     *
     * @throws Exception if an exception occurs.
     */
    void deliverDamage(RPGLContext context, List<RPGLResource> resources) throws Exception {
        DamageDelivery damageDelivery = new DamageDelivery();
        damageDelivery.joinSubeventData(new JsonObject() {{
            this.putJsonArray("damage", json.getJsonArray("damage"));
        }});
        damageDelivery.setOriginItem(this.getOriginItem());
        damageDelivery.setSource(this.getSource());
        damageDelivery.prepare(context, resources);
        damageDelivery.setTarget(this.getTarget());
        damageDelivery.invoke(context, resources);

        JsonObject damageByType = damageDelivery.getTarget().receiveDamage(damageDelivery, context);
        if (this.json.asMap().containsKey("vampirism")) {
            VampiricSubevent.handleVampirism(this, damageByType, context, resources);
        }
    }

}
