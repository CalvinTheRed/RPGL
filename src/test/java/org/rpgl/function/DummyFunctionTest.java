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
    @DisplayName("execute wrong function")
    void execute_wrongFunction_throwsException() {
        Function function = new DummyFunction();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "not_a_function"
            }*/
            this.putString("function", "not_a_function");
        }};

        DummyContext context = new DummyContext();

        assertThrows(FunctionMismatchException.class,
                () -> function.execute(null, null, functionJson, context, List.of()),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("execute default behavior")
    void execute_default_increasesStaticCounter() throws Exception {
        Function function = new DummyFunction();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "dummy_function"
            }*/
           this.putString("function", "dummy_function");
        }};

        DummyContext context = new DummyContext();

        function.execute(null, null, functionJson, context, List.of());
        assertEquals(1, DummyFunction.counter,
                "DummyFunction function should increment static counter variable upon execution"
        );
    }

}
