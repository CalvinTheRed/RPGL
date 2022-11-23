package org.rpgl.subevent;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;

public abstract class DamageDiceCollection extends Subevent {

    public DamageDiceCollection(String subeventId) {
        super(subeventId);
    }

    public boolean includesDamageType(String damageType) {
        JsonArray damageDiceArray = (JsonArray) this.subeventJson.get("damage");
        if (damageDiceArray != null) {
            for (Object damageDiceElement : damageDiceArray) {
                JsonObject damageDice = (JsonObject) damageDiceElement;
                if (damageDice.get("type").equals(damageType)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addTypedDamage(JsonArray typedDamageArray) {
        try {
            for (Object typedDamageElement : typedDamageArray) {
                JsonObject typedDamage = (JsonObject) typedDamageElement;
                if (this.includesDamageType((String) typedDamage.get("type"))) {
                    this.addExistingTypedDamage(typedDamage);
                } else {
                    this.addNewTypedDamage(typedDamage);
                }
            }
        } catch (JsonFormatException e) {
            throw new RuntimeException("An unexpected error occurred", e);
        }
    }

    void addExistingTypedDamage(JsonObject newTypedDamage) throws JsonFormatException {
        String damageType = (String) newTypedDamage.get("type");
        JsonObject typedDamage = (JsonObject) this.subeventJson.seek(String.format("""
                        damage[{"type":"%s"}]""",
                damageType
        ));

        /*
         * Add new damage dice, if any exist
         */
        JsonArray typedDamageDice = (JsonArray) typedDamage.get("dice");
        JsonArray newTypedDamageDice = (JsonArray) newTypedDamage.get("dice");
        if (typedDamageDice == null) {
            typedDamageDice = new JsonArray();
            typedDamage.put("dice", typedDamageDice);
        }
        if (newTypedDamageDice == null) {
            newTypedDamageDice = new JsonArray();
        }
        typedDamageDice.addAll(newTypedDamageDice);

        /*
         * Add extra damage bonus, if it exists
         */
        Long typedDamageBonus = (Long) typedDamage.get("bonus");
        Long extraTypedDamageBonus = (Long) newTypedDamage.get("bonus");
        if (typedDamageBonus == null) {
            typedDamageBonus = 0L;
        }
        if (extraTypedDamageBonus == null) {
            extraTypedDamageBonus = 0L;
        }
        typedDamage.put("bonus", typedDamageBonus + extraTypedDamageBonus);
    }

    void addNewTypedDamage(JsonObject typedDamage) {
        JsonArray typedDamageArray = (JsonArray) this.subeventJson.get("damage");
        typedDamageArray.add(typedDamage);
    }

    void doubleDice() throws JsonFormatException {
        for (Object typedDamageObjectElement : this.getDamageDiceCollection()) {
            JsonObject typedDamageObject = (JsonObject) typedDamageObjectElement;
            JsonArray typedDamageDice = (JsonArray) typedDamageObject.get("dice");
            typedDamageDice.addAll(JsonParser.parseArrayString(typedDamageDice.toString()));
        }
    }

    public JsonArray getDamageDiceCollection() {
        return (JsonArray) this.subeventJson.get("damage");
    }

}
