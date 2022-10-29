package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLContext;

public class CalculateAbilityScore extends Calculation {

    public CalculateAbilityScore() {
        super("calculate_ability_score");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new CalculateAbilityScore();
        clone.joinSubeventJson(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new CalculateAbilityScore();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        String abilityScoreName = (String) this.subeventJson.get("ability");
        Long rawAbilityScore = (Long) this.getSource().seek("ability_scores." + abilityScoreName);
        this.subeventJson.put("raw", rawAbilityScore);
    }

}
