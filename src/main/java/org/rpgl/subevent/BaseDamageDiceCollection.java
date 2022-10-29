package org.rpgl.subevent;

import org.jsonutils.JsonObject;

public class BaseDamageDiceCollection extends DamageDiceCollection {

    public BaseDamageDiceCollection() {
        super("base_damage_dice_collection");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new BaseDamageDiceCollection();
        clone.joinSubeventJson(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new BaseDamageDiceCollection();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

}
