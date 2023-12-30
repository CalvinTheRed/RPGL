package org.rpgl.condition;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.condition.Any class.
 *
 * @author Calvin Withun
 */
public class AnyTest {

    @BeforeAll
    static void beforeAll() {
        RPGLCore.initializeTesting();
    }

    @Test
    @DisplayName("errors on wrong condition")
    void errorsOnWrongCondition() {
        assertThrows(ConditionMismatchException.class,
                () -> new Any().evaluate(null, null, new JsonObject() {{
                    /*{
                        "condition": "not_a_condition"
                    }*/
                    this.putString("condition", "not_a_condition");
                }}, new DummyContext()),
                "Condition should throw a ConditionMismatchException if the specified condition doesn't match"
        );
    }

    @Test
    @DisplayName("evaluates true (no conditions)")
    void evaluatesTrue_noConditions() throws Exception {
        assertTrue(new Any().evaluate(null, null, new JsonObject() {{
            /*{
                "condition": "any",
                "conditions": [ ]
            }*/
            this.putString("condition", "any");
            this.putJsonArray("conditions", new JsonArray());
        }}, new DummyContext()),
                "Any condition should evaluate true by default"
        );
    }

    @Test
    @DisplayName("evaluates true (multiple true)")
    void evaluatesTrue_multipleTrue() throws Exception {
        assertTrue(new Any().evaluate(null, null, new JsonObject() {{
            /*{
                "condition": "any",
                "conditions": [
                    { "condition": "true" },
                    { "condition": "true" }
                ]
            }*/
            this.putString("condition", "any");
            this.putJsonArray("conditions", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("condition", "true");
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putString("condition", "true");
                }});
            }});
        }}, new DummyContext()),
                "Any condition should evaluate true for 2 true conditions"
        );
    }

    @Test
    @DisplayName("evaluates true (true and false)")
    void evaluatesTrue_trueAndFalse() throws Exception {
        assertTrue(new Any().evaluate(null, null, new JsonObject() {{
            /*{
                "condition": "any",
                "conditions": [
                    { "condition": "true" },
                    { "condition": "false" }
                ]
            }*/
            this.putString("condition", "any");
            this.putJsonArray("conditions", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("condition", "true");
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putString("condition", "false");
                }});
            }});
        }}, new DummyContext()),
                "Any condition should evaluate false for 1 true and 1 false condition"
        );
    }

    @Test
    @DisplayName("evaluates false (multiple false)")
    void evaluatesFalse_multipleFalse() throws Exception {
        assertFalse(new Any().evaluate(null, null, new JsonObject() {{
            /*{
                "condition": "any",
                "conditions": [
                    { "condition": "false" },
                    { "condition": "false" }
                ]
            }*/
            this.putString("condition", "any");
            this.putJsonArray("conditions", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("condition", "false");
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putString("condition", "false");
                }});
            }});
        }}, new DummyContext()),
                "Any condition should evaluate false for 2 false conditions"
        );
    }

}
