package org.rpgl.subevent;

import org.rpgl.json.JsonObject;

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
     * This method returns the healing delivered to <code>target</code>.
     *
     * @return an integer representing a quantity of healing
     */
    public int getHealing() {
        return this.json.getInteger("healing");
    }

}
