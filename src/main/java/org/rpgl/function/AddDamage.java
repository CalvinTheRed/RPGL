package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DamageCollection;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Function is dedicated to adding to a DamageCollection Subevent. Note that while this Function can add negative
 * static bonuses, it can not add "negative dice."
 *
 * @author Calvin Withun
 */
public class AddDamage extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddDamage.class);

    public AddDamage() {
        super("add_damage");
    }

    @Override
    public void execute(RPGLObject source, RPGLObject target, Subevent subevent,
                        JsonObject functionJson, RPGLContext context) throws Exception {
        super.verifyFunction(functionJson);
        if (subevent instanceof DamageCollection damageCollection) {
            damageCollection.addTypedDamage(this.unpackCompactedDamageDice(functionJson.getJsonArray("damage")));
        } else {
            LOGGER.warn("Can not execute AddDamage function on " + subevent.getClass());
        }
    }

    JsonArray unpackCompactedDamageDice(JsonArray damageArray) {
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
