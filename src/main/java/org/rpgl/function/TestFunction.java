package org.rpgl.function;

import org.jsonutils.JsonObject;
import org.rpgl.exception.FunctionMismatchException;

public class TestFunction extends Function {

    private static final String FUNCTION_ID = "test_function";

    public static int counter;

    static {
        Function.FUNCTIONS.put(FUNCTION_ID, new TestFunction());
        counter = 0;
    }

    @Override
    public void execute(long sourceUuid, long targetUuid, JsonObject data) throws FunctionMismatchException {
        super.verifyFunction(FUNCTION_ID, data);
        TestFunction.counter++;
    }

}
