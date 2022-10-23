package org.rpgl.subevent;

import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLObject;
import org.rpgl.exception.SubeventMismatchException;

public class CalculateSaveDifficultyClass extends Subevent {

    private static final String SUBEVENT_ID = "calculate_save_difficulty_class";

    private int difficultyClass = 8;

    public CalculateSaveDifficultyClass() {
        super(SUBEVENT_ID);
    }

    @Override
    public Subevent clone() {
        return new CalculateSaveDifficultyClass();
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new CalculateSaveDifficultyClass();
        clone.joinSubeventJson(subeventJson);
        return clone;
    }

    @Override
    public void invoke(RPGLObject source, RPGLObject target) throws SubeventMismatchException, JsonFormatException {
        super.invoke(source, target);
        this.addBonus(source.getProficiencyModifier());
        this.addBonus(target.getAbilityModifier((String) this.subeventJson.get("spellcasting_ability")));
    }

    public void addBonus(int bonus) {
        difficultyClass += bonus;
    }

    public int getDifficultyClass() {
        return this.difficultyClass;
    }

}
