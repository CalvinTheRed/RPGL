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
    @DisplayName("evaluate wrong condition")
    void evaluate_wrongCondition_throwsException() {
        Condition condition = new Any();
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
    @DisplayName("evaluate for no sub-conditions")
    void evaluate_default_true() throws Exception {
        Condition condition = new Any();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "any",
                "conditions": [ ]
            }*/
            this.putString("condition", "any");
            this.putJsonArray("conditions", new JsonArray());
        }};

        DummyContext context = new DummyContext();

        assertTrue(condition.evaluate(null, null, conditionJson, context),
                "Any condition should evaluate true for no sub-conditions"
        );
    }

    @Test
    @DisplayName("evaluate for true, true")
    void evaluate_trueTrue_true() throws Exception {
        Condition condition = new Any();
        JsonObject conditionJson = new JsonObject() {{
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
        }};

        DummyContext context = new DummyContext();

        assertTrue(condition.evaluate(null, null, conditionJson, context),
                "Any condition should evaluate true for 2 true conditions"
        );
    }

    @Test
    @DisplayName("evaluate for true, false")
    void evaluate_trueFalse_true() throws Exception {
        Condition condition = new Any();
        JsonObject conditionJson = new JsonObject() {{
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
        }};

        DummyContext context = new DummyContext();

        assertTrue(condition.evaluate(null, null, conditionJson, context),
                "Any condition should evaluate true for 1 true and 1 false condition"
        );
    }

    @Test
    @DisplayName("evaluate for false, false")
    void evaluate_falseFalse_false() throws Exception {
        Condition condition = new Any();
        JsonObject conditionJson = new JsonObject() {{
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
        }};

        DummyContext context = new DummyContext();

        assertFalse(condition.evaluate(null, null, conditionJson, context),
                "Any condition should evaluate false for 2 false conditions"
        );
    }

}
