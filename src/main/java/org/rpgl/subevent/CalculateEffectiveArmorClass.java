package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLContext;

public class CalculateEffectiveArmorClass extends Calculation {

    public CalculateEffectiveArmorClass() {
        super("calculate_effective_armor_class");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new CalculateEffectiveArmorClass();
        clone.joinSubeventJson(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new CalculateEffectiveArmorClass();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
    }

}
