package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DamageRoll;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaximizeDamage extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaximizeDamage.class);

    public MaximizeDamage() {
        super("maximize_damage");
    }

    @Override
    public void execute(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent,
                        JsonObject functionJson, RPGLContext context) throws Exception {
        super.verifyFunction(functionJson);
        if (subevent instanceof DamageRoll damageRoll) {
            damageRoll.maximizeTypedDamageDice(functionJson.getString("type"));
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

}
