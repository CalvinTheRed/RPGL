package org.rpgl.subevent;

import org.jsonutils.JsonObject;

public class GetWeaponProficiency extends GetProficiency {

    public GetWeaponProficiency() {
        super("get_weapon_proficiency");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new GetWeaponProficiency();
        clone.joinSubeventJson(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new GetWeaponProficiency();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

}
