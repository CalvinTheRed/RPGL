package org.rpgl.subevent;

import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

/**
 * This Subevent is dedicated to delivering a quantity of healing to an RPGLObject.
 * TODO is this Subevent really necessary?
 * <br>
 * <br>
 * Source: an RPGLObject performing healing
 * <br>
 * Target: an RPGLObject receiving healing
 *
 * @author Calvin Withun
 */
public class HealingDelivery extends Subevent {

    public HealingDelivery() {
        super("healing_delivery");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new HealingDelivery();
        clone.joinSubeventData(this.json);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new HealingDelivery();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    /**
     * Maximizes all healing dice contained in this subevent.
     */
    public void maximizeHealingDice() {
        JsonArray healingArray = this.json.getJsonArray("healing");
        for (int i = 0; i < healingArray.size(); i++) {
            JsonArray dice = healingArray.getJsonObject(i).getJsonArray("dice");
            for (int j = 0; j < dice.size(); j++) {
                JsonObject die = dice.getJsonObject(j);
                die.putInteger("roll", die.getInteger("size"));
            }
        }
    }

    /**
     * This method returns the healing delivered to <code>target</code>.
     *
     * @return an integer representing a quantity of healing
     */
    public int getHealing() {
        JsonArray healingArray = this.json.getJsonArray("healing");
        int healing = 0;
        for (int i = 0; i < healingArray.size(); i++) {
            JsonObject healingJson = healingArray.getJsonObject(i);
            healing += healingJson.getInteger("bonus");
            JsonArray dice = healingJson.getJsonArray("dice");
            for (int j = 0; j < dice.size(); j++) {
                JsonObject die = dice.getJsonObject(j);
                healing += die.getInteger("roll");
            }
        }
        return healing;
    }

}
