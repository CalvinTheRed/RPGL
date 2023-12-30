package org.rpgl.condition;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DummySubevent;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.condition.ObjectAbilityScoreComparison class.
 *
 * @author Calvin Withun
 */
public class ObjectAbilityScoreComparisonTest {

    @BeforeAll
    static void beforeAll() {
        DatapackLoader.loadDatapacks(
                new File("src/test/resources/datapacks".replace("/", File.separator))
        );
        RPGLCore.initializeTesting();
    }

    @AfterAll
    static void afterAll() {
        DatapackLoader.DATAPACKS.clear();
    }

    @AfterEach
    void afterEach() {
        UUIDTable.clear();
    }

    @Test
    @DisplayName("errors on wrong condition")
    void errorsOnWrongCondition() {
        assertThrows(ConditionMismatchException.class,
                () -> new ObjectAbilityScoreComparison().evaluate(null, null, new JsonObject() {{
                    /*{
                        "condition": "not_a_condition"
                    }*/
                    this.putString("condition", "not_a_condition");
                }}, new DummyContext()),
                "Condition should throw a ConditionMismatchException if the specified condition doesn't match"
        );
    }

    @Test
    @DisplayName("evaluates true (equals)")
    void evaluatesTrue_equals() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        source.getAbilityScores().putInteger("str", 15);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);

        assertTrue(new ObjectAbilityScoreComparison().evaluate(null, dummySubevent, new JsonObject() {{
            /*{
                "condition": "object_ability_score_comparison",
                "object": {
                    "from": "subevent",
                    "object": "source"
                },
                "ability": "str",
                "comparison": "=",
                "compare_to": 15
            }*/
            this.putString("condition", "object_ability_score_comparison");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
            this.putString("ability", "str");
            this.putString("comparison", "=");
            this.putInteger("compare_to", 15);
        }}, new DummyContext()),
                "evaluate should return true for equal comparison when score equals value"
        );
    }

    @Test
    @DisplayName("evaluates false (equals)")
    void evaluatesFalse_equals() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        source.getAbilityScores().putInteger("str", 15);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);

        assertFalse(new ObjectAbilityScoreComparison().evaluate(null, dummySubevent, new JsonObject() {{
            /*{
                "condition": "object_ability_score_comparison",
                "object": {
                    "from": "subevent",
                    "object": "source"
                },
                "ability": "str",
                "comparison": "=",
                "compare_to": 10
            }*/
            this.putString("condition", "object_ability_score_comparison");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
            this.putString("ability", "str");
            this.putString("comparison", "=");
            this.putInteger("compare_to", 10);
        }}, new DummyContext()),
                "evaluate should return false for equal comparison when score does not equal value"
        );
    }

    @Test
    @DisplayName("evaluates true (less than)")
    void evaluatesTrue_lessThan() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        source.getAbilityScores().putInteger("str", 15);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);

        assertTrue(new ObjectAbilityScoreComparison().evaluate(null, dummySubevent, new JsonObject() {{
            /*{
                "condition": "object_ability_score_comparison",
                "object": {
                    "from": "subevent",
                    "object": "source"
                },
                "ability": "str",
                "comparison": "<",
                "compare_to": 20
            }*/
            this.putString("condition", "object_ability_score_comparison");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
            this.putString("ability", "str");
            this.putString("comparison", "<");
            this.putInteger("compare_to", 20);
        }}, new DummyContext()),
                "evaluate should return true for less-than comparison when score is less than value"
        );
    }

    @Test
    @DisplayName("evaluates false (less than)")
    void evaluatesFalse_lessThan() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        source.getAbilityScores().putInteger("str", 15);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);

        assertFalse(new ObjectAbilityScoreComparison().evaluate(null, dummySubevent, new JsonObject() {{
            /*{
                "condition": "object_ability_score_comparison",
                "object": {
                    "from": "subevent",
                    "object": "source"
                },
                "ability": "str",
                "comparison": "<",
                "compare_to": 10
            }*/
            this.putString("condition", "object_ability_score_comparison");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
            this.putString("ability", "str");
            this.putString("comparison", "<");
            this.putInteger("compare_to", 10);
        }}, new DummyContext()),
                "evaluate should return false for less-than comparison when score is not less than value"
        );
    }

    @Test
    @DisplayName("evaluates true (greater than)")
    void evaluatesTrue_greaterThan() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        source.getAbilityScores().putInteger("str", 15);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);

        assertTrue(new ObjectAbilityScoreComparison().evaluate(null, dummySubevent, new JsonObject() {{
            /*{
                "condition": "object_ability_score_comparison",
                "object": {
                    "from": "subevent",
                    "object": "source"
                },
                "ability": "str",
                "comparison": ">",
                "compare_to": 10
            }*/
            this.putString("condition", "object_ability_score_comparison");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
            this.putString("ability", "str");
            this.putString("comparison", ">");
            this.putInteger("compare_to", 10);
        }}, new DummyContext()),
                "evaluate should return true for more-than comparison when score is more than value"
        );
    }

    @Test
    @DisplayName("evaluates false (greater than)")
    void evaluatesFalse_greaterThan() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        source.getAbilityScores().putInteger("str", 15);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);

        assertFalse(new ObjectAbilityScoreComparison().evaluate(null, dummySubevent, new JsonObject() {{
            /*{
                "condition": "object_ability_score_comparison",
                "object": {
                    "from": "subevent",
                    "object": "source"
                },
                "ability": "str",
                "comparison": ">",
                "compare_to": 20
            }*/
            this.putString("condition", "object_ability_score_comparison");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
            this.putString("ability", "str");
            this.putString("comparison", ">");
            this.putInteger("compare_to", 20);
        }}, new DummyContext()),
                "evaluate should return true for more-than comparison when score is more than value"
        );
    }

    @Test
    @DisplayName("evaluates true (less than or equal to) (less than)")
    void evaluatesTrue_lessThanOrEqualTo_lessThan() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        source.getAbilityScores().putInteger("str", 15);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);

        assertTrue(new ObjectAbilityScoreComparison().evaluate(null, dummySubevent, new JsonObject() {{
            /*{
                "condition": "object_ability_score_comparison",
                "object": {
                    "from": "subevent",
                    "object": "source"
                },
                "ability": "str",
                "comparison": "<=",
                "compare_to": 20
            }*/
            this.putString("condition", "object_ability_score_comparison");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
            this.putString("ability", "str");
            this.putString("comparison", "<=");
            this.putInteger("compare_to", 20);
        }}, new DummyContext()),
                "evaluate should return true for less-than-or-equal-to comparison when score is less than value"
        );
    }

    @Test
    @DisplayName("evaluates true (less than or equal to) (equals)")
    void evaluatesTrue_lessThanOrEqualTo_equals() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        source.getAbilityScores().putInteger("str", 15);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);

        assertTrue(new ObjectAbilityScoreComparison().evaluate(null, dummySubevent, new JsonObject() {{
            /*{
                "condition": "object_ability_score_comparison",
                "object": {
                    "from": "subevent",
                    "object": "source"
                },
                "ability": "str",
                "comparison": "<=",
                "compare_to": 15
            }*/
            this.putString("condition", "object_ability_score_comparison");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
            this.putString("ability", "str");
            this.putString("comparison", "<=");
            this.putInteger("compare_to", 15);
        }}, new DummyContext()),
                "evaluate should return true for less-than-or-equal-to comparison when score is equal to value"
        );
    }

    @Test
    @DisplayName("evaluates false (less than or equal to)")
    void evaluatesFalse_lessThanOrEqualTo() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        source.getAbilityScores().putInteger("str", 15);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);

        assertFalse(new ObjectAbilityScoreComparison().evaluate(null, dummySubevent, new JsonObject() {{
            /*{
                "condition": "object_ability_score_comparison",
                "object": {
                    "from": "subevent",
                    "object": "source"
                },
                "ability": "str",
                "comparison": "<=",
                "compare_to": 10
            }*/
            this.putString("condition", "object_ability_score_comparison");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
            this.putString("ability", "str");
            this.putString("comparison", "<=");
            this.putInteger("compare_to", 10);
        }}, new DummyContext()),
                "evaluate should return false for less-than-or-equal-to comparison when score is greater than value"
        );
    }

    @Test
    @DisplayName("evaluates true (greater than or equal to) (greater than)")
    void evaluatesTrue_greaterThanOrEqualTo_greaterThan() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        source.getAbilityScores().putInteger("str", 15);
        DummyContext context = new DummyContext();
        context.add(source);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);

        assertTrue(new ObjectAbilityScoreComparison().evaluate(null, dummySubevent, new JsonObject() {{
            /*{
                "condition": "object_ability_score_comparison",
                "object": {
                    "from": "subevent",
                    "object": "source"
                },
                "ability": "str",
                "comparison": ">=",
                "compare_to": 10
            }*/
            this.putString("condition", "object_ability_score_comparison");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
            this.putString("ability", "str");
            this.putString("comparison", ">=");
            this.putInteger("compare_to", 10);
        }}, context),
                "evaluate should return true for more-than-or-equal-to comparison when score is more than value"
        );
    }

    @Test
    @DisplayName("evaluates true (greater than or equal to) (equals)")
    void evaluatesTrue_greaterThanOrEqualTo_equals() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        source.getAbilityScores().putInteger("str", 15);
        DummyContext context = new DummyContext();
        context.add(source);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);

        assertTrue(new ObjectAbilityScoreComparison().evaluate(null, dummySubevent, new JsonObject() {{
            /*{
                "condition": "object_ability_score_comparison",
                "object": {
                    "from": "subevent",
                    "object": "source"
                },
                "ability": "str",
                "comparison": ">=",
                "compare_to": 15
            }*/
            this.putString("condition", "object_ability_score_comparison");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
            this.putString("ability", "str");
            this.putString("comparison", ">=");
            this.putInteger("compare_to", 15);
        }}, context),
                "evaluate should return true for more-than-or-equal-to comparison when score is equal to value"
        );
    }

    @Test
    @DisplayName("evaluates false (greater than or equal to)")
    void evaluatesFalse_greaterThanOrEqualTo() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        source.getAbilityScores().putInteger("str", 15);
        DummyContext context = new DummyContext();
        context.add(source);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);

        assertFalse(new ObjectAbilityScoreComparison().evaluate(null, dummySubevent, new JsonObject() {{
            /*{
                "condition": "object_ability_score_comparison",
                "object": {
                    "from": "subevent",
                    "object": "source"
                },
                "ability": "str",
                "comparison": ">=",
                "compare_to": 20
            }*/
                    this.putString("condition", "object_ability_score_comparison");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "subevent");
                        this.putString("object", "source");
                    }});
                    this.putString("ability", "str");
                    this.putString("comparison", ">=");
                    this.putInteger("compare_to", 20);
                }}, context),
                "evaluate should return false for more-than-or-equal-to comparison when score is less than value"
        );
    }

}
