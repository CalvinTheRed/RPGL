package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
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
    public void prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        this.json.asMap().putIfAbsent("damage", new ArrayList<>());
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
