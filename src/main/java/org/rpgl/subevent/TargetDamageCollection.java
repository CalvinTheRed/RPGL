package org.rpgl.subevent;

import org.rpgl.json.JsonObject;

/**
 * This Subevent is dedicated to collecting target-specific damage dice and damage bonuses for an attack roll or saving throw.
 * <br>
 * <br>
 * Source: an RPGLObject making an attack or forcing a saving throw
 * <br>
 * Target: an RPGLObject being attacked or making a saving throw
 *
 * @author Calvin Withun
 */
public class TargetDamageCollection extends DamageCollection {

    public TargetDamageCollection() {
        super("target_damage_collection");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new TargetDamageCollection();
        clone.joinSubeventData(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new TargetDamageCollection();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

}
