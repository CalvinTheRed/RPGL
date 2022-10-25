package org.rpgl.function;

import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.exception.FunctionMismatchException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DummyFunctionTest {

    @AfterEach
    void afterEach() {
        DummyFunction.resetCounter();
    }

    @Test
    @DisplayName("DummyFunction Function increments counter as expected")
    void test1() throws JsonFormatException, FunctionMismatchException {
        Function function = new DummyFunction();
        String functionJsonString = "{" +
                "\"function\": \"dummy_function\"" +
                "}";
        JsonObject functionJson = JsonParser.parseObjectString(functionJsonString);
        function.execute(null, null, functionJson);
        assertEquals(1, DummyFunction.counter,
                "DummyFunction Function should increment static counter variable upon execution."
        );
    }

    @Test
    @DisplayName("DummyFunction Function throws FunctionMismatchException when function type doesn't match")
    void test2() throws JsonFormatException {
        Function function = new DummyFunction();
        String functionJsonString = "{" +
                "\"function\": \"not_a_function\"" +
                "}";
        JsonObject functionJson = JsonParser.parseObjectString(functionJsonString);
        assertThrows(FunctionMismatchException.class,
                () -> function.execute(null, null, functionJson),
                "DummyFunction Function should throw a FunctionMismatchException if the specified function doesn't match."
        );
    }
}
