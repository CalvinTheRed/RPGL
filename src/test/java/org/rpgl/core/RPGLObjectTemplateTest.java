package org.rpgl.core;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonObject;
import org.junit.jupiter.api.*;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class RPGLObjectTemplateTest {

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
    @DisplayName("object items data processed correctly")
    void objectItemsDataProcessedCorrectly() {
        RPGLObject object = DatapackLoader.DATAPACKS.get("test").getObjectTemplate("goblin").newInstance();
        JsonObject items = (JsonObject) object.get("items");

        assertNotNull(items,
                "Object test:goblin missing items object."
        );
        JsonArray inventory = (JsonArray) items.get("inventory");

        assertNotNull(inventory,
                "Object test:goblin missing items.inventory array."
        );
        assertEquals(4, inventory.size(),
                "Object test:goblin does not have 4 items in items.inventory."
        );
        assertNotNull(inventory.get(0),
                "Object test:goblin missing uuid at items.inventory[0]."
        );
        assertNotNull(inventory.get(1),
                "Object test:goblin missing uuid at items.inventory[1]."
        );
        assertNotNull(inventory.get(2),
                "Object test:goblin missing uuid at items.inventory[2]."
        );
        assertNotNull(inventory.get(3),
                "Object test:goblin missing uuid at items.inventory[3]."
        );
        assertNotNull(UUIDTable.getItem((Long) inventory.get(0)),
                "Object test:goblin item items.inventory[0] is not registered to UUIDTable."
        );
        assertNotNull(UUIDTable.getItem((Long) inventory.get(1)),
                "Object test:goblin item items.inventory[1] is not registered to UUIDTable."
        );
        assertNotNull(UUIDTable.getItem((Long) inventory.get(2)),
                "Object test:goblin item items.inventory[2] is not registered to UUIDTable."
        );
        assertNotNull(UUIDTable.getItem((Long) inventory.get(3)),
                "Object test:goblin item items.inventory[3] is not registered to UUIDTable."
        );
        assertNotNull(items.get("armor"),
                "Object test:goblin armor slot empty."
        );
        assertNotNull(items.get("mainhand"),
                "Object test:goblin mainhand slot empty."
        );
        assertNotNull(items.get("offhand"),
                "Object test:goblin offhand slot empty."
        );
        assertTrue(inventory.contains(items.get("armor")),
                "Object test:goblin armor item not in inventory"
        );
        assertTrue(inventory.contains(items.get("mainhand")),
                "Object test:goblin mainhand item not in inventory"
        );
        assertTrue(inventory.contains(items.get("offhand")),
                "Object test:goblin offhand item not in inventory"
        );
        assertEquals(5, UUIDTable.size(),
                "UUIDTable does not have 5 objects registered to it."
        );
    }
}
