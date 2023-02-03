package org.rpgl.function;

import org.rpgl.core.RPGLObject;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Subevent;

public class DummyFunction extends Function {

    public static int counter;

    static {
        counter = 0;
    }

    @Override
    public void execute(RPGLObject source, RPGLObject target, Subevent subevent, JsonObject functionJson) throws FunctionMismatchException {
        super.verifyFunction("dummy_function", functionJson);
        DummyFunction.counter++;
    }

    public static void resetCounter() {
        DummyFunction.counter = 0;
    }

}
