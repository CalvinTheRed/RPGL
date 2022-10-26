package org.rpgl.subevent;

import org.jsonutils.JsonObject;

public class TargetDamageRoll extends DamageRoll {

    public TargetDamageRoll() {
        super("target_damage_roll");
    }

    @Override
    public Subevent clone() {
        return new TargetDamageRoll();
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new TargetDamageRoll();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

}
