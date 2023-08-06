package org.rpgl.core;

import org.rpgl.datapack.DatapackContent;
import org.rpgl.datapack.RPGLClassTO;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RPGLClass extends DatapackContent {

    public Integer getHitDie() {
        return this.getInteger(RPGLClassTO.HIT_DIE_ALIAS);
    }

    public void setHitDie(int hitDie) {
        this.putInteger(RPGLClassTO.HIT_DIE_ALIAS, hitDie);
    }

    public Integer getSubclassLevel() {
        return this.getInteger(RPGLClassTO.SUBCLASS_LEVEL_ALIAS);
    }

    public void setSubclassLevel(int subclassLevel) {
        this.putInteger(RPGLClassTO.SUBCLASS_LEVEL_ALIAS, subclassLevel);
    }

    public JsonArray getAbilityScoreIncreases() {
        return this.getJsonArray(RPGLClassTO.ABILITY_SCORE_INCREASES_ALIAS);
    }

    public void setAbilityScoreIncreases(JsonArray abilityScoreIncreases) {
        this.putJsonArray(RPGLClassTO.ABILITY_SCORE_INCREASES_ALIAS, abilityScoreIncreases);
    }

    public JsonArray getMulticlassingRequirements() {
        return this.getJsonArray(RPGLClassTO.MULTICLASSING_REQUIREMENTS_ALIAS);
    }

    public void setMulticlassingRequirements(JsonArray multiclassingRequirements) {
        this.putJsonArray(RPGLClassTO.MULTICLASSING_REQUIREMENTS_ALIAS, multiclassingRequirements);
    }

    public JsonObject getClassContributions() {
        return this.getJsonObject(RPGLClassTO.CLASS_CONTRIBUTIONS_ALIAS);
    }

    public void setClassContributions(JsonObject classContributions) {
        this.putJsonObject(RPGLClassTO.CLASS_CONTRIBUTIONS_ALIAS, classContributions);
    }

    public JsonObject getStartingFeatures() {
        return this.getJsonObject(RPGLClassTO.STARTING_FEATURES_ALIAS);
    }

    public void setStartingFeatures(JsonObject startingFeatures) {
        this.putJsonObject(RPGLClassTO.STARTING_FEATURES_ALIAS, startingFeatures);
    }

    public JsonObject getFeatures() {
        return this.getJsonObject(RPGLClassTO.FEATURES_ALIAS);
    }

    public void setFeatures(JsonObject features) {
        this.putJsonObject(RPGLClassTO.FEATURES_ALIAS, features);
    }

    // =================================================================================================================
    // methods not derived directly from json data
    // =================================================================================================================

    public void setBaseClass(RPGLObject object, JsonObject choices) {
        // TODO check for existing class levels
        this.grantGainedEffects(object, this.getStartingFeatures(), choices);
        this.levelUpRPGLObject(object, choices);
    }

    public void levelUpRPGLObject(RPGLObject object, JsonObject choices) {
        int level = this.incrementRPGLObjectLevel(object);
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
        // TODO update proficiency bonus when appropriate
    }

    int incrementRPGLObjectLevel(RPGLObject object) {
        // TODO check for meeting multiclassing requirements
        int level = object.getLevel(this.getId()) + 1;
        if (level == 1) {
            object.getClasses().addJsonObject(new JsonObject() {{
                this.putString("name", getName());
                this.putString("id", getId());
                this.putInteger("level", level);
            }});
        } else {
            JsonArray classes = object.getClasses();
            for (int i = 0; i < classes.size(); i++) {
                JsonObject classData = classes.getJsonObject(i);
                if (Objects.equals(this.getId(), classData.getString("id"))) {
                    classData.putInteger("level", level);
                    break;
                }
            }
        }
        return level;
    }

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

    void grantGainedEvents(RPGLObject object, JsonObject gainedFeatures) {
        JsonArray events = Objects.requireNonNullElse(gainedFeatures.getJsonArray("events"), new JsonArray());
        object.getEvents().asList().addAll(events.asList());
    }

    void grantGainedResources(RPGLObject object, JsonObject gainedFeatures) {
        JsonArray resources = Objects.requireNonNullElse(gainedFeatures.getJsonArray("resources"), new JsonArray());
        for (int i = 0; i < resources.size(); i++) {
            RPGLResource resource = RPGLFactory.newResource(resources.getString(i));
            object.addResource(resource);
        }
    }

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

    void revokeLostEvents(RPGLObject object, JsonObject lostFeatures) {
        JsonArray lostEvents = Objects.requireNonNullElse(lostFeatures.getJsonArray("events"), new JsonArray());
        object.getEvents().asList().removeAll(lostEvents.asList());
    }

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
