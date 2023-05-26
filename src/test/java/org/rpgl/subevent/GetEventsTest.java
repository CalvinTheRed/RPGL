package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.subevent.GetEvents class.
 *
 * @author Calvin Withun
 */
public class GetEventsTest {

    @BeforeAll
    static void beforeAll() throws Exception {
        DatapackLoader.loadDatapacks(
                new File(Objects.requireNonNull(DatapackTest.class.getClassLoader().getResource("datapacks")).toURI())
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
    @DisplayName("invoke wrong subevent")
    void invoke_wrongSubevent_throwsException() {
        Subevent subevent = new GetEvents();
        subevent.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "not_a_subevent"
            }*/
            this.putString("subevent", "not_a_subevent");
        }});

        assertThrows(SubeventMismatchException.class,
                () -> subevent.invoke(new DummyContext()),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("getEvents is empty by default")
    void getEvents_isEmptyByDefault() throws Exception {
        RPGLObject object = RPGLFactory.newObject("demo:commoner");
        DummyContext context = new DummyContext();
        context.add(object);

        GetEvents getEvents = new GetEvents();
        getEvents.setSource(object);
        getEvents.prepare(context);

        assertEquals("[]", getEvents.getEvents().toString(),
                "getEvents should return an empty array by default"
        );
    }

    @Test
    @DisplayName("getTags returns all granted event IDs")
    void getEvents_returnsAllGrantedEventIds() throws Exception {
        RPGLObject object = RPGLFactory.newObject("demo:commoner");
        DummyContext context = new DummyContext();
        context.add(object);

        GetEvents getEvents = new GetEvents();
        getEvents.setSource(object);
        getEvents.prepare(context);

        getEvents.addEvent("demo:test_1");
        getEvents.addEvent("demo:test_2");

        String expected = """
                ["demo:test_1","demo:test_2"]""";
        assertEquals(expected, getEvents.getEvents().toString(),
                "getEvents should return all event IDs which were granted to the subevent"
        );
    }

}
