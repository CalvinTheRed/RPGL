package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLContext;

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
