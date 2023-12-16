package org.rpgl.uuidtable;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.json.JsonArray;
import org.rpgl.testUtils.TestUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.uuidtable.UUIDTable class.
 *
 * @author Calvin Withun
 */
public class UUIDTableTest {

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
    @DisplayName("saves and loads content")
    void savesAndLoadsContent() throws IOException {
        String objectUuid = RPGLFactory.newObject("std:humanoid/knight", TestUtils.TEST_USER).getUuid();

        // save UUIDTable contents and then clear UUIDTable
        UUIDTable.saveToDirectory(
                new File("src/test/resources/saves/test_save".replace("/", File.separator))
        );
        UUIDTable.clear();
        assertNull(UUIDTable.getObject(objectUuid),
                "UUIDTable should be empty"
        );

        // load data back into UUIDTable and check contents
        UUIDTable.loadFromDirectory(
                new File("src/test/resources/saves/test_save".replace("/", File.separator))
        );
        RPGLObject object = UUIDTable.getObject(objectUuid);
        assertNotNull(object,
                "object should be successfully reloaded"
        );
        assertEquals(10, object.getResourceObjects().size(),
                "object's resources should all be re-loaded"
        );
        JsonArray inventory = object.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            String itemUuid = inventory.getString(i);
            RPGLItem item = UUIDTable.getItem(itemUuid);
            assertNotNull(item,
                    "object's inventory items should all be re-loaded"
            );
        }
        assertEquals(1, object.getEffectObjects().size(),
                "object's effects should all be re-loaded"
        );
    }

    @Test
    @DisplayName("retrieves objects by user id")
    void retrievesObjectsByUserId() {
        String dragonUser = "dragon-user";
        String knightUser = "knight-user";
        RPGLObject dragon1 = RPGLFactory.newObject("std:dragon/red/young", dragonUser);
        RPGLObject dragon2 = RPGLFactory.newObject("std:dragon/red/young", dragonUser);
        RPGLObject knight1 = RPGLFactory.newObject("std:humanoid/knight", knightUser);
        RPGLObject knight2 = RPGLFactory.newObject("std:humanoid/knight", knightUser);

        List<RPGLObject> dragonUserObjects = UUIDTable.getObjectsByUserId(dragonUser);
        List<RPGLObject> knightUserObjects = UUIDTable.getObjectsByUserId(knightUser);

        assertEquals(2, dragonUserObjects.size(),
                "dragonUser should be a user for 2 objects"
        );
        assertTrue(dragonUserObjects.contains(dragon1),
                "dragonUser should be user for dragon1"
        );
        assertTrue(dragonUserObjects.contains(dragon2),
                "dragonUser should be user for dragon2"
        );
        assertTrue(knightUserObjects.contains(knight1),
                "knightUser should be user for knight1"
        );
        assertTrue(knightUserObjects.contains(knight2),
                "knightUser should be user for knight2"
        );
    }

}
