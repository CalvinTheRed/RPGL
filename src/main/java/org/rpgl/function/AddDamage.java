package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Calculation;
import org.rpgl.subevent.CriticalHitDamageCollection;
import org.rpgl.subevent.DamageCollection;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Function is dedicated to adding to a DamageCollection or a CriticalHitDamageCollection Subevent.
 *
 * @author Calvin Withun
 */
public class AddDamage extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddDamage.class);

    public AddDamage() {
        super("add_damage");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context) throws Exception {
        if (subevent instanceof DamageCollection damageCollection) {
            JsonArray damageArray = functionJson.getJsonArray("damage");
            for (int i = 0; i < damageArray.size(); i++) {
                JsonObject damageElement = damageArray.getJsonObject(i);
                JsonObject damage = Calculation.processBonusJson(effect, subevent, damageElement, context);
                String damageType = damageElement.getString("damage_type");
                if (damageType == null) {
                    damage.putString("damage_type", damageCollection.getDamageCollection().getJsonObject(0).getString("damage_type"));
                } else {
                    damage.putString("damage_type", damageType);
                }
                damageCollection.addDamage(damage);
            }
        } else if (subevent instanceof CriticalHitDamageCollection criticalHitDamageCollection) {
            JsonArray damageArray = functionJson.getJsonArray("damage");
            for (int i = 0; i < damageArray.size(); i++) {
                JsonObject damageElement = damageArray.getJsonObject(i);
                JsonObject damage = Calculation.processBonusJson(effect, subevent, damageElement, context);
                String damageType = damageElement.getString("damage_type");
                if (damageType == null) {
                    damage.putString("damage_type", criticalHitDamageCollection.getDamageCollection().getJsonObject(0).getString("damage_type"));
                } else {
                    damage.putString("damage_type", damageType);
                }
                criticalHitDamageCollection.addDamage(damage);
            }
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }
}
