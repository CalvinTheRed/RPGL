package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLResource;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

import java.util.List;

/**
 * This Function is dedicated to adding tags to Subevents.
 *
 * @author Calvin Withun
 */
public class AddSubeventTag extends Function {

    public AddSubeventTag() {
        super("add_subevent_tag");
    }

    @Override
    public void run(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context,
                    List<RPGLResource> resources) {
        subevent.addTag(functionJson.getString("tag"));
    }

}
