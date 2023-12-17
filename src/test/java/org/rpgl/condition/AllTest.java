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
 * Testing class for the org.rpgl.condition.All class.
 *
 * @author Calvin Withun
 */
public class AllTest {

    @BeforeAll
    static void beforeAll() {
        RPGLCore.initializeTesting();
    }

    @Test
    @DisplayName("errors on wrong condition")
    void errorsOnWrongCondition() {
        assertThrows(ConditionMismatchException.class,
                () -> new All().evaluate(null, null, new JsonObject() {{
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
        assertTrue(new All().evaluate(null, null, new JsonObject() {{
            /*{
                "condition": "all",
                "conditions": [ ]
            }*/
            this.putString("condition", "all");
            this.putJsonArray("conditions", new JsonArray());
        }}, new DummyContext()),
                "All condition should evaluate true by default"
        );
    }

    @Test
    @DisplayName("evaluates true (multiple true)")
    void evaluatesTrue_multipleTrue() throws Exception {
        assertTrue(new All().evaluate(null, null, new JsonObject() {{
            /*{
                "condition": "all",
                "conditions": [
                    { "condition": "true" },
                    { "condition": "true" }
                ]
            }*/
            this.putString("condition", "all");
            this.putJsonArray("conditions", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("condition", "true");
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putString("condition", "true");
                }});
            }});
        }}, new DummyContext()),
                "All condition should evaluate true for 2 true conditions"
        );
    }

    @Test
    @DisplayName("evaluates false (true and false)")
    void evaluatesFalse_trueAndFalse() throws Exception {
        assertFalse(new All().evaluate(null, null, new JsonObject() {{
            /*{
                "condition": "all",
                "conditions": [
                    { "condition": "true" },
                    { "condition": "false" }
                ]
            }*/
            this.putString("condition", "all");
            this.putJsonArray("conditions", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("condition", "true");
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putString("condition", "false");
                }});
            }});
        }}, new DummyContext()),
                "All condition should evaluate false for 1 true and 1 false condition"
        );
    }

    @Test
    @DisplayName("evaluates false (multiple false)")
    void evaluatesFalse_multipleFalse() throws Exception {
        assertFalse(new All().evaluate(null, null, new JsonObject() {{
            /*{
                "condition": "all",
                "conditions": [
                    { "condition": "false" },
                    { "condition": "false" }
                ]
            }*/
            this.putString("condition", "all");
            this.putJsonArray("conditions", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("condition", "false");
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putString("condition", "false");
                }});
            }});
        }}, new DummyContext()),
                "All condition should evaluate false for 2 false conditions"
        );
    }

}
