package org.rpgl.subevent;

import org.rpgl.core.RPGLContext;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import java.util.Map;
import java.util.Objects;

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
        clone.joinSubeventData(this.json);
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

    @Override
    public void prepare(RPGLContext context) throws Exception {
        super.prepare(context);
        this.json.asMap().putIfAbsent("damage_proportion", "all");
    }

    /**
     * This method returns the typed damage being delivered to <code>target</code>.
     *
     * @return an object of damage types and values
     */
    public JsonObject getDamage() {
        String damageProportion = this.json.getString("damage_proportion");
        if (!"none".equals(damageProportion)) {
            JsonObject damage = new JsonObject();
            JsonArray damageArray = this.json.getJsonArray("damage");
            for (int i = 0; i < damageArray.size(); i++) {
                JsonObject damageJson = damageArray.getJsonObject(i);
                int total = damageJson.getInteger("bonus");
                JsonArray dice = damageJson.getJsonArray("dice");
                for (int j = 0; j < dice.size(); j++) {
                    total += dice.getJsonObject(j).getInteger("roll");
                }
                String damageType = damageJson.getString("damage_type");
                damage.putInteger(damageType, Objects.requireNonNullElse(damage.getInteger(damageType), 0) + total);
            }
            if ("half".equals(damageProportion)) {
                for (Map.Entry<String, ?> damageEntry : damage.asMap().entrySet()) {
                    String damageType = damageEntry.getKey();
                    damage.putInteger(damageType, damage.getInteger(damageType) / 2);
                }
            }
            return damage;
        } else {
            return new JsonObject();
        }

    }

}
