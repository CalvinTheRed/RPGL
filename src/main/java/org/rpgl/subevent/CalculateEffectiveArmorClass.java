package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
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
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new CalculateEffectiveArmorClass();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public CalculateEffectiveArmorClass invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (CalculateEffectiveArmorClass) super.invoke(context, originPoint);
    }

    @Override
    public CalculateEffectiveArmorClass joinSubeventData(JsonObject other) {
        return (CalculateEffectiveArmorClass) super.joinSubeventData(other);
    }

    @Override
    public CalculateEffectiveArmorClass prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        return (CalculateEffectiveArmorClass) super.prepare(context, originPoint);
    }

    @Override
    public CalculateEffectiveArmorClass run(RPGLContext context, JsonArray originPoint) throws Exception {
        return this;
    }

    @Override
    public CalculateEffectiveArmorClass setOriginItem(String originItem) {
        return (CalculateEffectiveArmorClass) super.setOriginItem(originItem);
    }

    @Override
    public CalculateEffectiveArmorClass setSource(RPGLObject source) {
        return (CalculateEffectiveArmorClass) super.setSource(source);
    }

    @Override
    public CalculateEffectiveArmorClass setTarget(RPGLObject target) {
        return (CalculateEffectiveArmorClass) super.setTarget(target);
    }

}
