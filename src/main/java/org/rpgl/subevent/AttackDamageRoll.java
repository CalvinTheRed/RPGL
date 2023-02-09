package org.rpgl.subevent;

import org.rpgl.json.JsonObject;

/**
 * This Subevent is dedicated to rolling all damage dice collected for an attack roll. This includes the combination of
 * BaseDamageCollection and TargetDamageCollection Subevents. Damage should only be rolled this way if the
 * attack roll hits the target.
 * <br>
 * <br>
 * Source: an RPGLObject dealing damage from an attack
 * <br>
 * Target: an RPGLObject which will take damage from an attack
 *
 * @author Calvin Withun
 */
public class AttackDamageRoll extends DamageRoll {

    public AttackDamageRoll() {
        super("attack_damage_roll");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new AttackDamageRoll();
        clone.joinSubeventData(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new AttackDamageRoll();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

}
