package org.rpgl.core;

import org.jsonutils.JsonArray;
import org.junit.jupiter.api.*;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class RPGLItemTemplateTest {

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
    @DisplayName("item when_equipped array processed correctly")
    void itemWhenEquippedArrayProcessedCorrectly() {
        RPGLItem item = DatapackLoader.DATAPACKS.get("test").getItemTemplate("test_item").newInstance();
        JsonArray whenEquippedEffectUuids = (JsonArray) item.get("when_equipped");

        assertNotNull(whenEquippedEffectUuids,
                "Item test:test_item missing when_equipped array."
        );
        assertEquals(2, whenEquippedEffectUuids.size(),
                "Item test:test_item does not have 2 elements in when_equipped array."
        );
        assertNotNull(whenEquippedEffectUuids.get(0),
                "Item test:test_item when_equipped array is missing uuid at index 0."
        );
        assertNotNull(whenEquippedEffectUuids.get(1),
                "Item test:test_item when_equipped array is missing uuid at index 1."
        );
        assertNotNull(UUIDTable.getEffect((Long) whenEquippedEffectUuids.get(0)),
                "Item test:test_item when_equipped effect index 0 not registered to UUIDTable."
        );
        assertNotNull(UUIDTable.getEffect((Long) whenEquippedEffectUuids.get(1)),
                "Item test:test_item when_equipped effect index 1 not registered to UUIDTable."
        );
        assertEquals(3, UUIDTable.size(),
                "UUIDTable does not have 3 objects registered to it."
        );
    }

}
