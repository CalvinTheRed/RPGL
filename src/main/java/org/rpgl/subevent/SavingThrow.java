package org.rpgl.subevent;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.rpgl.core.RPGLObject;

import java.util.Map;

public class SavingThrow extends ContestRoll {

    public SavingThrow() {
        super("saving_throw");
    }

    @Override
    public Subevent clone() {
        return new SavingThrow();
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new SavingThrow();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLObject source) throws Exception {
        super.prepare(source);
        this.calculateDifficultyClass(source);
        if (this.subeventJson.get("damage") != null) {
            this.calculateBaseDamage(source);
        }
        this.addBonus(source.getAbilityModifier((String) this.subeventJson.get("save_ability")));
        this.addBonus(source.getSaveProficiencyBonus((String) this.subeventJson.get("save_ability")));
    }

    @Override
    public void invoke(RPGLObject source, RPGLObject target) throws Exception {
        super.invoke(source, target);
        this.roll();
        this.checkForReroll(source, target); // TODO eventually have this in a while loop?
        if (this.getRoll() < (Long) this.subeventJson.get("save_difficulty_class")) {
            this.resolveSaveFail(source, target);
        } else {
            this.resolveSavePass(source, target);
        }
    }

    private void calculateDifficultyClass(RPGLObject source) throws Exception {
        CalculateSaveDifficultyClass calculateSaveDifficultyClass = new CalculateSaveDifficultyClass();
        String calculateSaveDifficultyClassJsonString = String.format("{" +
                        "\"subevent\":\"calculate_save_difficulty_class\"," +
                        "\"difficulty_class_ability\":\"%s\"" +
                        "}",
                this.subeventJson.get("difficulty_class_ability")
        );
        JsonObject calculateSaveDifficultyClassJson = JsonParser.parseObjectString(calculateSaveDifficultyClassJsonString);
        calculateSaveDifficultyClass.joinSubeventJson(calculateSaveDifficultyClassJson);
        calculateSaveDifficultyClass.prepare(source);
        calculateSaveDifficultyClass.invoke(source, source);
        this.subeventJson.put("save_difficulty_class", calculateSaveDifficultyClass.get());
    }

    private void calculateBaseDamage(RPGLObject source) throws Exception {
        /*
         * Collect base typed damage dice and bonuses
         */
        BaseDamageDiceCollection baseDamageDiceCollection = new BaseDamageDiceCollection();
        String baseDamageDiceCollectionJsonString = String.format("{" +
                        "\"subevent\":\"base_damage_dice_collection\"," +
                        "\"damage\":%s" +
                        "}",
                this.subeventJson.get("damage").toString()
        );
        JsonObject baseDamageDiceCollectionJson = JsonParser.parseObjectString(baseDamageDiceCollectionJsonString);
        baseDamageDiceCollection.joinSubeventJson(baseDamageDiceCollectionJson);
        //baseDamageDiceCollection.prepare(source);
        baseDamageDiceCollection.invoke(source, source);

        /*
         * Roll base damage dice
         */
        BaseDamageRoll baseDamageRoll = new BaseDamageRoll();
        String baseDamageRollJsonString = String.format("{" +
                        "\"subevent\":\"base_damage_roll\"," +
                        "\"damage\":%s" +
                        "}",
                baseDamageDiceCollection.getDamageDiceCollection().toString()
        );
        JsonObject baseDamageRollJson = JsonParser.parseObjectString(baseDamageRollJsonString);
        baseDamageRoll.joinSubeventJson(baseDamageRollJson);
        baseDamageRoll.prepare(source);
        baseDamageRoll.invoke(source, source);

        /*
         * Replace damage key with base damage calculation
         */
        this.subeventJson.put("damage", baseDamageRoll.getBaseDamage());
    }

    private void resolveSavePass(RPGLObject source, RPGLObject target) throws Exception {
        this.resolvePassDamage(source, target);
        this.resolveNestedSubevents(source, target, "pass");
    }

    private void resolveSaveFail(RPGLObject source, RPGLObject target) throws Exception {
        this.resolveFailDamage(source, target);
        this.resolveNestedSubevents(source, target, "fail");
    }

    private JsonObject getTargetDamage(RPGLObject source, RPGLObject target) throws Exception {
        /*
         * Collect target typed damage dice and bonuses
         */
        TargetDamageDiceCollection targetDamageDiceCollection = new TargetDamageDiceCollection();
        String targetDamageDiceCollectionJsonString = "{" +
                        "\"subevent\":\"target_damage_dice_collection\"," +
                        "\"damage\":[]" +
                        "}";
        JsonObject targetDamageDiceCollectionJson = JsonParser.parseObjectString(targetDamageDiceCollectionJsonString);
        targetDamageDiceCollection.joinSubeventJson(targetDamageDiceCollectionJson);
        //baseDamageDiceCollection.prepare(source);
        targetDamageDiceCollection.invoke(source, target);

        /*
         * Roll target damage dice
         */
        TargetDamageRoll targetDamageRoll = new TargetDamageRoll();
        String targetDamageRollJsonString = String.format("{" +
                        "\"subevent\":\"target_damage_roll\"," +
                        "\"damage\":%s" +
                        "}",
                targetDamageDiceCollection.getDamageDiceCollection().toString()
        );
        JsonObject targetDamageRollJson = JsonParser.parseObjectString(targetDamageRollJsonString);
        targetDamageRoll.joinSubeventJson(targetDamageRollJson);
        targetDamageRoll.prepare(source);
        targetDamageRoll.invoke(source, target);

        return targetDamageRoll.getBaseDamage();
    }

    private void resolvePassDamage(RPGLObject source, RPGLObject target) throws Exception {
        JsonObject baseDamage = (JsonObject) this.subeventJson.get("damage");
        String damageOnPass = (String) this.subeventJson.get("damage_on_pass");
        if (baseDamage != null && !"none".equals(damageOnPass)) {
            /*
             * Add base and target damage into final damage quantities
             */
            for (Map.Entry<String, Object> targetDamageEntry : getTargetDamage(source, target).entrySet()) {
                String damageType = targetDamageEntry.getKey();
                if (baseDamage.containsKey(damageType)) {
                    Long baseTypedDamage = (Long) baseDamage.get(damageType);
                    baseTypedDamage += (Long) targetDamageEntry.getValue();
                    if (baseTypedDamage < 1L) {
                        // You can never deal less than 1 point of damage for any given
                        // damage type, given that you deal damage of that type at all.
                        baseTypedDamage = 1L;
                    }
                    baseDamage.put(damageType, baseTypedDamage);
                } else {
                    baseDamage.entrySet().add(targetDamageEntry);
                }
            }

            /*
             * Account for half or no damage on pass (this should be a redundant check if this code is reached)
             */
            if ("half".equals(damageOnPass)) {
                for (Map.Entry<String, Object> damageEntryElement : baseDamage.entrySet()) {
                    Long value = (Long) damageEntryElement.getValue();
                    value /= 2L;
                    if (value < 1L) {
                        // You can never deal less than 1 point of damage for any given
                        // damage type, given that you deal damage of that type at all.
                        value = 1L;
                    }
                    damageEntryElement.setValue(value);
                }
            }

            this.deliverFinalDamage(source, target, baseDamage);
        }
    }

    private void resolveFailDamage(RPGLObject source, RPGLObject target) throws Exception {
        JsonObject baseDamage = (JsonObject) this.subeventJson.get("damage");
        if (baseDamage != null) {
            for (Map.Entry<String, Object> targetDamageEntry : getTargetDamage(source, target).entrySet()) {
                String damageType = targetDamageEntry.getKey();
                if (baseDamage.containsKey(damageType)) {
                    Long baseTypedDamage = (Long) baseDamage.get(damageType);
                    baseTypedDamage += (Long) targetDamageEntry.getValue();
                    if (baseTypedDamage < 1L) {
                        // You can never deal less than 1 point of damage for any given
                        // damage type, given that you deal damage of that type at all.
                        baseTypedDamage = 1L;
                    }
                    baseDamage.put(damageType, baseTypedDamage);
                } else {
                    baseDamage.entrySet().add(targetDamageEntry);
                }
            }

            this.deliverFinalDamage(source, target, baseDamage);
        }
    }

    private void resolveNestedSubevents(RPGLObject source, RPGLObject target, String passOrFail) throws Exception {
        JsonArray subeventJsonArray = (JsonArray) this.subeventJson.get(passOrFail);
        if (subeventJsonArray != null) {
            for (Object subeventJsonElement : subeventJsonArray) {
                JsonObject subeventJson = (JsonObject) subeventJsonElement;
                Subevent subevent = Subevent.SUBEVENTS.get((String) subeventJson.get("subevent")).clone(subeventJson);
                subevent.prepare(source);
                subevent.invoke(source, target);
            }
        }
    }

    void deliverFinalDamage(RPGLObject source, RPGLObject target, JsonObject damage) throws Exception {
        target.receiveDamage(source, damage);
    }

}
