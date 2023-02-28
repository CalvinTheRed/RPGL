package org.rpgl.subevent;

import org.rpgl.json.JsonObject;

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

    // TODO can this class just be a special tag version of CalculateArmorClass?

    @Override
    public Subevent clone() {
        Subevent clone = new CalculateEffectiveArmorClass();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new CalculateEffectiveArmorClass();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

}
