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
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.subevent.RemoveOriginItemTag class.
 *
 * @author Calvin Withun
 */
public class RemoveOriginItemTagTest {

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
        Subevent subevent = new RemoveOriginItemTag()
                .joinSubeventData(new JsonObject() {{
                    /*{
                        "subevent": "not_a_subevent"
                    }*/
                    this.putString("subevent", "not_a_subevent");
                }});

        assertThrows(SubeventMismatchException.class,
                () -> subevent.invoke(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("removes origin item tag")
    void removesOriginItemTag() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLItem item = RPGLFactory.newItem("std:weapon/melee/martial/longsword");
        item.addTag("test_tag");
        DummyContext context = new DummyContext();
        context.add(object);

        new RemoveOriginItemTag()
                .joinSubeventData(new JsonObject() {{
                    this.putString("tag", "test_tag");
                }})
                .setOriginItem(item.getUuid())
                .setSource(object)
                .prepare(context, object.getPosition())
                .setTarget(object)
                .invoke(context, object.getPosition());

        assertFalse(item.getTags().asList().contains("test_tag"),
                "invoke should remove intended tag from origin item"
        );
    }

}
