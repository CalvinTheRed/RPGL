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
 * Testing class for the org.rpgl.condition.CheckLevel class.
 *
 * @author Calvin Withun
 */
public class CheckLevelTest {

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
                () -> new CheckLevel().evaluate(null, null, new JsonObject() {{
                    /*{
                        "condition": "not_a_condition"
                    }*/
                    this.putString("condition", "not_a_condition");
                }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "Condition should throw a ConditionMismatchException if the specified condition doesn't match"
        );
    }

    @Test
    @DisplayName("evaluates true ")
    void evaluatesTrue() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        for (int i = 0; i < 15; i++) {
            object.levelUp("debug:blank", new JsonObject());
        }

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(object);

        assertTrue(new CheckLevel().evaluate(null, dummySubevent, new JsonObject() {{
            /*{
                "condition": "check_level",
                "object": {
                    "from": "subevent",
                    "object": "source"
                },
                "class": "debug:blank",
                "comparison": "=",
                "compare_to": 15
            }*/
            this.putString("condition", "check_level");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
            this.putString("class", "debug:blank");
            this.putString("comparison", "=");
            this.putInteger("compare_to", 15);
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "evaluate should return true"
        );
    }

    @Test
    @DisplayName("evaluates false")
    void evaluatesFalse() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        for (int i = 0; i < 10; i++) {
            object.levelUp("debug:blank", new JsonObject());
        }

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(object);

        assertFalse(new CheckLevel().evaluate(null, dummySubevent, new JsonObject() {{
            /*{
                "condition": "check_level",
                "object": {
                    "from": "subevent",
                    "object": "source"
                },
                "class": "debug:blank",
                "comparison": "=",
                "compare_to": 15
            }*/
            this.putString("condition", "check_level");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
            this.putString("class", "debug:blank");
            this.putString("comparison", "=");
            this.putInteger("compare_to", 15);
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "evaluate should return false"
        );
    }
}
