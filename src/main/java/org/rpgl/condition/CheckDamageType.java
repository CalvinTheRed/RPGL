package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
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
    public boolean evaluate(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent,
                            JsonObject conditionJson, RPGLContext context) throws Exception {
        super.verifyCondition(conditionJson);
        if (subevent instanceof DamageAffinity damageAffinity) {
            return conditionJson.getString("type").equals(damageAffinity.getDamageType());
        }
        LOGGER.warn("Can not evaluate condition for " + subevent.getClass());
        return false;
    }

}
