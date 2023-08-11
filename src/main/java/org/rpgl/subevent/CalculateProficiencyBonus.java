package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonObject;

import java.util.Objects;

/**
 * This subevent is dedicated to calculating the proficiency bonus of an RPGLObject.
 * <br>
 * <br>
 * Source: the RPGLObject whose proficiency bonus is being calculated
 * <br>
 * Target: should be the same as the source
 *
 * @author Calvin Withun
 */
public class CalculateProficiencyBonus extends Calculation {

    public CalculateProficiencyBonus() {
        super("calculate_proficiency_bonus");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new CalculateProficiencyBonus();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new CalculateProficiencyBonus();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        super.setBase(new JsonObject() {{
            Integer proficiencyBonus = getSource().getProficiencyBonus();
            this.putInteger("value", Objects.requireNonNullElseGet(
                    proficiencyBonus,
                    () -> getSource().getProficiencyBonusByLevel())
            );
        }});
    }

}
