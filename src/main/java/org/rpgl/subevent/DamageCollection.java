package org.rpgl.subevent;

import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.Objects;

/**
 * This abstract Subevent is dedicated to collecting unrolled damage dice and bonuses.
 * <br>
 * <br>
 * Source: an RPGLObject preparing to deal damage
 * <br>
 * Target: an RPGLObject which will later suffer the collected damage
 *
 * @author Calvin Withun
 */
public class DamageCollection extends Subevent {

    public DamageCollection() {
        super("damage_collection");
    }

    public DamageCollection(String subeventId) {
        super(subeventId);
    }

    @Override
    public Subevent clone() {
        Subevent clone = new DamageCollection();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new DamageCollection();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    /**
     * This method returns whether a given damage type is present in the damage dice collection.
     *
     * @param damageType the damage type being searched for
     * @return true if the passed damage type is present in the damage dice collection
     */
    public boolean includesDamageType(String damageType) {
        JsonArray damageDiceArray = this.json.getJsonArray("damage");
        if (damageDiceArray != null) {
            for (int i = 0; i < damageDiceArray.size(); i++) {
                JsonObject damageDice = damageDiceArray.getJsonObject(i);
                if (damageDice.getString("type").equals(damageType)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method adds typed damage to the damage dice collection.
     *
     * @param typedDamageArray the typed damage to be included in the damage dice collection
     */
    public void addTypedDamage(JsonArray typedDamageArray) {
        for (int i = 0; i < typedDamageArray.size(); i++) {
            JsonObject typedDamage = typedDamageArray.getJsonObject(i);
            if (this.includesDamageType(typedDamage.getString("type"))) {
                this.addExistingTypedDamage(typedDamage);
            } else {
                this.addNewTypedDamage(typedDamage);
            }
        }
    }

    /**
     * This helper method adds typed damage whose type is already present in the damage dice collection.
     *
     * @param typedDamageToBeAdded the typed damage to be added to the damage dice collection
     */
    void addExistingTypedDamage(JsonObject typedDamageToBeAdded) {
        String damageTypeToBeAdded = typedDamageToBeAdded.getString("type");
        JsonObject typedDamage = this.json.getJsonArray("damage").getJsonObjectMatching("type", damageTypeToBeAdded);

        /*
         * Add new damage dice, if any exist
         */
        JsonArray typedDamageDice = Objects.requireNonNullElse(typedDamage.getJsonArray("dice"), new JsonArray());
        JsonArray typedDamageDiceToBeAdded = Objects.requireNonNullElse(typedDamageToBeAdded.getJsonArray("dice"), new JsonArray());
        typedDamageDice.asList().addAll(typedDamageDiceToBeAdded.asList());

        /*
         * Add extra damage bonus, if it exists
         */
        Integer typedDamageBonus = Objects.requireNonNullElse(typedDamage.getInteger("bonus"), 0);
        Integer typedDamageBonusToBeAdded = Objects.requireNonNullElse(typedDamageToBeAdded.getInteger("bonus"), 0);
        typedDamage.putInteger(
                "bonus",
                typedDamageBonus + typedDamageBonusToBeAdded
        );
    }

    /**
     * This helper method adds typed damage whose type is already present in the damage dice collection.
     *
     * @param typedDamage the typed damage to be added to the damage dice collection
     */
    void addNewTypedDamage(JsonObject typedDamage) {
        JsonArray typedDamageArray = this.json.getJsonArray("damage");
        typedDamageArray.addJsonObject(typedDamage);
    }

    /**
     * This method returns the damage dice collection being gathered by this Subevent.
     *
     * @return an array of typed damage dice and bonuses
     */
    public JsonArray getDamageCollection() {
        return Objects.requireNonNullElse(this.json.getJsonArray("damage"), new JsonArray());
    }

}
