package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.math.Die;

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
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new HealingRoll();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        if (this.json.getJsonArray("dice") == null) {
            this.json.putJsonArray("dice", new JsonArray());
        }
        if (this.json.getInteger("bonus") == null) {
            this.json.putInteger("bonus", 0);
        }
        this.roll();
    }

    /**
     * This method rolls all haling associated with the Subevent.
     */
    public void roll() {
        JsonArray healingDice = this.json.getJsonArray("dice");
        for (int i = 0; i < healingDice.size(); i++) {
            JsonObject healingDie = healingDice.getJsonObject(i);
            healingDie.putInteger("roll", Die.roll(healingDie.getInteger("size"), healingDie.getJsonArray("determined").asList()));
        }
    }

    /**
     * Re-rolls all healing dice which rolled matching or below the passed threshold.
     *
     * @param threshold the value which a die must roll at or below to be changed by this method
     */
    public void rerollHealingDiceMatchingOrBelow(int threshold) {
        JsonArray healingDice = this.json.getJsonArray("dice");
        for (int i = 0; i < healingDice.size(); i++) {
            JsonObject healingDie = healingDice.getJsonObject(i);
            if (healingDie.getInteger("roll") <= threshold) {
                healingDie.putInteger("roll", Die.roll(healingDie.getInteger("size"), healingDie.getJsonArray("determined").asList()));
            }
        }
    }

    /**
     * Sets the face value of all dice which rolled below or at the passed threshold.
     *
     * @param threshold the value which a die must roll at or below to be changed by this method
     * @param set       the value to set for each die changed by this method
     */
    public void setHealingDiceMatchingOrBelow(int threshold, int set) {
        JsonArray healingDice = this.json.getJsonArray("dice");
        for (int i = 0; i < healingDice.size(); i++) {
            JsonObject healingDie = healingDice.getJsonObject(i);
            if (healingDie.getInteger("roll") <= threshold) {
                healingDie.putInteger("roll", set);
            }
        }
    }

    /**
     * Sets all healing dice to their maximum face value.
     */
    public void maximizeHealingDice() {
        JsonArray healingDice = this.json.getJsonArray("dice");
        for (int i = 0; i < healingDice.size(); i++) {
            JsonObject healingDie = healingDice.getJsonObject(i);
            healingDie.putInteger("roll", healingDie.getInteger("size"));
        }
    }

    /**
     * Returns the healing data provided to this Subevent after being rolled.
     *
     * @return a JsonObject storing rolled healing dice and a bonus
     */
    public JsonObject getHealing() {
        return new JsonObject() {{
            this.putJsonArray("dice", json.getJsonArray("dice").deepClone());
            this.putInteger("bonus", json.getInteger("bonus"));
        }};
    }

}
