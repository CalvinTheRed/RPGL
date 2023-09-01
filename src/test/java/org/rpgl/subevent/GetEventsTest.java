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
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;
import java.util.Objects;

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
        RPGLObject object = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(object);

        GetEvents getEvents = new GetEvents();
        getEvents.setSource(object);
        getEvents.prepare(context);

        assertTrue(getEvents.getEvents().isEmpty(),
                "getEvents should return an empty array by default"
        );
    }

    @Test
    @DisplayName("getEvents returns the correct events")
    void getEvents_returnsCorrectEvents() throws Exception {
        RPGLItem item = RPGLFactory.newItem("std:weapon/melee/martial/longsword");
        RPGLObject object = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(object);

        GetEvents getEvents = new GetEvents();
        getEvents.setSource(object);
        getEvents.prepare(context);

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
