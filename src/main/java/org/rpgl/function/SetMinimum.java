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
 * This Function is dedicated to assigning the minimum field of Calculation Subevents.
 * TODO this is insufficient to support features such as Reliable Talent or Silver Tongue
 *
 * @author Calvin Withun
 */
public class SetMinimum extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetMinimum.class);

    public SetMinimum() {
        super("set_minimum");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context,
                    List<RPGLResource> resources) throws Exception {
        if (subevent instanceof Calculation calculation) {
            calculation.setMinimum(processJson(effect, subevent, functionJson.getJsonObject("minimum"), context));
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

    /**
     * This helper method processes minimum JSON data and translates minimum formula data to numerical value form.
     *
     * @param effect the RPGLEffect applying this minimum
     * @param subevent the Calculation receiving this minimum
     * @param minimumJson the minimum formula data
     * @param context the context in which this minimum is being applied
     * @return a int evaluated from the provided minimum formula data
     *
     * @throws Exception if an exception occurs
     */
    public static int processJson(RPGLEffect effect, Subevent subevent, JsonObject minimumJson, RPGLContext context) throws Exception {
        /*[
            {
                "name": "...",
                "minimum_formula": "number",
                "number": #
            },{
                "name": "...",
                "minimum_formula": "modifier",
                "ability": "dex",
                "object": {
                    "from": "...",
                    "object": "...",
                    "as_origin": t/f
                }
            },{
                "name": "...",
                "minimum_formula": "ability",
                "ability": "dex",
                "object": {
                    "from": "...",
                    "object": "...",
                    "as_origin": t/f
                }
            },{
                "name": "...",
                "minimum_formula": "proficiency",
                "half": boolean,
                "object": {
                    "from": "...",
                    "object": "...",
                    "as_origin": t/f
                }
            },{
                "name": "...",
                "minimum_formula": "level",
                "class": "...",
                "object": {
                    "from": "...",
                    "object": "...",
                    "as_origin": t/f
                }
            }
        ]*/
        return switch (minimumJson.getString("minimum_formula")) {
            case "number" -> Objects.requireNonNullElse(minimumJson.getInteger("number"), 0);
            case "modifier" -> RPGLEffect.getObject(effect, subevent, minimumJson.getJsonObject("object"))
                    .getAbilityModifierFromAbilityName(minimumJson.getString("ability"), context);
            case "ability" -> RPGLEffect.getObject(effect, subevent, minimumJson.getJsonObject("object"))
                    .getAbilityScoreFromAbilityName(minimumJson.getString("ability"), context);
            case "proficiency" -> Objects.requireNonNullElse(minimumJson.getBoolean("half"), false)
                    ? RPGLEffect.getObject(effect, subevent, minimumJson.getJsonObject("object")).getEffectiveProficiencyBonus(context) / 2
                    : RPGLEffect.getObject(effect, subevent, minimumJson.getJsonObject("object")).getEffectiveProficiencyBonus(context);
            default -> 0;
        };
    }

}
