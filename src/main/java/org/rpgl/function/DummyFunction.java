package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLObject;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

/**
 * This class is a dummy function only intended to be used during testing. Its only behavior is to increment a static
 * counter variable to verify it executed at all.
 *
 * @author Calvin Withun
 */
public class DummyFunction extends Function {

    public static int counter = 0;

    @Override
    public void execute(RPGLObject source, RPGLObject target, Subevent subevent, JsonObject functionJson, RPGLContext context) throws FunctionMismatchException {
        super.verifyFunction("dummy_function", functionJson);
        DummyFunction.counter++;
    }

    /**
     * Reset the testing counter for this class.
     */
    public static void resetCounter() {
        DummyFunction.counter = 0;
    }

}
