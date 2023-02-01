package org.rpgl.subevent;

import java.util.Map;

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
        clone.joinSubeventData(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(Map<String, Object> subeventDataMap) {
        Subevent clone = new GetWeaponProficiency();
        clone.joinSubeventData(subeventDataMap);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

}
