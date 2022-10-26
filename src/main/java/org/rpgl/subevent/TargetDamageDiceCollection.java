package org.rpgl.subevent;

import org.jsonutils.JsonObject;

public class TargetDamageDiceCollection extends DamageDiceCollection {

    public TargetDamageDiceCollection() {
        super("target_damage_dice_collection");
    }

    @Override
    public Subevent clone() {
        return new TargetDamageDiceCollection();
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new TargetDamageDiceCollection();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

}
