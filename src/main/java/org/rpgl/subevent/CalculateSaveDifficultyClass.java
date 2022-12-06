package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;

public class CalculateSaveDifficultyClass extends Calculation {

    public CalculateSaveDifficultyClass() {
        super("calculate_save_difficulty_class");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new CalculateSaveDifficultyClass();
        clone.joinSubeventJson(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new CalculateSaveDifficultyClass();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        this.subeventJson.put("base", 8L);
        RPGLObject source = this.getSource();
        this.addBonus(source.getProficiencyBonus(context));
        this.addBonus(source.getAbilityModifierFromAbilityScore(context, (String) this.subeventJson.get("difficulty_class_ability")));
    }

}
