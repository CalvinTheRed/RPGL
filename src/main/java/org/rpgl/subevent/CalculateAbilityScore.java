package org.rpgl.subevent;

import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLObject;

public class CalculateAbilityScore extends Subevent {

    public CalculateAbilityScore() {
        super("calculate_ability_score");
    }

    @Override
    public Subevent clone() {
        return new CalculateAbilityScore();
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new CalculateAbilityScore();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLObject source) throws JsonFormatException {
        String abilityScoreName = (String) this.subeventJson.get("ability");
        Long rawAbilityScore = (Long) source.seek("ability_scores." + abilityScoreName);
        System.out.println(rawAbilityScore);
        this.subeventJson.put("raw", rawAbilityScore);
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

    public Long getAbilityScore() {
        Long bonus = (Long) this.subeventJson.get("bonus");
        Long set = (Long) this.subeventJson.get("set");
        if (set != null) {
            return set + bonus;
        }
        return (Long) this.subeventJson.get("raw") + bonus;
    }

}
