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

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        String objectUuid = RPGLFactory.newObject("std:humanoid/knight").getUuid();

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

}
