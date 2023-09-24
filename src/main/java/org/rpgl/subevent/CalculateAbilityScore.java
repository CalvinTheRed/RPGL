package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonObject;

import java.util.List;

/**
 * This Subevent is dedicated to calculating an ability score.
 * <br>
 * <br>
 * Source: the RPGLObject whose ability score is being calculated
 * <br>
 * Target: should be the same as the source
 *
 * @author Calvin Withun
 */
public class CalculateAbilityScore extends Calculation implements AbilitySubevent {

    public CalculateAbilityScore() {
        super("calculate_ability_score");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new CalculateAbilityScore();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new CalculateAbilityScore();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context, List<RPGLResource> resources) throws Exception {
        super.prepare(context, resources);
        this.setBase(getSource().getAbilityScores().getInteger(getAbility(context)));
    }

    @Override
    public String getAbility(RPGLContext context) {
        return this.json.getString("ability");
    }

}
