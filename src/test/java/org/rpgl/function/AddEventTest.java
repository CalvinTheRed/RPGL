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
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.GetEvents;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.AddEvent class.
 *
 * @author Calvin Withun
 */
public class AddEventTest {

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
        Function function = new AddEvent();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "not_a_function"
            }*/
            this.putString("function", "not_a_function");
        }};

        DummyContext context = new DummyContext();

        assertThrows(FunctionMismatchException.class,
                () -> function.execute(null, null, functionJson, context),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("")
    void test() throws Exception {
        RPGLObject object = RPGLFactory.newObject("std:commoner");
        DummyContext context = new DummyContext();
        context.add(object);

        GetEvents getEvents = new GetEvents();
        getEvents.setSource(object);
        getEvents.prepare(context);

        AddEvent addEvent = new AddEvent();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "add_event",
                "event": "std:dodge"
            }*/
            this.putString("function", "add_event");
            this.putString("event", "std:dodge");
        }};

        addEvent.execute(null, getEvents, functionJson, context);

        assertEquals("std:dodge", getEvents.getEvents().get(0).getId(),
                "execute should add the correct event to the subevent"
        );
    }
}
