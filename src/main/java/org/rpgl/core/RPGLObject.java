package org.rpgl.core;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.subevent.CalculateAbilityScore;
import org.rpgl.subevent.CalculateProficiencyModifier;
import org.rpgl.subevent.DamageAffinity;
import org.rpgl.subevent.Subevent;
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

    public void invokeEvent(RPGLObject[] targets, RPGLEvent event) throws Exception {
        // assume that any necessary resources have already been spent
        JsonArray subeventJsonArray = (JsonArray) event.get("subevents");
        for (Object subeventJsonElement : subeventJsonArray) {
            JsonObject subeventJson = (JsonObject) subeventJsonElement;
            String subeventId = (String) subeventJson.get("subevent");
            Subevent subevent = Subevent.SUBEVENTS.get(subeventId).clone(subeventJson);
            subevent.prepare(this);
            for (RPGLObject target : targets) {
                subevent.invoke(this, target);
            }
        }
    }

    public boolean processSubevent(RPGLObject source, RPGLObject target, Subevent subevent)
            throws ConditionMismatchException, FunctionMismatchException {
        boolean wasSubeventProcessed = false;
        for (RPGLEffect effect : getEffects()) {
            wasSubeventProcessed |= effect.processSubevent(source, target, subevent);
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

    RPGLEffect[] getEffects() {
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

    public Long getProficiencyBonus() throws Exception {
        CalculateProficiencyModifier calculateProficiencyModifier = new CalculateProficiencyModifier();
        String calculateProficiencyModifierJsonString = "{" +
                        "\"subevent\":\"calculate_proficiency_modifier\"" +
                        "}";
        JsonObject calculateProficiencyModifierJson = JsonParser.parseObjectString(calculateProficiencyModifierJsonString);
        calculateProficiencyModifier.joinSubeventJson(calculateProficiencyModifierJson);
        calculateProficiencyModifier.prepare(this);
        calculateProficiencyModifier.invoke(this, this);
        return calculateProficiencyModifier.get();
    }

    public Long getAbilityModifier(String ability) throws Exception {
        CalculateAbilityScore calculateAbilityScore = new CalculateAbilityScore();
        String calculateAbilityScoreJsonString = String.format("{" +
                        "\"subevent\":\"calculate_ability_score\"," +
                        "\"ability\":\"%s\"" +
                        "}",
                ability
        );
        JsonObject calculateAbilityScoreJson = JsonParser.parseObjectString(calculateAbilityScoreJsonString);
        calculateAbilityScore.joinSubeventJson(calculateAbilityScoreJson);
        calculateAbilityScore.prepare(this);
        calculateAbilityScore.invoke(this, this);
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

    public Long getSaveProficiencyBonus(String ability) {
        // TODO this should use a Subevent eventually...
        return 0L;
    }

    public void receiveDamage(RPGLObject source, JsonObject damageObject) throws Exception {
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
            damageAffinity.prepare(this);
            damageAffinity.invoke(source, this);
            String affinity =  damageAffinity.getAffinity();

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

}
