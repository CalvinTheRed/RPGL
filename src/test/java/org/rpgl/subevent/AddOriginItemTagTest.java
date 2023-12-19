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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.subevent.AddOriginItemTag class.
 *
 * @author Calvin Withun
 */
public class AddOriginItemTagTest {

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
        Subevent subevent = new AddOriginItemTag();
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
    @DisplayName("adds tag to item")
    void addsTagToItem() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLItem item = RPGLFactory.newItem("std:weapon/melee/martial/longsword");
        DummyContext context = new DummyContext();
        context.add(object);

        AddOriginItemTag addOriginItemTag = new AddOriginItemTag();
        addOriginItemTag.joinSubeventData(new JsonObject() {{
            this.putString("tag", "test_tag");
        }});
        addOriginItemTag.setOriginItem(item.getUuid());
        addOriginItemTag.setSource(object);
        addOriginItemTag.prepare(context, List.of());
        addOriginItemTag.setTarget(object);
        addOriginItemTag.invoke(context, List.of());

        assertTrue(item.getTags().asList().contains("test_tag"),
                "invoke should add intended tag to origin item"
        );
    }

}
