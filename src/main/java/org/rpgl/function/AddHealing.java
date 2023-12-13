package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLObject;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.math.Die;
import org.rpgl.subevent.HealingCollection;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context,
                    List<RPGLResource> resources) throws Exception {
        if (subevent instanceof HealingCollection healingCollection) {
            JsonArray healingArray = functionJson.getJsonArray("healing");
            for (int i = 0; i < healingArray.size(); i++) {
                healingCollection.addHealing(processJson(effect, subevent, healingArray.getJsonObject(i), context));
            }
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

    /**
     * This helper method processes healing JSON data and translates healing formula data to dice and bonus form.
     *
     * @param effect the RPGLEffect applying this healing
     * @param subevent the HealingCollection receiving this healing
     * @param healingJson the healing formula data
     * @param context the context in which this healing is being applied
     * @return a JsonObject representing the evaluated form of the provided healing formula data
     *
     * @throws Exception if an exception occurs
     */
    public static JsonObject processJson(RPGLEffect effect, Subevent subevent, JsonObject healingJson, RPGLContext context) throws Exception {
        /*[
            {
                "name": "...",
                "healing_formula": "range",
                "bonus": #,
                "dice": [
                    { "count": #, "size": #, "determined": [ # ] },
                    ...
                ]
            },{
                "name": "...",
                "healing_formula": "modifier",
                "ability": "dex",
                "object": {
                    "from": "...",
                    "object": "...",
                    "as_origin": t/f
                }
            },{
                "name": "...",
                "healing_formula": "ability",
                "ability": "dex",
                "object": {
                    "from": "...",
                    "object": "...",
                    "as_origin": t/f
                }
            },{
                "name": "...",
                "healing_formula": "proficiency",
                "half": boolean,
                "object": {
                    "from": "...",
                    "object": "...",
                    "as_origin": t/f
                }
            },{
                "name": "...",
                "healing_formula": "level",
                "class": "...",
                "object": {
                    "from": "...",
                    "object": "...",
                    "as_origin": t/f
                }
            }
        ]*/
        return switch (healingJson.getString("healing_formula")) {
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
            case "level" -> new JsonObject() {{
                RPGLObject object = RPGLEffect.getObject(effect, subevent, healingJson.getJsonObject("object"));
                String classId = healingJson.getString("class");
                if (classId == null) {
                    this.putInteger("bonus", object.getLevel());
                } else {
                    this.putInteger("bonus", object.getLevel(classId));
                }
                this.putJsonArray("dice", new JsonArray());
            }};
            default -> new JsonObject() {{
                // TODO log a warning here concerning an unexpected healing_formula value
                this.putInteger("bonus", 0);
                this.putJsonArray("dice", new JsonArray());
            }};
        };
    }

}
