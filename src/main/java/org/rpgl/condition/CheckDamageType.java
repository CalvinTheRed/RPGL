package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

/**
 * This Condition is dedicated to checking the damage type indicated by a DamageAffinity Subevent.
 *
 * @author Calvin Withun
 */
public class CheckDamageType extends Condition {

    public CheckDamageType() {
        super("check_damage_type");
    }

    @Override
    public boolean evaluate(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent,
                            JsonObject conditionJson, RPGLContext context) throws Exception {
        super.verifyCondition(conditionJson);
        return conditionJson.getString("type").equals(subevent.json.getString("type"));
    }

}
