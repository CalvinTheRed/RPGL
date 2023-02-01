package org.rpgl.datapack;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLItemTemplate;

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
        this.tags = rpglItem.getList(TAGS_ALIAS);
        this.weight = rpglItem.getInteger(WEIGHT_ALIAS);
        this.cost = rpglItem.getInteger(COST_ALIAS);
        this.proficiencyTags = rpglItem.getList(PROFICIENCY_TAGS_ALIAS);
        this.whileEquipped = rpglItem.getList(WHILE_EQUIPPED_ALIAS);
        this.weaponProperties = rpglItem.getList(WEAPON_PROPERTIES_ALIAS);
        this.damage = rpglItem.getMap(DAMAGE_ALIAS);
        this.attackBonus = rpglItem.getInteger(ATTACK_BONUS_ALIAS);
        this.attackAbilities = rpglItem.getMap(ATTACK_ABILITIES_ALIAS);
        this.armorClassBase = rpglItem.getInteger(ARMOR_CLASS_BASE_ALIAS);
        this.armorClassDexLimit = rpglItem.getInteger(ARMOR_CLASS_DEX_LIMIT_ALIAS);
        this.armorClassBonus = rpglItem.getInteger(ARMOR_CLASS_BONUS_ALIAS);
    }

    public RPGLItemTemplate toRPGLItemTemplate() {
        RPGLItemTemplate rpglItemTemplate = new RPGLItemTemplate();
        rpglItemTemplate.putAll(super.getTemplateData());
        rpglItemTemplate.put(TAGS_ALIAS, tags);
        rpglItemTemplate.put(WEIGHT_ALIAS, weight);
        rpglItemTemplate.put(COST_ALIAS, cost);
        rpglItemTemplate.put(PROFICIENCY_TAGS_ALIAS, proficiencyTags);
        rpglItemTemplate.put(WHILE_EQUIPPED_ALIAS, whileEquipped);
        rpglItemTemplate.put(WEAPON_PROPERTIES_ALIAS, weaponProperties);
        rpglItemTemplate.put(DAMAGE_ALIAS, damage);
        rpglItemTemplate.put(ATTACK_BONUS_ALIAS, attackBonus);
        rpglItemTemplate.put(ARMOR_CLASS_BASE_ALIAS, armorClassBase);
        rpglItemTemplate.put(ARMOR_CLASS_DEX_LIMIT_ALIAS, armorClassDexLimit);
        rpglItemTemplate.put(ARMOR_CLASS_BONUS_ALIAS, armorClassBonus);
        return rpglItemTemplate;
    }

    public RPGLItem toRPGLItem() {
        RPGLItem rpglItem = new RPGLItem();
        rpglItem.join(super.getTemplateData());
        rpglItem.put(TAGS_ALIAS, tags);
        rpglItem.put(WEIGHT_ALIAS, weight);
        rpglItem.put(COST_ALIAS, cost);
        rpglItem.put(PROFICIENCY_TAGS_ALIAS, proficiencyTags);
        rpglItem.put(WHILE_EQUIPPED_ALIAS, whileEquipped);
        rpglItem.put(WEAPON_PROPERTIES_ALIAS, weaponProperties);
        rpglItem.put(DAMAGE_ALIAS, damage);
        rpglItem.put(ATTACK_BONUS_ALIAS, attackBonus);
        rpglItem.put(ATTACK_ABILITIES_ALIAS, attackAbilities);
        rpglItem.put(ARMOR_CLASS_BASE_ALIAS, armorClassBase);
        rpglItem.put(ARMOR_CLASS_DEX_LIMIT_ALIAS, armorClassDexLimit);
        rpglItem.put(ARMOR_CLASS_BONUS_ALIAS, armorClassBonus);
        return rpglItem;
    }

}
