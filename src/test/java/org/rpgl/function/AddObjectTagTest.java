package org.rpgl.function;

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
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.GetObjectTags;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.AddObjectTag class.
 *
 * @author Calvin Withun
 */
public class AddObjectTagTest {

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
    @DisplayName("execute wrong function")
    void execute_wrongFunction_throwsException() {
        Function function = new AddObjectTag();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "not_a_function"
            }*/
            this.putString("function", "not_a_function");
        }};

        RPGLContext context = new RPGLContext();

        assertThrows(FunctionMismatchException.class,
                () -> function.execute(null, null, functionJson, context),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("execute adds object tag to subevent")
    void execute_addsObjectTagToSubevent() throws Exception {
        RPGLObject object = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(object);

        GetObjectTags getObjectTags = new GetObjectTags();
        getObjectTags.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "get_object_tags");
        }});
        getObjectTags.setSource(object);
        getObjectTags.prepare(context);

        AddObjectTag addObjectTag = new AddObjectTag();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "add_object_tag",
                "tag": "test_tag"
            }*/
            this.putString("function", "add_object_tag");
            this.putString("tag", "test_tag");
        }};

        addObjectTag.execute(null, getObjectTags, functionJson, context);

        String expected = """
                ["test_tag"]""";
        assertEquals(expected, getObjectTags.getObjectTags().toString(),
                "execute should add an object tag to the subevent"
        );
    }

}
