package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLContext;

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
        clone.joinSubeventJson(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new CalculateCriticalHitThreshold();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        this.subeventJson.put("base", 20L);
    }

}
