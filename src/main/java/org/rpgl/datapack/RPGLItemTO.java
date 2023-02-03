package org.rpgl.datapack;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLItemTemplate;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.List;
import java.util.Map;

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

    // armor property aliases
    public static final String ARMOR_CLASS_BASE_ALIAS = "armor_class_base";
    public static final String ARMOR_CLASS_DEX_LIMIT_ALIAS = "armor_class_dex_limit";

    // shield property aliases
    public static final String ARMOR_CLASS_BONUS_ALIAS = "armor_class_bonus";

    @JsonProperty(TAGS_ALIAS)
    List<Object> tags;
    @JsonProperty(WEIGHT_ALIAS)
    Integer weight;
    @JsonProperty(COST_ALIAS)
    Integer cost;

    @JsonProperty(PROFICIENCY_TAGS_ALIAS)
    List<Object> proficiencyTags;
    @JsonProperty(WHILE_EQUIPPED_ALIAS)
    List<Object> whileEquipped;

    @JsonProperty(WEAPON_PROPERTIES_ALIAS)
    List<Object> weaponProperties;
    @JsonProperty(DAMAGE_ALIAS)
    Map<String, Object> damage;
    @JsonProperty(ATTACK_BONUS_ALIAS)
    Integer attackBonus;
    @JsonProperty(ATTACK_ABILITIES_ALIAS)
    Map<String, Object> attackAbilities;

    @JsonProperty(ARMOR_CLASS_BASE_ALIAS)
    Integer armorClassBase;
    @JsonProperty(ARMOR_CLASS_DEX_LIMIT_ALIAS)
    Integer armorClassDexLimit;

    @JsonProperty(ARMOR_CLASS_BONUS_ALIAS)
    Integer armorClassBonus;

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
        this.armorClassBase = rpglItem.getInteger(ARMOR_CLASS_BASE_ALIAS);
        this.armorClassDexLimit = rpglItem.getInteger(ARMOR_CLASS_DEX_LIMIT_ALIAS);
        this.armorClassBonus = rpglItem.getInteger(ARMOR_CLASS_BONUS_ALIAS);
    }

    public RPGLItemTemplate toRPGLItemTemplate() {
        RPGLItemTemplate rpglItemTemplate = new RPGLItemTemplate();
        rpglItemTemplate.asMap().putAll(super.getTemplateData());
        rpglItemTemplate.putJsonArray(TAGS_ALIAS, new JsonArray(tags));
        rpglItemTemplate.putInteger(WEIGHT_ALIAS, weight);
        rpglItemTemplate.putInteger(COST_ALIAS, cost);
        rpglItemTemplate.putJsonArray(PROFICIENCY_TAGS_ALIAS, new JsonArray(proficiencyTags));
        rpglItemTemplate.putJsonArray(WHILE_EQUIPPED_ALIAS, new JsonArray(whileEquipped));
        rpglItemTemplate.putJsonArray(WEAPON_PROPERTIES_ALIAS, new JsonArray(weaponProperties));
        rpglItemTemplate.putJsonObject(DAMAGE_ALIAS, new JsonObject(damage));
        rpglItemTemplate.putInteger(ATTACK_BONUS_ALIAS, attackBonus);
        rpglItemTemplate.putInteger(ARMOR_CLASS_BASE_ALIAS, armorClassBase);
        rpglItemTemplate.putInteger(ARMOR_CLASS_DEX_LIMIT_ALIAS, armorClassDexLimit);
        rpglItemTemplate.putInteger(ARMOR_CLASS_BONUS_ALIAS, armorClassBonus);
        return rpglItemTemplate;
    }

    public RPGLItem toRPGLItem() {
        RPGLItem rpglItem = new RPGLItem();
        rpglItem.join(super.getTemplateData());
        rpglItem.putJsonArray(TAGS_ALIAS, new JsonArray(tags));
        rpglItem.putInteger(WEIGHT_ALIAS, weight);
        rpglItem.putInteger(COST_ALIAS, cost);
        rpglItem.putJsonArray(PROFICIENCY_TAGS_ALIAS, new JsonArray(proficiencyTags));
        rpglItem.putJsonArray(WHILE_EQUIPPED_ALIAS, new JsonArray(whileEquipped));
        rpglItem.putJsonArray(WEAPON_PROPERTIES_ALIAS, new JsonArray(weaponProperties));
        rpglItem.putJsonObject(DAMAGE_ALIAS, new JsonObject(damage));
        rpglItem.putInteger(ATTACK_BONUS_ALIAS, attackBonus);
        rpglItem.putJsonObject(ATTACK_ABILITIES_ALIAS, new JsonObject(attackAbilities));
        rpglItem.putInteger(ARMOR_CLASS_BASE_ALIAS, armorClassBase);
        rpglItem.putInteger(ARMOR_CLASS_DEX_LIMIT_ALIAS, armorClassDexLimit);
        rpglItem.putInteger(ARMOR_CLASS_BONUS_ALIAS, armorClassBonus);
        return rpglItem;
    }

}
