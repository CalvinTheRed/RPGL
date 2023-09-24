package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This Subevent is dedicated to representing a collection of damage to be rolled when a critical hit occurs.
 * <br>
 * <br>
 * Source: an RPGLObject delivering a critical hit attack
 * <br>
 * Target: an RPGLObject targeted by a critical hit attack
 *
 * @author Calvin Withun
 */
public class CriticalDamageCollection extends Subevent implements DamageTypeSubevent {

    public CriticalDamageCollection() {
        super("critical_damage_collection");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new CriticalDamageCollection();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new CriticalDamageCollection();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context, List<RPGLResource> resources) throws Exception {
        super.prepare(context, resources);
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
