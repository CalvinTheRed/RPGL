package org.rpgl.subevent;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.uuidtable.UUIDTable;

import java.util.Map;

public class SavingThrow extends ContestRoll {

    public SavingThrow() {
        super("saving_throw");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new SavingThrow();
        clone.joinSubeventJson(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new SavingThrow();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        this.calculateDifficultyClass(context);
        if (this.subeventJson.get("damage") != null) {
            this.getBaseDamage(context);
        }
        RPGLObject source = UUIDTable.getObject((String) this.subeventJson.get("source"));
        this.addBonus(source.getAbilityModifier(context, (String) this.subeventJson.get("save_ability")));
        this.addBonus(source.getSaveProficiencyBonus(context, (String) this.subeventJson.get("save_ability")));
    }

    @Override
    public void invoke(RPGLContext context) throws Exception {
        super.invoke(context);
        this.roll();
        this.checkForReroll(context); // TODO eventually have this in a while loop?
        if (this.getRoll() < (Long) this.subeventJson.get("save_difficulty_class")) {
            this.resolveSaveFail(context);
        } else {
            this.resolveSavePass(context);
        }
    }

    void calculateDifficultyClass(RPGLContext context) throws Exception {
        CalculateSaveDifficultyClass calculateSaveDifficultyClass = new CalculateSaveDifficultyClass();
        String calculateSaveDifficultyClassJsonString = String.format("""
                        {
                            "subevent": "calculate_save_difficulty_class",
                            "difficulty_class_ability": "%s"
                        }
                        """,
                this.subeventJson.get("difficulty_class_ability").toString()
        );
        JsonObject calculateSaveDifficultyClassJson = JsonParser.parseObjectString(calculateSaveDifficultyClassJsonString);
        calculateSaveDifficultyClass.joinSubeventJson(calculateSaveDifficultyClassJson);
        RPGLObject source = this.getSource();
        calculateSaveDifficultyClass.setSource(source);
        calculateSaveDifficultyClass.prepare(context);
        calculateSaveDifficultyClass.setTarget(source);
        calculateSaveDifficultyClass.invoke(context);
        this.subeventJson.put("save_difficulty_class", calculateSaveDifficultyClass.get());
    }

    void getBaseDamage(RPGLContext context) throws Exception {
        /*
         * Collect base typed damage dice and bonuses
         */
        BaseDamageDiceCollection baseDamageDiceCollection = new BaseDamageDiceCollection();
        String baseDamageDiceCollectionJsonString = String.format("""
                        {
                            "subevent": "base_damage_dice_collection",
                            "damage": %s
                        }
                        """,
                this.subeventJson.get("damage").toString()
        );
        JsonObject baseDamageDiceCollectionJson = JsonParser.parseObjectString(baseDamageDiceCollectionJsonString);
        baseDamageDiceCollection.joinSubeventJson(baseDamageDiceCollectionJson);
        baseDamageDiceCollection.prepare(context);
        baseDamageDiceCollection.invoke(context);

        /*
         * Roll base damage dice
         */
        BaseDamageRoll baseDamageRoll = new BaseDamageRoll();
        String baseDamageRollJsonString = String.format("""
                        {
                            "subevent": "base_damage_roll",
                            "damage": %s
                        }
                        """,
                baseDamageDiceCollection.getDamageDiceCollection().toString()
        );
        JsonObject baseDamageRollJson = JsonParser.parseObjectString(baseDamageRollJsonString);
        baseDamageRoll.joinSubeventJson(baseDamageRollJson);
        baseDamageRoll.prepare(context);
        baseDamageRoll.invoke(context);

        /*
         * Replace damage key with base damage calculation
         */
        this.subeventJson.put("damage", baseDamageRoll.getDamage());
    }

    void resolveSavePass(RPGLContext context) throws Exception {
        this.resolvePassDamage(context);
        this.resolveNestedSubevents(context, "pass");
    }

    void resolveSaveFail(RPGLContext context) throws Exception {
        this.resolveFailDamage(context);
        this.resolveNestedSubevents(context, "fail");
    }

    JsonObject getTargetDamage(RPGLContext context) throws Exception {
        /*
         * Collect target typed damage dice and bonuses
         */
        TargetDamageDiceCollection targetDamageDiceCollection = new TargetDamageDiceCollection();
        String targetDamageDiceCollectionJsonString = """
                {
                    "subevent": "target_damage_dice_collection",
                    "damage": [ ]
                }
                """; // TODO can the empty array be moved to prepare()?
        JsonObject targetDamageDiceCollectionJson = JsonParser.parseObjectString(targetDamageDiceCollectionJsonString);
        targetDamageDiceCollection.joinSubeventJson(targetDamageDiceCollectionJson);
        targetDamageDiceCollection.prepare(context);
        targetDamageDiceCollection.invoke(context);

        /*
         * Roll target damage dice
         */
        TargetDamageRoll targetDamageRoll = new TargetDamageRoll();
        String targetDamageRollJsonString = String.format("""
                        {
                            "subevent": "target_damage_roll",
                            "damage": %s
                        }
                        """,
                targetDamageDiceCollection.getDamageDiceCollection().toString()
        );
        JsonObject targetDamageRollJson = JsonParser.parseObjectString(targetDamageRollJsonString);
        targetDamageRoll.joinSubeventJson(targetDamageRollJson);
        targetDamageRoll.prepare(context);
        targetDamageRoll.invoke(context);

        return targetDamageRoll.getDamage();
    }

    void resolvePassDamage(RPGLContext context) throws Exception {
        JsonObject baseDamage = (JsonObject) this.subeventJson.get("damage");
        String damageOnPass = (String) this.subeventJson.get("damage_on_pass");
        if (baseDamage != null && !"none".equals(damageOnPass)) {
            /*
             * Add base and target damage into final damage quantities
             */
            for (Map.Entry<String, Object> targetDamageEntry : getTargetDamage(context).entrySet()) {
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

            this.deliverDamage(context);
        }
    }

    void resolveFailDamage(RPGLContext context) throws Exception {
        JsonObject baseDamage = (JsonObject) this.subeventJson.get("damage");
        if (baseDamage != null) {
            for (Map.Entry<String, Object> targetDamageEntry : getTargetDamage(context).entrySet()) {
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

            this.deliverDamage(context);
        }
    }

    void resolveNestedSubevents(RPGLContext context, String passOrFail) throws Exception {
        JsonArray subeventJsonArray = (JsonArray) this.subeventJson.get(passOrFail);
        if (subeventJsonArray != null) {
            for (Object subeventJsonElement : subeventJsonArray) {
                JsonObject subeventJson = (JsonObject) subeventJsonElement;
                Subevent subevent = Subevent.SUBEVENTS.get((String) subeventJson.get("subevent")).clone(subeventJson);
                subevent.prepare(context);
                subevent.invoke(context);
            }
        }
    }

    void deliverDamage(RPGLContext context) throws Exception {
        DamageDelivery damageDelivery = new DamageDelivery();
        String damageDeliveryJsonString = String.format("""
                        {
                            "subevent": "damage_delivery",
                            "damage": %s
                        }
                        """,
                this.subeventJson.get("damage").toString()
        );
        JsonObject damageDeliveryJson = JsonParser.parseObjectString(damageDeliveryJsonString);
        damageDelivery.joinSubeventJson(damageDeliveryJson);
        damageDelivery.setSource(this.getSource());
        damageDelivery.setTarget(this.getTarget());
        this.getTarget().receiveDamage(context, damageDelivery);
    }

}
