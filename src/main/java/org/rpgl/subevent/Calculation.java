package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.math.Die;

import java.util.Objects;

/**
 * This abstract subevent is dedicated to performing a calculation. Subevents which extend this class can add bonuses to
 * a value, set the calculated value to a particular value, and retrieve the final calculated value.
 * <br>
 * <br>
 * Source: the RPGLObject whose base armor class is being calculated
 * <br>
 * Target: should be the same as the source
 *
 * @author Calvin Withun
 */
public abstract class Calculation extends Subevent {

    public Calculation(String subeventId) {
        super(subeventId);
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        if (this.json.getJsonObject("base") == null) {
            this.json.putJsonObject("base", new JsonObject() {{
                this.putString("name", "DEFAULT");
                this.putString("effect", null);
                this.putInteger("value", 0);
            }});
        }
        if (this.json.getJsonArray("bonuses") == null) {
            this.json.putJsonArray("bonuses", new JsonArray());
        }
        if (this.json.getJsonObject("minimum") == null) {
            this.json.putJsonObject("minimum", new JsonObject() {{
                this.putString("name", "DEFAULT");
                this.putString("effect", null);
                this.putInteger("value", Integer.MIN_VALUE);
            }});
        }
    }

    public JsonObject getBase() {
        return this.json.getJsonObject("base");
    }

    public void setBase(JsonObject baseJson) {
        this.json.putJsonObject("base", baseJson);
    }

    public JsonArray getBonuses() {
        return this.json.getJsonArray("bonuses");
    }

    public void addBonus(JsonObject bonusJson) {
        this.json.getJsonArray("bonuses").addJsonObject(bonusJson);
    }

    public JsonObject getMinimum() {
        return this.json.getJsonObject("minimum");
    }

    public void setMinimum(JsonObject minimumJson) {
        if (minimumJson.getInteger("value") > this.json.getJsonObject("minimum").getInteger("value")) {
            this.json.putJsonObject("minimum", minimumJson);
        }
    }

    public int get() {
        int total = this.getBase().getInteger("value");
        total += this.getBonus();
        int minimum = this.getMinimum().getInteger("value");
        if (total < minimum) {
            total = minimum;
        }
        return total;
    }

    /**
     * Returns the final bonus to be applied to the Calculation. This method is intended to be used after any bonus dice
     * have been rolled. If this method is called before the bonus dice have been rolled, the dice are not included in
     * the bonus calculation.
     *
     * @return the bonus to be applied to the Calculation.
     */
    public int getBonus() {
        int bonus = 0;
        JsonArray bonuses = this.getBonuses();
        for (int i = 0; i < bonuses.size(); i++) {
            JsonObject bonusJson = bonuses.getJsonObject(i);
            bonus += bonusJson.getInteger("bonus");
            JsonArray dice = bonusJson.getJsonArray("dice");
            for (int j = 0; j < dice.size(); j++) {
                // if dice have not been rolled, treat them as having rolled 0
                bonus += Objects.requireNonNullElse(dice.getJsonObject(i).getInteger("roll"), 0);
            }
        }
        return bonus;
    }

    void rollBonusDice() {
        JsonArray bonuses = this.getBonuses();
        for (int i = 0; i < bonuses.size(); i++) {
            JsonObject bonus = bonuses.getJsonObject(i);
            JsonArray dice = bonus.getJsonArray("dice");
            for (int j = 0; j < dice.size(); j++) {
                Die.roll(dice.getJsonObject(j));
            }
        }
    }

}
