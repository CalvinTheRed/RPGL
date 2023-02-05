package org.rpgl.condition;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonObject;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FalseTest {

    @BeforeAll
    static void beforeFalse() {
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

        assertThrows(ConditionMismatchException.class,
                () -> condition.evaluate(null, null, conditionJson),
                "False condition should throw a ConditionMismatchException if the specified condition doesn't match"
        );
    }

    @Test
    @DisplayName("evaluate default behavior")
    void evaluate_default_false() throws ConditionMismatchException {
        Condition condition = new False();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "false"
            }*/
            this.putString("condition", "false");
        }};

        assertFalse(condition.evaluate(null, null, conditionJson),
                "False condition should always evaluate false"
        );
    }

}
