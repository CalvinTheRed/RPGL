package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.math.Die;

import java.util.ArrayList;

/**
 * This abstract Subevent is dedicated to rolling temporary hit points dice.
 * <br>
 * <br>
 * Source: an RPGLObject rolling temporary hit point dice
 * <br>
 * Target: an RPGLObject which will later receive the rolled temporary hit points
 *
 * @author Calvin Withun
 */
public class TemporaryHitPointRoll extends Subevent {

    public TemporaryHitPointRoll() {
        super("temporary_hit_points_roll");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new TemporaryHitPointRoll();
        clone.joinSubeventData(this.json);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new TemporaryHitPointRoll();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public TemporaryHitPointRoll invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (TemporaryHitPointRoll) super.invoke(context, originPoint);
    }

    @Override
    public TemporaryHitPointRoll joinSubeventData(JsonObject other) {
        return (TemporaryHitPointRoll) super.joinSubeventData(other);
    }

    @Override
    public TemporaryHitPointRoll prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        this.json.asMap().putIfAbsent("temporary_hit_points", new ArrayList<>());
        this.roll();
        return this;
    }

    @Override
    public TemporaryHitPointRoll run(RPGLContext context, JsonArray originPoint) throws Exception {
        return this;
    }

    @Override
    public TemporaryHitPointRoll setOriginItem(String originItem) {
        return (TemporaryHitPointRoll) super.setOriginItem(originItem);
    }

    @Override
    public TemporaryHitPointRoll setSource(RPGLObject source) {
        return (TemporaryHitPointRoll) super.setSource(source);
    }

    @Override
    public TemporaryHitPointRoll setTarget(RPGLObject target) {
        return (TemporaryHitPointRoll) super.setTarget(target);
    }

    /**
     * This method rolls all temporary hit points dice associated with the Subevent.
     */
    public void roll() {
        JsonArray temporaryHitPointsArray = this.json.getJsonArray("temporary_hit_points");
        for (int i = 0; i < temporaryHitPointsArray.size(); i++) {
            JsonObject temporaryHitPointsJson = temporaryHitPointsArray.getJsonObject(i);
            JsonArray dice = temporaryHitPointsJson.getJsonArray("dice");
            for (int j = 0; j < dice.size(); j++) {
                JsonObject die = dice.getJsonObject(j);
                Die.roll(die);
            }
        }
    }

    /**
     * Re-rolls all temporary hit points dice which rolled matching or below the passed threshold.
     *
     * @param threshold the value which a die must roll at or below to be changed by this method
     */
    public void rerollTemporaryHitPointsDiceMatchingOrBelow(int threshold) {
        JsonArray temporaryHitPointsArray = this.json.getJsonArray("temporary_hit_points");
        for (int i = 0; i < temporaryHitPointsArray.size(); i++) {
            JsonObject temporaryHitPointsJson = temporaryHitPointsArray.getJsonObject(i);
            JsonArray dice = temporaryHitPointsJson.getJsonArray("dice");
            for (int j = 0; j < dice.size(); j++) {
                JsonObject die = dice.getJsonObject(j);
                if (die.getInteger("roll") <= threshold) {
                    Die.roll(die);
                }
            }
        }
    }

    /**
     * Sets the face value of all dice which rolled below or at the passed threshold.
     *
     * @param threshold the value which a die must roll at or below to be changed by this method
     * @param set the value to set for each die changed by this method
     */
    public void setTemporaryHitPointsDiceMatchingOrBelow(int threshold, int set) {
        JsonArray temporaryHitPointsArray = this.json.getJsonArray("temporary_hit_points");
        for (int i = 0; i < temporaryHitPointsArray.size(); i++) {
            JsonObject temporaryHitPointsJson = temporaryHitPointsArray.getJsonObject(i);
            JsonArray dice = temporaryHitPointsJson.getJsonArray("dice");
            for (int j = 0; j < dice.size(); j++) {
                JsonObject die = dice.getJsonObject(j);
                if (die.getInteger("roll") <= threshold) {
                    die.putInteger("roll", set);
                }
            }
        }
    }

    /**
     * Sets all temporary hit points dice to their maximum face value.
     */
    public void maximizeTemporaryHitPointsDice() {
        JsonArray temporaryHitPointsArray = this.json.getJsonArray("temporary_hit_points");
        for (int i = 0; i < temporaryHitPointsArray.size(); i++) {
            JsonObject temporaryHitPointsJson = temporaryHitPointsArray.getJsonObject(i);
            JsonArray dice = temporaryHitPointsJson.getJsonArray("dice");
            for (int j = 0; j < dice.size(); j++) {
                JsonObject die = dice.getJsonObject(j);
                die.putInteger("roll", die.getInteger("size"));
            }
        }
    }

    /**
     * Returns the temporary hit points data provided to this Subevent after being rolled.
     *
     * @return the total temporary hit points determined by this Subevent after rolling all involved dice
     */
    public JsonArray getTemporaryHitPoints() {
        return this.json.getJsonArray("temporary_hit_points");
    }

}
