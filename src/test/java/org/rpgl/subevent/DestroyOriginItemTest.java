package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;
import org.rpgl.core.RPGLResource;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.subevent.DestroyOriginItem class.
 *
 * @author Calvin Withun
 */
public class DestroyOriginItemTest {

    @BeforeAll
    static void beforeAll() {
        DatapackLoader.loadDatapacks(
                new File("src/test/resources/datapacks".replace("/", File.separator))
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
    @DisplayName("errors on wrong subevent")
    void errorsOnWrongSubevent() {
        Subevent subevent = new DestroyOriginItem();
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
    @DisplayName("destroys origin item")
    void destroysOriginItem() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        RPGLResource action = RPGLFactory.newResource("std:common/action/01");
        object.addResource(action);

        RPGLItem potionOfHealing = RPGLFactory.newItem("std:potion/potion_of_healing");
        object.giveItem(potionOfHealing.getUuid());
        object.equipItem(potionOfHealing.getUuid(), "left_hand");
        object.equipItem(potionOfHealing.getUuid(), "right_hand");

        String potionOfHealingUuid = potionOfHealing.getUuid();

        object.invokeEvent(
                new RPGLObject[]{ object },
                TestUtils.getEventById(object.getEventObjects(new DummyContext()), "std:item/potion/potion_of_healing/drink"),
                object.getResourcesWithTag("action"),
                new DummyContext()
        );

        assertNull(object.getEquippedItems().getString("left_hand"),
                "left_hand equipment slot should be empty once origin item is destroyed"
        );
        assertNull(object.getEquippedItems().getString("right_hand"),
                "left_hand equipment slot should be empty once origin item is destroyed"
        );
        assertFalse(object.getInventory().asList().contains(potionOfHealingUuid),
                "inventory should not contain origin item uuid once it is destroyed"
        );
        assertNull(UUIDTable.getItem(potionOfHealingUuid),
                "UUIDTable should no longer have potion of healing registered"
        );
        assertNull(potionOfHealing.getUuid(),
                "origin item should be stripped of its uuid upon being destroyed"
        );
    }

}
