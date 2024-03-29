package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.math.Die;

import java.util.Objects;

/**
 * This Subevent is dedicated to rolling damage dice.
 * <br>
 * <br>
 * Source: an RPGLObject rolling damage
 * <br>
 * Target: an RPGLObject which will later suffer the rolled damage
 *
 * @author Calvin Withun
 */
public class DamageRoll extends Subevent implements DamageTypeSubevent {

    public DamageRoll() {
        super("damage_roll");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new DamageRoll();
        clone.joinSubeventData(this.json);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new DamageRoll();
        clone.joinSubeventData(jsonData);
        clone.appliedEffects.addAll(this.appliedEffects);
        return clone;
    }

    @Override
    public DamageRoll invoke(RPGLContext context, JsonArray originPoint) throws Exception {
        return (DamageRoll) super.invoke(context, originPoint);
    }

    @Override
    public DamageRoll joinSubeventData(JsonObject other) {
        return (DamageRoll) super.joinSubeventData(other);
    }

    @Override
    public DamageRoll prepare(RPGLContext context, JsonArray originPoint) throws Exception {
        super.prepare(context, originPoint);
        this.roll();
        return this;
    }

    @Override
    public DamageRoll run(RPGLContext context, JsonArray originPoint) throws Exception {
        return this;
    }

    @Override
    public DamageRoll setOriginItem(String originItem) {
        return (DamageRoll) super.setOriginItem(originItem);
    }

    @Override
    public DamageRoll setSource(RPGLObject source) {
        return (DamageRoll) super.setSource(source);
    }

    @Override
    public DamageRoll setTarget(RPGLObject target) {
        return (DamageRoll) super.setTarget(target);
    }

    @Override
    public boolean includesDamageType(String damageType) {
        JsonArray damageArray = this.json.getJsonArray("damage");
        for (int i = 0; i < damageArray.size(); i++) {
            if (Objects.equals(damageType, damageArray.getJsonObject(i).getString("damage_type"))) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method rolls all dice associated with the Subevent.
     */
    public void roll() {
        JsonArray typedDamageArray = this.json.getJsonArray("damage");
        for (int i = 0; i < typedDamageArray.size(); i++) {
            JsonArray typedDamageDieArray = Objects.requireNonNullElse(typedDamageArray.getJsonObject(i).getJsonArray("dice"), new JsonArray());
            for (int j = 0; j < typedDamageDieArray.size(); j++) {
                Die.roll(typedDamageDieArray.getJsonObject(j));
            }
        }
    }

    /**
     * This method re-rolls any dice of a given damage type whose rolled values are matching or below a given threshold.
     * Passing a null damage type or "" counts as a wild card and applies the changes to all damage types.
     *
     * @param threshold the value a die must roll at or below to be changed by this method
     * @param damageType the damage type of dice to be changed by this method
     */
    public void rerollDamageDiceMatchingOrBelow(int threshold, String damageType) {
        JsonArray typedDamageArray = this.json.getJsonArray("damage");
        for (int i = 0; i < typedDamageArray.size(); i++) {
            JsonObject typedDamage = typedDamageArray.getJsonObject(i);
            if (damageType == null || "".equals(damageType) || Objects.equals(damageType, typedDamage.getString("damage_type"))) {
                JsonArray typedDamageDieArray = Objects.requireNonNullElse(typedDamage.getJsonArray("dice"), new JsonArray());
                for (int j = 0; j < typedDamageDieArray.size(); j++) {
                    JsonObject typedDamageDie = typedDamageDieArray.getJsonObject(j);
                    if (typedDamageDie.getInteger("roll") <= threshold) {
                        Die.roll(typedDamageDie);
                    }
                }
            }
        }
    }

    /**
     * This method overrides the face value of all dice of a given damage type whose rolled values are matching or below
     * a given threshold. Passing a null damage type or "" counts as a wild card and applies the changes to all
     * damage types.
     *
     * @param threshold the value a die must roll at or below to be changed by this method
     * @param damageType the damage type of dice to be changed by this method
     */
    public void setDamageDiceMatchingOrBelow(int threshold, int set, String damageType) {
        JsonArray typedDamageArray = this.json.getJsonArray("damage");
        for (int i = 0; i < typedDamageArray.size(); i++) {
            JsonObject typedDamage = typedDamageArray.getJsonObject(i);
            if (damageType == null || "".equals(damageType) || Objects.equals(damageType, typedDamage.getString("damage_type"))) {
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

    /**
     * This method sets all damage dice of the indicated type to their maximum face values. Passing a null damage type
     * counts as a wild card and applies the changes to all damage types.
     *
     * @param damageType the damage type of dice to be changed by this method
     */
    public void maximizeDamageDice(String damageType) {
        JsonArray typedDamageArray = this.json.getJsonArray("damage");
        for (int i = 0; i < typedDamageArray.size(); i++) {
            JsonObject typedDamage = typedDamageArray.getJsonObject(i);
            if (damageType == null || Objects.equals(damageType, typedDamage.getString("damage_type"))) {
                JsonArray typedDamageDieArray = Objects.requireNonNullElse(typedDamage.getJsonArray("dice"), new JsonArray());
                for (int j = 0; j < typedDamageDieArray.size(); j++) {
                    JsonObject typedDamageDie = typedDamageDieArray.getJsonObject(j);
                    typedDamageDie.putInteger("roll", typedDamageDie.getInteger("size"));
                }
            }
        }
    }

    /**
     * This method returns the damage collection associated with the Subevent after all dice have been rolled.
     *
     * @return a collection of damage dice and bonuses
     */
    public JsonArray getDamage() {
        return this.json.getJsonArray("damage");
    }

}
