package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;

/**
 * This subevent is dedicated to calculating the save difficulty class against which saving throws are made.
 * <br>
 * <br>
 * Source: the RPGLObject whose save difficulty class is being calculated
 * <br>
 * Target: should be the same as the source
 *
 * @author Calvin Withun
 */
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
