package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DamageCollection;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Function is dedicated to adding to a DamageCollection Subevent. Note that while this Function can add negative
 * bonuses, it can not add "negative dice."
 *
 * @author Calvin Withun
 */
public class AddDamage extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddDamage.class);

    public AddDamage() {
        super("add_damage");
    }

    @Override
    public void execute(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context) throws Exception {
        super.verifyFunction(functionJson);
        if (subevent instanceof DamageCollection damageCollection) {
            damageCollection.addTypedDamage(this.unpackDamage(functionJson.getJsonArray("damage")));
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

    /**
     * This helper method unpacks and returns the damage stored by the functionJson provided to this Function.
     *
     * @param damageArray a JsonArray containing compacted damage information
     * @return a deep clone of damageArray in an unpacked format
     */
    JsonArray unpackDamage(JsonArray damageArray) {
        JsonArray unpackedDamageArray = damageArray.deepClone();
        for (int i = 0; i < unpackedDamageArray.size(); i++) {
            JsonObject damage = unpackedDamageArray.getJsonObject(i);
            JsonArray dice = damage.removeJsonArray("dice");
            JsonArray unpackedDice = new JsonArray();
            for (int j = 0; j < dice.size(); j++) {
                JsonObject functionDamageDie = dice.getJsonObject(j);
                JsonObject damageDie = new JsonObject() {{
                   this.putInteger("size", functionDamageDie.getInteger("size"));
                   this.putJsonArray("determined", functionDamageDie.getJsonArray("determined"));
                }};
                for (int k = 0; k < functionDamageDie.getInteger("count"); k++) {
                    unpackedDice.addJsonObject(damageDie.deepClone());
                }
            }
            damage.putJsonArray("dice", unpackedDice);
        }
        return unpackedDamageArray;
    }

}
