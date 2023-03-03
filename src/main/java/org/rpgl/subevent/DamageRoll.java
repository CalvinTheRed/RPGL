package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.math.Die;

import java.util.Objects;

/**
 * This abstract Subevent is dedicated to rolling damage dice.
 * <br>
 * <br>
 * Source: an RPGLObject rolling damage
 * <br>
 * Target: an RPGLObject which will later suffer the rolled damage
 *
 * @author Calvin Withun
 */
public class DamageRoll extends Subevent {

    public DamageRoll() {
        super("damage_roll");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new DamageRoll();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new DamageRoll();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        this.roll();
    }

    /**
     * This method rolls all dice associated with the Subevent.
     */
    public void roll() {
        JsonArray typedDamageArray = this.json.getJsonArray("damage");
        for (int i = 0; i < typedDamageArray.size(); i++) {
            JsonArray typedDamageDieArray = Objects.requireNonNullElse(typedDamageArray.getJsonObject(i).getJsonArray("dice"), new JsonArray());
            for (int j = 0; j < typedDamageDieArray.size(); j++) {
                JsonObject typedDamageDie = typedDamageDieArray.getJsonObject(j);
                typedDamageDie.putInteger("roll", Die.roll(typedDamageDie.getInteger("size"), typedDamageDie.getJsonArray("determined").asList()));
            }
        }
    }

    /**
     * This method re-rolls any dice of a given damage type whose rolled values are less than or equal to a given
     * threshold. Passing a null damage type counts as a wild card and applies the changes to all damage types.
     */
    public void rerollTypedDiceLessThanOrEqualTo(int threshold, String damageType) {
        JsonArray typedDamageArray = this.json.getJsonArray("damage");
        for (int i = 0; i < typedDamageArray.size(); i++) {
            JsonObject typedDamage = typedDamageArray.getJsonObject(i);
            if (damageType == null || damageType.equals(typedDamage.getString("type"))) {
                JsonArray typedDamageDieArray = Objects.requireNonNullElse(typedDamage.getJsonArray("dice"), new JsonArray());
                for (int j = 0; j < typedDamageDieArray.size(); j++) {
                    JsonObject typedDamageDie = typedDamageDieArray.getJsonObject(j);
                    if (typedDamageDie.getInteger("roll") <= threshold) {
                        typedDamageDie.putInteger("roll", Die.roll(typedDamageDie.getInteger("size"), typedDamageDie.getJsonArray("determined").asList()));
                    }
                }
            }
        }
    }

    /**
     * This method overrides the face value of all dice of a given damage type whose rolled values are less than or
     * equal to a given threshold. Passing a null damage type counts as a wild card and applies the changes to all
     * damage types.
     */
    public void setTypedDiceLessThanOrEqualTo(int threshold, int set, String damageType) {
        JsonArray typedDamageArray = this.json.getJsonArray("damage");
        for (int i = 0; i < typedDamageArray.size(); i++) {
            JsonObject typedDamage = typedDamageArray.getJsonObject(i);
            if (damageType == null || damageType.equals(typedDamage.getString("type"))) {
                JsonArray typedDamageDieArray = Objects.requireNonNullElse(typedDamage.getJsonArray("dice"), new JsonArray());
                for (int j = 0; j < typedDamageDieArray.size(); j++) {
                    JsonObject typedDamageDie = typedDamageDieArray.getJsonObject(j);
                    if (typedDamageDie.getInteger("roll") <= threshold) {
                        typedDamageDie.putInteger("roll", set);
                    }
                }
            }
        }
    }

    public void maximizeTypedDamageDice(String damageType) {
        JsonArray typedDamageArray = this.json.getJsonArray("damage");
        for (int i = 0; i < typedDamageArray.size(); i++) {
            JsonObject typedDamage = typedDamageArray.getJsonObject(i);
            if (damageType == null || "".equals(damageType) || damageType.equals(typedDamage.getString("type"))) {
                JsonArray typedDamageDieArray = Objects.requireNonNullElse(typedDamage.getJsonArray("dice"), new JsonArray());
                for (int j = 0; j < typedDamageDieArray.size(); j++) {
                    JsonObject typedDamageDie = typedDamageDieArray.getJsonObject(j);
                    typedDamageDie.putInteger("roll", typedDamageDie.getInteger("size"));
                }
            }
        }
    }

    /**
     * This method returns the damage dice collection associated with the Subevent.
     *
     * @return a collection of damage dice and bonuses
     */
    public JsonObject getDamage() {
        JsonObject baseDamage = new JsonObject();
        JsonArray typedDamageArray = this.json.getJsonArray("damage");
        for (int i = 0; i < typedDamageArray.size(); i++) {
            JsonObject typedDamage = typedDamageArray.getJsonObject(i);
            JsonArray typedDamageDieArray = Objects.requireNonNullElse(typedDamage.getJsonArray("dice"), new JsonArray());
            int bonus = Objects.requireNonNullElse(typedDamage.getInteger("bonus"), 0);
            for (int j = 0; j < typedDamageDieArray.size(); j++) {
                JsonObject typedDamageDie = typedDamageDieArray.getJsonObject(j);
                bonus += typedDamageDie.getInteger("roll");
            }
            baseDamage.putInteger(typedDamage.getString("type"), bonus);
        }
        return baseDamage;
    }

}
