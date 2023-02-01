package org.rpgl.subevent;

import java.util.Map;

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
        clone.joinSubeventData(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(Map<String, Object> subeventDataMap) {
        Subevent clone = new TargetDamageRoll();
        clone.joinSubeventData(subeventDataMap);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

}
