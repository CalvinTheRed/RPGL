package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.math.Die;

import java.util.ArrayList;
import java.util.List;

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
    public void prepare(RPGLContext context, List<RPGLResource> resources) throws Exception {
        super.prepare(context, resources);
        this.json.asMap().putIfAbsent("healing", new ArrayList<>());
        this.roll();
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
     */
    public void rerollHealingDiceMatchingOrBelow(int threshold) {
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
    }

    /**
     * Sets the face value of all dice which rolled below or at the passed threshold.
     *
     * @param threshold the value which a die must roll at or below to be changed by this method
     * @param set the value to set for each die changed by this method
     */
    public void setHealingDiceMatchingOrBelow(int threshold, int set) {
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
    }

    /**
     * Sets all healing dice to their maximum face value.
     */
    public void maximizeHealingDice() {
        JsonArray healingArray = this.json.getJsonArray("healing");
        for (int i = 0; i < healingArray.size(); i++) {
            JsonObject healingJson = healingArray.getJsonObject(i);
            JsonArray dice = healingJson.getJsonArray("dice");
            for (int j = 0; j < dice.size(); j++) {
                JsonObject die = dice.getJsonObject(j);
                die.putInteger("roll", die.getInteger("size"));
            }
        }
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
