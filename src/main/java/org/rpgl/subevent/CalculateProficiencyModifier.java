package org.rpgl.subevent;

import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLObject;

public class CalculateProficiencyModifier extends Subevent {

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
    public void prepare(RPGLObject source) throws JsonFormatException {
        Long rawProficiencyModifier = (Long) source.seek("proficiency_bonus");
        this.subeventJson.put("raw", rawProficiencyModifier);
        this.subeventJson.put("bonus", 0L);
    }

    public void addBonus(long bonus) {
        this.subeventJson.put("bonus", bonus);
    }

    public void set(long value) {
        Long previousValue = (Long) this.subeventJson.get("set");
        if (previousValue == null || previousValue < value) {
            this.subeventJson.put("set", value);
        }
    }

    public long getProficiencyModifier() {
        Long bonus = (Long) this.subeventJson.get("bonus");
        Long set = (Long) this.subeventJson.get("set");
        if (set != null) {
            return set + bonus;
        }
        return (Long) this.subeventJson.get("raw") + bonus;
    }

}
