package org.rpgl.subevent;

import org.rpgl.json.JsonObject;

/**
 * This Subevent is dedicated to determining whether an RPGLObject is proficient with an ability check.
 * <br>
 * <br>
 * Source: an RPGLObject attempting to determine if it is proficient with a saving throw
 * <br>
 * Target: should be the same as the source
 *
 * @author Calvin Withun
 */
public class GetAbilityCheckProficiency extends GetProficiency {

    public GetAbilityCheckProficiency() {
        super("get_ability_check_proficiency");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new GetAbilityCheckProficiency();
        clone.joinSubeventData(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new GetAbilityCheckProficiency();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

}
