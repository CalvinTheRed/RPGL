package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.CancelableSubevent;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Function is dedicated to canceling cancelable Subevents.
 *
 * @author Calvin Withun
 */
public class CancelSubevent extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(CancelSubevent.class);

    public CancelSubevent() {
        super("cancel_subevent");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context, JsonArray originPoint) {
        if (subevent instanceof CancelableSubevent cancelableSubevent) {
            cancelableSubevent.cancel();
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }

}
