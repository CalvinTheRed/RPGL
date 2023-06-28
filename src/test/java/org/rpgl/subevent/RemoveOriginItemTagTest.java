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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.subevent.RemoveOriginItemTag class.
 *
 * @author Calvin Withun
 */
public class RemoveOriginItemTagTest {

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
    @DisplayName("invoke removes tag")
    void invoke_removesTag() throws Exception {
        RPGLObject object = RPGLFactory.newObject("std:commoner");
        RPGLItem item = RPGLFactory.newItem("std:longsword");
        item.addTag("test_tag");
        DummyContext context = new DummyContext();
        context.add(object);

        RemoveOriginItemTag removeOriginItemTag = new RemoveOriginItemTag();
        removeOriginItemTag.joinSubeventData(new JsonObject() {{
            this.putString("tag", "test_tag");
        }});
        removeOriginItemTag.setOriginItem(item.getUuid());
        removeOriginItemTag.setSource(object);
        removeOriginItemTag.prepare(context);
        removeOriginItemTag.setTarget(object);
        removeOriginItemTag.invoke(context);

        assertFalse(item.getTags().asList().contains("test_tag"),
                "invoke should remove intended tag from origin item"
        );
    }

}
