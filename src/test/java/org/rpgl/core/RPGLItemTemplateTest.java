package org.rpgl.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackContentTO;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.datapack.RPGLItemTO;
import org.rpgl.datapack.RPGLTaggableTO;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing class for the org.rpgl.core.RPGLItemTemplate class.
 *
 * @author Calvin Withun
 */
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
    @DisplayName("processEquippedEffects processes effects correctly")
    void processEquippedEffects_processesEffectsCorrectly() {
        RPGLItemTemplate itemTemplate = DatapackLoader.DATAPACKS.get("std").getItemTemplate("frostbrand");
        RPGLItem item = new RPGLItem();
        item.join(itemTemplate);

        RPGLItemTemplate.processEquippedEffects(item);

        List<RPGLEffect> equippedEffects = item.getEquippedEffectsObjects();
        assertEquals(2, equippedEffects.size(),
                "2 effects should be created"
        );

        assertEquals("std:cold_resistance", equippedEffects.get(0).getId(),
                "First effect should be std:cold_resistance"
        );
        assertEquals("std:fire_resistance", equippedEffects.get(1).getId(),
                "Second effect should be std:fire_resistance"
        );
    }

    @Test
    @DisplayName("newInstance correctly creates frostbrand from template")
    void newInstance_correctlyCreatesFrostbrandFromTemplate() {
        RPGLItemTemplate itemTemplate = DatapackLoader.DATAPACKS.get("std").getItemTemplate("frostbrand");
        RPGLItem item = itemTemplate.newInstance();
        String expected;

        expected = """
                {"author":"Calvin Withun"}""";
        assertEquals(expected, item.getMetadata().toString(),
                "incorrect field value: " + DatapackContentTO.METADATA_ALIAS
        );
        assertEquals("Frostbrand", item.getName(),
                "incorrect field value: " + DatapackContentTO.NAME_ALIAS
        );
        assertEquals("A legendary scimitar wielded by Drizzt Do'Urden.", item.getDescription(),
                "incorrect field value: " + DatapackContentTO.DESCRIPTION_ALIAS
        );

        expected = """
                ["scimitar","metal","magic","martial_melee","finesse"]""";
        assertEquals(expected, item.getTags().toString(),
                "incorrect field value: " + RPGLTaggableTO.TAGS_ALIAS
        );

        assertEquals(3, item.getWeight(),
                "incorrect field value: " + RPGLItemTO.WEIGHT_ALIAS
        );
        assertEquals(10000, item.getCost(),
                "incorrect field value: " + RPGLItemTO.COST_ALIAS
        );
        expected = """
                {"multiple_hands":["std:frostbrand_melee","std:improvised_thrown"],"one_hand":["std:frostbrand_melee","std:improvised_thrown"],"special":[]}""";
        assertEquals(expected, item.getEvents().toString(),
                "incorrect field value: " + RPGLItemTO.EVENTS_ALIAS
        );

        List<RPGLEffect> equippedEffects = item.getEquippedEffectsObjects();
        assertEquals(2, equippedEffects.size(),
                "2 effects should be created"
        );
        assertEquals("std:cold_resistance", equippedEffects.get(0).getId(),
                "First effect should be std:cold_resistance"
        );
        assertEquals("std:fire_resistance", equippedEffects.get(1).getId(),
                "Second effect should be std:fire_resistance"
        );

        assertEquals(3, item.getAttackBonus(),
                "incorrect field value: " + RPGLItemTO.ATTACK_BONUS_ALIAS
        );
        assertEquals(3, item.getDamageBonus(),
                "incorrect field value: " + RPGLItemTO.DAMAGE_BONUS_ALIAS
        );
    }

}
