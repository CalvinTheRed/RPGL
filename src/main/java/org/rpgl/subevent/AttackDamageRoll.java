package org.rpgl.subevent;

import org.jsonutils.JsonObject;

public class AttackDamageRoll extends DamageRoll {

    public AttackDamageRoll() {
        super("attack_damage_roll");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new AttackDamageRoll();
        clone.joinSubeventJson(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new AttackDamageRoll();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

}
