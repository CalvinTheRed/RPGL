package org.rpgl.function;

import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLEffect;
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

    public DummyFunction() {
        super("dummy_function");
    }

    @Override
    public void execute(RPGLEffect effect, Subevent subevent, JsonObject functionJson, RPGLContext context) throws Exception {
        super.verifyFunction(functionJson);
        DummyFunction.counter++;
    }

    /**
     * Reset the testing counter for this class.
     */
    public static void resetCounter() {
        DummyFunction.counter = 0;
    }

}
