package org.rpgl.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.function.DummyFunction;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RPGLEffectTest {

    @BeforeAll
    static void beforeAll() {
        RPGLCore.initializeTesting();
    }

    @AfterEach
    void afterEach() {
        DummyFunction.resetCounter();
    }

    @Test
    @DisplayName("evaluateConditions returns condition evaluation (true)")
    void evaluateConditions_returnsConditionEvaluation_true() throws ConditionMismatchException {
        JsonArray conditions = new JsonArray() {{
            this.addJsonObject(new JsonObject() {{
                this.putString("condition", "true");
            }});
        }};

        assertTrue(RPGLEffect.evaluateConditions(null, null, null, conditions),
                "true condition should evaluate to true"
        );
    }

    @Test
    @DisplayName("executeFunctions executes functions (DummySubevent)")
    void executeFunctions_executesFunctions_dummySubevent() throws FunctionMismatchException {
        JsonArray functions = new JsonArray() {{
            this.addJsonObject(new JsonObject() {{
                this.putString("function", "dummy_function");
            }});
        }};

        RPGLEffect.executeFunctions(null, null, null, functions);

        assertEquals(1, DummyFunction.counter,
                "DummyFunction counter should increment by 1 for executing the function"
        );
    }

    // TODO more tests needed once Conditions and Functions are added

}
