package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.math.Die;
import org.rpgl.subevent.HealingCollection;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * This Function is dedicated to adding some amount of healing to a HealingCollection Subevent.
 *
 * @author Calvin Withun
 */
public class AddHealing extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddHealing.class);

    public AddHealing() {
        super("add_healing");
    }

    @Override
    public void execute(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context) throws Exception {
        super.verifyFunction(functionJson);
        if (subevent instanceof HealingCollection healingCollection) {
            healingCollection.addHealing(processJson(effect, subevent, functionJson.getJsonObject("healing"), context));
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

    public static JsonObject processJson(RPGLEffect effect, Subevent subevent, JsonObject healingJson, RPGLContext context) throws Exception {
        /*[
            {
                "name": "...",
                "healing_type": "range",
                "bonus": #,
                "dice": [
                    { "count": #, "size": #, "determined": [ # ] },
                    ...
                ]
            },{
                "name": "...",
                "healing_type": "modifier",
                "ability": "dex",
                "object": "..."
            },{
                "name": "...",
                "healing_type": "ability",
                "ability": "dex",
                "object": "..."
            },{
                "name": "...",
                "healing_type": "proficiency",
                "half": boolean,
                "object": "..."
            },{
                "name": "...",
                "healing_type": "level", // TODO this feature not yet supported
                "class": "...",
                "object": "..."
            }
        ]*/
        return switch (healingJson.getString("healing_type")) {
            case "range" -> new JsonObject() {{
                this.putInteger("bonus", Objects.requireNonNullElse(healingJson.getInteger("bonus"), 0));
                this.putJsonArray("dice", Objects.requireNonNullElse(Die.unpack(healingJson.getJsonArray("dice")), new JsonArray()));
            }};
            case "modifier" -> new JsonObject() {{
                RPGLObject object = RPGLEffect.getObject(effect, subevent, healingJson.getJsonObject("object"));
                this.putInteger("bonus", object.getAbilityModifierFromAbilityName(healingJson.getString("ability"), context));
                this.putJsonArray("dice", new JsonArray());
            }};
            case "ability" -> new JsonObject() {{
                RPGLObject object = RPGLEffect.getObject(effect, subevent, healingJson.getJsonObject("object"));
                this.putInteger("bonus", object.getAbilityScoreFromAbilityName(healingJson.getString("ability"), context));
                this.putJsonArray("dice", new JsonArray());
            }};
            case "proficiency" -> new JsonObject() {{
                RPGLObject object = RPGLEffect.getObject(effect, subevent, healingJson.getJsonObject("object"));
                if (Objects.requireNonNullElse(healingJson.getBoolean("half"), false)) {
                    this.putInteger("bonus", object.getEffectiveProficiencyBonus(context) / 2);
                } else {
                    this.putInteger("bonus", object.getEffectiveProficiencyBonus(context));
                }
                this.putJsonArray("dice", new JsonArray());
            }};
            default -> new JsonObject() {{
                // TODO log a warning here concerning an unexpected healing_type value
                this.putInteger("bonus", 0);
                this.putJsonArray("dice", new JsonArray());
            }};
        };
    }

}
