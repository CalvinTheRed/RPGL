package org.rpgl.function;

import org.jsonutils.JsonObject;
import org.rpgl.core.RPGLObject;
import org.rpgl.exception.FunctionMismatchException;

import java.util.HashMap;
import java.util.Map;

public abstract class Function {

    public static final Map<String, Function> FUNCTIONS;

    static {
        FUNCTIONS = new HashMap<>();
        Function.FUNCTIONS.put("dummy_function", new DummyFunction());
    }

    public void verifyFunction(String expected, JsonObject data) throws FunctionMismatchException {
        if (!expected.equals(data.get("function"))) {
            throw new FunctionMismatchException(expected, (String) data.get("function"));
        }
    }

    public abstract void execute(RPGLObject source, RPGLObject target, JsonObject functionJson) throws FunctionMismatchException;

}
