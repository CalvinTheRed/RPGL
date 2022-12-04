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

/**
 * Testing class for core.RPGLObjectTemplate class.
 *
 * @author Calvin Withun
 */
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
    @DisplayName("object data processed correctly")
    void test1() {
        RPGLObject object = RPGLFactory.newObject("dummy:dummy");
        assert object != null;
        JsonObject items = (JsonObject) object.get("items");

        assertNotNull(items,
                "Object dummy:dummy missing items object."
        );
        JsonArray inventory = (JsonArray) items.get("inventory");

        assertNotNull(inventory,
                "Object dummy:dummy missing items.inventory array."
        );
        assertEquals(4, inventory.size(),
                // hand_1, hand_2, and 2 inventory dummy items
                "Object dummy:dummy does not have 4 items in items.inventory."
        );
        assertNotNull(inventory.get(0),
                "Object dummy:dummy missing uuid at items.inventory[0]."
        );
        assertNotNull(inventory.get(1),
                "Object dummy:dummy missing uuid at items.inventory[1]."
        );
        assertNotNull(inventory.get(2),
                "Object dummy:dummy missing uuid at items.inventory[2]."
        );
        assertNotNull(inventory.get(3),
                "Object dummy:dummy missing uuid at items.inventory[3]."
        );
        assertNotNull(UUIDTable.getItem((String) inventory.get(0)),
                "Object dummy:dummy item items.inventory[0] is not registered to UUIDTable."
        );
        assertNotNull(UUIDTable.getItem((String) inventory.get(1)),
                "Object dummy:dummy item items.inventory[1] is not registered to UUIDTable."
        );
        assertNotNull(UUIDTable.getItem((String) inventory.get(2)),
                "Object dummy:dummy item items.inventory[2] is not registered to UUIDTable."
        );
        assertNotNull(UUIDTable.getItem((String) inventory.get(3)),
                "Object dummy:dummy item items.inventory[3] is not registered to UUIDTable."
        );
        assertNotNull(items.get("hand_1"),
                "Object dummy:dummy hand_1 slot empty."
        );
        assertNotNull(items.get("hand_2"),
                "Object dummy:dummy hand_2 slot empty."
        );
        assertTrue(inventory.contains(items.get("hand_1")),
                "Object dummy:dummy hand_1 item not in inventory"
        );
        assertTrue(inventory.contains(items.get("hand_2")),
                "Object dummy:dummy hand_2 item not in inventory"
        );

        JsonArray effects = (JsonArray) object.get("effects");
        assertNotNull(effects,
                "Object dummy:dummy missing effects array."
        );
        assertEquals(2, effects.size(),
                "Object dummy:dummy does not have 2 effects."
        );
        assertNotNull(effects.get(0),
                "Object dummy:dummy missing uuid at effects[0]."
        );
        assertNotNull(effects.get(1),
                "Object dummy:dummy missing uuid at effects[1]."
        );
        assertNotNull(UUIDTable.getEffect((String) effects.get(0)),
                "Object dummy:dummy effect effects[0] is not registered to UUIDTable."
        );
        assertNotNull(UUIDTable.getEffect((String) effects.get(1)),
                "Object dummy:dummy effect effects[1] is not registered to UUIDTable."
        );

        // 1 object, 4 items, 2 effects
        assertEquals(7, UUIDTable.size(),
                // 1 object, 2 effects, 4 items
                "UUIDTable does not have 7 objects registered to it."
        );
    }
}
