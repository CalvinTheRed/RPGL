package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
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
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new DamageCollection();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context, List<RPGLResource> resources) throws Exception {
        super.prepare(context, resources);
        this.json.asMap().putIfAbsent("damage", new ArrayList<>());
        this.prepareDamage(context);
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
        effect.setSource(this.getSource());
        effect.setTarget(this.getSource());
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

}
