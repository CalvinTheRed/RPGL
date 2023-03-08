package org.rpgl.core;

import org.rpgl.datapack.RPGLItemTO;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * This class represents any artifact which might appear in an RPGLObject object's inventory. Examples of this include
 * longswords, teacups, and suits of plate armor.
 *
 * @author Calvin Withun
 */
public class RPGLItem extends RPGLTaggable {

    /**
     * Returns the weight of the RPGLItem.
     *
     * @return a Integer representing the RPGLItem's weight
     */
    public int getWeight() {
        return Objects.requireNonNullElse(this.getInteger(RPGLItemTO.WEIGHT_ALIAS), 0);
    }

    /**
     * Setter for weight.
     *
     * @param weight a new weight int
     */
    public void setWeight(int weight) {
        this.putInteger(RPGLItemTO.WEIGHT_ALIAS, weight);
    }

    /**
     * Returns the cost of the RPGLItem.
     *
     * @return a Integer representing the RPGLItem's cost
     */
    public int getCost() {
        return Objects.requireNonNullElse(this.getInteger(RPGLItemTO.COST_ALIAS), 0);
    }

    /**
     * Setter for cost.
     *
     * @param cost a new cost int
     */
    public void setCost(int cost) {
        this.putInteger(RPGLItemTO.COST_ALIAS, cost);
    }

    /**
     * Returns the proficiency tags of the RPGLObject.
     *
     * @return a JsonArray containing proficiency tags
     */
    public JsonArray getProficiencyTags() {
        return this.getJsonArray(RPGLItemTO.PROFICIENCY_TAGS_ALIAS);
    }

    /**
     * Setter for proficiency tags.
     *
     * @param proficiencyTags a new proficiency tags JsonArray
     */
    public void setProficiencyTags(JsonArray proficiencyTags) {
        this.putJsonArray(RPGLItemTO.PROFICIENCY_TAGS_ALIAS, proficiencyTags);
    }

    /**
     * Returns a list of UUIDs for RPGLEffects which are to be applied to any RPGLObject wielding the RPGLItem.
     *
     * @return a JsonArray containing RPGLEffect UUIDs
     */
    public JsonArray getWhileEquippedEffects() {
        return this.getJsonArray(RPGLItemTO.WHILE_EQUIPPED_ALIAS);
    }

    /**
     * Setter for while equipped.
     *
     * @param whileEquippedEffects a new while equipped JsonArray
     */
    public void setWhileEquippedEffects(JsonArray whileEquippedEffects) {
        this.putJsonArray(RPGLItemTO.WHILE_EQUIPPED_ALIAS, whileEquippedEffects);
    }

    /**
     * Returns the weapon properties of the RPGLObject.
     *
     * @return a JsonArray containing weapon properties
     */
    public JsonArray getWeaponProperties() {
        return this.getJsonArray(RPGLItemTO.WEAPON_PROPERTIES_ALIAS);
    }

    /**
     * Setter for weapon properties.
     *
     * @param weaponProperties a new weapon properties JsonArray
     */
    public void setWeaponProperties(JsonArray weaponProperties) {
        this.putJsonArray(RPGLItemTO.WEAPON_PROPERTIES_ALIAS, weaponProperties);
    }

    /**
     * Returns the damage values of the RPGLObject.
     *
     * @return a JsonObject containing damage values
     */
    public JsonObject getDamage() {
        return this.getJsonObject(RPGLItemTO.DAMAGE_ALIAS);
    }

    /**
     * Setter for damage.
     *
     * @param damage a new damage JsonObject
     */
    public void setDamage(JsonObject damage) {
        this.putJsonObject(RPGLItemTO.DAMAGE_ALIAS, damage);
    }

    /**
     * Returns the item's attack bonus (typically reflective of a magic weapon's bonus to attack rolls).
     *
     * @return the item's attack bonus
     */
    public int getAttackBonus() {
        return Objects.requireNonNullElse(this.getInteger(RPGLItemTO.ATTACK_BONUS_ALIAS), 0);
    }

    /**
     * Setter for attack bonus.
     *
     * @param attackBonus a new attack bonus int
     */
    public void setAttackBonus(int attackBonus) {
        this.putInteger(RPGLItemTO.ATTACK_BONUS_ALIAS, attackBonus);
    }

    /**
     * Returns the current ability scores associated with each type of weapon attack made by this weapon.
     *
     * @return a JsonObject storing ability scores mapped to modes (<code>melee</code>, <code>thrown</code>>,
     *         <code>ranged</code>>) of attacking
     */
    public JsonObject getAttackAbilities() {
        return this.getJsonObject(RPGLItemTO.ATTACK_ABILITIES_ALIAS);
    }

    /**
     * Setter for attack abilities.
     *
     * @param attackAbilities a new attack abilities JsonObject
     */
    public void setAttackAbilities(JsonObject attackAbilities) {
        this.putJsonObject(RPGLItemTO.ATTACK_ABILITIES_ALIAS, attackAbilities);
    }

    /**
     * Returns the range of the RPGLItem when used for a thrown or ranged attack.
     *
     * @return a JsonObject containing range data for thrown and ranged attacks made using the RPGLItem
     */
    public JsonObject getRange() {
        return this.getJsonObject(RPGLItemTO.RANGE_ALIAS);
    }

    /**
     * Setter for range.
     *
     * @param range a new range JsonObject
     */
    public void setRange(JsonObject range) {
        this.putJsonObject(RPGLItemTO.RANGE_ALIAS, range);
    }

    /**
     * Returns the base armor class value of the RPGLItem. This does not include any dexterity bonuses which are allowed.
     *
     * @return the base armor class of the RPGLItem
     */
    public Integer getArmorClassBase() {
        return this.getInteger(RPGLItemTO.ARMOR_CLASS_BASE_ALIAS);
    }

    /**
     * Setter for armor class base.
     *
     * @param armorClassBase a new armor class base int
     */
    public void setArmorClassBase(int armorClassBase) {
        this.putInteger(RPGLItemTO.ARMOR_CLASS_BASE_ALIAS, armorClassBase);
    }

    /**
     * Returns the maximum dex bonus which can be applied to an RPGLObject's armor class while wearing this RPGLItem. If
     * null, there is no upper limit which can be applied in this way.
     *
     * @return the maximum armor class bonus which can be applied from an RPGLObject's dex modifier
     */
    public Integer getArmorClassDexLimit() {
        return this.getInteger(RPGLItemTO.ARMOR_CLASS_DEX_LIMIT_ALIAS);
    }

    /**
     * Setter for armor class dex limit.
     *
     * @param armorClassDexLimit a new armor class dex limit int
     */
    public void setArmorClassDexLimit(int armorClassDexLimit) {
        this.putInteger(RPGLItemTO.ARMOR_CLASS_DEX_LIMIT_ALIAS, armorClassDexLimit);
    }

    /**
     * Returns the bonus to an RPGLObject's armor class when wielding the RPGLItem as a shield.
     *
     * @return an integer bonus to armor class
     */
    public Integer getArmorClassBonus() {
        return this.getInteger(RPGLItemTO.ARMOR_CLASS_BONUS_ALIAS);
    }

    /**
     * Setter for armor class bonus.
     *
     * @param armorClassBonus a new armor class bonus int
     */
    public void setArmorClassBonus(int armorClassBonus) {
        this.putInteger(RPGLItemTO.ARMOR_CLASS_BONUS_ALIAS, armorClassBonus);
    }

    // =================================================================================================================
    // Methods not derived directly from transfer objects
    // =================================================================================================================

    /**
     * This method returns all RPGLEffect objects which are meant to apply to whichever RPGLObject is wielding the
     * RPGLItem.
     *
     * @return a List of RPGLEffect objects
     */
    public List<RPGLEffect> getWhileEquippedEffectObjects() {
        JsonArray whileEquippedUuids = this.getJsonArray(RPGLItemTO.WHILE_EQUIPPED_ALIAS);
        List<RPGLEffect> effects = new ArrayList<>();
        for (int i = 0; i < whileEquippedUuids.size(); i++) {
            effects.add(UUIDTable.getEffect(whileEquippedUuids.getString(i)));
        }
        return effects;
    }

    /**
     * Returns the damage dice associated with the weapon for attacks of the given type, or <code>null</code> if the
     * passed attack type does not apply to the RPGLItem.
     *
     * @param attackType a type of weapon attack <code>("melee", "ranged", "thrown")</code>
     * @return the damage associated with the RPGLItem for the given attackType
     */
    public JsonArray getDamageForAttackType(String attackType) {
        return this.getDamage().getJsonArray(attackType);
    }

    /**
     * Returns the ability score the weapon is currently set to use for attacks of the given type.
     *
     * @param attackType a type of weapon attack <code>("melee", "ranged", "thrown")</code>
     * @return an ability score, or <code>null</code> if an invalid attackType for the RPGLItem was passed
     */
    public String getAttackAbility(String attackType) {
        return this.getAttackAbilities().getString(attackType);
    }

    /**
     * This method defines the default attack abilities for RPGLItems. <i>Melee</i> and <i>thrown</i> attacks default
     * to using <i>str</i>, unless they have the <i>finesse</i> property, in which case they default to <i>dex</i>.
     * <i>Ranged</i> attacks always default to <i>dex</i>.
     */
    public void defaultAttackAbilities() {
        HashMap<String, Object> attackAbilities = new HashMap<>();
        if (this.getWeaponProperties().asList().contains("ranged")) {
            attackAbilities.put("ranged", "dex");
        }
        if (this.getWeaponProperties().asList().contains("finesse")) {
            attackAbilities.put("melee", "dex");
            attackAbilities.put("thrown", "dex");
        } else {
            attackAbilities.put("melee", "str");
            attackAbilities.put("thrown", "str");
        }
        this.putJsonObject(RPGLItemTO.ATTACK_ABILITIES_ALIAS, new JsonObject(attackAbilities));
    }

    /**
     * This method assigns an ability score to be used for attack and damage rolls made using this item for a given
     * attack type.
     *
     * @param attackType a type of weapon attack <code>("melee", "ranged", "thrown")</code>
     * @param ability    an ability score reference <code>("str", "dex", etc.)</code>
     */
    public void setAttackAbility(String attackType, String ability) {
        this.getAttackAbilities().asMap().put(attackType, ability);
    }

    /**
     * Updates the source and target of RPGLEffect objects associated with this RPGLItem. This is to be used whenever a
     * RPGLObject equips the RPGLItem.
     *
     * @param wielder an RPGLObject which has equipped this RPGLItem
     */
    public void updateEquippedEffects(RPGLObject wielder) {
        JsonArray whileEquippedEffects = this.getWhileEquippedEffects();
        for (int i = 0; i < whileEquippedEffects.size(); i++) {
            String effectUuid = whileEquippedEffects.getString(i);
            RPGLEffect effect = UUIDTable.getEffect(effectUuid);
            effect.setSource(wielder);
            effect.setTarget(wielder);
        }
    }

}
