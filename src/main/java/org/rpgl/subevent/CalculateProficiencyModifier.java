package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLObject;

public class CalculateProficiencyModifier extends AttributeCalculation {

    public CalculateProficiencyModifier() {
        super("calculate_proficiency_modifier");
    }

    @Override
    public Subevent clone() {
        return new CalculateProficiencyModifier();
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new CalculateProficiencyModifier();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLObject source) throws Exception {
        super.prepare(source);
        Long rawProficiencyModifier = (Long) source.seek("proficiency_bonus");
        this.subeventJson.put("raw", rawProficiencyModifier);
    }

}
