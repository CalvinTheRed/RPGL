package org.rpgl.subevent;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.junit.jupiter.api.*;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.math.Die;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DummySubeventTest {

    @BeforeAll
    static void beforeAll() throws Exception {
        DatapackLoader.loadDatapacks(
                new File(Objects.requireNonNull(DatapackTest.class.getClassLoader().getResource("datapacks")).toURI())
        );
        Die.setTesting(true);
    }

    @AfterAll
    static void afterAll() {
        DatapackLoader.DATAPACKS.clear();
    }

    @AfterEach
    void afterEach() {
        DummySubevent.resetCounter();
    }

    @Test
    @DisplayName("DummySubevent Subevent throws SubeventMismatchException when subevent type doesn't match")
    void test0() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new DummySubevent();
        String subeventJsonString = """
                {
                    "subevent": "not_a_subevent"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        RPGLContext context = new RPGLContext(null);

        /*
         * Verify subevent behaves as expected
         */
        assertThrows(SubeventMismatchException.class,
                () -> subevent.clone(subeventJson).invoke(context),
                "DummySubevent Subevent should throw a SubeventMismatchException if the specified subevent doesn't match."
        );
    }

    @Test
    @DisplayName("DummySubevent Subevent can be prepared and invoked")
    void test1() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new DummySubevent();
        String subeventJsonString = """
                {
                    "subevent": "dummy_subevent"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        DummySubevent dummySubevent = (DummySubevent) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent methods
         */
        dummySubevent.setSource(object);
        dummySubevent.prepare(context);
        dummySubevent.setTarget(object);
        dummySubevent.invoke(context);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(1, DummySubevent.counter,
                "DummySubevent Subevent should increment counter by 1 each time it is invoked."
        );
    }

}
