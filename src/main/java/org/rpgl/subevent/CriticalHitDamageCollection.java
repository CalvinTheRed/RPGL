package org.rpgl.subevent;

import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

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
public class CriticalHitDamageCollection extends Subevent {

    public CriticalHitDamageCollection() {
        super("critical_hit_damage_collection");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new CriticalHitDamageCollection();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new CriticalHitDamageCollection();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
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
     * This method returns whether a given damage type is present in the damage dice collection.
     *
     * @param damageType the damage type being searched for
     * @return true if the passed damage type is present in the damage dice collection
     */
    public boolean includesDamageType(String damageType) {
        JsonArray damageDiceArray = this.json.getJsonArray("damage");
        if (damageDiceArray != null) {
            for (int i = 0; i < damageDiceArray.size(); i++) {
                JsonObject damageDice = damageDiceArray.getJsonObject(i);
                if (damageDice.getString("damage_type").equals(damageType)) {
                    return true;
                }
            }
        }
        return false;
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
