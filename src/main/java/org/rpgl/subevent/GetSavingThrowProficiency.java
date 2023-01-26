package org.rpgl.subevent;

import org.jsonutils.JsonObject;

/**
 * This Subevent is dedicated to determining whether an RPGLObject is proficient with a saving throw.
 * <br>
 * <br>
 * Source: an RPGLObject attempting to determine if it is proficient with a saving throw
 * <br>
 * Target: should be the same as the source
 *
 * @author Calvin Withun
 */
public class GetSavingThrowProficiency extends GetProficiency {

    public GetSavingThrowProficiency() {
        super("get_saving_throw_proficiency");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new GetSavingThrowProficiency();
        clone.joinSubeventJson(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new GetSavingThrowProficiency();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

}
