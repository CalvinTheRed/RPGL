package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLContext;

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

    @Override
    public void prepare(RPGLContext context) {
        this.subeventJson.put("is_proficient", false);
    }

    public void grantProficiency() {
        this.subeventJson.put("is_proficient", true);
    }

    public Boolean getIsProficient() {
        return (Boolean) this.subeventJson.get("is_proficient");
    }

}
