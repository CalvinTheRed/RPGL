package org.rpgl.datapack;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rpgl.core.RPGLObject;
import org.rpgl.core.RPGLObjectTemplate;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.List;
import java.util.Map;

public class RPGLObjectTO extends UUIDTableElementTO {

    // JSON property aliases
    public static final String ABILITY_SCORES_ALIAS    = "ability_scores";
    public static final String HEALTH_DATA_ALIAS       = "health_data";
    public static final String EQUIPPED_ITEMS_ALIAS    = "equipped_items";
    public static final String INVENTORY_ALIAS         = "inventory";
    public static final String EVENTS_ALIAS            = "events";
    public static final String EFFECTS_ALIAS           = "effects";
    public static final String PROFICIENCY_BONUS_ALIAS = "proficiency_bonus";

    @JsonProperty(ABILITY_SCORES_ALIAS)
    Map<String, Object> abilityScores;
    @JsonProperty(HEALTH_DATA_ALIAS)
    Map<String, Object> healthData;
    @JsonProperty(EQUIPPED_ITEMS_ALIAS)
    Map<String, Object> equippedItems;
    @JsonProperty(INVENTORY_ALIAS)
    List<Object> inventory;
    @JsonProperty(EVENTS_ALIAS)
    List<Object> events;
    @JsonProperty(EFFECTS_ALIAS)
    List<Object> effects;
    @JsonProperty(PROFICIENCY_BONUS_ALIAS)
    Integer proficiencyBonus;

    /**
     * Constructor to be used when storing data from a fully instantiated RPGLObject. Intended to be used for saving data.
     *
     * @param rpglObject a fully instantiated RPGLObject
     */
    public RPGLObjectTO(RPGLObject rpglObject) {
        super(rpglObject);
        this.abilityScores = rpglObject.getJsonObject(ABILITY_SCORES_ALIAS).asMap();
        this.healthData = rpglObject.getJsonObject(HEALTH_DATA_ALIAS).asMap();
        this.equippedItems = rpglObject.getJsonObject(EQUIPPED_ITEMS_ALIAS).asMap();
        this.inventory = rpglObject.getJsonArray(INVENTORY_ALIAS).asList();
        this.events = rpglObject.getJsonArray(EVENTS_ALIAS).asList();
        this.effects = rpglObject.getJsonArray(EFFECTS_ALIAS).asList();
        this.proficiencyBonus = rpglObject.getInteger(PROFICIENCY_BONUS_ALIAS);
    }

    public RPGLObjectTemplate toRPGLObjectTemplate() {
        RPGLObjectTemplate rpglObjectTemplate = new RPGLObjectTemplate();
        rpglObjectTemplate.putJsonObject(ABILITY_SCORES_ALIAS, new JsonObject(abilityScores));
        rpglObjectTemplate.putJsonObject(HEALTH_DATA_ALIAS, new JsonObject(healthData));
        rpglObjectTemplate.putJsonObject(EQUIPPED_ITEMS_ALIAS, new JsonObject(equippedItems));
        rpglObjectTemplate.putJsonArray(INVENTORY_ALIAS, new JsonArray(inventory));
        rpglObjectTemplate.putJsonArray(EVENTS_ALIAS, new JsonArray(events));
        rpglObjectTemplate.putJsonArray(EFFECTS_ALIAS, new JsonArray(effects));
        rpglObjectTemplate.putInteger(PROFICIENCY_BONUS_ALIAS, proficiencyBonus);
        return rpglObjectTemplate;
    }

    public RPGLObject toRPGLObject() {
        RPGLObject rpglObject = new RPGLObject();
        rpglObject.putJsonObject(ABILITY_SCORES_ALIAS, new JsonObject(abilityScores));
        rpglObject.putJsonObject(HEALTH_DATA_ALIAS, new JsonObject(healthData));
        rpglObject.putJsonObject(EQUIPPED_ITEMS_ALIAS, new JsonObject(equippedItems));
        rpglObject.putJsonArray(INVENTORY_ALIAS, new JsonArray(inventory));
        rpglObject.putJsonArray(EVENTS_ALIAS, new JsonArray(events));
        rpglObject.putJsonArray(EFFECTS_ALIAS, new JsonArray(effects));
        rpglObject.putInteger(PROFICIENCY_BONUS_ALIAS, proficiencyBonus);
        return rpglObject;
    }

}
