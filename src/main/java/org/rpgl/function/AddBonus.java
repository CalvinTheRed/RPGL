package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
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
                ],
                "optional": boolean
            },{
                "name": "...",
                "bonus_type": "modifier",
                "ability": "dex",
                "object": "...",
                "optional": boolean
            },{
                "name": "...",
                "bonus_type": "ability",
                "ability": "dex",
                "object": "...",
                "optional": boolean
            },{
                "name": "...",
                "bonus_type": "proficiency",
                "half": boolean,
                "object": "...",
                "optional": boolean
            },{
                "name": "...",
                "bonus_type": "level", // TODO this feature not yet supported
                "class": "...",
                "object": "...",
                "optional": boolean
            }
        ]*/
        return switch (bonusJson.getString("bonus_type")) {
            case "range" -> new JsonObject() {{
                this.putString("name", effect.getName());
                this.putString("effect", effect.getUuid());
                this.putInteger("bonus", Objects.requireNonNullElse(bonusJson.getInteger("bonus"), 0));
                this.putJsonArray("dice", Objects.requireNonNullElse(unpackDice(bonusJson.getJsonArray("dice")), new JsonArray()));
                this.putBoolean("optional", Objects.requireNonNullElse(bonusJson.getBoolean("optional"), false));
            }};
            case "modifier" -> new JsonObject() {{
                this.putString("name", effect.getName());
                this.putString("effect", effect.getUuid());
                RPGLObject object = RPGLEffect.getObject(effect, subevent, bonusJson.getJsonObject("object"));
                this.putInteger("bonus", object.getAbilityModifierFromAbilityName(bonusJson.getString("ability"), context));
                this.putJsonArray("dice", new JsonArray());
                this.putBoolean("optional", Objects.requireNonNullElse(bonusJson.getBoolean("optional"), false));
            }};
            case "ability" -> new JsonObject() {{
                this.putString("name", effect.getName());
                this.putString("effect", effect.getUuid());
                RPGLObject object = RPGLEffect.getObject(effect, subevent, bonusJson.getJsonObject("object"));
                this.putInteger("bonus", object.getAbilityScoreFromAbilityName(bonusJson.getString("ability"), context));
                this.putJsonArray("dice", new JsonArray());
                this.putBoolean("optional", Objects.requireNonNullElse(bonusJson.getBoolean("optional"), false));
            }};
            case "proficiency" -> new JsonObject() {{
                this.putString("name", effect.getName());
                this.putString("effect", effect.getUuid());
                RPGLObject object = RPGLEffect.getObject(effect, subevent, bonusJson.getJsonObject("object"));
                if (Objects.requireNonNullElse(bonusJson.getBoolean("half"), false)) {
                    this.putInteger("bonus", object.getEffectiveProficiencyBonus(context) / 2);
                } else {
                    this.putInteger("bonus", object.getEffectiveProficiencyBonus(context));
                }
                this.putJsonArray("dice", new JsonArray());
                this.putBoolean("optional", Objects.requireNonNullElse(bonusJson.getBoolean("optional"), false));
            }};
            default -> new JsonObject() {{
                // TODO log a warning here concerning an unexpected bonus_type value
                this.putString("name", "DEFAULT");
                this.putString("effect", null);
                this.putInteger("bonus", 0);
                this.putJsonArray("dice", new JsonArray());
                this.putBoolean("optional", false);
            }};
        };
    }

    public static JsonArray unpackDice(JsonArray bonusDice) {
        JsonArray processedDice = new JsonArray();
        for (int i = 0; i < bonusDice.size(); i++) {
            JsonObject bonusDie = bonusDice.getJsonObject(i);
            JsonObject processedDie = new JsonObject() {{
                this.putInteger("size", bonusDie.getInteger("size"));
                this.putJsonArray("determined", bonusDie.getJsonArray("determined"));
            }};
            for (int j = 0; j < bonusDie.getInteger("count"); j++) {
                processedDice.addJsonObject(processedDie.deepClone());
            }
        }
        return processedDice;
    }

}
