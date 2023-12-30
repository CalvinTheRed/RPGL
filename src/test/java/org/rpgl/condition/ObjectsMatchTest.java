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
import org.rpgl.subevent.DummySubevent;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.condition.ObjectsMatch class.
 *
 * @author Calvin Withun
 */
public class ObjectsMatchTest {

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
                () -> new ObjectsMatch().evaluate(null, null, new JsonObject() {{
                    /*{
                        "condition": "not_a_condition"
                    }*/
                    this.putString("condition", "not_a_condition");
                }}, new DummyContext()),
                "Condition should throw a ConditionMismatchException if the specified condition doesn't match"
        );
    }

    @Test
    @DisplayName("evaluates true")
    void evaluatesTrue() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(object);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(object);

        assertTrue(new ObjectsMatch().evaluate(effect, dummySubevent, new JsonObject() {{
            /*{
                "condition": "objects_match",
                "effect": "source",
                "subevent": "source"
            }*/
            this.putString("condition", "objects_match");
            this.putString("effect", "source");
            this.putString("subevent", "source");
        }}, new DummyContext()),
                "evaluate should return true when objects match"
        );
    }

    @Test
    @DisplayName("evaluates false")
    void evaluatesFalse() throws Exception {
        RPGLObject effectSource = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject subeventSource = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(effectSource);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(subeventSource);

        assertFalse(new ObjectsMatch().evaluate(effect, dummySubevent, new JsonObject() {{
            /*{
                "condition": "objects_match",
                "effect": "source",
                "subevent": "source"
            }*/
            this.putString("condition", "objects_match");
            this.putString("effect", "source");
            this.putString("subevent", "source");
        }}, new DummyContext()),
                "evaluate should return false when objects do not match"
        );
    }

}
