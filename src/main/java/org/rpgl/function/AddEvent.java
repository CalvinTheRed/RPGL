package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.GetEvents;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Function is dedicated to adding a RPGLEvent datapack ID to GetEvents Subevents.
 *
 * @author Calvin Withun
 */
public class AddEvent extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddEvent.class);

    public AddEvent() {
        super("add_event");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context) throws Exception {
        if (subevent instanceof GetEvents getEvents) {
            JsonObject sourceInstructions = functionJson.getJsonObject("source");
            getEvents.addEvent(
                    functionJson.getString("event"),
                    effect.getOriginItem(),
                    sourceInstructions != null
                            ? RPGLEffect.getObject(effect, subevent, sourceInstructions).getUuid()
                            : null
            );
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }
}
