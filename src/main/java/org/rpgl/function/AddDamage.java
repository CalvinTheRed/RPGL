package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Calculation;
import org.rpgl.subevent.CriticalHitDamageCollection;
import org.rpgl.subevent.DamageCollection;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context,
                    List<RPGLResource> resources) throws Exception {
        if (subevent instanceof DamageCollection damageCollection) {
            JsonArray damageArray = functionJson.getJsonArray("damage");
            for (int i = 0; i < damageArray.size(); i++) {
                JsonObject damageElement = damageArray.getJsonObject(i);
                JsonObject damage = Calculation.processBonusJson(effect, subevent, damageElement, context);
                damage.putString("damage_type", damageElement.getString("damage_type"));
                damageCollection.addDamage(damage);
            }
        } else if (subevent instanceof CriticalHitDamageCollection criticalHitDamageCollection) {
            JsonArray damageArray = functionJson.getJsonArray("damage");
            for (int i = 0; i < damageArray.size(); i++) {
                JsonObject damageElement = damageArray.getJsonObject(i);
                JsonObject damage = Calculation.processBonusJson(effect, subevent, damageElement, context);
                damage.putString("damage_type", damageElement.getString("damage_type"));
                criticalHitDamageCollection.addDamage(damage);
            }
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }
}
