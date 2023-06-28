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
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DummySubevent;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

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
        Condition condition = new SubeventHasTag();
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
    @DisplayName("evaluate returns true when subevent has desired tag")
    void evaluate_returnsTrueWhenSubeventHasDesiredTag() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:commoner");
        RPGLObject target = RPGLFactory.newObject("std:commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);
        dummySubevent.setTarget(target);
        dummySubevent.addTag("test_tag");

        SubeventHasTag subeventHasTag = new SubeventHasTag();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "subevent_has_tag",
                "tag": "test_tag"
            }*/
            this.putString("condition", "subevent_has_tag");
            this.putString("tag", "test_tag");
        }};

        assertTrue(subeventHasTag.evaluate(null, dummySubevent, conditionJson, context),
                "evaluate should return true when subevent has desired tag"
        );
    }

    @Test
    @DisplayName("evaluate returns false when subevent does not have desired tag")
    void evaluate_returnsTrueWhenSubeventDoesNotHaveDesiredTag() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:commoner");
        RPGLObject target = RPGLFactory.newObject("std:commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);
        dummySubevent.setTarget(target);
        dummySubevent.addTag("test_tag");

        SubeventHasTag subeventHasTag = new SubeventHasTag();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "subevent_has_tag",
                "tag": "wrong_tag"
            }*/
            this.putString("condition", "subevent_has_tag");
            this.putString("tag", "wrong_tag");
        }};

        assertFalse(subeventHasTag.evaluate(null, dummySubevent, conditionJson, context),
                "evaluate should return false when subevent does not have desired tag"
        );
    }

}
