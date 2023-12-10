package org.rpgl.condition;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.condition.False class.
 *
 * @author Calvin Withun
 */
public class FalseTest {

    @BeforeAll
    static void beforeAll() {
        RPGLCore.initializeTesting();
    }

    @Test
    @DisplayName("evaluate wrong condition")
    void evaluate_wrongCondition_throwsException() {
        Condition condition = new False();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "not_a_condition"
            }*/
            this.putString("condition", "not_a_condition");
        }};

        DummyContext context = new DummyContext();

        assertThrows(ConditionMismatchException.class,
                () -> condition.evaluate(null, null, conditionJson, context),
                "Condition should throw a ConditionMismatchException if the specified condition doesn't match"
        );
    }

    @Test
    @DisplayName("evaluate default behavior")
    void evaluate_default_false() throws Exception {
        Condition condition = new False();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "false"
            }*/
            this.putString("condition", "false");
        }};

        DummyContext context = new DummyContext();

        assertFalse(condition.evaluate(null, null, conditionJson, context),
                "False condition should always evaluate false"
        );
    }

}
