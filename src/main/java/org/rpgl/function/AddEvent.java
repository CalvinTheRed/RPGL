package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
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
    public void execute(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent,
                        JsonObject functionJson, RPGLContext context) throws Exception {
        super.verifyFunction(functionJson);
        if (subevent instanceof GetEvents getEvents) {
            getEvents.addEvent(functionJson.getString("event"));
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }
}
