package org.rpgl.condition;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DummySubevent;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.condition.SubeventHasTag class.
 *
 * @author Calvin Withun
 */
public class SubeventHasTagTest {

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
                () -> new SubeventHasTag().evaluate(null, null, new JsonObject() {{
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
        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.addTag("test_tag");

        assertTrue(new SubeventHasTag().evaluate(null, dummySubevent, new JsonObject() {{
            /*{
                "condition": "subevent_has_tag",
                "tag": "test_tag"
            }*/
            this.putString("condition", "subevent_has_tag");
            this.putString("tag", "test_tag");
        }}, new DummyContext()),
                "should evaluate true when subevent has desired tag"
        );
    }

    @Test
    @DisplayName("evaluates false")
    void evaluatesFalse() throws Exception {
        assertFalse(new SubeventHasTag().evaluate(null, new DummySubevent(), new JsonObject() {{
            /*{
                "condition": "subevent_has_tag",
                "tag": "test_tag"
            }*/
            this.putString("condition", "subevent_has_tag");
            this.putString("tag", "test_tag");
        }}, new DummyContext()),
                "should evaluate false when subevent does not have desired tag"
        );
    }

}
