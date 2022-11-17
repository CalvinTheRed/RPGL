package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLContext;

public class CalculateProficiencyModifier extends Calculation {

    public CalculateProficiencyModifier() {
        super("calculate_proficiency_modifier");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new CalculateProficiencyModifier();
        clone.joinSubeventJson(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new CalculateProficiencyModifier();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        this.subeventJson.put("base", this.getSource().seek("proficiency_bonus"));
    }

}
