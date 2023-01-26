package org.rpgl.subevent;

import org.jsonutils.JsonObject;

/**
 * This subevent is dedicated to calculating the armor class against which attack rolls are made for the purposes of
 * determining whether an attack hits or misses. This value accounts for reactive increases in armor class made after
 * the attack roll is determined.
 * <br>
 * <br>
 * Source: the RPGLObject whose effective armor class is being calculated
 * <br>
 * Target: should be the same as the source
 *
 * @author Calvin Withun
 */
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

}
