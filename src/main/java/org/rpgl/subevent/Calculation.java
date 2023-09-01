package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.function.AddBonus;
import org.rpgl.function.SetBase;
import org.rpgl.function.SetMinimum;
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
        this.prepareBase(context);
        this.prepareBonuses(context);
        this.prepareMinimum(context);
    }

    /**
     * This helper method interprets the base formula provided in the Subevent JSON, if it exists, and stores it as the
     * base of the Calculation.
     *
     * @param context the context in which the Calculation is being prepared
     *
     * @throws Exception if an exception occurs
     */
    void prepareBase(RPGLContext context) throws Exception {
        JsonObject baseJson = this.json.getJsonObject("base");
        this.setBase(0);
        if (baseJson != null) {
            RPGLEffect effect = new RPGLEffect();
            effect.setSource(this.getSource());
            effect.setTarget(this.getSource());
            this.setBase(SetBase.processJson(effect, this, baseJson, context));
        }
    }

    /**
     * This helper method interprets the bonus formulas provided in the Subevent JSON, if they exist, and stores it as
     * the bonuses of the Calculation.
     *
     * @param context the context in which the Calculation is being prepared
     *
     * @throws Exception if an exception occurs
     */
    void prepareBonuses(RPGLContext context) throws Exception {
        JsonArray bonuses = this.json.removeJsonArray("bonuses");
        this.json.putJsonArray("bonuses", new JsonArray());
        if (bonuses != null) {
            RPGLEffect effect = new RPGLEffect();
            effect.setSource(this.getSource());
            effect.setTarget(this.getSource());
            for (int i = 0; i < bonuses.size(); i++) {
                JsonObject bonus = bonuses.getJsonObject(i);
                this.addBonus(AddBonus.processJson(effect, this, bonus, context));
            }
        }
    }

    /**
     * This helper method interprets the minimum formula provided in the Subevent JSON, if it exists, and stores it as
     * the minimum of the Calculation.
     *
     * @param context the context in which the Calculation is being prepared
     *
     * @throws Exception if an exception occurs
     */
    void prepareMinimum(RPGLContext context) throws Exception {
        JsonObject minimumJson = this.json.removeJsonObject("minimum");
        this.setMinimum(Integer.MIN_VALUE);
        if (minimumJson != null) {
            RPGLEffect effect = new RPGLEffect();
            effect.setSource(this.getSource());
            effect.setTarget(this.getSource());
            this.setMinimum(SetMinimum.processJson(effect, this, minimumJson, context));
        }
    }

    /**
     * Returns the base of the Calculation. Call getInteger("value") to get the base value.
     *
     * @return a JsonObject storing the base calculation value.
     */
    public int getBase() {
        return this.json.getJsonObject("base").getInteger("value");
    }

    /**
     * Sets the base of the Calculation. This always overrides the previous base value for the Calculation.
     *
     * @param baseValue the base value for the Calculation
     */
    public void setBase(int baseValue) {
        JsonObject baseJson = this.json.getJsonObject("base");
        if (baseJson == null) {
            this.json.putJsonObject("base", new JsonObject() {{
                this.putInteger("value", baseValue);
            }});
        } else {
            baseJson.putInteger("value", baseValue);
        }
    }

    /**
     * Returns the list of bonuses applied to the Calculation.
     *
     * @return a JsonArray of bonuses
     */
    public JsonArray getBonuses() {
        return this.json.getJsonArray("bonuses");
    }

    /**
     * Adds a bonus to the Calculation.
     *
     * @param bonusJson a bonus to be added to the Calculation
     */
    public void addBonus(JsonObject bonusJson) {
        this.getBonuses().addJsonObject(bonusJson);
    }

    /**
     * Returns the minimum of the Calculation. Call getInteger("value") to get the base value.
     *
     * @return a JsonObject storing the minimum calculation value.
     */
    public int getMinimum() {
        JsonObject minimumJson = this.json.getJsonObject("minimum");
        if (minimumJson == null) {
            this.json.putJsonObject("minimum", new JsonObject() {{
                this.putInteger("value", 0);
            }});
            return 0;
        } else {
            return Objects.requireNonNullElse(this.json.getJsonObject("minimum").getInteger("value"), 0);
        }
    }

    /**
     * Sets the minimum of the Calculation. If the provided minimum is lower than the current minimum, this method will
     * not do anything to modify the Subevent.
     *
     * @param minimum the minimum value for the Calculation
     */
    public void setMinimum(int minimum) {
        int currentMinimum = this.getMinimum();
        if (minimum > currentMinimum) {
            this.json.getJsonObject("minimum").putInteger("value", minimum);
        }
    }

    /**
     * Returns the final value of the Calculation. Any unrolled dice in the Calculation bonuses are rolled by calling
     * this method.
     *
     * @return the final value of the Calculation
     */
    public int get() {
        int total = this.getBase();
        total += this.getBonus();
        int minimum = this.getMinimum();
        if (total < minimum) {
            total = minimum;
        }
        return total;
    }

    /**
     * Returns the final bonus to be applied to the Calculation. This method is intended to be used after any bonus dice
     * have been rolled. If this method is called before the bonus dice have been rolled, the dice are rolled during the
     * calculation.
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
                JsonObject die = dice.getJsonObject(i);
                bonus += Objects.requireNonNullElseGet(die.getInteger("roll"), () -> Die.roll(die));
            }
        }
        return bonus;
    }

}
