package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DamageAffinity;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Condition is dedicated to checking the damage type indicated by a DamageAffinity Subevent.
 *
 * @author Calvin Withun
 */
public class CheckDamageType extends Condition {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckDamageType.class);

    public CheckDamageType() {
        super("check_damage_type");
    }

    @Override
    public boolean run(RPGLEffect effect, Subevent subevent, JsonObject conditionJson, RPGLContext context) {
        if (subevent instanceof DamageAffinity damageAffinity) {
            return conditionJson.getString("damage_type").equals(damageAffinity.getDamageType());
        }
        LOGGER.warn("Can not evaluate condition for " + subevent.getClass());
        return false;
    }

}
