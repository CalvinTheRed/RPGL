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
 * Testing class for the org.rpgl.condition.IsObjectsTurn class.
 *
 * @author Calvin Withun
 */
public class IsObjectsTurnTest {

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
                () -> new IsObjectsTurn().evaluate(null, null, new JsonObject() {{
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
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.setIsTurn(true);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);

        assertTrue(new IsObjectsTurn().evaluate(null, dummySubevent, new JsonObject() {{
            /*{
                "condition": "is_objects_turn",
                "object": {
                    "from": "subevent",
                    "object": "source"
                }
            }*/
            this.putString("condition", "is_objects_turn");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
        }}, context, TestUtils.TEST_ARRAY_0_0_0),
                "evaluate should return true when it is object's turn"
        );
    }

    @Test
    @DisplayName("evaluates false")
    void evaluatesFalse() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.setIsTurn(false);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);

        assertFalse(new IsObjectsTurn().evaluate(null, dummySubevent, new JsonObject() {{
            /*{
                "condition": "is_objects_turn",
                "object": {
                    "from": "subevent",
                    "object": "source"
                }
            }*/
            this.putString("condition", "is_objects_turn");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
        }}, context, TestUtils.TEST_ARRAY_0_0_0),
                "evaluate should return false when it is not object's turn"
        );
    }

}
