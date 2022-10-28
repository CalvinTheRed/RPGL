package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLObject;

public class CalculateArmorClass extends Calculation {

    public CalculateArmorClass() {
        super("calculate_armor_class");
    }

    @Override
    public Subevent clone() {
        return new CalculateArmorClass();
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new CalculateArmorClass();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLObject source) throws Exception {
        super.prepare(source);
        Long baseArmorClass = 10L + source.getAbilityModifier("dex");
        this.subeventJson.put("raw", baseArmorClass);
    }

}
