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
    public void execute(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context) throws Exception {
        super.verifyFunction(functionJson);
        if (subevent instanceof Calculation calculation) {
            calculation.addBonus(processJson(effect, subevent, functionJson.getJsonObject("bonus"), context));
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

    // TODO should the two below methods be here or elsewhere? Maybe a shared abstract base class?

    public static JsonObject processJson(RPGLEffect effect, Subevent subevent, JsonObject bonusJson, RPGLContext context) throws Exception {
        /*[
            {
                "name": "...",
                "bonus_type": "range",
                "bonus": #,
                "dice": [
                    { "count": #, "size": #, "determined": [ # ] },
                    ...
                ]
            },{
                "name": "...",
                "bonus_type": "modifier",
                "ability": "dex",
                "object": "..."
            },{
                "name": "...",
                "bonus_type": "ability",
                "ability": "dex",
                "object": "..."
            },{
                "name": "...",
                "bonus_type": "proficiency",
                "half": boolean,
                "object": "..."
            },{
                "name": "...",
                "bonus_type": "level", // TODO this feature not yet supported
                "class": "...",
                "object": "..."
            }
        ]*/
        return switch (bonusJson.getString("bonus_type")) {
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
                // TODO log a warning here concerning an unexpected bonus_type value
                this.putInteger("bonus", 0);
                this.putJsonArray("dice", new JsonArray());
            }};
        };
    }

}
