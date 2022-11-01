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
        System.out.println("New RPGLObject being created with data: " + data);
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

    public boolean processSubevent(Subevent subevent)
            throws ConditionMismatchException, FunctionMismatchException {
        boolean wasSubeventProcessed = false;
        for (RPGLEffect effect : getEffects()) {
            wasSubeventProcessed |= effect.processSubevent(subevent.getSource(), subevent.getTarget(), subevent);
        }
        return wasSubeventProcessed;
    }

    public boolean addEffect(RPGLEffect effect) {
        JsonArray effects = (JsonArray) this.get("effects");
        return effects.add(effect.get("uuid"));
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

    public Long getProficiencyBonus(RPGLContext context) throws Exception {
        CalculateProficiencyModifier calculateProficiencyModifier = new CalculateProficiencyModifier();
        String calculateProficiencyModifierJsonString = "{" +
                        "\"subevent\":\"calculate_proficiency_modifier\"" +
                        "}";
        JsonObject calculateProficiencyModifierJson = JsonParser.parseObjectString(calculateProficiencyModifierJsonString);
        calculateProficiencyModifier.joinSubeventJson(calculateProficiencyModifierJson);
        calculateProficiencyModifier.setSource(this);
        calculateProficiencyModifier.prepare(context);
        calculateProficiencyModifier.setTarget(this);
        calculateProficiencyModifier.invoke(context);
        return calculateProficiencyModifier.get();
    }

    public Long getAbilityModifier(RPGLContext context, String ability) throws Exception {
        CalculateAbilityScore calculateAbilityScore = new CalculateAbilityScore();
        String calculateAbilityScoreJsonString = String.format("{" +
                        "\"subevent\":\"calculate_ability_score\"," +
                        "\"ability\":\"%s\"" +
                        "}",
                ability
        );
        JsonObject calculateAbilityScoreJson = JsonParser.parseObjectString(calculateAbilityScoreJsonString);
        calculateAbilityScore.joinSubeventJson(calculateAbilityScoreJson);
        calculateAbilityScore.setSource(this);
        calculateAbilityScore.prepare(context);
        calculateAbilityScore.setTarget(this);
        calculateAbilityScore.invoke(context);
        return getAbilityModifier(calculateAbilityScore.get());
    }

    static Long getAbilityModifier(long abilityScore) {
        if (abilityScore < 10L) {
            // integer division rounds toward zero, so abilityScore must be
            // adjusted to calculate the correct values for negative modifiers
            abilityScore --;
        }
        return (abilityScore - 10L) / 2L;
    }

    public Long getSaveProficiencyBonus(RPGLContext context, String saveAbility) throws Exception {
        GetSaveProficiency getSaveProficiency = new GetSaveProficiency();
        String getSaveProficiencyJsonString = String.format("{" +
                        "\"subevent\":\"get_save_proficiency\"," +
                        "\"save_ability\":\"%s\"" +
                        "}",
                saveAbility
        );
        JsonObject getSaveProficiencyJson = JsonParser.parseObjectString(getSaveProficiencyJsonString);
        getSaveProficiency.joinSubeventJson(getSaveProficiencyJson);
        getSaveProficiency.setSource(this);
        getSaveProficiency.prepare(context);
        getSaveProficiency.setTarget(this);
        getSaveProficiency.invoke(context);
        if (getSaveProficiency.getIsProficient()) {
            return this.getProficiencyBonus(context);
        } else {
            return 0L;
        }
    }

    public void receiveDamage(RPGLContext context, DamageDelivery damageDelivery) throws Exception {
        JsonObject damageObject = damageDelivery.getDamage();
        for (Map.Entry<String, Object> damageObjectEntry : damageObject.entrySet()) {
            String damageType = damageObjectEntry.getKey();
            Long damage = (Long) damageObjectEntry.getValue();

            DamageAffinity damageAffinity = new DamageAffinity();
            String damageAffinityJsonString = String.format("{" +
                            "\"subevent\":\"damage_affinity\"," +
                            "\"type\":\"%s\"" +
                            "}",
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
        System.out.println("REDUCING HIT POINTS! (removing " + amount + " from " + currentHitPoints + " for " + this.get("uuid") + ")");
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

}
