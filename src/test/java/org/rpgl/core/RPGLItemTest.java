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
        RPGLItem item = RPGLFactory.newItem("std:frostbrand");

        List<RPGLEffect> equippedEffects = item.getEquippedEffectsObjects();
        assertEquals(2, equippedEffects.size(),
                "2 effects should be present"
        );
        assertEquals("std:cold_resistance", equippedEffects.get(0).getId(),
                "First effect should be std:cold_resistance"
        );
        assertEquals("std:fire_resistance", equippedEffects.get(1).getId(),
                "Second effect should be std:fire_resistance"
        );
    }

    @Test
    @DisplayName("getOneHandedEventObjects returns correct event objects")
    void getOneHandedEventObjects_returnsCorrectEventObjects() {
        RPGLItem item = RPGLFactory.newItem("std:longsword");
        String expected;

        List<RPGLEvent> events = item.getOneHandedEventObjects();
        RPGLEvent event;
        assertEquals(2, events.size(),
                "longswords should provide 2 one-handed events"
        );

        event = events.get(0);
        assertEquals("std:longsword_melee", event.getId(),
                "first event should be a melee longsword strike"
        );
        expected = """
                ["melee","longsword","metal","martial_melee","versatile"]""";
        assertEquals(expected, event.getSubevents().getJsonObject(0).getJsonArray("tags").toString(),
                "subevent attack tags should include item tags when not improvised"
        );

        event = events.get(1);
        assertEquals("std:improvised_thrown", event.getId(),
                "first event should be an improvised thrown weapon attack"
        );
        expected = """
                ["improvised"]""";
        assertEquals(expected, event.getSubevents().getJsonObject(0).getJsonArray("tags").toString(),
                "subevent attack tags should not include item tags when improvised"
        );
    }

    @Test
    @DisplayName("getMultiHandedEventObjects returns correct event objects")
    void getMultiHandedEventObjects_returnsCorrectEventObjects() {
        RPGLItem item = RPGLFactory.newItem("std:longsword");
        String expected;

        List<RPGLEvent> events = item.getMultiHandedEventObjects();
        RPGLEvent event;
        assertEquals(2, events.size(),
                "longswords should provide 2 multiple-handed events"
        );

        event = events.get(0);
        assertEquals("std:longsword_melee_versatile", event.getId(),
                "first event should be a melee longsword strike (versatile)"
        );
        expected = """
                ["melee","longsword","metal","martial_melee","versatile"]""";
        assertEquals(expected, event.getSubevents().getJsonObject(0).getJsonArray("tags").toString(),
                "subevent attack tags should include item tags when not improvised"
        );

        event = events.get(1);
        assertEquals("std:improvised_thrown", event.getId(),
                "first event should be an improvised thrown weapon attack"
        );
        expected = """
                ["improvised"]""";
        assertEquals(expected, event.getSubevents().getJsonObject(0).getJsonArray("tags").toString(),
                "subevent attack tags should not include item tags when improvised"
        );
    }

    @Test
    @DisplayName("getSpecialEventObjects returns correct event objects")
    void getSpecialEventObjects_returnsCorrectEventObjects() {
        RPGLItem item = RPGLFactory.newItem("std:robe_of_stars");
        String expected;

        List<RPGLEvent> events = item.getSpecialEventObjects();
        RPGLEvent event;
        assertEquals(1, events.size(),
                "robe of stars should provide 1 special event"
        );

        event = events.get(0);
        assertEquals("std:robe_of_stars", event.getId(),
                "first event should be a robe of stars attack"
        );
        expected = """
                ["spell","magic_missile"]""";
        assertEquals(expected, event.getSubevents().getJsonObject(0).getJsonArray("tags").toString(),
                "subevent attack tags should match robe of stars event template tags"
        );
    }

}
