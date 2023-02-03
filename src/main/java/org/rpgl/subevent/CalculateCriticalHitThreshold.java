package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
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
        clone.joinSubeventData(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new CalculateCriticalHitThreshold();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        this.subeventJson.putInteger("base", 20);
    }

}
