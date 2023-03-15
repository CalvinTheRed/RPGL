package org.rpgl.core;

import org.rpgl.datapack.RPGLItemTO;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.util.Map;
import java.util.Objects;

/**
 * This class is used to contain a "template" to be used in the creation of new RPGLItem objects. Data stored in this
 * object is copied and then processed to create a specific RPGLItem defined somewhere in a datapack.
 *
 * @author Calvin Withun
 */
public class RPGLItemTemplate extends JsonObject {

    /**
     * This object represents the damage dealt by an improvised weapon for an arbitrary attack type.
     */
    private static final JsonArray DEFAULT_TEMPLATE_ITEM_DAMAGE = new JsonArray() {{
        this.addJsonObject(new JsonObject() {{
            this.putString("damage_type", "bludgeoning");
            this.putJsonArray("dice", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putInteger("count", 1);
                    this.putInteger("size", 4);
                    this.putJsonArray("determined", new JsonArray() {{
                        this.addInteger(2);
                    }});
                }});
            }});
            this.putInteger("bonus", 0);
        }});
    }};

    /**
     * This object represents the range of an improvised weapon when it is thrown.
     */
    private static final JsonObject DEFAULT_RANGE = new JsonObject() {{
        this.putInteger("normal", 20);
        this.putInteger("long", 60);
    }};

    /**
     * Constructs a new RPGLItem object corresponding to the contents of the RPGLItemTemplate object. The new object is
     * registered to the UUIDTable class when it is constructed.
     *
     * @return a new RPGLItem object
     */
    public RPGLItem newInstance() {
        RPGLItem item = new RPGLItem();
        item.join(this);
        item.asMap().putIfAbsent(RPGLItemTO.WEIGHT_ALIAS, 0);
        item.asMap().putIfAbsent(RPGLItemTO.ATTACK_BONUS_ALIAS, 0);
        item.asMap().putIfAbsent(RPGLItemTO.COST_ALIAS, 0);
        if (item.getJsonObject(RPGLItemTO.RANGE_ALIAS).asMap().isEmpty()) {
            item.putJsonObject(RPGLItemTO.RANGE_ALIAS, DEFAULT_RANGE);
        }
        item.defaultAttackAbilities();
        processImprovisedTags(item);
        processEquippedEffects(item);
        setDefaultItemDamage(item);
        processItemDamage(item);
        UUIDTable.register(item);
        return item;
    }

    /**
     * This helper method adds the <code>improvised_melee</code> and <code>improvised_thrown</code> weapon property
     * tags to the item, as appropriate based on its other weapon property tags.
     *
     * @param item a RPGLItem being created by this object
     */
    static void processImprovisedTags(RPGLItem item) {
        JsonArray weaponProperties = item.getWeaponProperties();
        if (!weaponProperties.asList().contains("melee")) {
            weaponProperties.addString("improvised_melee");
        }
        if (!weaponProperties.asList().contains("thrown")) {
            weaponProperties.addString("improvised_thrown");
        }
    }

    /**
     * This helper method converts effectId's in an RPGLItemTemplate's while_equipped array to RPGLEffects. The UUID's
     * of these new RPGLEffects replace the original array contents.
     *
     * @param item a RPGLItem being created by this object
     */
    static void processEquippedEffects(RPGLItem item) {
        JsonArray equippedEffectsIdArray = Objects.requireNonNullElse(item.removeJsonArray(RPGLItemTO.WHILE_EQUIPPED_ALIAS), new JsonArray());
        JsonArray equippedEffectsUuidArray = new JsonArray();
        for (int i = 0; i < equippedEffectsIdArray.size(); i++) {
            String effectId = equippedEffectsIdArray.getString(i);
            RPGLEffect effect = RPGLFactory.newEffect(effectId);
            equippedEffectsUuidArray.addString(effect.getUuid());
        }
        item.putJsonArray(RPGLItemTO.WHILE_EQUIPPED_ALIAS, equippedEffectsUuidArray);
    }

    /**
     * This helper method sets the damage for an item in the cases where it would function as an improvised weapon.
     *
     * @param item a RPGLItem being created by this object
     */
    static void setDefaultItemDamage(RPGLItem item) {
        JsonObject damage = new JsonObject();
        damage.join(Objects.requireNonNullElse(item.removeJsonObject(RPGLItemTO.DAMAGE_ALIAS), new JsonObject()));

        if (damage.asMap().isEmpty()) {
            damage.putJsonArray("melee", DEFAULT_TEMPLATE_ITEM_DAMAGE.deepClone());
            damage.putJsonArray("thrown", DEFAULT_TEMPLATE_ITEM_DAMAGE.deepClone());
        } else {
            JsonArray melee = damage.getJsonArray("melee");
            if (melee == null || melee.asList().isEmpty()) {
                damage.putJsonArray("melee", DEFAULT_TEMPLATE_ITEM_DAMAGE.deepClone());
            }
            JsonArray thrown = damage.getJsonArray("thrown");
            if (thrown == null || thrown.asList().isEmpty()) {
                damage.putJsonArray("thrown", DEFAULT_TEMPLATE_ITEM_DAMAGE.deepClone());
            }
        }
        item.putJsonObject(RPGLItemTO.DAMAGE_ALIAS, damage);
    }

    /**
     * This helper method unpacks the condensed representation of damage dice in a RPGLItemTemplate into multiple dice
     * objects in accordance with the <code>count</code> field.
     *
     * @param item a RPGLItem being created by this object
     */
    static void processItemDamage(RPGLItem item) {
        JsonObject damage = item.getDamage();
        for (Map.Entry<String, ?> damageObjectEntry : damage.asMap().entrySet()) {
            String attackType = damageObjectEntry.getKey();
            JsonArray attackTypeDamageArray = damage.getJsonArray(attackType);
            for (int i = 0; i < attackTypeDamageArray.size(); i++) {
                JsonObject attackTypeDamageObject = attackTypeDamageArray.getJsonObject(i);
                JsonArray templateDamageDiceArray = attackTypeDamageObject.removeJsonArray("dice");
                JsonArray damageDiceArray = new JsonArray();
                for (int k = 0; k < templateDamageDiceArray.size(); k++) {
                    JsonObject templateDamageDiceDefinition = templateDamageDiceArray.getJsonObject(k);
                    JsonObject damageDie = new JsonObject() {{
                        this.putInteger("size", templateDamageDiceDefinition.getInteger("size"));
                        this.putJsonArray("determined", templateDamageDiceDefinition.getJsonArray("determined"));
                    }};
                    for (int l = 0; l < templateDamageDiceDefinition.getInteger("count"); l++) {
                        damageDiceArray.addJsonObject(damageDie.deepClone());
                    }
                }
                attackTypeDamageObject.putJsonArray("dice", damageDiceArray);
            }
        }
    }

}
