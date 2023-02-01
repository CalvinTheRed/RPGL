package org.rpgl.subevent;

import java.util.Map;

/**
 * This Subevent is dedicated to rolling all dice which will be copied to all targets of a damaging RPGLEvent. This
 * Subevent is typically only created within a SavingThrow Subevent.
 * <br>
 * <br>
 * Source: an RPGLObject invoking an RPGLEvent which deals damage
 * <br>
 * Target: an RPGLObject being targeted by the damaging RPGLEvent
 *
 * @author Calvin Withun
 */
public class BaseDamageRoll extends DamageRoll {

    public BaseDamageRoll() {
        super("base_damage_roll");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new BaseDamageRoll();
        clone.joinSubeventData(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(Map<String, Object> subeventDataMap) {
        Subevent clone = new BaseDamageRoll();
        clone.joinSubeventData(subeventDataMap);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

}
