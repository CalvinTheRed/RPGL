package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLObject;

public class CalculateSaveDifficultyClass extends Subevent {

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
        this.subeventJson.put("difficulty_class", 8L);
        this.addBonus(source.getProficiencyBonus());
        this.addBonus(source.getAbilityModifier((String) this.subeventJson.get("difficulty_class_ability")));
    }

    public void addBonus(long bonus) {
        this.subeventJson.put("difficulty_class", (Long) this.subeventJson.get("difficulty_class") + bonus);
    }

    public long getSaveDifficultyClass() {
        return (long) this.subeventJson.get("difficulty_class");
    }

}
