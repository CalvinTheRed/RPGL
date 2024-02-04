package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
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
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new CalculateProficiencyBonus();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public CalculateProficiencyBonus invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (CalculateProficiencyBonus) super.invoke(context, originPoint);
    }

    @Override
    public CalculateProficiencyBonus joinSubeventData(JsonObject other) {
        return (CalculateProficiencyBonus) super.joinSubeventData(other);
    }

    @Override
    public CalculateProficiencyBonus prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint).setBase(
                Objects.requireNonNullElse(
                    getSource().getProficiencyBonus(),
                    getSource().getProficiencyBonusByLevel()
                )
        );
        return this;
    }

    @Override
    public CalculateProficiencyBonus run(RPGLContext context, JsonArray originPoint) throws Exception {
        return this;
    }

    @Override
    public CalculateProficiencyBonus setOriginItem(String originItem) {
        return (CalculateProficiencyBonus) super.setOriginItem(originItem);
    }

    @Override
    public CalculateProficiencyBonus setSource(RPGLObject source) {
        return (CalculateProficiencyBonus) super.setSource(source);
    }

    @Override
    public CalculateProficiencyBonus setTarget(RPGLObject target) {
        return (CalculateProficiencyBonus) super.setTarget(target);
    }

}
