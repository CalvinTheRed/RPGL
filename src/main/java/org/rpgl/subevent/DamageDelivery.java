package org.rpgl.subevent;

import org.rpgl.json.JsonObject;

/**
 * This Subevent is dedicated to delivering a quantity of typed damage to an RPGLObject.
 * <br>
 * <br>
 * Source: an RPGLObject dealing damage
 * <br>
 * Target: an RPGLObject suffering damage
 *
 * @author Calvin Withun
 */
public class DamageDelivery extends Subevent {

    public DamageDelivery() {
        super("damage_delivery");
    }

    @Override
    public Subevent clone() {
        Subevent clone = new DamageDelivery();
        clone.joinSubeventData(this.subeventJson);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    @Override
    public Subevent clone(JsonObject jsonData) {
        Subevent clone = new DamageDelivery();
        clone.joinSubeventData(jsonData);
        clone.modifyingEffects.addAll(this.modifyingEffects);
        return clone;
    }

    /**
     * This method returns the typed damage being delivered to <code>target</code>.
     *
     * @return an object of damage types and values
     */
    public JsonObject getDamage() {
        return this.subeventJson.getJsonObject("damage");
    }

}
