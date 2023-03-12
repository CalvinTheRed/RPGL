package org.rpgl.condition;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLCore;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;

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
    @DisplayName("evaluate wrong condition")
    void evaluate_wrongCondition_throwsException() {
        Condition condition = new All();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "not_a_condition"
            }*/
            this.putString("condition", "not_a_condition");
        }};

        RPGLContext context = new RPGLContext();

        assertThrows(ConditionMismatchException.class,
                () -> condition.evaluate(null, null, conditionJson, context),
                "Condition should throw a ConditionMismatchException if the specified condition doesn't match"
        );
    }

    @Test
    @DisplayName("evaluate for no sub-conditions")
    void evaluate_default_true() throws Exception {
        Condition condition = new All();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "all",
                "conditions": [ ]
            }*/
            this.putString("condition", "all");
            this.putJsonArray("conditions", new JsonArray());
        }};

        RPGLContext context = new RPGLContext();

        assertTrue(condition.evaluate(null, null, conditionJson, context),
                "All condition should evaluate true when evaluated without sub-conditions"
        );
    }

    @Test
    @DisplayName("evaluate for true, true")
    void evaluate_trueTrue_true() throws Exception {
        Condition condition = new All();
        JsonObject conditionJson = new JsonObject() {{
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
        }};

        RPGLContext context = new RPGLContext();

        assertTrue(condition.evaluate(null, null, conditionJson, context),
                "All condition should evaluate true for 2 true conditions"
        );
    }

    @Test
    @DisplayName("evaluate for true, false")
    void evaluate_trueFalse_false() throws Exception {
        Condition condition = new All();
        JsonObject conditionJson = new JsonObject() {{
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
        }};

        RPGLContext context = new RPGLContext();

        assertFalse(condition.evaluate(null, null, conditionJson, context),
                "All condition should evaluate false for 1 true and 1 false condition"
        );
    }

    @Test
    @DisplayName("evaluate for false, false")
    void evaluate_falseFalse_false() throws Exception {
        Condition condition = new All();
        JsonObject conditionJson = new JsonObject() {{
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
        }};

        RPGLContext context = new RPGLContext();

        assertFalse(condition.evaluate(null, null, conditionJson, context),
                "All condition should evaluate false for 2 false conditions"
        );
    }

}
