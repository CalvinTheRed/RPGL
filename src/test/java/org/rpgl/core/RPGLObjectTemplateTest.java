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
    @DisplayName("Template sets default effects array")
    void test1() {
        RPGLObject dummyObject = RPGLFactory.newObject("test:blank");
        assert dummyObject != null;

        JsonArray effectsArray = (JsonArray) dummyObject.get("effects");
        assertNotNull(effectsArray,
                "RPGLObjectTemplate should create an array for a new RPGLObject's effects if none are specified."
        );
        assertTrue(effectsArray.isEmpty(),
                "The default effects array should be empty."
        );
    }

    @Test
    @DisplayName("Template sets default events array")
    void test2() {
        RPGLObject dummyObject = RPGLFactory.newObject("test:blank");
        assert dummyObject != null;

        JsonArray eventsArray = (JsonArray) dummyObject.get("events");
        assertNotNull(eventsArray,
                "RPGLObjectTemplate should create an array for a new RPGLObject's events if none are specified."
        );
        assertTrue(eventsArray.isEmpty(),
                "The default events array should be empty."
        );
    }

    @Test
    @DisplayName("Template sets default items data")
    void test3() {
        RPGLObject dummyObject = RPGLFactory.newObject("test:blank");
        assert dummyObject != null;

        JsonObject items = (JsonObject) dummyObject.get("items");
        assertNotNull(items,
                "RPGLObjectTemplate should create an object for a new RPGLObject's items if none are specified."
        );
        JsonArray inventory = (JsonArray) items.get("inventory");
        assertNotNull(inventory,
                "RPGLObjectTemplate should create an array for a new RPGLObject's inventory if none is specified."
        );
        assertTrue(inventory.isEmpty(),
                "The default inventory array should be empty."
        );
        assertEquals(1, items.size(),
                "There should only be one key-value pair in the items object (the inventory)."
        );
    }

    @Test
    @DisplayName("Template creates specified Effects")
    void test4() {
        RPGLObject dummyObject = RPGLFactory.newObject("test:blank_plus_effects");
        assert dummyObject != null;

        JsonArray effectsArray = (JsonArray) dummyObject.get("effects");
        assertNotNull(effectsArray,
                "RPGLObjectTemplate should not delete the new RPGLObject's effects array."
        );
        assertEquals(1, effectsArray.size(),
                "The effects array should have one element."
        );
        String effectUuid = (String) effectsArray.get(0);
        assertNotNull(effectUuid,
                "The effects array element should not be null."
        );
        assertNotNull(UUIDTable.getEffect(effectUuid),
                "The new Effect should be registered in the UUIDTable."
        );

        // TODO check for source and target of Effects to match the object
    }

    @Test
    @DisplayName("Template creates specified Items")
    void test5() {
        RPGLObject dummyObject = RPGLFactory.newObject("test:blank_plus_items");
        assert dummyObject != null;

        JsonObject items = (JsonObject) dummyObject.get("items");
        assertNotNull(items,
                "RPGLObjectTemplate should not delete the new RPGLObject's items object."
        );
        assertEquals(2, items.size(),
                "The items object should have two key-value pairs (hand_1 and inventory)."
        );

        String hand1ItemUuid = (String) items.get("hand_1");
        assertNotNull(hand1ItemUuid,
                "Equipment slots should be preserved."
        );
        assertNotNull(UUIDTable.getItem(hand1ItemUuid),
                "Equipment Items should be registered in the UUIDTable."
        );

        JsonArray inventory = (JsonArray) items.get("inventory");
        assertNotNull(inventory,
                "Inventory should be preserved."
        );
        assertEquals(2, inventory.size(),
                "Inventory should contain 2 items (hand_1 item and unequipped inventory item)."
        );
        String inventoryItem1Uuid = (String) inventory.get(0);
        String inventoryItem2Uuid = (String) inventory.get(1);
        assertNotNull(UUIDTable.getItem(inventoryItem1Uuid),
                "Inventory Item 1 should be registered in the UUIDTable."
        );
        assertNotNull(UUIDTable.getItem(inventoryItem2Uuid),
                "Inventory Item 2 should be registered in the UUIDTable."
        );
        assertEquals(hand1ItemUuid, inventoryItem2Uuid,
                "Equipment slot item should be copied to inventory array."
        );
    }

}
