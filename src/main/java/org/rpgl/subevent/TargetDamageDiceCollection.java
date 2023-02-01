package org.rpgl.subevent;

import java.util.Map;

/**
 * This Subevent is dedicated to collecting target-specific damage dice and damage bonuses for an attack roll or saving throw.
 * <br>
 * <br>
 * Source: an RPGLObject making an attack or forcing a saving throw
 * <br>
 * Target: an RPGLObject being attacked or making a saving throw
 *
 * @author Calvin Withun
 */
public class TargetDamageDiceCollection extends DamageDiceCollection {

    public TargetDamageDiceCollection() {
        super("target_damage_dice_collection");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new TargetDamageDiceCollection();
        clone.joinSubeventData(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(Map<String, Object> subeventDataMap) {
        Subevent clone = new TargetDamageDiceCollection();
        clone.joinSubeventData(subeventDataMap);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

}
