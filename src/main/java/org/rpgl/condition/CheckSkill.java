package org.rpgl.condition;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.AbilityCheck;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * This Condition is dedicated to checking the skill being used for a AbilityCheck.
 *
 * @author Calvin Withun
 */
public class CheckSkill extends Condition {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckSkill.class);

    public CheckSkill() {
        super("check_skill");
    }

    @Override
    public boolean run(RPGLEffect effect, Subevent subevent, JsonObject conditionJson, RPGLContext context, JsonArray originPoint) {
        if (subevent instanceof AbilityCheck abilityCheck) {
            return Objects.equals(abilityCheck.getSkill(), conditionJson.getString("skill"));
        }
        LOGGER.warn("Can not evaluate condition for " + subevent.getClass());
        return false;
    }

}
