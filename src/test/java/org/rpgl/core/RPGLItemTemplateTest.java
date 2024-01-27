package org.rpgl.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackContentTO;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.RPGLItemTO;
import org.rpgl.datapack.RPGLTaggableTO;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing class for the org.rpgl.core.RPGLItemTemplate class.
 *
 * @author Calvin Withun
 */
public class RPGLItemTemplateTest {

    @BeforeAll
    static void beforeAll() {
        DatapackLoader.loadDatapacks(
                new File("src/test/resources/datapacks".replace("/", File.separator))
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
    @DisplayName("defaults events list to empty arrays")
    void defaultsEventsListToEmptyArrays() {
        RPGLItemTemplate itemTemplate = DatapackLoader.DATAPACKS.get("std").getItemTemplate("common/teacup");
        RPGLItem item = new RPGLItem();
        itemTemplate.setup(item);

        RPGLItemTemplate.processEvents(item);

        String expected = """
                {"multiple_hands":[],"one_hand":[],"special":[]}""";
        assertEquals(expected, item.getEvents().toString(),
                "events arrays should be defaulted to empty arrays when not specified"
        );
    }

    @Test
    @DisplayName("creates equipped effects")
    void createsEquippedEffects() {
        RPGLItemTemplate itemTemplate = DatapackLoader.DATAPACKS.get("std").getItemTemplate("weapon/melee/martial/scimitar/frostbrand");
        RPGLItem item = new RPGLItem();
        itemTemplate.setup(item);

        RPGLItemTemplate.processEquippedEffects(item);

        List<RPGLEffect> equippedEffects = item.getEquippedEffectsObjects();
        assertEquals(2, equippedEffects.size(),
                "2 effects should be created"
        );

        assertEquals("std:common/damage/resistance/cold", equippedEffects.get(0).getId(),
                "First effect should be std:common/damage/resistance/cold"
        );
        assertEquals("std:common/damage/resistance/fire", equippedEffects.get(1).getId(),
                "Second effect should be std:common/damage/resistance/fire"
        );
    }

    @Test
    @DisplayName("creates equipped resources")
    void createsEquippedResources() {
        RPGLItemTemplate itemTemplate = DatapackLoader.DATAPACKS.get("std").getItemTemplate("wand/wand_of_fireballs");
        RPGLItem item = new RPGLItem();
        itemTemplate.setup(item);

        RPGLItemTemplate.processEquippedResources(item);

        List<RPGLResource> resources = item.getEquippedResourcesObjects();
        assertEquals(3, resources.size(),
                "item should have 3 resources"
        );
        for (RPGLResource resource : resources) {
            assertEquals("std:item/wand/wand_of_fireballs_charge", resource.getId(),
                    "resource should be a std:item/wand/wand_of_fireballs_charge"
            );
        }
    }

    @Test
    @DisplayName("creates new items")
    void createsNewItems() {
        RPGLItemTemplate itemTemplate = DatapackLoader.DATAPACKS.get("std").getItemTemplate("weapon/melee/martial/scimitar/frostbrand");
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
                ["scimitar","metal","magic","martial_melee","finesse","weapon"]""";
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
                {"multiple_hands":["std:item/weapon/melee/martial/scimitar/frostbrand/melee","std:common/improvised_thrown"],"one_hand":["std:item/weapon/melee/martial/scimitar/frostbrand/melee","std:common/improvised_thrown"],"special":[]}""";
        assertEquals(expected, item.getEvents().toString(),
                "incorrect field value: " + RPGLItemTO.EVENTS_ALIAS
        );

        List<RPGLEffect> equippedEffects = item.getEquippedEffectsObjects();
        assertEquals(2, equippedEffects.size(),
                "2 effects should be created"
        );
        assertEquals("std:common/damage/resistance/cold", equippedEffects.get(0).getId(),
                "First effect should be std:common/damage/resistance/cold"
        );
        assertEquals("std:common/damage/resistance/fire", equippedEffects.get(1).getId(),
                "Second effect should be std:common/damage/resistance/fire"
        );

        assertEquals(3, item.getAttackBonus(),
                "incorrect field value: " + RPGLItemTO.ATTACK_BONUS_ALIAS
        );
        assertEquals(3, item.getDamageBonus(),
                "incorrect field value: " + RPGLItemTO.DAMAGE_BONUS_ALIAS
        );
    }

}
