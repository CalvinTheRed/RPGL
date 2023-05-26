package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DamageAffinity;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Condition is dedicated to checking if a DamageAffinity Subevent includes a given damage type.
 *
 * @author Calvin Withun
 */
public class IncludesDamageType extends Condition {

    private static final Logger LOGGER = LoggerFactory.getLogger(IncludesDamageType.class);

    public IncludesDamageType() {
        super("includes_damage_type");
    }

    @Override
    public boolean run(RPGLEffect effect, Subevent subevent, JsonObject conditionJson, RPGLContext context) {
        if (subevent instanceof DamageAffinity damageAffinity) {
            return damageAffinity.includesDamageType(conditionJson.getString("damage_type"));
        }
        LOGGER.warn("Can not evaluate condition for " + subevent.getClass());
        return false;
    }

}
