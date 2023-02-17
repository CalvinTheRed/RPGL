package org.rpgl.subevent;

import org.rpgl.json.JsonObject;

/**
 * This Subevent is dedicated to collecting dice and bonuses for a damage roll which will be copied to all targets of
 * a damaging RPGLEvent. This Subevent does not account for any damage dealt to only select targets.
 * <br>
 * <br>
 * Source: an RPGLObject invoking an RPGLEvent which deals damage
 * <br>
 * Target: an RPGLObject being targeted by the damaging RPGLEvent
 *
 * @author Calvin Withun
 */
public class BaseDamageCollection extends DamageCollection {

    public BaseDamageCollection() {
        super("base_damage_collection");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new BaseDamageCollection();
        clone.joinSubeventData(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new BaseDamageCollection();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

}
