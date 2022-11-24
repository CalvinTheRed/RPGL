package org.rpgl.subevent;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;

public class CriticalHitDamageDiceCollection extends DamageDiceCollection {

    public CriticalHitDamageDiceCollection() {
        super("critical_hit_damage_dice_collection");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new CriticalHitDamageDiceCollection();
        clone.joinSubeventJson(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject subeventJson) {
        Subevent clone = new CriticalHitDamageDiceCollection();
        clone.joinSubeventJson(subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    void doubleDice() throws JsonFormatException {
        for (Object typedDamageObjectElement : this.getDamageDiceCollection()) {
            JsonObject typedDamageObject = (JsonObject) typedDamageObjectElement;
            JsonArray typedDamageDice = (JsonArray) typedDamageObject.get("dice");
            typedDamageDice.addAll(JsonParser.parseArrayString(typedDamageDice.toString()));
        }
    }

}
