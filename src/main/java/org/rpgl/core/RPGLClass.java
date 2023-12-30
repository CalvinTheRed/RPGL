package org.rpgl.core;

import org.rpgl.datapack.DatapackContent;
import org.rpgl.datapack.RPGLClassTO;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class represents a RPG class such as Wizard or Fighter, and contains all necessary information to progress along
 * within that class.
 *
 * @author Calvin Withun
 */
public class RPGLClass extends DatapackContent {

    /**
     * Getter for subclass level.
     *
     * @return the level at which this class gains a subclass
     */
    public Integer getSubclassLevel() {
        return super.getInteger(RPGLClassTO.SUBCLASS_LEVEL_ALIAS);
    }

    /**
     * Setter for subclass level.
     *
     * @param subclassLevel the new level at which this class gains a subclass
     */
    public void setSubclassLevel(int subclassLevel) {
        super.putInteger(RPGLClassTO.SUBCLASS_LEVEL_ALIAS, subclassLevel);
    }

    /**
     * Getter for ability score increases.
     *
     * @return an array of levels at which this class grants ability score increases
     */
    public JsonArray getAbilityScoreIncreases() {
        return super.getJsonArray(RPGLClassTO.ABILITY_SCORE_INCREASES_ALIAS);
    }

    /**
     * Setter for ability score increases.
     *
     * @param abilityScoreIncreases the new array of levels at which this class grants ability score increases
     */
    public void setAbilityScoreIncreases(JsonArray abilityScoreIncreases) {
        super.putJsonArray(RPGLClassTO.ABILITY_SCORE_INCREASES_ALIAS, abilityScoreIncreases);
    }

    /**
     * Getter for multiclassing requirements.
     *
     * @return an array of multiclassing requirements which must be met to multiclass into or out of this class
     */
    public JsonArray getMulticlassingRequirements() {
        return super.getJsonArray(RPGLClassTO.MULTICLASSING_REQUIREMENTS_ALIAS);
    }

    /**
     * Setter for multiclassing requirements.
     *
     * @param multiclassingRequirements the new array of multiclassing requirements which must be met to multiclass into
     *                                  or out of this class
     */
    public void setMulticlassingRequirements(JsonArray multiclassingRequirements) {
        super.putJsonArray(RPGLClassTO.MULTICLASSING_REQUIREMENTS_ALIAS, multiclassingRequirements);
    }

    /**
     * Getter for nested classes.
     *
     * @return an array of class IDs for classes nested within this class
     */
    public JsonObject getNestedClasses() {
        return super.getJsonObject(RPGLClassTO.NESTED_CLASSES_ALIAS);
    }

    /**
     * Setter for nested classes.
     *
     * @param nestedClasses the new array of class IDs for classes nested within this class
     */
    public void setNestedClasses(JsonObject nestedClasses) {
        super.putJsonObject(RPGLClassTO.NESTED_CLASSES_ALIAS, nestedClasses);
    }

    /**
     * Getter for starting features.
     *
     * @return a JSON object indicating features granted by this class if this class is assigned as an object's first
     * level
     */
    public JsonObject getStartingFeatures() {
        return super.getJsonObject(RPGLClassTO.STARTING_FEATURES_ALIAS);
    }

    /**
     * Setter for starting features.
     *
     * @param startingFeatures the new JSON object indicating features granted by this class if this class is assigned
     *                         as an object's first level
     */
    public void setStartingFeatures(JsonObject startingFeatures) {
        super.putJsonObject(RPGLClassTO.STARTING_FEATURES_ALIAS, startingFeatures);
    }

    /**
     * Getter for features.
     *
     * @return a JSON object indicating features to be gained or lost at specified levels in this class
     */
    public JsonObject getFeatures() {
        return super.getJsonObject(RPGLClassTO.FEATURES_ALIAS);
    }

    /**
     * Setter for features.
     *
     * @param features the new JSON object indicating features to be gained or lost at specified levels in this class
     */
    public void setFeatures(JsonObject features) {
        super.putJsonObject(RPGLClassTO.FEATURES_ALIAS, features);
    }

    // =================================================================================================================
    // methods not derived directly from json data
    // =================================================================================================================

    /**
     * Grants the passed object the starting features specified by this class.
     *
     * @param object an RPGLObject to be granted this class's starting features
     * @param choices a JSON object indicating any choices required to choose starting features in this class
     */
    public void grantStartingFeatures(RPGLObject object, JsonObject choices) {
        this.grantGainedEvents(object, this.getStartingFeatures());
        this.grantGainedEffects(object, this.getStartingFeatures(), choices);
        this.grantGainedResources(object, this.getStartingFeatures());
        this.levelUpRPGLObject(object, choices);
    }

    /**
     * Levels up an object in this class.
     *
     * @param object an RPGLObject to be leveled up in this class
     * @param choices a JSON object indicating any choices required to choose features in this class on level-up
     */
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
    }

    /**
     * This helper method increments an objejct's level in this class.
     *
     * @param object an RPGLObject
     * @return the new level of the object in this class
     */
    int incrementRPGLObjectLevel(RPGLObject object) {
        // TODO check for meeting multiclassing requirements
        int level = object.getLevel(this.getId()) + 1;
        if (level == 1) {
            object.getClasses().addJsonObject(new JsonObject() {{
                this.putString("name", getName());
                this.putString("id", getId());
                this.putInteger("level", level);
                this.putJsonObject("additional_nested_classes", new JsonObject());
            }});
        } else {
            JsonArray classes = object.getClasses();
            for (int i = 0; i < classes.size(); i++) {
                JsonObject classData = classes.getJsonObject(i);
                if (Objects.equals(super.getId(), classData.getString("id"))) {
                    classData.putInteger("level", level);
                    break;
                }
            }
        }
        return level;
    }

    /**
     * Grants the passed object the passed features (effects only).
     *
     * @param object an RPGLObject to gain the passed features
     * @param gainedFeatures a JSON object of class features gained at a particular level
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
     * @param gainedFeatures a JSON object of class features gained at a particular level
     */
    void grantGainedEvents(RPGLObject object, JsonObject gainedFeatures) {
        JsonArray events = Objects.requireNonNullElse(gainedFeatures.getJsonArray("events"), new JsonArray());
        object.getEvents().asList().addAll(events.asList());
    }

    /**
     * Grants the passed object the passed features (resources only).
     *
     * @param object an RPGLObject to gain the passed features
     * @param gainedFeatures a JSON object of class features gained at a particular level
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
     * Revokes the passed features from the passed object (effects only).
     *
     * @param object an RPGLObject to lose the passed features
     * @param lostFeatures a JSON object of class features lost at a particular level
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
     * Revokes the passed features from the passed object (events only).
     *
     * @param object an RPGLObject to lose the passed features
     * @param lostFeatures a JSON object of class features lost at a particular level
     */
    void revokeLostEvents(RPGLObject object, JsonObject lostFeatures) {
        JsonArray lostEvents = Objects.requireNonNullElse(lostFeatures.getJsonArray("events"), new JsonArray());
        object.getEvents().asList().removeAll(lostEvents.asList());
    }

    /**
     * Revokes the passed features from the passed object (resources only).
     *
     * @param object an RPGLObject to lose the passed features
     * @param lostFeatures a JSON object of class features lost at a particular level
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
