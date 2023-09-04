package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.AttackRoll;
import org.rpgl.subevent.DealDamage;
import org.rpgl.subevent.SavingThrow;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * This Function is dedicated to applying vampirism to AttackRoll, DealDamage, and SavingThrow Subevents.
 *
 * @author Calvin Withun
 */
public class ApplyVampirism extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplyVampirism.class);

    public ApplyVampirism() {
        super("apply_vampirism");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context) {
        if (subevent instanceof AttackRoll || subevent instanceof DealDamage || subevent instanceof SavingThrow) {
            subevent.joinSubeventData(new JsonObject() {{
                this.putJsonObject("vampirism", Objects.requireNonNullElse(
                        functionJson.getJsonObject("vampirism"),
                        new JsonObject()
                ));
            }});
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

}
