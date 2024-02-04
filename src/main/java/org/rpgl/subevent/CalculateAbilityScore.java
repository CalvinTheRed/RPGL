package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

/**
 * This Subevent is dedicated to rolling all dice which will be copied to all targets of a damaging RPGLEvent. This
 * Subevent is typically only created within a SavingThrow Subevent.
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
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new CalculateAbilityScore();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public CalculateAbilityScore invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (CalculateAbilityScore) super.invoke(context, originPoint);
    }

    @Override
    public CalculateAbilityScore joinSubeventData(JsonObject other) {
        return (CalculateAbilityScore) super.joinSubeventData(other);
    }

    @Override
    public CalculateAbilityScore prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint).setBase(getSource().getAbilityScores().getInteger(getAbility(context)));
        return this;
    }

    @Override
    public CalculateAbilityScore run(RPGLContext context, JsonArray originPoint) throws Exception {
        return this;
    }

    @Override
    public CalculateAbilityScore setOriginItem(String originItem) {
        return (CalculateAbilityScore) super.setOriginItem(originItem);
    }

    @Override
    public CalculateAbilityScore setSource(RPGLObject source) {
        return (CalculateAbilityScore) super.setSource(source);
    }

    @Override
    public CalculateAbilityScore setTarget(RPGLObject target) {
        return (CalculateAbilityScore) super.setTarget(target);
    }

    @Override
    public String getAbility(RPGLContext context) {
        return this.json.getString("ability");
    }

}
