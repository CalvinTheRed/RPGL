package org.rpgl.datapack;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLItemTemplate;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is used to create transfer objects between a datapack and RPGL for RPGLItems.
 *
 * @author Calvin Withun
 */
public class RPGLItemTO extends RPGLTaggableTO {

    public static final String WEIGHT_ALIAS = "weight";
    public static final String COST_ALIAS = "cost";
    public static final String EVENTS_ALIAS = "events";
    public static final String ATTACK_BONUS_ALIAS = "attack_bonus";
    public static final String DAMAGE_BONUS_ALIAS = "damage_bonus";
    public static final String EQUIPPED_EFFECTS_ALIAS = "equipped_effects";
    public static final String EQUIPPED_RESOURCES_ALIAS = "equipped_resources";
    public static final String ARMOR_CLASS_BASE_ALIAS = "armor_class_base";
    public static final String ARMOR_CLASS_DEX_LIMIT_ALIAS = "armor_class_dex_limit";
    public static final String ARMOR_CLASS_BONUS_ALIAS = "armor_class_bonus";

    @JsonProperty(WEIGHT_ALIAS)
    Integer weight;
    @JsonProperty(COST_ALIAS)
    Integer cost;
    @JsonProperty(EVENTS_ALIAS)
    HashMap<String, Object> events;
    @JsonProperty(ATTACK_BONUS_ALIAS)
    Integer attackBonus;
    @JsonProperty(DAMAGE_BONUS_ALIAS)
    Integer damageBonus;
    @JsonProperty(EQUIPPED_EFFECTS_ALIAS)
    ArrayList<Object> equippedEffects;
    @JsonProperty(EQUIPPED_RESOURCES_ALIAS)
    ArrayList<Object> equippedResources;
    @JsonProperty(ARMOR_CLASS_BASE_ALIAS)
    Integer armorClassBase;
    @JsonProperty(ARMOR_CLASS_DEX_LIMIT_ALIAS)
    Integer armorClassDexLimit;
    @JsonProperty(ARMOR_CLASS_BONUS_ALIAS)
    Integer armorClassBonus;

    /**
     * Default constructor for RPGLItemTO class.
     */
    @SuppressWarnings("unused")
    public RPGLItemTO() {
        // this constructor is needed for jackson-databind to interface with this class
    }

    /**
     * Constructor to be used when storing data from a fully instantiated RPGLItem. Intended to be used for saving data.
     *
     * @param rpglItem a fully instantiated RPGLItem
     */
    public RPGLItemTO(RPGLItem rpglItem) {
        super(rpglItem);
        this.weight = rpglItem.getWeight();
        this.cost = rpglItem.getCost();
        this.events = rpglItem.getEvents().asMap();
        this.attackBonus = rpglItem.getAttackBonus();
        this.damageBonus = rpglItem.getDamageBonus();
        this.equippedEffects = rpglItem.getEquippedEffects().asList();
        this.equippedResources = rpglItem.getEquippedResources().asList();
        this.armorClassBase = rpglItem.getArmorClassBase();
        this.armorClassDexLimit = rpglItem.getArmorClassDexLimit();
        this.armorClassBonus = rpglItem.getArmorClassBonus();
    }

    /**
     * This method translates the stored data into a RPGLItemTemplate object.
     *
     * @return a RPGLItemTemplate
     */
    public RPGLItemTemplate toRPGLItemTemplate() {
        RPGLItemTemplate rpglItemTemplate = new RPGLItemTemplate() {{
            this.putInteger(WEIGHT_ALIAS, weight);
            this.putInteger(COST_ALIAS, cost);
            this.putJsonObject(EVENTS_ALIAS, new JsonObject(events));
            this.putInteger(ATTACK_BONUS_ALIAS, attackBonus);
            this.putInteger(DAMAGE_BONUS_ALIAS, damageBonus);
            this.putJsonArray(EQUIPPED_EFFECTS_ALIAS, new JsonArray(equippedEffects));
            this.putJsonArray(EQUIPPED_RESOURCES_ALIAS, new JsonArray(equippedResources));
            this.putInteger(ARMOR_CLASS_BASE_ALIAS, armorClassBase);
            this.putInteger(ARMOR_CLASS_DEX_LIMIT_ALIAS, armorClassDexLimit);
            this.putInteger(ARMOR_CLASS_BONUS_ALIAS, armorClassBonus);
        }};
        rpglItemTemplate.join(super.getTemplateData());
        return rpglItemTemplate;
    }

    /**
     * This method translates the stored data into a RPGLItem object.
     *
     * @return a RPGLItem
     */
    public RPGLItem toRPGLItem() {
        RPGLItem rpglItem = new RPGLItem() {{
            this.setTags(new JsonArray(tags));
            this.setWeight(weight);
            this.setCost(cost);
            this.setEvents(new JsonObject(events));
            this.setAttackBonus(attackBonus);
            this.setDamageBonus(damageBonus);
            this.setEquippedEffects(new JsonArray(equippedEffects));
            this.setEquippedResources(new JsonArray(equippedEffects));
            this.setArmorClassBase(armorClassBase);
            this.setArmorClassDexLimit(armorClassDexLimit);
            this.setArmorClassBonus(armorClassBonus);
        }};
        rpglItem.join(super.getTemplateData());
        rpglItem.join(super.getUUIDTableElementData());
        return rpglItem;
    }

}
