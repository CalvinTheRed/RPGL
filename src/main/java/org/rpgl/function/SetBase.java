package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Calculation;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context,
                    List<RPGLResource> resources) throws Exception {
        if (subevent instanceof Calculation calculation) {
            calculation.setBase(processJson(effect, subevent, functionJson.getJsonObject("base"), context));
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

    /**
     * This helper method processes base JSON data and translates base formula data to numerical value form.
     *
     * @param effect the RPGLEffect applying this base
     * @param subevent the Calculation receiving this base
     * @param baseJson the base formula data
     * @param context the context in which this base is being applied
     * @return a JsonObject representing the evaluated form of the provided base formula data
     *
     * @throws Exception if an exception occurs
     */
    public static int processJson(RPGLEffect effect, Subevent subevent, JsonObject baseJson, RPGLContext context) throws Exception {
        /*[
            {
                "name": "...",
                "base_formula": "number",
                "number": #
            },{
                "name": "...",
                "base_formula": "modifier",
                "ability": "dex",
                "object": {
                    "from": "...",
                    "object": "...",
                    "as_origin": t/f
                }
            },{
                "name": "...",
                "base_formula": "ability",
                "ability": "dex",
                "object": {
                    "from": "...",
                    "object": "...",
                    "as_origin": t/f
                }
            },{
                "name": "...",
                "base_formula": "proficiency",
                "half": boolean,
                "object": {
                    "from": "...",
                    "object": "...",
                    "as_origin": t/f
                }
            },{
                "name": "...",
                "base_formula": "level", // TODO allow level references to have a scale: { numerator, denominator }
                "class": "...",
                "object": {
                    "from": "...",
                    "object": "...",
                    "as_origin": t/f
                }
            }
        ]*/
        return switch (baseJson.getString("base_formula")) {
            case "number" -> baseJson.getInteger("number");
            case "modifier" -> RPGLEffect.getObject(effect, subevent, baseJson.getJsonObject("object"))
                    .getAbilityModifierFromAbilityName(baseJson.getString("ability"), context);
            case "ability" -> RPGLEffect.getObject(effect, subevent, baseJson.getJsonObject("object"))
                    .getAbilityScoreFromAbilityName(baseJson.getString("ability"), context);
            case "proficiency" -> Objects.requireNonNullElse(baseJson.getBoolean("half"), false)
                    ? RPGLEffect.getObject(effect, subevent, baseJson.getJsonObject("object")).getEffectiveProficiencyBonus(context) / 2
                    : RPGLEffect.getObject(effect, subevent, baseJson.getJsonObject("object")).getEffectiveProficiencyBonus(context);
            case "level" -> RPGLEffect.getObject(effect, subevent, baseJson.getJsonObject("object"))
                    .getLevel(baseJson.getString("class"));
            default -> 0;
        };
    }

}
