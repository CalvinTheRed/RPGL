package org.rpgl.datapack;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rpgl.core.RPGLObject;
import org.rpgl.core.RPGLObjectTemplate;

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
        this.abilityScores = (Map<String, Object>) rpglObject.get(ABILITY_SCORES_ALIAS);
        this.healthData = (Map<String, Object>) rpglObject.get(HEALTH_DATA_ALIAS);
        this.equippedItems = (Map<String, Object>) rpglObject.get(EQUIPPED_ITEMS_ALIAS);
        this.inventory = (List<Object>) rpglObject.get(INVENTORY_ALIAS);
        this.events = (List<Object>) rpglObject.get(EVENTS_ALIAS);
        this.effects = (List<Object>) rpglObject.get(EFFECTS_ALIAS);
        this.proficiencyBonus = (Integer) rpglObject.get(PROFICIENCY_BONUS_ALIAS);
    }

    public RPGLObjectTemplate toRPGLObjectTemplate() {
        RPGLObjectTemplate rpglObjectTemplate = new RPGLObjectTemplate();
        rpglObjectTemplate.put(ABILITY_SCORES_ALIAS, abilityScores);
        rpglObjectTemplate.put(HEALTH_DATA_ALIAS, healthData);
        rpglObjectTemplate.put(EQUIPPED_ITEMS_ALIAS, equippedItems);
        rpglObjectTemplate.put(INVENTORY_ALIAS, inventory);
        rpglObjectTemplate.put(EVENTS_ALIAS, events);
        rpglObjectTemplate.put(EFFECTS_ALIAS, effects);
        rpglObjectTemplate.put(PROFICIENCY_BONUS_ALIAS, proficiencyBonus);
        return rpglObjectTemplate;
    }

    public RPGLObject toRPGLObject() {
        RPGLObject rpglObject = new RPGLObject();
        rpglObject.put(ABILITY_SCORES_ALIAS, abilityScores);
        rpglObject.put(HEALTH_DATA_ALIAS, healthData);
        rpglObject.put(EQUIPPED_ITEMS_ALIAS, equippedItems);
        rpglObject.put(INVENTORY_ALIAS, inventory);
        rpglObject.put(EVENTS_ALIAS, events);
        rpglObject.put(EFFECTS_ALIAS, effects);
        rpglObject.put(PROFICIENCY_BONUS_ALIAS, proficiencyBonus);
        return rpglObject;
    }

}
