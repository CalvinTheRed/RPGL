package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * This Subevent is dedicated to representing a collection of damage dice to be rolled when a critical hit occurs.
 * <br>
 * <br>
 * Source: an RPGLObject delivering a critical hit attack
 * <br>
 * Target: an RPGLObject targeted by a critical hit attack
 *
 * @author Calvin Withun
 */
public class CriticalHitDamageCollection extends Subevent implements DamageTypeSubevent {

    public CriticalHitDamageCollection() {
        super("critical_hit_damage_collection");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new CriticalHitDamageCollection();
        clone.joinSubeventData(this.json);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new CriticalHitDamageCollection();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public CriticalHitDamageCollection invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (CriticalHitDamageCollection) super.invoke(context, originPoint);
    }

    @Override
    public CriticalHitDamageCollection joinSubeventData(JsonObject other) {
        return (CriticalHitDamageCollection) super.joinSubeventData(other);
    }

    @Override
    public CriticalHitDamageCollection prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        this.json.asMap().putIfAbsent("damage", new ArrayList<>());
        return this;
    }

    @Override
    public CriticalHitDamageCollection run(RPGLContext context, JsonArray originPoint) throws Exception {
        return this;
    }

    @Override
    public CriticalHitDamageCollection setOriginItem(String originItem) {
        return (CriticalHitDamageCollection) super.setOriginItem(originItem);
    }

    @Override
    public CriticalHitDamageCollection setSource(RPGLObject source) {
        return (CriticalHitDamageCollection) super.setSource(source);
    }

    @Override
    public CriticalHitDamageCollection setTarget(RPGLObject target) {
        return (CriticalHitDamageCollection) super.setTarget(target);
    }

    @Override
    public boolean includesDamageType(String damageType) {
        JsonArray damageDiceArray = this.json.getJsonArray("damage");
        for (int i = 0; i < damageDiceArray.size(); i++) {
            if (Objects.equals(damageDiceArray.getJsonObject(i).getString("damage_type"), damageType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds extra damage to the Collection.
     *
     * @param damageJson the damage to be added to the Collection
     */
    public void addDamage(JsonObject damageJson) {
        this.getDamageCollection().addJsonObject(damageJson);
    }

    /**
     * This method returns the damage collection being gathered by this Subevent.
     *
     * @return an array of typed damage dice and bonuses
     */
    public JsonArray getDamageCollection() {
        return this.json.getJsonArray("damage");
    }

    /**
     * This helper method doubles the number of dice in the damage dice collection.
     */
    void doubleDice() {
        JsonArray damageCollection = this.getDamageCollection();
        for (int i = 0; i < damageCollection.size(); i++) {
            JsonArray typedDamageDice = damageCollection.getJsonObject(i).getJsonArray("dice");
            typedDamageDice.asList().addAll(typedDamageDice.deepClone().asList());
        }
    }

}
