package org.rpgl.subevent;

import java.util.Map;

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
        clone.joinSubeventData(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(Map<String, Object> subeventDataMap) {
        Subevent clone = new GetSavingThrowProficiency();
        clone.joinSubeventData(subeventDataMap);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

}
