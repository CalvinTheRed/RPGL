package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.math.Die;
import org.rpgl.subevent.Calculation;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * This Function is dedicated to adding a bonus to Calculation Subevents.
 *
 * @author Calvin Withun
 */
public class AddBonus extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddBonus.class);

    public AddBonus() {
        super("add_bonus");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context) throws Exception {
        if (subevent instanceof Calculation calculation) {
            JsonArray bonusArray = functionJson.getJsonArray("bonus");
            for (int i = 0; i < bonusArray.size(); i++) {
                calculation.addBonus(processJson(effect, subevent, bonusArray.getJsonObject(i), context));
            }
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

    /**
     * This helper method processes bonus JSON data and translates bonus formula data to dice and bonus form.
     *
     * @param effect    the RPGLEffect applying this bonus
     * @param subevent  the Calculation receiving this bonus
     * @param bonusJson the bonus formula data
     * @param context   the context in which this bonus is being applied
     * @return a JsonObject representing the evaluated form of the provided bonus formula data
     *
     * @throws Exception if an exception occurs
     */
    public static JsonObject processJson(RPGLEffect effect, Subevent subevent, JsonObject bonusJson, RPGLContext context) throws Exception {
        /*[
            {
                "name": "...",
                "bonus_formula": "range",
                "bonus": #,
                "dice": [
                    { "count": #, "size": #, "determined": [ # ] },
                    ...
                ]
            },{
                "name": "...",
                "bonus_formula": "modifier",
                "ability": "dex",
                "object": "..."
            },{
                "name": "...",
                "bonus_formula": "ability",
                "ability": "dex",
                "object": "..."
            },{
                "name": "...",
                "bonus_formula": "proficiency",
                "half": boolean,
                "object": "..."
            },{
                "name": "...",
                "bonus_formula": "level", // TODO this feature not yet supported
                "class": "...",
                "object": "..."
            }
        ]*/
        return switch (bonusJson.getString("bonus_formula")) {
            case "range" -> new JsonObject() {{
                this.putInteger("bonus", Objects.requireNonNullElse(bonusJson.getInteger("bonus"), 0));
                this.putJsonArray("dice", Objects.requireNonNullElse(Die.unpack(bonusJson.getJsonArray("dice")), new JsonArray()));
            }};
            case "modifier" -> new JsonObject() {{
                RPGLObject object = RPGLEffect.getObject(effect, subevent, bonusJson.getJsonObject("object"));
                this.putInteger("bonus", object.getAbilityModifierFromAbilityName(bonusJson.getString("ability"), context));
                this.putJsonArray("dice", new JsonArray());
            }};
            case "ability" -> new JsonObject() {{
                RPGLObject object = RPGLEffect.getObject(effect, subevent, bonusJson.getJsonObject("object"));
                this.putInteger("bonus", object.getAbilityScoreFromAbilityName(bonusJson.getString("ability"), context));
                this.putJsonArray("dice", new JsonArray());
            }};
            case "proficiency" -> new JsonObject() {{
                RPGLObject object = RPGLEffect.getObject(effect, subevent, bonusJson.getJsonObject("object"));
                if (Objects.requireNonNullElse(bonusJson.getBoolean("half"), false)) {
                    this.putInteger("bonus", object.getEffectiveProficiencyBonus(context) / 2);
                } else {
                    this.putInteger("bonus", object.getEffectiveProficiencyBonus(context));
                }
                this.putJsonArray("dice", new JsonArray());
            }};
            default -> new JsonObject() {{
                // TODO log a warning here concerning an unexpected bonus_formula value
                this.putInteger("bonus", 0);
                this.putJsonArray("dice", new JsonArray());
            }};
        };
    }

}
