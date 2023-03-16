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
            calculation.setBase(this.processJson(effect, subevent, functionJson.getJsonObject("base"), context));
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

    JsonObject processJson(RPGLEffect effect, Subevent subevent, JsonObject baseJson, RPGLContext context) throws Exception {
        /*[
            {
                "name": "...",
                "base_type": "number",
                "value": #
            },{
                "name": "...",
                "base_type": "modifier",
                "ability": "dex",
                "object": "..."
            },{
                "name": "...",
                "base_type": "ability",
                "ability": "dex",
                "object": "..."
            },{
                "name": "...",
                "base_type": "proficiency",
                "half": boolean,
                "object": "..."
            },{
                "name": "...",
                "base_type": "level", // TODO this feature not yet supported
                "class": "...",
                "object": "..."
            }
        ]*/
        return switch (baseJson.getString("base_type")) {
            case "number" -> new JsonObject() {{
                this.putString("name", effect.getName());
                this.putString("effect", effect.getUuid());
                this.putInteger("value", Objects.requireNonNullElse(baseJson.getInteger("value"), 0));
            }};
            case "modifier" -> new JsonObject() {{
                this.putString("name", effect.getName());
                this.putString("effect", effect.getUuid());
                RPGLObject object = RPGLEffect.getObject(effect, subevent, baseJson.getJsonObject("object"));
                this.putInteger("value", object.getAbilityModifierFromAbilityName(baseJson.getString("ability"), context));
            }};
            case "ability" -> new JsonObject() {{
                this.putString("name", effect.getName());
                this.putString("effect", effect.getUuid());
                RPGLObject object = RPGLEffect.getObject(effect, subevent, baseJson.getJsonObject("object"));
                this.putInteger("value", object.getAbilityScoreFromAbilityName(baseJson.getString("ability"), context));
            }};
            case "proficiency" -> new JsonObject() {{
                this.putString("name", effect.getName());
                this.putString("effect", effect.getUuid());
                RPGLObject object = RPGLEffect.getObject(effect, subevent, baseJson.getJsonObject("object"));
                if (Objects.requireNonNullElse(baseJson.getBoolean("half"), false)) {
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
