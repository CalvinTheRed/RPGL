package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;

import java.util.Map;

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
public class CalculateProficiencyModifier extends Calculation {

    public CalculateProficiencyModifier() {
        super("calculate_proficiency_modifier");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new CalculateProficiencyModifier();
        clone.joinSubeventData(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(Map<String, Object> subeventDataMap) {
        Subevent clone = new CalculateProficiencyModifier();
        clone.joinSubeventData(subeventDataMap);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        this.subeventJson.put("base", this.getSource().seek("proficiency_bonus"));
    }

}
