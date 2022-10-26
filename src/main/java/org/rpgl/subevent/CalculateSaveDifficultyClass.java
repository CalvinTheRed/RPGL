package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLObject;

public class CalculateSaveDifficultyClass extends AttributeCalculation {

    public CalculateSaveDifficultyClass() {
        super("calculate_save_difficulty_class");
    }

    @Override
    public Subevent clone() {
        return new CalculateSaveDifficultyClass();
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new CalculateSaveDifficultyClass();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLObject source) throws Exception {
        super.prepare(source);
        this.subeventJson.put("raw", 8L);
        this.addBonus(source.getProficiencyBonus());
        this.addBonus(source.getAbilityModifier((String) this.subeventJson.get("difficulty_class_ability")));
    }

}
