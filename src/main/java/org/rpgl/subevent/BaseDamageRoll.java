package org.rpgl.subevent;

import org.jsonutils.JsonObject;

public class BaseDamageRoll extends DamageRoll {

    public BaseDamageRoll() {
        super("base_damage_roll");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new BaseDamageRoll();
        clone.joinSubeventJson(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new BaseDamageRoll();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

}
