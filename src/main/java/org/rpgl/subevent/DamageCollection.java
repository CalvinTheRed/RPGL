package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * This Subevent is dedicated to collecting unrolled damage dice and bonuses.
 * <br>
 * <br>
 * Source: an RPGLObject preparing to deal damage
 * <br>
 * Target: an RPGLObject which will later suffer the collected damage
 *
 * @author Calvin Withun
 */
public class DamageCollection extends Subevent implements DamageTypeSubevent {

    public DamageCollection() {
        super("damage_collection");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new DamageCollection();
        clone.joinSubeventData(this.json);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new DamageCollection();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public DamageCollection invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (DamageCollection) super.invoke(context, originPoint);
    }

    @Override
    public DamageCollection joinSubeventData(JsonObject other) {
        return (DamageCollection) super.joinSubeventData(other);
    }

    @Override
    public DamageCollection prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        this.json.asMap().putIfAbsent("damage", new ArrayList<>());
        this.prepareDamage(context);
        return this;
    }

    @Override
    public DamageCollection run(RPGLContext context, JsonArray originPoint) throws Exception {
        return this;
    }

    @Override
    public DamageCollection setOriginItem(String originItem) {
        return (DamageCollection) super.setOriginItem(originItem);
    }

    @Override
    public DamageCollection setSource(RPGLObject source) {
        return (DamageCollection) super.setSource(source);
    }

    @Override
    public DamageCollection setTarget(RPGLObject target) {
        return (DamageCollection) super.setTarget(target);
    }

    @Override
    public boolean includesDamageType(String damageType) {
        JsonArray damageDiceArray = this.getDamageCollection();
        for (int i = 0; i < damageDiceArray.size(); i++) {
            if (Objects.equals(damageDiceArray.getJsonObject(i).getString("damage_type"), damageType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This helper method interprets the damage formulas provided to the damage collection upon construction and stores
     * the result in the Subevent.
     *
     * @param context the context in which the damage collection is being prepared
     *
     * @throws Exception if an exception occurs
     */
    void prepareDamage(RPGLContext context) throws Exception {
        JsonArray damageArray = this.json.removeJsonArray("damage");
        this.json.putJsonArray("damage", new JsonArray());

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(super.getSource());
        effect.setTarget(super.getSource());
        for (int i = 0; i < damageArray.size(); i++) {
            JsonObject damageElement = damageArray.getJsonObject(i);
            JsonObject damage = Calculation.processBonusJson(effect, this, damageElement, context);
            damage.putString("damage_type", damageElement.getString("damage_type"));
            this.addDamage(damage);
        }
    }

    /**
     * Adds damage to the damage collection.
     *
     * @param damageJson the JSON data for the damage to be added to the damage collection
     * @return this DamageCollection
     */
    public DamageCollection addDamage(JsonObject damageJson) {
        this.getDamageCollection().addJsonObject(damageJson);
        return this;
    }

    /**
     * This method returns the damage collection being gathered by this Subevent.
     *
     * @return an array of typed damage dice and bonuses
     */
    public JsonArray getDamageCollection() {
        return this.json.getJsonArray("damage");
    }

}
