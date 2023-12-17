package org.rpgl.condition;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.condition.True class.
 *
 * @author Calvin Withun
 */
public class TrueTest {

    @BeforeAll
    static void beforeAll() {
        RPGLCore.initializeTesting();
    }

    @Test
    @DisplayName("errors on wrong condition")
    void errorsOnWrongCondition() {
        assertThrows(ConditionMismatchException.class,
                () -> new True().evaluate(null, null, new JsonObject() {{
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
        assertTrue(new True().evaluate(null, null, new JsonObject() {{
            /*{
                "condition": "true"
            }*/
            this.putString("condition", "true");
        }}, new DummyContext()),
                "should evaluate true"
        );
    }

}
