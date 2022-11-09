package org.rpgl.core;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.rpgl.uuidtable.UUIDTable;

import java.util.Collections;

/**
 * This class contains a JSON template defining a particular type of RPGLItem. It is not intended to be used for any
 * purpose other than constructing new RPGLItem objects.
 *
 * @author Calvin Withun
 */
public class RPGLItemTemplate extends JsonObject {

    /**
     * A copy-constructor for the RPGLItemTemplate class.
     *
     * @param data the data to be copied to this object
     */
    public RPGLItemTemplate(JsonObject data) {
        this.join(data);
    }

    /**
     * This method returns a new RPGLItem object derived from the JSON template stored in the calling object.
     *
     * @return a new RPGLItem object
     */
    public RPGLItem newInstance() {
        RPGLItem item = new RPGLItem(this);
        processWhileEquipped(item);
        processOptionalFields(item);
        processDefaultAttackAbilities(item);
        UUIDTable.register(item);
        return item;
    }

    /**
     * This helper method converts effectId's in an RPGLItemTemplate's while_equipped array to RPGLEffects. The UUID's of
     * these new RPGLEffects replace the original array contents.
     *
     * @param item the item being processed.
     */
    static void processWhileEquipped(RPGLItem item) {
        Object keyValue = item.remove("while_equipped");
        JsonArray whileEquippedIdArray = (JsonArray) keyValue;
        JsonArray whileEquippedUuidArray = new JsonArray();
        for (Object whileEquippedIdElement : whileEquippedIdArray) {
            String effectId = (String) whileEquippedIdElement;
            RPGLEffect effect = RPGLFactory.newEffect(effectId);
            whileEquippedUuidArray.add(effect.get("uuid"));
        }
        item.put("while_equipped", whileEquippedUuidArray);
    }

    static void processOptionalFields(RPGLItem item) {
        if (item.get("weapon_properties") == null) {
            item.put("weapon_properties", new JsonArray(Collections.singleton("improvised")));
        }
        if (item.get("proficiency_tags") == null) {
            item.put("proficiency_tags", new JsonArray(Collections.singleton("improvised")));
        }
        if (item.get("damage") == null || ((JsonArray) item.get("damage")).isEmpty()) {
            try {
                String damageArrayString = """
                        [
                            {
                                "type": "bludgeoning",
                                "dice": [
                                    { "size": 4, "determined": 1 }
                                ]
                            }
                        ]
                        """;
                JsonArray damageArray = JsonParser.parseArrayString(damageArrayString);
                item.put("damage", damageArray);
            } catch (JsonFormatException e) {
                // This code should never execute
                throw new RuntimeException("An unexpected error occurred", e);
            }
        }
    }

    static void processDefaultAttackAbilities(RPGLItem item) {
        JsonObject attackAbilities = new JsonObject();
        if (item.getWeaponProperties().contains("ranged")) {
            attackAbilities.put("ranged", "dex");
        }
        if (item.getWeaponProperties().contains("finesse")) {
            attackAbilities.put("melee", "dex");
            attackAbilities.put("thrown", "dex");
        } else {
            attackAbilities.put("melee", "str");
            attackAbilities.put("thrown", "str");
        }
        item.put("attack_abilities", attackAbilities);
    }

}
