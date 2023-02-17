package org.rpgl.subevent;

import org.rpgl.json.JsonObject;

/**
 * This Subevent is dedicated to rolling all dice which will be copied to all targets of a damaging RPGLEvent. This
 * Subevent is typically only created within a SavingThrow Subevent.
 * <br>
 * <br>
 * Source: an RPGLObject invoking an RPGLEvent which deals damage
 * <br>
 * Target: should be the same as the source
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
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new BaseDamageRoll();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

}
