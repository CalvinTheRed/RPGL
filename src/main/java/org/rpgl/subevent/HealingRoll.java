package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.math.Die;

import java.util.ArrayList;

/**
 * This abstract Subevent is dedicated to rolling healing dice.
 * <br>
 * <br>
 * Source: an RPGLObject rolling healing
 * <br>
 * Target: an RPGLObject which will later receive the rolled healing
 *
 * @author Calvin Withun
 */
public class HealingRoll extends Subevent {

    public HealingRoll() {
        super("healing_roll");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new HealingRoll();
        clone.joinSubeventData(this.json);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new HealingRoll();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public HealingRoll invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (HealingRoll) super.invoke(context, originPoint);
    }

    @Override
    public HealingRoll joinSubeventData(JsonObject other) {
        return (HealingRoll) super.joinSubeventData(other);
    }

    @Override
    public HealingRoll prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        this.json.asMap().putIfAbsent("healing", new ArrayList<>());
        this.roll();
        return this;
    }

    @Override
    public HealingRoll run(RPGLContext context, JsonArray originPoint) throws Exception {
        return this;
    }

    @Override
    public HealingRoll setOriginItem(String originItem) {
        return (HealingRoll) super.setOriginItem(originItem);
    }

    @Override
    public HealingRoll setSource(RPGLObject source) {
        return (HealingRoll) super.setSource(source);
    }

    @Override
    public HealingRoll setTarget(RPGLObject target) {
        return (HealingRoll) super.setTarget(target);
    }

    /**
     * This method rolls all healing dice associated with the Subevent.
     */
    public void roll() {
        JsonArray healingArray = this.json.getJsonArray("healing");
        for (int i = 0; i < healingArray.size(); i++) {
            JsonObject healingJson = healingArray.getJsonObject(i);
            JsonArray dice = healingJson.getJsonArray("dice");
            for (int j = 0; j < dice.size(); j++) {
                JsonObject die = dice.getJsonObject(j);
                Die.roll(die);
            }
        }
    }

    /**
     * Re-rolls all healing dice which rolled matching or below the passed threshold.
     *
     * @param threshold the value which a die must roll at or below to be changed by this method
     * @return this HealingRoll
     */
    public HealingRoll rerollHealingDiceMatchingOrBelow(int threshold) {
        JsonArray healingArray = this.json.getJsonArray("healing");
        for (int i = 0; i < healingArray.size(); i++) {
            JsonObject healingJson = healingArray.getJsonObject(i);
            JsonArray dice = healingJson.getJsonArray("dice");
            for (int j = 0; j < dice.size(); j++) {
                JsonObject die = dice.getJsonObject(j);
                if (die.getInteger("roll") <= threshold) {
                    Die.roll(die);
                }
            }
        }
        return this;
    }

    /**
     * Sets the face value of all dice which rolled below or at the passed threshold.
     *
     * @param threshold the value which a die must roll at or below to be changed by this method
     * @param set the value to set for each die changed by this method
     * @return this HealingRoll
     */
    public HealingRoll setHealingDiceMatchingOrBelow(int threshold, int set) {
        JsonArray healingArray = this.json.getJsonArray("healing");
        for (int i = 0; i < healingArray.size(); i++) {
            JsonObject healingJson = healingArray.getJsonObject(i);
            JsonArray dice = healingJson.getJsonArray("dice");
            for (int j = 0; j < dice.size(); j++) {
                JsonObject die = dice.getJsonObject(j);
                if (die.getInteger("roll") <= threshold) {
                    die.putInteger("roll", set);
                }
            }
        }
        return this;
    }

    /**
     * Sets all healing dice to their maximum face value.
     *
     * @return this HealingRoll
     */
    public HealingRoll maximizeHealingDice() {
        JsonArray healingArray = this.json.getJsonArray("healing");
        for (int i = 0; i < healingArray.size(); i++) {
            JsonObject healingJson = healingArray.getJsonObject(i);
            JsonArray dice = healingJson.getJsonArray("dice");
            for (int j = 0; j < dice.size(); j++) {
                JsonObject die = dice.getJsonObject(j);
                die.putInteger("roll", die.getInteger("size"));
            }
        }
        return this;
    }

    /**
     * Returns the rolled healing collection associated with this Subevent.
     *
     * @return a JsonArray storing rolled healing data
     */
    public JsonArray getHealing() {
        return this.json.getJsonArray("healing");
    }

}
