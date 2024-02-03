package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

/**
 * This subevent is dedicated to calculating the threshold which must be met on the d20 of an attack to count as a
 * critical hit. Typically, an attack is a critical hit if the d20 rolls a 20.
 * <br>
 * <br>
 * Source: the RPGLObject whose critical hit threshold is being calculated
 * <br>
 * Target: should be the same as the source
 *
 * @author Calvin Withun
 */
public class CalculateCriticalHitThreshold extends Calculation {

    public CalculateCriticalHitThreshold() {
        super("calculate_critical_hit_threshold");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new CalculateCriticalHitThreshold();
        clone.joinSubeventData(this.json);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new CalculateCriticalHitThreshold();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public CalculateCriticalHitThreshold invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (CalculateCriticalHitThreshold) super.invoke(context, originPoint);
    }

    @Override
    public CalculateCriticalHitThreshold joinSubeventData(JsonObject other) {
        return (CalculateCriticalHitThreshold) super.joinSubeventData(other);
    }

    @Override
    public CalculateCriticalHitThreshold prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint).setBase(20);
        return this;
    }

    @Override
    public CalculateCriticalHitThreshold run(RPGLContext context, JsonArray originPoint) throws Exception {
        return this;
    }

    @Override
    public CalculateCriticalHitThreshold setOriginItem(String originItem) {
        return (CalculateCriticalHitThreshold) super.setOriginItem(originItem);
    }

    @Override
    public CalculateCriticalHitThreshold setSource(RPGLObject source) {
        return (CalculateCriticalHitThreshold) super.setSource(source);
    }

    @Override
    public CalculateCriticalHitThreshold setTarget(RPGLObject target) {
        return (CalculateCriticalHitThreshold) super.setTarget(target);
    }

}
