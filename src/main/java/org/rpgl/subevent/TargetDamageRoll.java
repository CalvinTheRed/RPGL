package org.rpgl.subevent;

import org.jsonutils.JsonObject;

/**
 * This Subevent is dedicated to rolling damage dice collected by a <code>TargetDamageDiceCollection</code> Subevent.
 * <br>
 * <br>
 * Source: an RPGLObject making an attack
 * <br>
 * Target: an RPGLObject being attacked
 *
 * @author Calvin Withun
 */
public class TargetDamageRoll extends DamageRoll {

    public TargetDamageRoll() {
        super("target_damage_roll");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new TargetDamageRoll();
        clone.joinSubeventJson(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new TargetDamageRoll();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

}
