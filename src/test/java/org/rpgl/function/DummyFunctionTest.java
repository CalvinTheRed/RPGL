package org.rpgl.function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DummySubevent;
import org.rpgl.testUtils.DummyContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.DummyFunction class.
 *
 * @author Calvin Withun
 */
public class DummyFunctionTest {

    @BeforeAll
    static void beforeAll() {
        RPGLCore.initializeTesting();
    }

    @AfterEach
    void afterEach() {
        DummySubevent.resetCounter();
    }

    @Test
    @DisplayName("errors on wrong function")
    void errorsOnWrongFunction() {
        assertThrows(FunctionMismatchException.class,
                () -> new DummyFunction().execute(null, null, new JsonObject() {{
                    /*{
                        "function": "not_a_function"
                    }*/
                    this.putString("function", "not_a_function");
                }}, new DummyContext(), List.of()),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("increments test counter")
    void incrementsTestCounter() throws Exception {
        new DummyFunction().execute(null, null, new JsonObject() {{
            /*{
                "function": "dummy_function"
            }*/
            this.putString("function", "dummy_function");
        }}, new DummyContext(), List.of());

        assertEquals(1, DummyFunction.counter,
                "DummyFunction function should increment static counter variable upon execution"
        );
    }

}
