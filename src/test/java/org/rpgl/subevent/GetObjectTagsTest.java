package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.subevent.GetObjectTags class.
 *
 * @author Calvin Withun
 */
public class GetObjectTagsTest {

    @BeforeAll
    static void beforeAll() {
        DatapackLoader.loadDatapacks(
                new File("src/test/resources/datapacks".replace("/", File.separator))
        );
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
    @DisplayName("errors on wrong subevent")
    void errorsOnWrongSubevent() {
        Subevent subevent = new GetObjectTags();
        subevent.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "not_a_subevent"
            }*/
            this.putString("subevent", "not_a_subevent");
        }});

        assertThrows(SubeventMismatchException.class,
                () -> subevent.invoke(new DummyContext(), List.of()),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("defaults to empty list")
    void defaultsToEmptyList() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        GetObjectTags getObjectTags = new GetObjectTags();
        getObjectTags.setSource(object);
        getObjectTags.prepare(new DummyContext(), List.of());

        assertEquals("[]", getObjectTags.getObjectTags().toString(),
                "getTags should return an empty array by default"
        );
    }

    @Test
    @DisplayName("returns tags")
    void returnsTags() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        GetObjectTags getObjectTags = new GetObjectTags();
        getObjectTags.setSource(object);
        getObjectTags.prepare(new DummyContext(), List.of());

        getObjectTags.addObjectTag("test_tag_1");
        getObjectTags.addObjectTag("test_tag_2");

        String expected = """
                ["test_tag_1","test_tag_2"]""";
        assertEquals(expected, getObjectTags.getObjectTags().toString(),
                "getTags should return all tags which were granted to the subevent"
        );
    }

}
