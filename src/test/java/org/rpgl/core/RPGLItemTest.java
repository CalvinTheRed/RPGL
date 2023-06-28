package org.rpgl.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing class for the org.rpgl.core.RPGLItem class.
 *
 * @author Calvin Withun
 */
public class RPGLItemTest {

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
    @DisplayName("getEquippedEffectsObjects returns correct effects as objects")
    void getEquippedEffectObjects_returnsCorrectEffectsAsObjects() {
        RPGLItem item = RPGLFactory.newItem("std:ring_of_protection");

        List<RPGLEffect> equippedEffects = item.getEquippedEffectsObjects();
        assertEquals(1, equippedEffects.size(),
                "1 effect should be present"
        );
        assertEquals("std:ring_of_protection", equippedEffects.get(0).getId(),
                "First effect should be std:ring_of_protection"
        );

        for (RPGLEffect effect: equippedEffects) {
            assertEquals(item.getUuid(), effect.getOriginItem(),
                    "item-based events should indicate the item providing the event as the origin item"
            );
        }
    }

    @Test
    @DisplayName("getOneHandedEventObjects returns correct event objects")
    void getOneHandedEventObjects_returnsCorrectEventObjects() {
        RPGLItem item = RPGLFactory.newItem("std:longsword");
        String expected;

        List<RPGLEvent> events = item.getOneHandedEventObjects();
        assertEquals(2, events.size(),
                "longswords should provide 2 one-handed events"
        );

        assertEquals("std:longsword_melee", events.get(0).getId(),
                "first event should be a melee longsword strike"
        );
        expected = """
                ["melee","longsword","metal","martial_melee","versatile"]""";
        assertEquals(expected, events.get(0).getSubevents().getJsonObject(0).getJsonArray("tags").toString(),
                "subevent attack tags should include item tags when not improvised"
        );

        assertEquals("std:improvised_thrown", events.get(1).getId(),
                "first event should be an improvised thrown weapon attack"
        );
        expected = """
                ["improvised"]""";
        assertEquals(expected, events.get(1).getSubevents().getJsonObject(0).getJsonArray("tags").toString(),
                "subevent attack tags should not include item tags when improvised"
        );

        for (RPGLEvent event : events) {
            assertEquals(item.getUuid(), event.getOriginItem(),
                    "item-based events should indicate the item providing the event as the origin item"
            );
        }
    }

    @Test
    @DisplayName("getMultiHandedEventObjects returns correct event objects")
    void getMultiHandedEventObjects_returnsCorrectEventObjects() {
        RPGLItem item = RPGLFactory.newItem("std:longsword");
        String expected;

        List<RPGLEvent> events = item.getMultiHandedEventObjects();
        assertEquals(2, events.size(),
                "longswords should provide 2 multiple-handed events"
        );

        assertEquals("std:longsword_melee_versatile", events.get(0).getId(),
                "first event should be a melee longsword strike (versatile)"
        );
        expected = """
                ["melee","longsword","metal","martial_melee","versatile"]""";
        assertEquals(expected, events.get(0).getSubevents().getJsonObject(0).getJsonArray("tags").toString(),
                "subevent attack tags should include item tags when not improvised"
        );

        assertEquals("std:improvised_thrown", events.get(1).getId(),
                "first event should be an improvised thrown weapon attack"
        );
        expected = """
                ["improvised"]""";
        assertEquals(expected, events.get(1).getSubevents().getJsonObject(0).getJsonArray("tags").toString(),
                "subevent attack tags should not include item tags when improvised"
        );

        for (RPGLEvent event : events) {
            assertEquals(item.getUuid(), event.getOriginItem(),
                    "item-based events should indicate the item providing the event as the origin item"
            );
        }
    }

    @Test
    @DisplayName("getSpecialEventObjects returns correct event objects")
    void getSpecialEventObjects_returnsCorrectEventObjects() {
        RPGLItem item = RPGLFactory.newItem("std:robe_of_stars");
        String expected;

        List<RPGLEvent> events = item.getSpecialEventObjects();
        assertEquals(1, events.size(),
                "robe of stars should provide 1 special event"
        );

        assertEquals("std:robe_of_stars", events.get(0).getId(),
                "first event should be a robe of stars attack"
        );
        expected = """
                ["spell","magic_missile"]""";
        assertEquals(expected, events.get(0).getSubevents().getJsonObject(0).getJsonArray("tags").toString(),
                "subevent attack tags should match robe of stars event template tags"
        );

        for (RPGLEvent event : events) {
            assertEquals(item.getUuid(), event.getOriginItem(),
                    "item-based events should indicate the item providing the event as the origin item"
            );
        }
    }

}
