package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.math.Die;
import org.rpgl.subevent.CriticalHitDamageCollection;
import org.rpgl.subevent.DamageCollection;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * This Function is dedicated to adding to a DamageCollection or a CriticalHitDamageCollection Subevent. Note that while
 * this Function can add negative bonuses, it can not add "negative dice."
 *
 * @author Calvin Withun
 */
public class AddDamage extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddDamage.class);

    public AddDamage() {
        super("add_damage");
    }

    @Override
    public void execute(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context) throws Exception {
        super.verifyFunction(functionJson);
        if (subevent instanceof DamageCollection damageCollection) {
            JsonArray damageArray = functionJson.getJsonArray("damage");
            for (int i = 0; i < damageArray.size(); i++) {
                damageCollection.addDamage(processJson(effect, subevent, damageArray.getJsonObject(i), context));
            }
        } else if (subevent instanceof CriticalHitDamageCollection criticalHitDamageCollection) {
            criticalHitDamageCollection.addDamage(processJson(effect, subevent, functionJson.getJsonObject("damage"), context));
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

    /**
     * This helper method processes damage JSON data and translates damage formula data to dice and bonus form.
     *
     * @param effect     the RPGLEffect applying this damage
     * @param subevent   the DamageCollection or CriticalDamageCollection receiving this damage
     * @param damageJson the damage formula data
     * @param context    the context in which this damage is being applied
     * @return a JsonObject representing the evaluated form of the provided damage formula data
     *
     * @throws Exception if an exception occurs
     */
    public static JsonObject processJson(RPGLEffect effect, Subevent subevent, JsonObject damageJson, RPGLContext context) throws Exception {
        /*[
            {
                "name": "...",
                "damage_formula": "range",
                "damage_type": string,
                "bonus": #,
                "dice": [
                    { "count": #, "size": #, "determined": [ # ] },
                    ...
                ]
            },{
                "name": "...",
                "damage_formula": "modifier",
                "damage_type": string,
                "ability": "dex",
                "object": "..."
            },{
                "name": "...",
                "damage_formula": "ability",
                "damage_type": string,
                "ability": "dex",
                "object": "..."
            },{
                "name": "...",
                "damage_formula": "proficiency",
                "damage_type": string,
                "half": boolean,
                "object": "..."
            },{
                "name": "...",
                "damage_formula": "level", // TODO this feature not yet supported
                "damage_type": string,
                "class": "...",
                "object": "..."
            }
        ]*/
        return switch (damageJson.getString("damage_formula")) {
            // TODO add support for "same as original damage type"
            case "range" -> new JsonObject() {{
                this.putString("damage_type", damageJson.getString("damage_type"));
                this.putInteger("bonus", Objects.requireNonNullElse(damageJson.getInteger("bonus"), 0));
                this.putJsonArray("dice", Objects.requireNonNullElse(Die.unpack(damageJson.getJsonArray("dice")), new JsonArray()));
            }};
            case "modifier" -> new JsonObject() {{
                this.putString("damage_type", damageJson.getString("damage_type"));
                RPGLObject object = RPGLEffect.getObject(effect, subevent, damageJson.getJsonObject("object"));
                this.putInteger("bonus", object.getAbilityModifierFromAbilityName(damageJson.getString("ability"), context));
                this.putJsonArray("dice", new JsonArray());
            }};
            case "ability" -> new JsonObject() {{
                this.putString("damage_type", damageJson.getString("damage_type"));
                RPGLObject object = RPGLEffect.getObject(effect, subevent, damageJson.getJsonObject("object"));
                this.putInteger("bonus", object.getAbilityScoreFromAbilityName(damageJson.getString("ability"), context));
                this.putJsonArray("dice", new JsonArray());
            }};
            case "proficiency" -> new JsonObject() {{
                this.putString("damage_type", damageJson.getString("damage_type"));
                RPGLObject object = RPGLEffect.getObject(effect, subevent, damageJson.getJsonObject("object"));
                if (Objects.requireNonNullElse(damageJson.getBoolean("half"), false)) {
                    this.putInteger("bonus", object.getEffectiveProficiencyBonus(context) / 2);
                } else {
                    this.putInteger("bonus", object.getEffectiveProficiencyBonus(context));
                }
                this.putJsonArray("dice", new JsonArray());
            }};
            default -> new JsonObject() {{
                // TODO log a warning here concerning an unexpected bonus_formula value
                this.putString("damage_type", "bludgeoning");
                this.putInteger("bonus", 0);
                this.putJsonArray("dice", new JsonArray());
            }};
        };
    }

}
