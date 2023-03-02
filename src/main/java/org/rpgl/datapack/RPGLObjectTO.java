package org.rpgl.datapack;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rpgl.core.RPGLObject;
import org.rpgl.core.RPGLObjectTemplate;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is used to create transfer objects between a datapack and RPGL for RPGLObjects.
 *
 * @author Calvin Withun
 */
public class RPGLObjectTO extends RPGLTaggableTO {

    // JSON property aliases
    public static final String ABILITY_SCORES_ALIAS    = "ability_scores";
    public static final String HEALTH_DATA_ALIAS       = "health_data";
    public static final String EQUIPPED_ITEMS_ALIAS    = "equipped_items";
    public static final String INVENTORY_ALIAS         = "inventory";
    public static final String EVENTS_ALIAS            = "events";
    public static final String EFFECTS_ALIAS           = "effects";
    public static final String PROFICIENCY_BONUS_ALIAS = "proficiency_bonus";

    @JsonProperty(ABILITY_SCORES_ALIAS)
    HashMap<String, Object> abilityScores;
    @JsonProperty(HEALTH_DATA_ALIAS)
    HashMap<String, Object> healthData;
    @JsonProperty(EQUIPPED_ITEMS_ALIAS)
    HashMap<String, Object> equippedItems;
    @JsonProperty(INVENTORY_ALIAS)
    ArrayList<Object> inventory;
    @JsonProperty(EVENTS_ALIAS)
    ArrayList<Object> events;
    @JsonProperty(EFFECTS_ALIAS)
    ArrayList<Object> effects;
    @JsonProperty(PROFICIENCY_BONUS_ALIAS)
    Integer proficiencyBonus;

    /**
     * Default constructor for RPGLObjectTO class.
     */
    @SuppressWarnings("unused")
    public RPGLObjectTO() {
        // this constructor is needed for jackson-databind to interface with this class
    }

    /**
     * Constructor to be used when storing data from a fully instantiated RPGLObject. Intended to be used for saving data.
     *
     * @param rpglObject a fully instantiated RPGLObject
     */
    public RPGLObjectTO(RPGLObject rpglObject) {
        super(rpglObject);
        this.abilityScores = rpglObject.getAbilityScores().asMap();
        this.healthData = rpglObject.getHealthData().asMap();
        this.equippedItems = rpglObject.getEquippedItems().asMap();
        this.inventory = rpglObject.getInventory().asList();
        this.events = rpglObject.getEvents().asList();
        this.effects = rpglObject.getEffects().asList();
        this.proficiencyBonus = rpglObject.getProficiencyBonus();
    }

    /**
     * This method translates the stored data into a RPGLObjectTemplate object.
     *
     * @return a RPGLObjectTemplate
     */
    public RPGLObjectTemplate toRPGLObjectTemplate() {
        RPGLObjectTemplate rpglObjectTemplate = new RPGLObjectTemplate() {{
            this.putJsonObject(ABILITY_SCORES_ALIAS, new JsonObject(abilityScores));
            this.putJsonObject(HEALTH_DATA_ALIAS, new JsonObject(healthData));
            this.putJsonObject(EQUIPPED_ITEMS_ALIAS, new JsonObject(equippedItems));
            this.putJsonArray(INVENTORY_ALIAS, new JsonArray(inventory));
            this.putJsonArray(EVENTS_ALIAS, new JsonArray(events));
            this.putJsonArray(EFFECTS_ALIAS, new JsonArray(effects));
            this.putInteger(PROFICIENCY_BONUS_ALIAS, proficiencyBonus);
        }};
        rpglObjectTemplate.join(super.getTemplateData());
        return rpglObjectTemplate;
    }

    /**
     * This method translates the stored data into a RPGLObject object.
     *
     * @return a RPGLObject
     */
    public RPGLObject toRPGLObject() {
        RPGLObject rpglObject = new RPGLObject() {{
            this.putJsonObject(ABILITY_SCORES_ALIAS, new JsonObject(abilityScores));
            this.putJsonObject(HEALTH_DATA_ALIAS, new JsonObject(healthData));
            this.putJsonObject(EQUIPPED_ITEMS_ALIAS, new JsonObject(equippedItems));
            this.putJsonArray(INVENTORY_ALIAS, new JsonArray(inventory));
            this.putJsonArray(EVENTS_ALIAS, new JsonArray(events));
            this.putJsonArray(EFFECTS_ALIAS, new JsonArray(effects));
            this.putInteger(PROFICIENCY_BONUS_ALIAS, proficiencyBonus);
        }};
        rpglObject.join(super.getTemplateData());
        rpglObject.join(super.getUUIDTableElementData());
        return rpglObject;
    }

}
