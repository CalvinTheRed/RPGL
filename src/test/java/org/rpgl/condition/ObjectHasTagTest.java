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
 * Testing class for the org.rpgl.condition.ObjectHasTag class.
 *
 * @author Calvin Withun
 */
public class ObjectHasTagTest {

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
        Condition condition = new ObjectHasTag();
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
    @DisplayName("evaluate returns true when object has desired tag")
    void evaluate_returnsTrueWhenObjectHasDesiredTag() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std_objects:commoner");
        RPGLObject target = RPGLFactory.newObject("std_objects:commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.addTag("test_tag");

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);
        dummySubevent.setTarget(target);

        ObjectHasTag objectHasTag = new ObjectHasTag();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "object_has_tag",
                "object": {
                    "from": "subevent",
                    "object": "source"
                },
                "tag": "test_tag"
            }*/
            this.putString("condition", "object_has_tag");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
            this.putString("tag", "test_tag");
        }};

        assertTrue(objectHasTag.evaluate(null, dummySubevent, conditionJson, context),
                "evaluate should return true when object has desired tag"
        );
    }

    @Test
    @DisplayName("evaluate returns false when object does not have desired tag")
    void evaluate_returnsFalseWhenObjectDoesNotHaveDesiredTag() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std_objects:commoner");
        RPGLObject target = RPGLFactory.newObject("std_objects:commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.addTag("test_tag");

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);
        dummySubevent.setTarget(target);

        ObjectHasTag objectHasTag = new ObjectHasTag();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "object_has_tag",
                "object": {
                    "from": "subevent",
                    "object": "source"
                },
                "tag": "wrong_tag"
            }*/
            this.putString("condition", "object_has_tag");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
            this.putString("tag", "wrong_tag");
        }};

        assertFalse(objectHasTag.evaluate(null, dummySubevent, conditionJson, context),
                "evaluate should return false when object does not have desired tag"
        );
    }

}
