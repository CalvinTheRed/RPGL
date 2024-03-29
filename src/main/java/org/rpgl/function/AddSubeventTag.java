package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

/**
 * This Function is dedicated to adding tags to Subevents.
 *
 * @author Calvin Withun
 */
public class AddSubeventTag extends Function {

    // TODO support multiple tags?

    public AddSubeventTag() {
        super("add_subevent_tag");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context, JsonArray originPoint) {
        subevent.addTag(functionJson.getString("tag"));
    }

}
