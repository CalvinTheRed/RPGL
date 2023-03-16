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
    public void execute(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context) throws Exception {
        super.verifyFunction(functionJson);
        if (subevent instanceof Calculation calculation) {
            calculation.setMinimum(this.processJson(effect, subevent, functionJson.getJsonObject("minimum"), context));
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

    JsonObject processJson(RPGLEffect effect, Subevent subevent, JsonObject minimumJson, RPGLContext context) throws Exception {
        /*[
            {
                "name": "...",
                "minimum_type": "number",
                "value": #
            },{
                "name": "...",
                "minimum_type": "modifier",
                "ability": "dex",
                "object": "..."
            },{
                "name": "...",
                "minimum_type": "ability",
                "ability": "dex",
                "object": "..."
            },{
                "name": "...",
                "minimum_type": "proficiency",
                "half": boolean,
                "object": "..."
            },{
                "name": "...",
                "minimum_type": "level", // TODO this feature not yet supported
                "class": "...",
                "object": "..."
            }
        ]*/
        return switch (minimumJson.getString("minimum_type")) {
            case "number" -> new JsonObject() {{
                this.putString("name", effect.getName());
                this.putString("effect", effect.getUuid());
                this.putInteger("value", Objects.requireNonNullElse(minimumJson.getInteger("value"), 0));
            }};
            case "modifier" -> new JsonObject() {{
                this.putString("name", effect.getName());
                this.putString("effect", effect.getUuid());
                RPGLObject object = RPGLEffect.getObject(effect, subevent, minimumJson.getJsonObject("object"));
                this.putInteger("value", object.getAbilityModifierFromAbilityName(minimumJson.getString("ability"), context));
            }};
            case "ability" -> new JsonObject() {{
                this.putString("name", effect.getName());
                this.putString("effect", effect.getUuid());
                RPGLObject object = RPGLEffect.getObject(effect, subevent, minimumJson.getJsonObject("object"));
                this.putInteger("value", object.getAbilityScoreFromAbilityName(minimumJson.getString("ability"), context));
            }};
            case "proficiency" -> new JsonObject() {{
                this.putString("name", effect.getName());
                this.putString("effect", effect.getUuid());
                RPGLObject object = RPGLEffect.getObject(effect, subevent, minimumJson.getJsonObject("object"));
                if (Objects.requireNonNullElse(minimumJson.getBoolean("half"), false)) {
                    this.putInteger("value", object.getEffectiveProficiencyBonus(context) / 2);
                } else {
                    this.putInteger("value", object.getEffectiveProficiencyBonus(context));
                }
            }};
            default -> new JsonObject() {{
                // TODO log a warning here concerning an unexpected bonus_type value
                this.putString("name", "DEFAULT");
                this.putString("effect", null);
                this.putInteger("value", 0);
            }};
        };
    }

}
