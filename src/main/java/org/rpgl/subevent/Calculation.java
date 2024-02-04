package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLObject;
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
    public Calculation prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        this.prepareBase(context);
        this.prepareBonuses(context);
        this.prepareMinimum(context);
        return this;
    }

    /**
     * This helper method scales a given value according to a provided factor.
     *
     * @param value the value to be scaled
     * @param scaleJson a JSON object indicating the factor by which the value will be scaled, in the form of <code>
     *                  { "numerator": int, "denominator": int, "round_up": boolean }</code>
     * @return the scaled value
     */
    public static int scale(int value, JsonObject scaleJson) {
        return Objects.requireNonNullElse(scaleJson.getBoolean("round_up"), false)
                ? (int) Math.ceil((double) value
                * (double) Objects.requireNonNullElse(scaleJson.getInteger("numerator"), 1)
                / (double) Objects.requireNonNullElse(scaleJson.getInteger("denominator"), 2))
                : value
                * Objects.requireNonNullElse(scaleJson.getInteger("numerator"), 1)
                / Objects.requireNonNullElse(scaleJson.getInteger("denominator"), 2);
    }

    /**
     * This helper method processes JSON bonus data and translates it to a numerical value.
     *
     * @param effect the RPGLEffect applying this bonus
     * @param subevent the Calculation receiving this bonus
     * @param formulaData the bonus formula data
     * @param context the context in which this bonus is being applied
     * @return a JsonObject representing the evaluated bonus
     *
     * @throws Exception if an exception occurs
     */
    public static JsonObject processBonusJson(RPGLEffect effect, Subevent subevent, JsonObject formulaData, RPGLContext context) throws Exception {
        /*[
            {
                "formula": "range",
                "bonus": #,
                "dice": [
                    { "count": #, "size": #, "determined": [ # ] },
                    ...
                ],
                "scale": {
                    "numerator": #,
                    "denominator": #,
                    "round_up": t/f
                }
            },{
                "formula": "modifier",
                "ability": "dex",
                "object": {
                    "from": "...",
                    "object": "...",
                    "as_origin": t/f
                },
                "scale": {
                    "numerator": #,
                    "denominator": #,
                    "round_up": t/f
                }
            },{
                "formula": "ability",
                "ability": "dex",
                "object": {
                    "from": "...",
                    "object": "...",
                    "as_origin": t/f
                },
                "scale": {
                    "numerator": #,
                    "denominator": #,
                    "round_up": t/f
                }
            },{
                "formula": "proficiency",
                "object": {
                    "from": "...",
                    "object": "...",
                    "as_origin": t/f
                },
                "scale": {
                    "numerator": #,
                    "denominator": #,
                    "round_up": t/f
                }
            },{
                "formula": "level",
                "class": "...",
                "object": {
                    "from": "...",
                    "object": "...",
                    "as_origin": t/f
                },
                "scale": {
                    "numerator": #,
                    "denominator": #,
                    "round_up": t/f
                }
            }
        ]*/
        return switch (formulaData.getString("formula")) {
            case "range" -> new JsonObject() {{
                this.putInteger("bonus", Objects.requireNonNullElse(formulaData.getInteger("bonus"), 0));
                this.putJsonArray("dice", Objects.requireNonNullElse(Die.unpack(formulaData.getJsonArray("dice")), new JsonArray()));
                this.putJsonObject("scale", Objects.requireNonNullElse(formulaData.getJsonObject("scale"), new JsonObject() {{
                    this.putInteger("numerator", 1);
                    this.putInteger("denominator", 1);
                    this.putBoolean("round_up", false);
                }}));
            }};
            case "modifier" -> new JsonObject() {{
                RPGLObject object = RPGLEffect.getObject(effect, subevent, formulaData.getJsonObject("object"));
                this.putInteger("bonus", object.getAbilityModifierFromAbilityName(formulaData.getString("ability"), context));
                this.putJsonArray("dice", new JsonArray());
                this.putJsonObject("scale", Objects.requireNonNullElse(formulaData.getJsonObject("scale"), new JsonObject() {{
                    this.putInteger("numerator", 1);
                    this.putInteger("denominator", 1);
                    this.putBoolean("round_up", false);
                }}));
            }};
            case "ability" -> new JsonObject() {{
                RPGLObject object = RPGLEffect.getObject(effect, subevent, formulaData.getJsonObject("object"));
                this.putInteger("bonus", object.getAbilityScoreFromAbilityName(formulaData.getString("ability"), context));
                this.putJsonArray("dice", new JsonArray());
                this.putJsonObject("scale", Objects.requireNonNullElse(formulaData.getJsonObject("scale"), new JsonObject() {{
                    this.putInteger("numerator", 1);
                    this.putInteger("denominator", 1);
                    this.putBoolean("round_up", false);
                }}));
            }};
            case "proficiency" -> new JsonObject() {{
                RPGLObject object = RPGLEffect.getObject(effect, subevent, formulaData.getJsonObject("object"));
                this.putInteger("bonus", object.getEffectiveProficiencyBonus(context));
                this.putJsonArray("dice", new JsonArray());
                this.putJsonObject("scale", Objects.requireNonNullElse(formulaData.getJsonObject("scale"), new JsonObject() {{
                    this.putInteger("numerator", 1);
                    this.putInteger("denominator", 1);
                    this.putBoolean("round_up", false);
                }}));
            }};
            case "level" -> new JsonObject() {{
                RPGLObject object = RPGLEffect.getObject(effect, subevent, formulaData.getJsonObject("object"));
                String classId = formulaData.getString("class");
                if (classId == null) {
                    this.putInteger("bonus", object.getLevel());
                } else {
                    this.putInteger("bonus", object.getLevel(classId));
                }
                this.putJsonArray("dice", new JsonArray());
                this.putJsonObject("scale", Objects.requireNonNullElse(formulaData.getJsonObject("scale"), new JsonObject() {{
                    this.putInteger("numerator", 1);
                    this.putInteger("denominator", 1);
                    this.putBoolean("round_up", false);
                }}));
            }};
            default -> new JsonObject() {{
                // TODO log a warning here concerning an unexpected formula value
                this.putInteger("bonus", 0);
                this.putJsonArray("dice", new JsonArray());
                this.putJsonObject("scale", Objects.requireNonNullElse(formulaData.getJsonObject("scale"), new JsonObject() {{
                    this.putInteger("numerator", 1);
                    this.putInteger("denominator", 1);
                    this.putBoolean("round_up", false);
                }}));
            }};
        };
    }

    /**
     * This helper method processes JSON set data and translates it to a numerical value.
     *
     * @param effect the RPGLEffect setting a value
     * @param subevent the Calculation having a value set
     * @param formulaData the formula data
     * @param context the context in which the value is being set
     * @return a JsonObject representing the evaluated value to be set
     *
     * @throws Exception if an exception occurs
     */
    public static int processSetJson(RPGLEffect effect, Subevent subevent, JsonObject formulaData, RPGLContext context) throws Exception {
        /*[
            {
                "formula": "number",
                "number": #,
                "scale": {
                    "numerator": #,
                    "denominator": #,
                    "round_up": t/f
                }
            },{
                "formula": "modifier",
                "ability": "dex",
                "object": {
                    "from": "...",
                    "object": "...",
                    "as_origin": t/f
                },
                "scale": {
                    "numerator": #,
                    "denominator": #,
                    "round_up": t/f
                }
            },{
                "formula": "ability",
                "ability": "dex",
                "object": {
                    "from": "...",
                    "object": "...",
                    "as_origin": t/f
                },
                "scale": {
                    "numerator": #,
                    "denominator": #,
                    "round_up": t/f
                }
            },{
                "formula": "proficiency",
                "half": boolean,
                "object": {
                    "from": "...",
                    "object": "...",
                    "as_origin": t/f
                },
                "scale": {
                    "numerator": #,
                    "denominator": #,
                    "round_up": t/f
                }
            },{
                "formula": "level",
                "class": "...",
                "object": {
                    "from": "...",
                    "object": "...",
                    "as_origin": t/f
                },
                "scale": {
                    "numerator": #,
                    "denominator": #,
                    "round_up": t/f
                }
            }
        ]*/
        // TODO can these operate with scales?
        return switch (formulaData.getString("formula")) {
            case "number" -> formulaData.getInteger("number");
            case "modifier" -> RPGLEffect.getObject(effect, subevent, formulaData.getJsonObject("object"))
                    .getAbilityModifierFromAbilityName(formulaData.getString("ability"), context);
            case "ability" -> RPGLEffect.getObject(effect, subevent, formulaData.getJsonObject("object"))
                    .getAbilityScoreFromAbilityName(formulaData.getString("ability"), context);
            case "proficiency" -> RPGLEffect.getObject(effect, subevent, formulaData.getJsonObject("object"))
                    .getEffectiveProficiencyBonus(context);
            case "level" -> formulaData.getString("class") == null
                    ? RPGLEffect.getObject(effect, subevent, formulaData.getJsonObject("object")).getLevel()
                    : RPGLEffect.getObject(effect, subevent, formulaData.getJsonObject("object")).getLevel(formulaData.getString("class"));
            default -> 0; // TODO log a warning here concerning an unexpected formula value
        };
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
            effect.setSource(super.getSource());
            effect.setTarget(super.getSource());
            this.setBase(processSetJson(effect, this, baseJson, context));
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
            effect.setSource(super.getSource());
            effect.setTarget(super.getSource());
            for (int i = 0; i < bonuses.size(); i++) {
                JsonObject bonus = bonuses.getJsonObject(i);
                this.addBonus(Calculation.processBonusJson(effect, this, bonus, context));
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
            effect.setSource(super.getSource());
            effect.setTarget(super.getSource());
            this.setMinimum(Calculation.processSetJson(effect, this, minimumJson, context));
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
        return Math.max(this.getBase() + this.getBonus(), this.getMinimum());
    }

    /**
     * Returns the final bonus to be applied to the Calculation. This method is intended to be used after any bonus dice
     * have been rolled. If this method is called before the bonus dice have been rolled, the dice are rolled during the
     * calculation.
     *
     * @return the bonus to be applied to the Calculation.
     */
    public int getBonus() {
        int totalBonus = 0;
        JsonArray bonuses = this.getBonuses();
        for (int i = 0; i < bonuses.size(); i++) {
            JsonObject bonusJson = bonuses.getJsonObject(i);
            int bonus = bonusJson.getInteger("bonus");
            JsonArray dice = bonusJson.getJsonArray("dice");
            for (int j = 0; j < dice.size(); j++) {
                JsonObject die = dice.getJsonObject(j);
                bonus += Objects.requireNonNullElseGet(die.getInteger("roll"), () -> Die.roll(die));
            }
            totalBonus += scale(bonus, bonusJson.getJsonObject("scale"));
        }
        return totalBonus;
    }

}
