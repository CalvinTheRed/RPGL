package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

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
    public void execute(RPGLObject source, RPGLObject target, Subevent subevent, JsonObject functionJson, RPGLContext context) throws FunctionMismatchException {
        super.verifyFunction(functionJson);
        subevent.addTag(functionJson.getString("tag"));
    }

}
