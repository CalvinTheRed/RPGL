package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Calculation;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * This Function is dedicated to assigning the base field of Calculation Subevents.
 *
 * @author Calvin Withun
 */
public class SetBase extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetBase.class);

    public SetBase() {
        super("set_base");
    }

    @Override
    public void execute(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context) throws Exception {
        super.verifyFunction(functionJson);
        if (subevent instanceof Calculation calculation) {
            calculation.setBase(processJson(effect, subevent, functionJson.getJsonObject("base"), context));
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

    /**
     * This helper method processes base JSON data and translates base formula data to numerical value form.
     *
     * @param effect   the RPGLEffect applying this base
     * @param subevent the Calculation receiving this base
     * @param baseJson the base formula data
     * @param context  the context in which this base is being applied
     * @return a JsonObject representing the evaluated form of the provided base formula data
     *
     * @throws Exception if an exception occurs
     */
    public static JsonObject processJson(RPGLEffect effect, Subevent subevent, JsonObject baseJson, RPGLContext context) throws Exception {
        /*[
            {
                "name": "...",
                "base_formula": "number",
                "value": #
            },{
                "name": "...",
                "base_formula": "modifier",
                "ability": "dex",
                "object": "..."
            },{
                "name": "...",
                "base_formula": "ability",
                "ability": "dex",
                "object": "..."
            },{
                "name": "...",
                "base_formula": "proficiency",
                "half": boolean,
                "object": "..."
            },{
                "name": "...",
                "base_formula": "level", // TODO this feature not yet supported
                "class": "...",
                "object": "..."
            }
        ]*/
        return switch (baseJson.getString("base_formula")) {
            case "number" -> new JsonObject() {{
                this.putInteger("value", Objects.requireNonNullElse(baseJson.getInteger("value"), 0));
            }};
            case "modifier" -> new JsonObject() {{
                RPGLObject object = RPGLEffect.getObject(effect, subevent, baseJson.getJsonObject("object"));
                this.putInteger("value", object.getAbilityModifierFromAbilityName(baseJson.getString("ability"), context));
            }};
            case "ability" -> new JsonObject() {{
                RPGLObject object = RPGLEffect.getObject(effect, subevent, baseJson.getJsonObject("object"));
                this.putInteger("value", object.getAbilityScoreFromAbilityName(baseJson.getString("ability"), context));
            }};
            case "proficiency" -> new JsonObject() {{
                RPGLObject object = RPGLEffect.getObject(effect, subevent, baseJson.getJsonObject("object"));
                if (Objects.requireNonNullElse(baseJson.getBoolean("half"), false)) {
                    this.putInteger("value", object.getEffectiveProficiencyBonus(context) / 2);
                } else {
                    this.putInteger("value", object.getEffectiveProficiencyBonus(context));
                }
            }};
            default -> new JsonObject() {{
                // TODO log a warning here concerning an unexpected bonus_formula value
                this.putInteger("value", 0);
            }};
        };
    }

}
