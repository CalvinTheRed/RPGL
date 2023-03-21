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

    void prepareBase(RPGLContext context) throws Exception {
        JsonObject baseJson = this.json.removeJsonObject("base");
        this.setBase(new JsonObject() {{
            this.putInteger("value", 0);
        }});
        if (baseJson != null) {
            RPGLEffect effect = new RPGLEffect();
            effect.setSource(this.getSource());
            effect.setTarget(this.getSource());
            this.setBase(SetBase.processJson(effect, this, baseJson, context));
        }
    }

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

    void prepareMinimum(RPGLContext context) throws Exception {
        JsonObject minimumJson = this.json.removeJsonObject("minimum");
        this.setMinimum(new JsonObject() {{
            this.putInteger("value", Integer.MIN_VALUE);
        }});
        if (minimumJson != null) {
            RPGLEffect effect = new RPGLEffect();
            effect.setSource(this.getSource());
            effect.setTarget(this.getSource());
            this.setMinimum(SetMinimum.processJson(effect, this, minimumJson, context));
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
        JsonObject currentMinimum = this.getMinimum();
        if (currentMinimum == null || minimumJson.getInteger("value") > currentMinimum.getInteger("value")) {
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
