package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.HealingCollection;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * This Function is dedicated to adding some amount of healing to a HealingCollection Subevent.
 *
 * @author Calvin Withun
 */
public class AddHealing extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddHealing.class);

    public AddHealing() {
        super("add_healing");
    }

    @Override
    public void execute(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context) throws Exception {
        super.verifyFunction(functionJson);
        if (subevent instanceof HealingCollection healingCollection) {
            healingCollection.addHealing(this.unpackHealing(functionJson.getJsonObject("healing")));
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

    /**
     * This helper method unpacks and returns the healing stored by the functionJson provided to this Function.
     *
     * @param healing a JsonObject containing compacted healing data
     * @return a deep clone of healing in an unpacked format
     */
    JsonObject unpackHealing(JsonObject healing) {
        JsonObject unpackedHealing = new JsonObject();
        unpackedHealing.putInteger("bonus", Objects.requireNonNullElse(healing.getInteger("bonus"), 0));
        JsonArray healingDice = healing.getJsonArray("dice");
        JsonArray unpackedHealingDice = new JsonArray();
        for (int i = 0; i < healingDice.size(); i++) {
            JsonObject healingDie = healingDice.getJsonObject(i);
            JsonObject unpackedHealingDie = new JsonObject() {{
                this.putInteger("size", healingDie.getInteger("size"));
                this.putJsonArray("determined", healingDie.getJsonArray("determined"));
            }};
            for (int k = 0; k < healingDie.getInteger("count"); k++) {
                unpackedHealingDice.addJsonObject(unpackedHealingDie.deepClone());
            }
        }
        unpackedHealing.putJsonArray("dice", unpackedHealingDice);
        return unpackedHealing;
    }

}
