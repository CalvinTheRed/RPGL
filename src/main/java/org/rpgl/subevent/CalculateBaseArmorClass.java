package org.rpgl.subevent;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLContext;

public class CalculateBaseArmorClass extends Calculation {

    public CalculateBaseArmorClass() {
        super("calculate_base_armor_class");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new CalculateBaseArmorClass();
        clone.joinSubeventJson(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new CalculateBaseArmorClass();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        Long baseArmorClass = 10L + this.getSource().getAbilityModifier(context, "dex");
        this.subeventJson.put("base", baseArmorClass);
    }

}
