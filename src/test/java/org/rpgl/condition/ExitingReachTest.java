package org.rpgl.condition;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLEffect;
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
 * Testing class for the org.rpgl.condition.ExitingReach class.
 *
 * @author Calvin Withun
 */
public class ExitingReachTest {

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
                () -> new ExitingReach().evaluate(null, null, new JsonObject() {{
                    /*{
                        "condition": "not_a_condition"
                    }*/
                    this.putString("condition", "not_a_condition");
                }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "Condition should throw a ConditionMismatchException if the specified condition doesn't match"
        );
    }

    @Test
    @DisplayName("evaluates true")
    void evaluatesTrue() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER,
                TestUtils.TEST_ARRAY_0_0_0, TestUtils.TEST_ARRAY_0_0_0);
        RPGLObject mover = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER,
                TestUtils.TEST_ARRAY_0_0_0, TestUtils.TEST_ARRAY_0_0_0);

        RPGLEffect effect = new RPGLEffect().setSource(object).setTarget(object);

        assertTrue(new ExitingReach().evaluate(effect, new Movement().setSource(mover).setTarget(mover), new JsonObject() {{
            /*{
                "condition": "exiting_reach"
            }*/
            this.putString("condition", "exiting_reach");
        }}, new DummyContext(), TestUtils.TEST_ARRAY_10_10_10),
                "mover exited reach of object"
        );
    }

    @Test
    @DisplayName("evaluates false (stays within reach)")
    void evaluatesFalse_staysWithinReach() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER,
                TestUtils.TEST_ARRAY_0_0_0, TestUtils.TEST_ARRAY_0_0_0);
        RPGLObject mover = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER,
                TestUtils.TEST_ARRAY_0_0_0, TestUtils.TEST_ARRAY_0_0_0);

        RPGLEffect effect = new RPGLEffect().setSource(object).setTarget(object);

        assertFalse(new ExitingReach().evaluate(effect, new Movement().setSource(mover).setTarget(mover), new JsonObject() {{
            /*{
                "condition": "exiting_reach"
            }*/
            this.putString("condition", "exiting_reach");
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "mover stayed within reach"
        );
    }

    @Test
    @DisplayName("evaluates false (stays beyond reach)")
    void evaluatesFalse_staysBeyondReach() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER,
                TestUtils.TEST_ARRAY_0_0_0, TestUtils.TEST_ARRAY_0_0_0);
        RPGLObject mover = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER,
                TestUtils.TEST_ARRAY_10_10_10, TestUtils.TEST_ARRAY_0_0_0);

        RPGLEffect effect = new RPGLEffect().setSource(object).setTarget(object);

        assertFalse(new ExitingReach().evaluate(effect, new Movement().setSource(mover).setTarget(mover), new JsonObject() {{
            /*{
                "condition": "exiting_reach"
            }*/
            this.putString("condition", "exiting_reach");
        }}, new DummyContext(), TestUtils.TEST_ARRAY_10_10_10),
                "mover stayed beyond reach"
        );
    }

}
