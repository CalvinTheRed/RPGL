package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLObject;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.math.Die;
import org.rpgl.subevent.Subevent;
import org.rpgl.subevent.TemporaryHitPointCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context,
                    List<RPGLResource> resources) throws Exception {
        if (subevent instanceof TemporaryHitPointCollection temporaryHitPointCollection) {
            JsonArray temporaryHitPointsArray = functionJson.getJsonArray("temporary_hit_points");
            for (int i = 0; i < temporaryHitPointsArray.size(); i++) {
                temporaryHitPointCollection.addTemporaryHitPoints(processJson(effect, subevent, temporaryHitPointsArray.getJsonObject(i), context));
            }
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

    /**
     * This helper method processes temporary hit point JSON data and translates temporary hit point formula data to
     * dice and bonus form.
     *
     * @param effect                 the RPGLEffect applying this bonus
     * @param subevent               the TemporaryHitPointCollection receiving these temporary hit points
     * @param temporaryHitPointsJson the temporary hit point formula data
     * @param context                the context in which these temporary hit points are being applied
     * @return a JsonObject representing the evaluated form of the provided temporary hit point formula data
     *
     * @throws Exception if an exception occurs
     */
    public static JsonObject processJson(RPGLEffect effect, Subevent subevent, JsonObject temporaryHitPointsJson, RPGLContext context) throws Exception {
        /*[
            {
                "name": "...",
                "temporary_hit_point_formula": "range",
                "bonus": #,
                "dice": [
                    { "count": #, "size": #, "determined": [ # ] },
                    ...
                ]
            },{
                "name": "...",
                "temporary_hit_point_formula": "modifier",
                "ability": "dex",
                "object": {
                    "from": "...",
                    "object": "..."
                }
            },{
                "name": "...",
                "temporary_hit_point_formula": "ability",
                "ability": "dex",
                "object": {
                    "from": "...",
                    "object": "..."
                }
            },{
                "name": "...",
                "temporary_hit_point_formula": "proficiency",
                "half": boolean,
                "object": {
                    "from": "...",
                    "object": "..."
                }
            },{
                "name": "...",
                "temporary_hit_point_formula": "level",
                "class": "...",
                "object": {
                    "from": "...",
                    "object": "..."
                }
            }
        ]*/
        return switch (temporaryHitPointsJson.getString("temporary_hit_point_formula")) {
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
            case "level" -> new JsonObject() {{
                RPGLObject object = RPGLEffect.getObject(effect, subevent, temporaryHitPointsJson.getJsonObject("object"));
                String classId = temporaryHitPointsJson.getString("class");
                if (classId == null) {
                    this.putInteger("bonus", object.getLevel());
                } else {
                    this.putInteger("bonus", object.getLevel(classId));
                }
                this.putJsonArray("dice", new JsonArray());
            }};
            default -> new JsonObject() {{
                // TODO log a warning here concerning an unexpected temporary_hit_point_formula value
                this.putInteger("bonus", 0);
                this.putJsonArray("dice", new JsonArray());
            }};
        };
    }

}
