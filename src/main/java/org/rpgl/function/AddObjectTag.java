package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.GetObjectTags;
import org.rpgl.subevent.Subevent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Function is dedicated to adding a tag to GetObjectTags Subevents.
 *
 * @author Calvin Withun
 */
public class AddObjectTag extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddObjectTag.class);

    public AddObjectTag() {
        super("add_object_tag");
    }

    @Override
    public void execute(RPGLObject effectSource, RPGLObject effectTarget, Subevent subevent,
                        JsonObject functionJson, RPGLContext context) throws Exception {
        super.verifyFunction(functionJson);
        if (subevent instanceof GetObjectTags getObjectTags) {
            getObjectTags.addObjectTag(functionJson.getString("tag"));
        } else {
            LOGGER.warn("Can not execute function on " + subevent.getClass());
        }
    }
}
