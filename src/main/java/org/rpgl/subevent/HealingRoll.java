package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.math.Die;

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

    public void rerollHealingDiceLessThanOrEqualTo(int threshold) {
        JsonArray healingDice = this.json.getJsonArray("dice");
        for (int i = 0; i < healingDice.size(); i++) {
            JsonObject healingDie = healingDice.getJsonObject(i);
            if (healingDie.getInteger("roll") <= threshold) {
                healingDie.putInteger("roll", Die.roll(healingDie.getInteger("size"), healingDie.getJsonArray("determined").asList()));
            }
        }
    }

    public void setHealingDiceLessThanOrEqualTo(int threshold, int set) {
        JsonArray healingDice = this.json.getJsonArray("dice");
        for (int i = 0; i < healingDice.size(); i++) {
            JsonObject healingDie = healingDice.getJsonObject(i);
            if (healingDie.getInteger("roll") <= threshold) {
                healingDie.putInteger("roll", set);
            }
        }
    }

    public void maximizeHealingDice() {
        JsonArray healingDice = this.json.getJsonArray("dice");
        for (int i = 0; i < healingDice.size(); i++) {
            JsonObject healingDie = healingDice.getJsonObject(i);
            healingDie.putInteger("roll", healingDie.getInteger("size"));
        }
    }

    public JsonObject getHealing() {
        return new JsonObject() {{
            this.putJsonArray("dice", json.getJsonArray("dice").deepClone());
            this.putInteger("bonus", json.getInteger("bonus"));
        }};
    }

}
