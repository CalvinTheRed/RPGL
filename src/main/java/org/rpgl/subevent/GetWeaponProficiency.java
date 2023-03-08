package org.rpgl.subevent;

import org.rpgl.json.JsonObject;

/**
 * This Subevent is dedicated to determining whether an RPGLObject is proficient with a weapon.
 * <br>
 * <br>
 * Source: an RPGLObject attempting to determine if it is proficient with a weapon
 * <br>
 * Target: should be the same as the source
 *
 * @author Calvin Withun
 */
public class GetWeaponProficiency extends GetProficiency {

    public GetWeaponProficiency() {
        super("get_weapon_proficiency");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new GetWeaponProficiency();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new GetWeaponProficiency();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

}
