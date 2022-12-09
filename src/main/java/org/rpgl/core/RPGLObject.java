package org.rpgl.core;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.subevent.*;
import org.rpgl.uuidtable.UUIDTable;

import java.util.Map;

/**
 * RPGLObjects are objects which represent anything which might be placed on a game board (not including the game board
 * or terrain itself).
 *
 * @author Calvin Withun
 */
public class RPGLObject extends JsonObject {

    /**
     * A copy-constructor for the RPGLObject class.
     *
     * @param data the data to be copied to this object
     */
    RPGLObject(JsonObject data) {
        this.join(data);
    }

    public void invokeEvent(RPGLObject[] targets, RPGLEvent event, RPGLContext context) throws Exception {
        // assume that any necessary resources have already been spent
        JsonArray subeventJsonArray = (JsonArray) event.get("subevents");
        for (Object subeventJsonElement : subeventJsonArray) {
            JsonObject subeventJson = (JsonObject) subeventJsonElement;
            String subeventId = (String) subeventJson.get("subevent");
            Subevent subevent = Subevent.SUBEVENTS.get(subeventId).clone(subeventJson);
            subevent.setSource(this);
            subevent.prepare(context);
            for (RPGLObject target : targets) {
                Subevent targetClone = subevent.clone();
                targetClone.setTarget(target);
                targetClone.invoke(context);
            }
        }
    }

    public boolean processSubevent(Subevent subevent) throws ConditionMismatchException, FunctionMismatchException {
        boolean wasSubeventProcessed = false;
        for (RPGLEffect effect : getEffects()) {
            wasSubeventProcessed |= effect.processSubevent(subevent);
        }
        return wasSubeventProcessed;
    }

    public boolean addEffect(RPGLEffect effect) {
        JsonArray effects = (JsonArray) this.get("effects");
        return effects.add(effect.get("uuid"));
        // TODO restrict like effects from being applied to a common object
    }

    public boolean removeEffect(RPGLEffect effect) {
        JsonArray effects = (JsonArray) this.get("effects");
        return effects.remove(effect.get("uuid"));
    }

    public RPGLEffect[] getEffects() {
        JsonArray effectUuidArray = (JsonArray) this.get("effects");
        RPGLEffect[] effects = new RPGLEffect[effectUuidArray.size()];
        int i = 0;
        for (Object effectUuidElement : effectUuidArray) {
            String effectUuid = (String) effectUuidElement;
            effects[i] = UUIDTable.getEffect(effectUuid);
            i++;
        }
        return effects;
    }

    public String[] getEvents() {
        JsonArray eventsArray = (JsonArray) this.get("events");
        String[] events = new String[eventsArray.size()];
        int i = 0;
        for (Object eventId : eventsArray) {
            events[i] = (String) eventId;
            i++;
        }
        // TODO also have a Subevent dedicated to collecting additional Events
        return events;
    }

    public Long getProficiencyBonus(RPGLContext context) throws Exception {
        CalculateProficiencyModifier calculateProficiencyModifier = new CalculateProficiencyModifier();
        String calculateProficiencyModifierJsonString = """
                {
                    "subevent": "calculate_proficiency_modifier"
                }
                """;
        JsonObject calculateProficiencyModifierJson = JsonParser.parseObjectString(calculateProficiencyModifierJsonString);
        calculateProficiencyModifier.joinSubeventJson(calculateProficiencyModifierJson);
        calculateProficiencyModifier.setSource(this);
        calculateProficiencyModifier.prepare(context);
        calculateProficiencyModifier.setTarget(this);
        calculateProficiencyModifier.invoke(context);
        return calculateProficiencyModifier.get();
    }

    public long getAbilityModifierFromAbilityScore(RPGLContext context, String ability) throws Exception {
        CalculateAbilityScore calculateAbilityScore = new CalculateAbilityScore();
        String calculateAbilityScoreJsonString = String.format("""
                        {
                            "subevent": "calculate_ability_score",
                            "ability": "%s"
                        }
                        """,
                ability
        );
        JsonObject calculateAbilityScoreJson = JsonParser.parseObjectString(calculateAbilityScoreJsonString);
        calculateAbilityScore.joinSubeventJson(calculateAbilityScoreJson);
        calculateAbilityScore.setSource(this);
        calculateAbilityScore.prepare(context);
        calculateAbilityScore.setTarget(this);
        calculateAbilityScore.invoke(context);
        return getAbilityModifierFromAbilityScore(calculateAbilityScore.get());
    }

    static long getAbilityModifierFromAbilityScore(long abilityScore) {
        if (abilityScore < 10L) {
            // integer division rounds toward zero, so abilityScore must be
            // adjusted to calculate the correct values for negative modifiers
            abilityScore --;
        }
        return (abilityScore - 10L) / 2L;
    }

    public boolean isProficientInSavingThrow(RPGLContext context, String saveAbility) throws Exception {
        GetSavingThrowProficiency getSavingThrowProficiency = new GetSavingThrowProficiency();
        String getSaveProficiencyJsonString = String.format("""
                        {
                            "subevent": "get_saving_throw_proficiency",
                            "save_ability": "%s"
                        }
                        """,
                saveAbility
        );
        JsonObject getSaveProficiencyJson = JsonParser.parseObjectString(getSaveProficiencyJsonString);
        getSavingThrowProficiency.joinSubeventJson(getSaveProficiencyJson);
        getSavingThrowProficiency.setSource(this);
        getSavingThrowProficiency.prepare(context);
        getSavingThrowProficiency.setTarget(this);
        getSavingThrowProficiency.invoke(context);
        return getSavingThrowProficiency.getIsProficient();
    }

    public boolean isProficientWithWeapon(RPGLContext context, String itemUuid) throws Exception {
        GetWeaponProficiency getWeaponProficiency = new GetWeaponProficiency();
        String getWeaponProficiencyJsonString = String.format("""
                        {
                            "subevent": "get_weapon_proficiency",
                            "item": "%s"
                        }
                        """,
                itemUuid
        );
        JsonObject getSaveProficiencyJson = JsonParser.parseObjectString(getWeaponProficiencyJsonString);
        getWeaponProficiency.joinSubeventJson(getSaveProficiencyJson);
        getWeaponProficiency.setSource(this);
        getWeaponProficiency.prepare(context);
        getWeaponProficiency.setTarget(this);
        getWeaponProficiency.invoke(context);
        return getWeaponProficiency.getIsProficient();
    }

    public void receiveDamage(RPGLContext context, DamageDelivery damageDelivery) throws Exception {
        JsonObject damageObject = damageDelivery.getDamage();
        for (Map.Entry<String, Object> damageObjectEntry : damageObject.entrySet()) {
            String damageType = damageObjectEntry.getKey();
            Long damage = (Long) damageObjectEntry.getValue();

            DamageAffinity damageAffinity = new DamageAffinity();
            String damageAffinityJsonString = String.format("""
                            {
                                "subevent": "damage_affinity",
                                "type": "%s"
                            }
                            """,
                    damageType
            );
            JsonObject damageAffinityJson = JsonParser.parseObjectString(damageAffinityJsonString);
            damageAffinity.joinSubeventJson(damageAffinityJson);
            damageAffinity.setSource(this);
            damageAffinity.prepare(context);
            damageAffinity.setTarget(this);
            damageAffinity.invoke(context);
            String affinity = damageAffinity.getAffinity();

            if ("normal".equals(affinity)) {
                this.reduceHitPoints(damage);
            } else if ("resistance".equals(affinity)) {
                this.reduceHitPoints(damage / 2L);
            } else if ("vulnerability".equals(affinity)) {
                this.reduceHitPoints(damage * 2L);
            }
        }
    }

    void reduceHitPoints(long amount) {
        JsonObject healthData = (JsonObject) this.get("health_data");
        Long temporaryHitPoints = (Long) healthData.get("temporary");
        Long currentHitPoints = (Long) healthData.get("current");
        if (amount > temporaryHitPoints) {
            amount -= temporaryHitPoints;
            temporaryHitPoints = 0L;
            currentHitPoints -= amount;
        } else {
            temporaryHitPoints -= amount;
        }
        healthData.put("temporary", temporaryHitPoints);
        healthData.put("current", currentHitPoints);
        // TODO deal with 0 or negative hit points after this...
    }

    public long getBaseArmorClass(RPGLContext context) throws Exception {
        CalculateBaseArmorClass calculateBaseArmorClass = new CalculateBaseArmorClass();
        String calculateBaseArmorClassJsonString = """
                {
                    "subevent": "calculate_base_armor_class"
                }
                """;
        JsonObject calculateBaseArmorClassJson = JsonParser.parseObjectString(calculateBaseArmorClassJsonString);
        calculateBaseArmorClass.joinSubeventJson(calculateBaseArmorClassJson);
        calculateBaseArmorClass.setSource(this);
        calculateBaseArmorClass.prepare(context);
        calculateBaseArmorClass.setTarget(this);
        calculateBaseArmorClass.invoke(context);
        return calculateBaseArmorClass.get();
    }

    public void giveItem(String itemUuid) {
        JsonObject items = (JsonObject) this.get("items");
        JsonArray inventory = (JsonArray) items.get("inventory");
        inventory.add(itemUuid);
    }

    public void equipItem(String itemUuid, String equipmentSlot) {
        // TODO make a subevent for equipping an item
        JsonObject items = (JsonObject) this.get("items");
        JsonArray inventory = (JsonArray) items.get("inventory");
        if (inventory.contains(itemUuid)) {
            items.put(equipmentSlot, itemUuid);
            // TODO account for 2-handed items...
        }
    }

}
