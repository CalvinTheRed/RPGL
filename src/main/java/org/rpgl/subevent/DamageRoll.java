package org.rpgl.subevent;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLObject;
import org.rpgl.math.Die;

public abstract class DamageRoll extends Subevent {

    public DamageRoll(String subeventId) {
        super(subeventId);
    }

    @Override
    public void prepare(RPGLObject source) {
        this.roll();
    }

    public void roll() {
        JsonArray typedDamageArray = (JsonArray) this.subeventJson.get("damage");
        for (Object typedDamageElement : typedDamageArray) {
            JsonObject typedDamage = (JsonObject) typedDamageElement;
            JsonArray typedDamageDieArray = (JsonArray) typedDamage.get("dice");
            if (typedDamageDieArray == null) {
                typedDamageDieArray = new JsonArray();
            }

            for (Object typedDamageDieElement : typedDamageDieArray) {
                JsonObject typedDamageDie = (JsonObject) typedDamageDieElement;
                long size = (Long) typedDamageDie.get("size");
                long roll = Die.roll(size);
                typedDamageDie.put("roll", roll);
            }
        }
    }

    public void rerollTypedDiceLessThanOrEqualTo(long threshhold, String damageType) {
        JsonArray typedDamageArray = (JsonArray) this.subeventJson.get("damage");
        for (Object typedDamageElement : typedDamageArray) {
            JsonObject typedDamage = (JsonObject) typedDamageElement;
            if (damageType == null || damageType.equals(typedDamage.get("type"))) {
                JsonArray typedDamageDieArray = (JsonArray) typedDamage.get("dice");
                if (typedDamageDieArray == null) {
                    typedDamageDieArray = new JsonArray();
                }

                for (Object typedDamageDieElement : typedDamageDieArray) {
                    JsonObject typedDamageDie = (JsonObject) typedDamageDieElement;
                    if ((Long) typedDamageDie.get("roll") <= threshhold) {
                        long size = (Long) typedDamageDie.get("size");
                        long roll = Die.roll(size);
                        typedDamageDie.put("roll", roll);
                    }
                }
            }
        }
    }

    public void setTypedDiceLessThanOrEqualTo(long threshhold, long faceValue, String damageType) {
        JsonArray typedDamageArray = (JsonArray) this.subeventJson.get("damage");
        for (Object typedDamageElement : typedDamageArray) {
            JsonObject typedDamage = (JsonObject) typedDamageElement;
            if (damageType == null || damageType.equals(typedDamage.get("type"))) {
                JsonArray typedDamageDieArray = (JsonArray) typedDamage.get("dice");
                if (typedDamageDieArray == null) {
                    typedDamageDieArray = new JsonArray();
                }

                for (Object typedDamageDieElement : typedDamageDieArray) {
                    JsonObject typedDamageDie = (JsonObject) typedDamageDieElement;
                    if ((Long) typedDamageDie.get("roll") <= threshhold) {
                        typedDamageDie.put("roll", faceValue);
                    }
                }
            }
        }
    }

    public JsonObject getBaseDamage() {
        JsonObject baseDamage = new JsonObject();
        JsonArray typedDamageArray = (JsonArray) this.subeventJson.get("damage");
        for (Object typedDamageElement : typedDamageArray) {
            JsonObject typedDamage = (JsonObject) typedDamageElement;
            JsonArray typedDamageDieArray = (JsonArray) typedDamage.get("dice");
            if (typedDamageDieArray == null) {
                typedDamageDieArray = new JsonArray();
            }
            Long typedDamageBonus = (Long) typedDamage.get("bonus");
            if (typedDamageBonus == null) {
                typedDamageBonus = 0L;
            }

            long sum = typedDamageBonus;
            for (Object typedDamageDieElement : typedDamageDieArray) {
                JsonObject typedDamageDie = (JsonObject) typedDamageDieElement;
                sum += (Long) typedDamageDie.get("roll");
            }
            baseDamage.put((String) typedDamage.get("type"), sum);
        }
        return baseDamage;
    }

}
