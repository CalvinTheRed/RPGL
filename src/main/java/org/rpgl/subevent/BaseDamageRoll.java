package org.rpgl.subevent;

import org.jsonutils.JsonObject;

public class BaseDamageRoll extends DamageRoll {

    public BaseDamageRoll() {
        super("base_damage_roll");
    }

    @Override
    public Subevent clone() {
        return new BaseDamageRoll();
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new BaseDamageRoll();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

}
