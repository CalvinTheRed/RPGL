package org.rpgl.function;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.GetObjectTags;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.AddObjectTag class.
 *
 * @author Calvin Withun
 */
public class AddObjectTagTest {

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
    @DisplayName("errors on wrong function")
    void errorsOnWrongFunction() {
        assertThrows(FunctionMismatchException.class,
                () -> new AddObjectTag().execute(null, null, new JsonObject() {{
                    /*{
                        "function": "not_a_function"
                    }*/
                    this.putString("function", "not_a_function");
                }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("adds object tag")
    void addsObjectTag() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        GetObjectTags getObjectTags = new GetObjectTags();
        getObjectTags.setSource(object);
        getObjectTags.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        new AddObjectTag().execute(null, getObjectTags, new JsonObject() {{
            /*{
                "function": "add_object_tag",
                "tag": "test_tag"
            }*/
            this.putString("function", "add_object_tag");
            this.putString("tag", "test_tag");
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        String expected = """
                ["test_tag"]""";
        assertEquals(expected, getObjectTags.getObjectTags().toString(),
                "execute should add an object tag to the subevent"
        );
    }

}
