package org.rpgl.subevent;

import org.jsonutils.JsonObject;

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
