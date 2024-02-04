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
import org.rpgl.subevent.Movement;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.condition.CheckDistance class.
 *
 * @author Calvin Withun
 */
public class CheckDistanceTest {

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
                () -> new CheckDistance().evaluate(null, null, new JsonObject() {{
                    /*{
                        "condition": "not_a_condition"
                    }*/
                    this.putString("condition", "not_a_condition");
                }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "Condition should throw a ConditionMismatchException if the specified condition doesn't match"
        );
    }

    @Test
    @DisplayName("evaluates true (`from` specified)")
    void evaluatesTrue_fromSpecified() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER,
                TestUtils.TEST_ARRAY_0_0_0, TestUtils.TEST_ARRAY_0_0_0);

        assertTrue(new CheckDistance().evaluate(null, new Movement().setSource(object).setTarget(object), new JsonObject() {{
            /*{
                "condition": "check_distance",
                "from": {
                    "from": "subevent",
                    "object": "source"
                },
                "to": {
                    "from": "subevent",
                    "object": "target"
                },
                "comparison": "<",
                "boundary": 10.0
            }*/
            this.putString("condition", "check_distance");
            this.putJsonObject("from", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
            this.putJsonObject("to", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "target");
            }});
            this.putString("comparison", "<");
            this.putDouble("boundary", 10d);
        }}, new DummyContext(), TestUtils.TEST_ARRAY_10_10_10),
                "both objects are within 10 units of each other"
        );
    }

    @Test
    @DisplayName("evaluates false (`from` specified)")
    void evaluatesFalse_fromSpecified() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER,
                TestUtils.TEST_ARRAY_0_0_0, TestUtils.TEST_ARRAY_0_0_0);

        assertFalse(new CheckDistance().evaluate(null, new Movement().setSource(object).setTarget(object), new JsonObject() {{
            /*{
                "condition": "check_distance",
                "from": {
                    "from": "subevent",
                    "object": "source"
                },
                "to": {
                    "from": "subevent",
                    "object": "target"
                },
                "comparison": ">",
                "boundary": 10.0
            }*/
            this.putString("condition", "check_distance");
            this.putJsonObject("from", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
            this.putJsonObject("to", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "target");
            }});
            this.putString("comparison", ">");
            this.putDouble("boundary", 10d);
        }}, new DummyContext(), TestUtils.TEST_ARRAY_10_10_10),
                "both objects are not within 10 units of each other"
        );
    }

    @Test
    @DisplayName("evaluates true (`from` not specified)")
    void evaluatesTrue_fromNotSpecified() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER,
                TestUtils.TEST_ARRAY_0_0_0, TestUtils.TEST_ARRAY_0_0_0);

        assertTrue(new CheckDistance().evaluate(null, new Movement().setSource(object).setTarget(object), new JsonObject() {{
            /*{
                "condition": "check_distance",
                "to": {
                    "from": "subevent",
                    "object": "target"
                },
                "comparison": "<",
                "boundary": 10.0
            }*/
            this.putString("condition", "check_distance");
            this.putJsonObject("to", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "target");
            }});
            this.putString("comparison", "<");
            this.putDouble("boundary", 10d);
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "object and origin point are within 10 units of each other"
        );
    }

    @Test
    @DisplayName("evaluates false (`from` not specified)")
    void evaluatesFalse_fromNotSpecified() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER,
                TestUtils.TEST_ARRAY_0_0_0, TestUtils.TEST_ARRAY_0_0_0);

        assertFalse(new CheckDistance().evaluate(null, new Movement().setSource(object).setTarget(object), new JsonObject() {{
            /*{
                "condition": "check_distance",
                "to": {
                    "from": "subevent",
                    "object": "target"
                },
                "comparison": ">",
                "boundary": 10.0
            }*/
            this.putString("condition", "check_distance");
            this.putJsonObject("to", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "target");
            }});
            this.putString("comparison", ">");
            this.putDouble("boundary", 10d);
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "both objects are not within 10 units of each other"
        );
    }

}
