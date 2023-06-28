package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.subevent.AddOriginItemTag class.
 *
 * @author Calvin Withun
 */
public class AddOriginItemTagTest {

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
        Subevent subevent = new AddOriginItemTag();
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
    @DisplayName("invoke adds tag")
    void invoke_addsTag() throws Exception {
        RPGLObject object = RPGLFactory.newObject("std:commoner");
        RPGLItem item = RPGLFactory.newItem("std:longsword");
        DummyContext context = new DummyContext();
        context.add(object);

        AddOriginItemTag addOriginItemTag = new AddOriginItemTag();
        addOriginItemTag.joinSubeventData(new JsonObject() {{
            this.putString("tag", "test_tag");
        }});
        addOriginItemTag.setOriginItem(item.getUuid());
        addOriginItemTag.setSource(object);
        addOriginItemTag.prepare(context);
        addOriginItemTag.setTarget(object);
        addOriginItemTag.invoke(context);

        assertTrue(item.getTags().asList().contains("test_tag"),
                "invoke should add intended tag to origin item"
        );
    }

}
