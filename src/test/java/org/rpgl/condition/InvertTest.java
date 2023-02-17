package org.rpgl.condition;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonObject;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.condition.Invert class.
 *
 * @author Calvin Withun
 */
public class InvertTest {

    @BeforeAll
    static void beforeFalse() {
        RPGLCore.initializeTesting();
    }

    @Test
    @DisplayName("evaluate wrong condition")
    void evaluate_wrongCondition_throwsException() {
        Condition condition = new Invert();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "not_a_condition"
            }*/
            this.putString("condition", "not_a_condition");
        }};

        assertThrows(ConditionMismatchException.class,
                () -> condition.evaluate(null, null, null, conditionJson),
                "Invert condition should throw a ConditionMismatchException if the specified condition doesn't match"
        );
    }

    @Test
    @DisplayName("evaluate inverting true condition")
    void evaluate_true_false() throws ConditionMismatchException {
        Condition condition = new Invert();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "invert",
                "invert": {
                    "condition": "true"
                }
            }*/
            this.putString("condition", "invert");
            this.putJsonObject("invert", new JsonObject() {{
                this.putString("condition", "true");
            }});
        }};

        assertFalse(condition.evaluate(null, null, null, conditionJson),
                "Invert condition should evaluate false when provided a true sub-condition"
        );
    }

    @Test
    @DisplayName("evaluate inverting false condition")
    void evaluate_false_true() throws ConditionMismatchException {
        Condition condition = new Invert();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "invert",
                "invert": {
                    "condition": "false"
                }
            }*/
            this.putString("condition", "invert");
            this.putJsonObject("invert", new JsonObject() {{
                this.putString("condition", "false");
            }});
        }};

        assertTrue(condition.evaluate(null, null, null, conditionJson),
                "Invert condition should evaluate true when provided a false sub-condition"
        );
    }

}
