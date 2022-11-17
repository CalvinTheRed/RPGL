package org.rpgl.subevent;

import org.jsonutils.JsonObject;

import java.util.Objects;

public class GetSaveProficiency extends Subevent {

    public GetSaveProficiency() {
        super("get_save_proficiency");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new GetSaveProficiency();
        clone.joinSubeventJson(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new GetSaveProficiency();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    public void grantProficiency() {
        this.subeventJson.put("is_proficient", true);
    }

    public Boolean getIsProficient() {
        return Objects.requireNonNullElse((Boolean) this.subeventJson.get("is_proficient"), false);
    }

}
