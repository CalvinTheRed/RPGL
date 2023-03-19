package org.rpgl.subevent;

import org.rpgl.json.JsonObject;

/**
 * This Subevent is dedicated to delivering a quantity of temporary hit points to an RPGLObject.
 * TODO is this Subevent really necessary?
 * <br>
 * <br>
 * Source: an RPGLObject giving temporary hit points
 * <br>
 * Target: an RPGLObject receiving temporary hit points
 *
 * @author Calvin Withun
 */
public class TemporaryHitPointsDelivery extends Subevent {

    public TemporaryHitPointsDelivery() {
        super("temporary_hit_points_delivery");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new TemporaryHitPointsDelivery();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new TemporaryHitPointsDelivery();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    /**
     * This method returns the temporary hit points delivered to <code>target</code>.
     *
     * @return an integer representing a quantity of temporary hit points
     */
    public int getTemporaryHitPoints() {
        return this.json.getInteger("temporary_hit_points");
    }

}
