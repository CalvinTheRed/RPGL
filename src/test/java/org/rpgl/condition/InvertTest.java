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
    @DisplayName("errors on wrong condition")
    void errorsOnWrongCondition() {
        assertThrows(ConditionMismatchException.class,
                () -> new Invert().evaluate(null, null, new JsonObject() {{
                    /*{
                        "condition": "not_a_condition"
                    }*/
                    this.putString("condition", "not_a_condition");
                }}, new DummyContext()),
                "Condition should throw a ConditionMismatchException if the specified condition doesn't match"
        );
    }

    @Test
    @DisplayName("evaluates true")
    void evaluatesTrue() throws Exception {
        assertTrue(new Invert().evaluate(null, null, new JsonObject() {{
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
        }}, new DummyContext()),
                "evaluate should return true for false condition"
        );
    }

    @Test
    @DisplayName("evaluates false")
    void evaluatesFalse() throws Exception {
        assertFalse(new Invert().evaluate(null, null, new JsonObject() {{
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
        }}, new DummyContext()),
                "evaluate should return false for true condition"
        );
    }

}
