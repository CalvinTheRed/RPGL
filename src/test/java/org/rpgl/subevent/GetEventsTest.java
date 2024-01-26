package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLEvent;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLItem;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.subevent.GetEvents class.
 *
 * @author Calvin Withun
 */
public class GetEventsTest {

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
    @DisplayName("defaults to empty list")
    void defaultsToEmptyList() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        GetEvents getEvents = new GetEvents();
        getEvents.setSource(object);
        getEvents.prepare(new DummyContext());

        assertTrue(getEvents.getEvents().isEmpty(),
                "getEvents should return an empty array by default"
        );
    }

    @Test
    @DisplayName("returns events")
    void returnsEvents() throws Exception {
        RPGLItem item = RPGLFactory.newItem("std:weapon/melee/martial/longsword");
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        GetEvents getEvents = new GetEvents();
        getEvents.setSource(object);
        getEvents.prepare(new DummyContext());

        getEvents.addEvent("std:item/weapon/melee/martial/longsword/melee", item.getUuid(), null);
        getEvents.addEvent("std:common/improvised_thrown", item.getUuid(), null);

        List<RPGLEvent> events = getEvents.getEvents();
        assertEquals(2, events.size(),
                "2 events should be granted"
        );

        for (RPGLEvent event : events) {
            assertEquals(item.getUuid(), event.getOriginItem(),
                    "item-based events should indicate the item providing the event as the origin item"
            );
        }
    }

}
