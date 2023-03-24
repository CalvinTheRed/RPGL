package org.rpgl.subevent;

import org.rpgl.json.JsonArray;
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
     * This method returns the sum of all bonuses and dice being delivered to target as temporary hit points.
     *
     * @return an integer representing a quantity of temporary hit points
     */
    public int getTemporaryHitPoints() {
        JsonArray temporaryHitPointsArray = this.json.getJsonArray("temporary_hit_points");
        int temporaryHitPoints = 0;
        for (int i = 0; i < temporaryHitPointsArray.size(); i++) {
            JsonObject temporaryHitPointsJson = temporaryHitPointsArray.getJsonObject(i);
            temporaryHitPoints += temporaryHitPointsJson.getInteger("bonus");
            JsonArray dice = temporaryHitPointsJson.getJsonArray("dice");
            for (int j = 0; j < dice.size(); j++) {
                JsonObject die = dice.getJsonObject(j);
                temporaryHitPoints += die.getInteger("roll");
            }
        }
        return temporaryHitPoints;
    }

}
