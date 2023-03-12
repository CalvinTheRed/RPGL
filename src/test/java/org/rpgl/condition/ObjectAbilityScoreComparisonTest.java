package org.rpgl.condition;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DummySubevent;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

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
    static void beforeAll() throws Exception {
        DatapackLoader.loadDatapacks(
                new File(Objects.requireNonNull(DatapackTest.class.getClassLoader().getResource("datapacks")).toURI())
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
    @DisplayName("evaluate wrong condition")
    void evaluate_wrongCondition_throwsException() {
        Condition condition = new ObjectAbilityScoreComparison();
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
    @DisplayName("evaluate returns true (equals) (score equal to value)")
    void evaluate_returnsTrue_equals_scoreEqualToValue() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("str", 15);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);
        dummySubevent.setTarget(target);

        ObjectAbilityScoreComparison objectAbilityScoreComparison = new ObjectAbilityScoreComparison();

        JsonObject conditionJson = new JsonObject() {{
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
        }};

        assertTrue(objectAbilityScoreComparison.evaluate(null, dummySubevent, conditionJson, context),
                "evaluate should return true for equal comparison when score equals value"
        );
    }

    @Test
    @DisplayName("evaluate returns false (equals) (score not equal to value)")
    void evaluate_returnsTrue_equals_scoreNotEqualToValue() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("str", 15);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);
        dummySubevent.setTarget(target);

        ObjectAbilityScoreComparison objectAbilityScoreComparison = new ObjectAbilityScoreComparison();

        JsonObject conditionJson = new JsonObject() {{
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
        }};

        assertFalse(objectAbilityScoreComparison.evaluate(null, dummySubevent, conditionJson, context),
                "evaluate should return false for equal comparison when score does not equal value"
        );
    }

    @Test
    @DisplayName("evaluate returns true (less than) (score less than value)")
    void evaluate_returnsTrue_lessThan_scoreLessThanValue() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("str", 15);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);
        dummySubevent.setTarget(target);

        ObjectAbilityScoreComparison objectAbilityScoreComparison = new ObjectAbilityScoreComparison();

        JsonObject conditionJson = new JsonObject() {{
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
        }};

        assertTrue(objectAbilityScoreComparison.evaluate(null, dummySubevent, conditionJson, context),
                "evaluate should return true for less-than comparison when score is less than value"
        );
    }

    @Test
    @DisplayName("evaluate returns false (less than) (score not less than value)")
    void evaluate_returnsFalse_lessThan_scoreNotLessThanValue() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("str", 15);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);
        dummySubevent.setTarget(target);

        ObjectAbilityScoreComparison objectAbilityScoreComparison = new ObjectAbilityScoreComparison();

        JsonObject conditionJson = new JsonObject() {{
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
        }};

        assertFalse(objectAbilityScoreComparison.evaluate(null, dummySubevent, conditionJson, context),
                "evaluate should return false for less-than comparison when score is not less than value"
        );
    }

    @Test
    @DisplayName("evaluate returns true (more than) (score more than value)")
    void evaluate_returnsTrue_moreThan_scoreMoreThanValue() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("str", 15);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);
        dummySubevent.setTarget(target);

        ObjectAbilityScoreComparison objectAbilityScoreComparison = new ObjectAbilityScoreComparison();

        JsonObject conditionJson = new JsonObject() {{
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
        }};

        assertTrue(objectAbilityScoreComparison.evaluate(null, dummySubevent, conditionJson, context),
                "evaluate should return true for more-than comparison when score is more than value"
        );
    }

    @Test
    @DisplayName("evaluate returns false (more than) (score not more than value)")
    void evaluate_returnsFalse_moreThan_scoreNotMoreThanValue() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("str", 15);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);
        dummySubevent.setTarget(target);

        ObjectAbilityScoreComparison objectAbilityScoreComparison = new ObjectAbilityScoreComparison();

        JsonObject conditionJson = new JsonObject() {{
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
        }};

        assertFalse(objectAbilityScoreComparison.evaluate(null, dummySubevent, conditionJson, context),
                "evaluate should return false for more-than comparison when score is not more than value"
        );
    }

    @Test
    @DisplayName("evaluate returns true (less than or equal to) (score less than value)")
    void evaluate_returnsTrue_lessThanOrEqualTo_scoreLessThanValue() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("str", 15);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);
        dummySubevent.setTarget(target);

        ObjectAbilityScoreComparison objectAbilityScoreComparison = new ObjectAbilityScoreComparison();

        JsonObject conditionJson = new JsonObject() {{
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
        }};

        assertTrue(objectAbilityScoreComparison.evaluate(null, dummySubevent, conditionJson, context),
                "evaluate should return true for less-than-or-equal-to comparison when score is less than value"
        );
    }

    @Test
    @DisplayName("evaluate returns true (less than or equal to) (score equal to value)")
    void evaluate_returnsTrue_lessThanOrEqualTo_scoreEqualToValue() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("str", 15);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);
        dummySubevent.setTarget(target);

        ObjectAbilityScoreComparison objectAbilityScoreComparison = new ObjectAbilityScoreComparison();

        JsonObject conditionJson = new JsonObject() {{
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
        }};

        assertTrue(objectAbilityScoreComparison.evaluate(null, dummySubevent, conditionJson, context),
                "evaluate should return true for less-than-or-equal-to comparison when score is equal to value"
        );
    }

    @Test
    @DisplayName("evaluate returns false (less than or equal to) (score more than value)")
    void evaluate_returnsFalse_lessThanOrEqualTo_scoreMoreThanValue() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("str", 15);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);
        dummySubevent.setTarget(target);

        ObjectAbilityScoreComparison objectAbilityScoreComparison = new ObjectAbilityScoreComparison();

        JsonObject conditionJson = new JsonObject() {{
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
        }};

        assertFalse(objectAbilityScoreComparison.evaluate(null, dummySubevent, conditionJson, context),
                "evaluate should return false for less-than-or-equal-to comparison when score is more than value"
        );
    }

    @Test
    @DisplayName("evaluate returns true (more than or equal to) (score more than value)")
    void evaluate_returnsTrue_moreThanOrEqualTo_scoreMoreThanValue() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("str", 15);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);
        dummySubevent.setTarget(target);

        ObjectAbilityScoreComparison objectAbilityScoreComparison = new ObjectAbilityScoreComparison();

        JsonObject conditionJson = new JsonObject() {{
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
        }};

        assertTrue(objectAbilityScoreComparison.evaluate(null, dummySubevent, conditionJson, context),
                "evaluate should return true for more-than-or-equal-to comparison when score is more than value"
        );
    }

    @Test
    @DisplayName("evaluate returns true (more than or equal to) (score equal to value)")
    void evaluate_returnsTrue_moreThanOrEqualTo_scoreEqualToValue() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("str", 15);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);
        dummySubevent.setTarget(target);

        ObjectAbilityScoreComparison objectAbilityScoreComparison = new ObjectAbilityScoreComparison();

        JsonObject conditionJson = new JsonObject() {{
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
        }};

        assertTrue(objectAbilityScoreComparison.evaluate(null, dummySubevent, conditionJson, context),
                "evaluate should return true for more-than-or-equal-to comparison when score is equal to value"
        );
    }

    @Test
    @DisplayName("evaluate returns false (more than or equal to) (score less than value)")
    void evaluate_returnsFalse_moreThanOrEqualTo_scoreLessThanValue() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("str", 15);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);
        dummySubevent.setTarget(target);

        ObjectAbilityScoreComparison objectAbilityScoreComparison = new ObjectAbilityScoreComparison();

        JsonObject conditionJson = new JsonObject() {{
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
        }};

        assertFalse(objectAbilityScoreComparison.evaluate(null, dummySubevent, conditionJson, context),
                "evaluate should return false for more-than-or-equal-to comparison when score is less than value"
        );
    }

}
