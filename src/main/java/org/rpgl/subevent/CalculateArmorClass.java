package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLContext;

public class CalculateArmorClass extends Calculation {

    public CalculateArmorClass() {
        super("calculate_armor_class");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new CalculateArmorClass();
        clone.joinSubeventJson(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new CalculateArmorClass();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        Long baseArmorClass = 10L + this.getSource().getAbilityModifier(context, "dex");
        this.subeventJson.put("raw", baseArmorClass);
    }

}
