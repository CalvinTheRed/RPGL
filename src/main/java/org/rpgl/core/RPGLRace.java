package org.rpgl.core;

import org.rpgl.datapack.DatapackContent;
import org.rpgl.datapack.RPGLRaceTO;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class represents a RPG race such as Elf or Goblin, and contains all necessary information to level up as a
 * member of that race.
 *
 * @author Calvin Withun
 */
public class RPGLRace extends DatapackContent {

    /**
     * Getter for ability score increases.
     *
     * @return a JSON object indicating the ability score increases offered by this race
     */
    public JsonObject getAbilityScoreIncreases() {
        return this.getJsonObject(RPGLRaceTO.ABILITY_SCORE_INCREASES_ALIAS);
    }

    /**
     * Setter for ability score increases.
     *
     * @param abilityScoreIncreases a new JSON object indicating the ability score increases offered by this race
     */
    public void setAbilityScoreIncreases(JsonArray abilityScoreIncreases) {
        this.putJsonArray(RPGLRaceTO.ABILITY_SCORE_INCREASES_ALIAS, abilityScoreIncreases);
    }

    /**
     * Getter for features.
     *
     * @return a JSON object indicating all features offered by this race at each level
     */
    public JsonObject getFeatures() {
        return this.getJsonObject(RPGLRaceTO.FEATURES_ALIAS);
    }

    /**
     * Setter for features.
     *
     * @param features a new JSON object indicating all features offered by this race at each level
     */
    public void setFeatures(JsonObject features) {
        this.putJsonObject(RPGLRaceTO.FEATURES_ALIAS, features);
    }

    // =================================================================================================================
    // methods not derived directly from json data
    // =================================================================================================================

    /**
     * Grants the passed object features offered by this race for the passed level.
     *
     * @param object an RPGLObject being leveled up
     * @param choices a JSON object indicating any choices required to choose features offered by this race
     * @param level the passed object's new level
     */
    public void levelUpRPGLObject(RPGLObject object, JsonObject choices, int level) {
        JsonObject features = this.getFeatures().getJsonObject(Integer.toString(level));
        if (features != null) {
            JsonObject gainedFeatures = Objects.requireNonNullElse(features.getJsonObject("gain"), new JsonObject());
            this.grantGainedEffects(object, gainedFeatures, choices);
            this.grantGainedEvents(object, gainedFeatures);
            this.grantGainedResources(object, gainedFeatures);
            JsonObject lostFeatures = Objects.requireNonNullElse(features.getJsonObject("lose"), new JsonObject());
            this.revokeLostEffects(object, lostFeatures);
            this.revokeLostEvents(object, lostFeatures);
            this.revokeLostResources(object, lostFeatures);
        }
    }

    /**
     * Grants the passed object the passed features (effects only).
     *
     * @param object an RPGLObject to gain the passed features
     * @param gainedFeatures a JSON object of race features gained at a particular level
     * @param choices a JSON object indicating any choices required to choose from the passed features
     */
    void grantGainedEffects(RPGLObject object, JsonObject gainedFeatures, JsonObject choices) {
        JsonArray effects = Objects.requireNonNullElse(gainedFeatures.getJsonArray("effects"), new JsonArray());
        for (int i = 0; i < effects.size(); i++) {
            Object effectElement = effects.asList().get(i);
            if (effectElement instanceof String effectId) {
                RPGLEffect effect = RPGLFactory.newEffect(effectId);
                effect.setSource(object);
                effect.setTarget(object);
                object.addEffect(effect);
            } else if (effectElement instanceof HashMap<?,?>) {
                JsonObject effectsAlternatives = effects.getJsonObject(i);
                String name = effectsAlternatives.getString("name");
                int count = Objects.requireNonNullElse(effectsAlternatives.getInteger("count"), 1);
                JsonArray options = effectsAlternatives.getJsonArray("options");
                JsonArray choiceIndices = new JsonArray();
                for (Map.Entry<String, ?> choicesEntry : choices.asMap().entrySet()) {
                    if (Objects.equals(name, choicesEntry.getKey())) {
                        choiceIndices = choices.getJsonArray(choicesEntry.getKey());
                        break;
                    }
                }
                for (int j = 0; j < count; j++) {
                    String effectId = options.getString(choiceIndices.getInteger(j));
                    RPGLEffect effect = RPGLFactory.newEffect(effectId);
                    effect.setSource(object);
                    effect.setTarget(object);
                    object.addEffect(effect);
                }
            }
        }
    }

    /**
     * Grants the passed object the passed features (events only).
     *
     * @param object an RPGLObject to gain the passed features
     * @param gainedFeatures a JSON object of race features gained at a particular level
     */
    void grantGainedEvents(RPGLObject object, JsonObject gainedFeatures) {
        JsonArray events = Objects.requireNonNullElse(gainedFeatures.getJsonArray("events"), new JsonArray());
        object.getEvents().asList().addAll(events.asList());
    }

    /**
     * Grants the passed object the passed features (resources only).
     *
     * @param object an RPGLObject to gain the passed features
     * @param gainedFeatures a JSON object of race features gained at a particular level
     */
    void grantGainedResources(RPGLObject object, JsonObject gainedFeatures) {
        JsonArray resources = Objects.requireNonNullElse(gainedFeatures.getJsonArray("resources"), new JsonArray());
        for (int i = 0; i < resources.size(); i++) {
            JsonObject resourceData = resources.getJsonObject(i);
            int count = Objects.requireNonNullElse(resourceData.getInteger("count"), 1);
            for (int j = 0; j < count; j++) {
                RPGLResource resource = RPGLFactory.newResource(resourceData.getString("resource"));
                object.addResource(resource);
            }
        }
    }

    /**
     * Removes the passed features from the passed object (effects only).
     *
     * @param object an RPGLObject to gain the passed features
     * @param lostFeatures a JSON object of race features lost at a particular level
     */
    void revokeLostEffects(RPGLObject object, JsonObject lostFeatures) {
        JsonArray lostEffects = Objects.requireNonNullElse(lostFeatures.getJsonArray("effects"), new JsonArray());
        JsonArray effects = object.getEffects();
        for (int i = 0; i < lostEffects.size(); i++) {
            String lostEffectId = lostEffects.getString(i);
            for (int j = 0; j < effects.size(); j++) {
                RPGLEffect effect = UUIDTable.getEffect(effects.getString(j));
                if (Objects.equals(lostEffectId, effect.getId())) {
                    object.getEffects().asList().remove(effect.getUuid());
                    UUIDTable.unregister(effect.getUuid());
                    break;
                }
            }
        }
    }

    /**
     * Removes the passed features from the passed object (events only).
     *
     * @param object an RPGLObject to gain the passed features
     * @param lostFeatures a JSON object of race features lost at a particular level
     */
    void revokeLostEvents(RPGLObject object, JsonObject lostFeatures) {
        JsonArray lostEvents = Objects.requireNonNullElse(lostFeatures.getJsonArray("events"), new JsonArray());
        object.getEvents().asList().removeAll(lostEvents.asList());
    }

    /**
     * Removes the passed features from the passed object (resources only).
     *
     * @param object an RPGLObject to gain the passed features
     * @param lostFeatures a JSON object of race features lost at a particular level
     */
    void revokeLostResources(RPGLObject object, JsonObject lostFeatures) {
        JsonArray lostResources = Objects.requireNonNullElse(lostFeatures.getJsonArray("resources"), new JsonArray());
        JsonArray resources = object.getResources();
        for (int i = 0; i < lostResources.size(); i++) {
            String lostResourceId = lostResources.getString(i);
            for (int j = 0; j < resources.size(); j++) {
                RPGLResource resource = UUIDTable.getResource(resources.getString(j));
                if (Objects.equals(lostResourceId, resource.getId())) {
                    object.getResources().asList().remove(resource.getUuid());
                    UUIDTable.unregister(resource.getUuid());
                    break;
                }
            }
        }
    }

}
