package org.rpgl.condition;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonObject;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.condition.True class.
 *
 * @author Calvin Withun
 */
public class TrueTest {

    @BeforeAll
    static void beforeFalse() {
        RPGLCore.initializeTesting();
    }

    @Test
    @DisplayName("evaluate wrong condition")
    void evaluate_wrongCondition_throwsException() {
        Condition condition = new True();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "not_a_condition"
            }*/
            this.putString("condition", "not_a_condition");
        }};

        assertThrows(ConditionMismatchException.class,
                () -> condition.evaluate(null, null, null, conditionJson),
                "True condition should throw a ConditionMismatchException if the specified condition doesn't match"
        );
    }

    @Test
    @DisplayName("evaluate default behavior")
    void evaluate_default_true() throws Exception {
        Condition condition = new True();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "true"
            }*/
            this.putString("condition", "true");
        }};

        assertTrue(condition.evaluate(null, null, null, conditionJson),
                "True condition should always evaluate true"
        );
    }

}
