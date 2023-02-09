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
public class RPGLItemTO extends UUIDTableElementTO {

    // universal item property aliases
    public static final String TAGS_ALIAS = "tags";
    public static final String WEIGHT_ALIAS = "weight";
    public static final String COST_ALIAS = "cost";

    // equippable item property aliases
    public static final String PROFICIENCY_TAGS_ALIAS = "proficiency_tags";
    public static final String WHILE_EQUIPPED_ALIAS = "while_equipped";

    // weapon property aliases
    public static final String WEAPON_PROPERTIES_ALIAS = "weapon_properties";
    public static final String DAMAGE_ALIAS = "damage";
    public static final String ATTACK_BONUS_ALIAS = "attack_bonus";
    public static final String ATTACK_ABILITIES_ALIAS = "attack_abilities";

    // ranged weapon property aliases
    public static final String RANGE_ALIAS = "range";

    // armor property aliases
    public static final String ARMOR_CLASS_BASE_ALIAS = "armor_class_base";
    public static final String ARMOR_CLASS_DEX_LIMIT_ALIAS = "armor_class_dex_limit";

    // shield property aliases
    public static final String ARMOR_CLASS_BONUS_ALIAS = "armor_class_bonus";

    @JsonProperty(TAGS_ALIAS)
    ArrayList<Object> tags;
    @JsonProperty(WEIGHT_ALIAS)
    Integer weight;
    @JsonProperty(COST_ALIAS)
    Integer cost;

    @JsonProperty(PROFICIENCY_TAGS_ALIAS)
    ArrayList<Object> proficiencyTags;
    @JsonProperty(WHILE_EQUIPPED_ALIAS)
    ArrayList<Object> whileEquipped;

    @JsonProperty(WEAPON_PROPERTIES_ALIAS)
    ArrayList<Object> weaponProperties;
    @JsonProperty(DAMAGE_ALIAS)
    HashMap<String, Object> damage;
    @JsonProperty(ATTACK_BONUS_ALIAS)
    Integer attackBonus;
    @JsonProperty(ATTACK_ABILITIES_ALIAS)
    HashMap<String, Object> attackAbilities;

    @JsonProperty(RANGE_ALIAS)
    HashMap<String, Object> range;

    @JsonProperty(ARMOR_CLASS_BASE_ALIAS)
    Integer armorClassBase;
    @JsonProperty(ARMOR_CLASS_DEX_LIMIT_ALIAS)
    Integer armorClassDexLimit;

    @JsonProperty(ARMOR_CLASS_BONUS_ALIAS)
    Integer armorClassBonus;

    // TODO javadoc here
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
        this.tags = rpglItem.getJsonArray(TAGS_ALIAS).asList();
        this.weight = rpglItem.getInteger(WEIGHT_ALIAS);
        this.cost = rpglItem.getInteger(COST_ALIAS);
        this.proficiencyTags = rpglItem.getJsonArray(PROFICIENCY_TAGS_ALIAS).asList();
        this.whileEquipped = rpglItem.getJsonArray(WHILE_EQUIPPED_ALIAS).asList();
        this.weaponProperties = rpglItem.getJsonArray(WEAPON_PROPERTIES_ALIAS).asList();
        this.damage = rpglItem.getJsonObject(DAMAGE_ALIAS).asMap();
        this.attackBonus = rpglItem.getInteger(ATTACK_BONUS_ALIAS);
        this.attackAbilities = rpglItem.getJsonObject(ATTACK_ABILITIES_ALIAS).asMap();
        this.range = rpglItem.getJsonObject(RANGE_ALIAS).asMap();
        this.armorClassBase = rpglItem.getInteger(ARMOR_CLASS_BASE_ALIAS);
        this.armorClassDexLimit = rpglItem.getInteger(ARMOR_CLASS_DEX_LIMIT_ALIAS);
        this.armorClassBonus = rpglItem.getInteger(ARMOR_CLASS_BONUS_ALIAS);
    }

    // TODO javadoc here
    public RPGLItemTemplate toRPGLItemTemplate() {
        RPGLItemTemplate rpglItemTemplate = new RPGLItemTemplate() {{
            this.putJsonArray(TAGS_ALIAS, new JsonArray(tags));
            this.putInteger(WEIGHT_ALIAS, weight);
            this.putInteger(COST_ALIAS, cost);
            this.putJsonArray(PROFICIENCY_TAGS_ALIAS, new JsonArray(proficiencyTags));
            this.putJsonArray(WHILE_EQUIPPED_ALIAS, new JsonArray(whileEquipped));
            this.putJsonArray(WEAPON_PROPERTIES_ALIAS, new JsonArray(weaponProperties));
            this.putJsonObject(DAMAGE_ALIAS, new JsonObject(damage));
            this.putInteger(ATTACK_BONUS_ALIAS, attackBonus);
            this.putJsonObject(RANGE_ALIAS, new JsonObject(range));
            this.putInteger(ARMOR_CLASS_BASE_ALIAS, armorClassBase);
            this.putInteger(ARMOR_CLASS_DEX_LIMIT_ALIAS, armorClassDexLimit);
            this.putInteger(ARMOR_CLASS_BONUS_ALIAS, armorClassBonus);
        }};
        rpglItemTemplate.join(super.getTemplateData());
        return rpglItemTemplate;
    }

    // TODO javadoc here
    public RPGLItem toRPGLItem() {
        RPGLItem rpglItem = new RPGLItem() {{
            this.putJsonArray(TAGS_ALIAS, new JsonArray(tags));
            this.putInteger(WEIGHT_ALIAS, weight);
            this.putInteger(COST_ALIAS, cost);
            this.putJsonArray(PROFICIENCY_TAGS_ALIAS, new JsonArray(proficiencyTags));
            this.putJsonArray(WHILE_EQUIPPED_ALIAS, new JsonArray(whileEquipped));
            this.putJsonArray(WEAPON_PROPERTIES_ALIAS, new JsonArray(weaponProperties));
            this.putJsonObject(DAMAGE_ALIAS, new JsonObject(damage));
            this.putInteger(ATTACK_BONUS_ALIAS, attackBonus);
            this.putJsonObject(ATTACK_ABILITIES_ALIAS, new JsonObject(attackAbilities));
            this.putJsonObject(RANGE_ALIAS, new JsonObject(range));
            this.putInteger(ARMOR_CLASS_BASE_ALIAS, armorClassBase);
            this.putInteger(ARMOR_CLASS_DEX_LIMIT_ALIAS, armorClassDexLimit);
            this.putInteger(ARMOR_CLASS_BONUS_ALIAS, armorClassBonus);
        }};
        rpglItem.join(super.getTemplateData());
        return rpglItem;
    }

}
