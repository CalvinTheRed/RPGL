package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.math.Die;
import org.rpgl.subevent.Subevent;
import org.rpgl.subevent.TemporaryHitPointCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * This Function is dedicated to adding some amount of temporary hit points to a TemporaryHitPointsCollection Subevent.
 *
 * @author Calvin Withun
 */
public class AddTemporaryHitPoints extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddTemporaryHitPoints.class);

    public AddTemporaryHitPoints() {
        super("add_temporary_hit_points");
    }

    @Override
    public void execute(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context) throws Exception {
        super.verifyFunction(functionJson);
        if (subevent instanceof TemporaryHitPointCollection temporaryHitPointCollection) {
            temporaryHitPointCollection.addTemporaryHitPoints(processJson(effect, subevent, functionJson.getJsonObject("temporary_hit_points"), context));
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

    public static JsonObject processJson(RPGLEffect effect, Subevent subevent, JsonObject temporaryHitPointsJson, RPGLContext context) throws Exception {
        /*[
            {
                "name": "...",
                "temporary_hit_point_type": "range",
                "bonus": #,
                "dice": [
                    { "count": #, "size": #, "determined": [ # ] },
                    ...
                ]
            },{
                "name": "...",
                "temporary_hit_point_type": "modifier",
                "ability": "dex",
                "object": "..."
            },{
                "name": "...",
                "temporary_hit_point_type": "ability",
                "ability": "dex",
                "object": "..."
            },{
                "name": "...",
                "temporary_hit_point_type": "proficiency",
                "half": boolean,
                "object": "..."
            },{
                "name": "...",
                "temporary_hit_point_type": "level", // TODO this feature not yet supported
                "class": "...",
                "object": "..."
            }
        ]*/
        return switch (temporaryHitPointsJson.getString("temporary_hit_point_type")) {
            case "range" -> new JsonObject() {{
                this.putInteger("bonus", Objects.requireNonNullElse(temporaryHitPointsJson.getInteger("bonus"), 0));
                this.putJsonArray("dice", Objects.requireNonNullElse(Die.unpack(temporaryHitPointsJson.getJsonArray("dice")), new JsonArray()));
            }};
            case "modifier" -> new JsonObject() {{
                RPGLObject object = RPGLEffect.getObject(effect, subevent, temporaryHitPointsJson.getJsonObject("object"));
                this.putInteger("bonus", object.getAbilityModifierFromAbilityName(temporaryHitPointsJson.getString("ability"), context));
                this.putJsonArray("dice", new JsonArray());
            }};
            case "ability" -> new JsonObject() {{
                RPGLObject object = RPGLEffect.getObject(effect, subevent, temporaryHitPointsJson.getJsonObject("object"));
                this.putInteger("bonus", object.getAbilityScoreFromAbilityName(temporaryHitPointsJson.getString("ability"), context));
                this.putJsonArray("dice", new JsonArray());
            }};
            case "proficiency" -> new JsonObject() {{
                RPGLObject object = RPGLEffect.getObject(effect, subevent, temporaryHitPointsJson.getJsonObject("object"));
                if (Objects.requireNonNullElse(temporaryHitPointsJson.getBoolean("half"), false)) {
                    this.putInteger("bonus", object.getEffectiveProficiencyBonus(context) / 2);
                } else {
                    this.putInteger("bonus", object.getEffectiveProficiencyBonus(context));
                }
                this.putJsonArray("dice", new JsonArray());
            }};
            default -> new JsonObject() {{
                // TODO log a warning here concerning an unexpected temporary_hit_point_type value
                this.putInteger("bonus", 0);
                this.putJsonArray("dice", new JsonArray());
            }};
        };
    }

}
