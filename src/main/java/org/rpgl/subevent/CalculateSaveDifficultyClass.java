package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

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
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new CalculateSaveDifficultyClass();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        super.setBase(new JsonObject() {{
            this.putInteger("value", 8);
        }});
        RPGLObject source = this.getSource();
        super.addBonus(new JsonObject() {{
            this.putInteger("bonus", source.getEffectiveProficiencyBonus(context));
            this.putJsonArray("dice", new JsonArray());
        }});
        super.addBonus(new JsonObject() {{
            this.putInteger("bonus", source.getAbilityModifierFromAbilityName(json.getString("difficulty_class_ability"), context));
            this.putJsonArray("dice", new JsonArray());
        }});
    }

}
