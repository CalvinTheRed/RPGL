package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLObject;

public class CalculateAbilityScore extends AttributeCalculation {

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
    public void prepare(RPGLObject source) throws Exception {
        super.prepare(source);
        String abilityScoreName = (String) this.subeventJson.get("ability");
        Long rawAbilityScore = (Long) source.seek("ability_scores." + abilityScoreName);
        this.subeventJson.put("raw", rawAbilityScore);
    }

}
