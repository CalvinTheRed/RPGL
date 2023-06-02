package org.rpgl.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackContentTO;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.datapack.RPGLObjectTO;
import org.rpgl.datapack.RPGLTaggableTO;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Testing class for the org.rpgl.core.RPGLObjectTemplate class.
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
    @DisplayName("processEffects effects are constructed")
    void processEffects_effectsAreConstructed() {
        RPGLObjectTemplate objectTemplate = DatapackLoader.DATAPACKS.get("demo").getObjectTemplate("young_red_dragon");
        RPGLObject object = new RPGLObject();
        object.join(objectTemplate);

        RPGLObjectTemplate.processEffects(object);

        JsonArray effectsArray = object.getJsonArray(RPGLObjectTO.EFFECTS_ALIAS);
        assertEquals(1, effectsArray.size(),
                "object should have 1 effect constructed"
        );
        for (int i = 0; i < effectsArray.size(); i++) {
            String effectUuid = effectsArray.getString(i);
            RPGLEffect effect = UUIDTable.getEffect(effectUuid);
            assertNotNull(effect,
                    "effect UUID should be present in UUIDTable"
            );
        }
    }

    @Test
    @DisplayName("processInventory items are constructed")
    void processInventory_itemsAreConstructed() {
        RPGLObjectTemplate objectTemplate = DatapackLoader.DATAPACKS.get("demo").getObjectTemplate("knight");
        RPGLObject object = new RPGLObject();
        object.join(objectTemplate);

        RPGLObjectTemplate.processInventory(object);

        JsonArray inventory = object.getInventory();
        assertEquals(1, inventory.size(),
                "inventory should only have 1 item without calling processEquippedItems()"
        );
        for (int i = 0; i < inventory.size(); i++) {
            String itemUuid = inventory.getString(i);
            RPGLItem item = UUIDTable.getItem(itemUuid);
            assertNotNull(item,
                    "item UUID should be present in UUIDTable"
            );
        }
    }

    @Test
    @DisplayName("processEquippedItems items are constructed and added to inventory")
    void processEquippedItems_itemsAreConstructedAndAddedToInventory() {
        RPGLObjectTemplate objectTemplate = DatapackLoader.DATAPACKS.get("demo").getObjectTemplate("knight");
        RPGLObject object = new RPGLObject();
        object.join(objectTemplate);

        RPGLObjectTemplate.processEquippedItems(object);

        JsonArray inventory = object.getInventory();
        assertEquals(4, inventory.size(),
                "inventory should have 4 items after calling processEquippedItems()"
        );
        for (int i = 1; i < inventory.size(); i++) {
            // start at i=1 to skip the first un-processed inventory item
            String itemUuid = inventory.getString(i);
            RPGLItem item = UUIDTable.getItem(itemUuid);
            assertNotNull(item,
                    "item UUID should be present in UUIDTable"
            );
        }
    }

    @Test
    @DisplayName("processHealthData hit dice enumeration")
    void processHealthData_hitDiceAreEnumerated() {
        RPGLObjectTemplate objectTemplate = DatapackLoader.DATAPACKS.get("demo").getObjectTemplate("young_red_dragon");
        RPGLObject object = new RPGLObject();
        object.join(objectTemplate);

        RPGLObjectTemplate.processHealthData(object);

        JsonArray hitDiceArray = object.getHealthData().getJsonArray("hit_dice");
        assertEquals(17, hitDiceArray.size(),
                "compact hit dice view should be unpacked into 17 objects"
        );
        for (int i = 0; i < hitDiceArray.size(); i++) {
            JsonObject hitDie = hitDiceArray.getJsonObject(i);
            assertFalse(hitDie.getBoolean("spent"),
                    "hit die (index " + i + ") should be unspent"
            );
            assertEquals(10, hitDie.getInteger("size"),
                    "hit die (index " + i + ") has the wrong size"
            );
            assertEquals("[5]", hitDie.getJsonArray("determined").toString(),
                    "hit die (index " + i + ") has the wrong determined value"
            );
        }
    }

    @Test
    @DisplayName("processResources resources are constructed correctly")
    void processResources_resourcesAreConstructedCorrectly() {
        RPGLObjectTemplate objectTemplate = DatapackLoader.DATAPACKS.get("demo").getObjectTemplate("young_red_dragon");
        RPGLObject object = new RPGLObject();
        object.join(objectTemplate);

        RPGLObjectTemplate.processResources(object);

        assertEquals(2, object.getResourceObjects().size(),
                "young red dragon should be given 2 resources"
        );
        assertEquals("demo:action", object.getResourceObjects().get(0).getId(),
                "one resource should be demo:action"
        );
        assertEquals("demo:young_red_dragon_breath_charge", object.getResourceObjects().get(1).getId(),
                "one resource should be demo:young_red_dragon_breath_charge"
        );
    }

    @Test
    @DisplayName("newInstance comprehensive test using demo:knight template")
    void newInstance_knightTemplate() {
        RPGLObjectTemplate objectTemplate = DatapackLoader.DATAPACKS.get("demo").getObjectTemplate("knight");
        RPGLObject object = objectTemplate.newInstance();
        String expected;

        expected = """
                {"author":"Calvin Withun"}""";
        assertEquals(expected, object.getMetadata().toString(),
                "incorrect field value: " + DatapackContentTO.METADATA_ALIAS
        );
        assertEquals("Knight", object.getName(),
                "incorrect field value: " + DatapackContentTO.NAME_ALIAS
        );
        assertEquals("A knight.", object.getDescription(),
                "incorrect field value: " + DatapackContentTO.DESCRIPTION_ALIAS
        );
        assertEquals("demo:knight", object.getId(),
                "incorrect field value: " + DatapackContentTO.ID_ALIAS
        );

        expected= """
                ["humanoid"]""";
        assertEquals(expected, object.getTags().toString(),
                "incorrect field value: " + RPGLTaggableTO.TAGS_ALIAS
        );

        expected = """
                {"cha":15,"con":14,"dex":11,"int":11,"str":16,"wis":11}""";
        assertEquals(expected, object.getAbilityScores().toString(),
                "incorrect field value: " + RPGLObjectTO.ABILITY_SCORES_ALIAS
        );
        expected = """
                {"base":36,"current":52,"hit_dice":[{"determined":[4],"size":8,"spent":false},{"determined":[4],"size":8,"spent":false},{"determined":[4],"size":8,"spent":false},{"determined":[4],"size":8,"spent":false},{"determined":[4],"size":8,"spent":false},{"determined":[4],"size":8,"spent":false},{"determined":[4],"size":8,"spent":false},{"determined":[4],"size":8,"spent":false}],"temporary":0}""";
        assertEquals(expected, object.getHealthData().toString(),
                "incorrect field value: " + RPGLObjectTO.HEALTH_DATA_ALIAS
        );
        JsonObject equippedItems = object.getEquippedItems();
        for (Map.Entry<String, Object> equippedItemsEntry : equippedItems.asMap().entrySet()) {
            String itemUuid = equippedItems.getString(equippedItemsEntry.getKey());
            assertNotNull(UUIDTable.getItem(itemUuid),
                    "item in equipment slot " + equippedItemsEntry.getKey() + " is missing from UUIDTable"
            );
        }
        JsonArray inventory = object.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            String itemUuid = inventory.getString(i);
            assertNotNull(UUIDTable.getItem(itemUuid),
                    "item at inventory index " + i + " is missing from UUIDTable"
            );
        }
        expected = """
                ["demo:weapon_attack_mainhand_melee"]""";
        assertEquals(expected, object.getEvents().toString(),
                "incorrect field value: " + RPGLObjectTO.EVENTS_ALIAS
        );
        assertEquals("[]", object.getEffects().toString(),
                "incorrect field value: " + RPGLObjectTO.EFFECTS_ALIAS
        );
        assertEquals(2, object.getProficiencyBonus(),
                "incorrect field value: " + RPGLObjectTO.PROFICIENCY_BONUS_ALIAS
        );
    }

    @Test
    @DisplayName("newInstance comprehensive test using demo:young_red_dragon template")
    void newInstance_youngRedDragonTemplate() {
        RPGLObjectTemplate objectTemplate = DatapackLoader.DATAPACKS.get("demo").getObjectTemplate("young_red_dragon");
        RPGLObject object = objectTemplate.newInstance();
        String expected;

        expected = """
                {"author":"Calvin Withun"}""";
        assertEquals(expected, object.getMetadata().toString(),
                "incorrect field value: " + DatapackContentTO.METADATA_ALIAS
        );
        assertEquals("Young Red Dragon", object.getName(),
                "incorrect field value: " + DatapackContentTO.NAME_ALIAS
        );
        assertEquals("A young red dragon.", object.getDescription(),
                "incorrect field value: " + DatapackContentTO.DESCRIPTION_ALIAS
        );

        expected= """
                ["dragon"]""";
        assertEquals(expected, object.getTags().toString(),
                "incorrect field value: " + RPGLTaggableTO.TAGS_ALIAS
        );

        expected = """
                {"cha":19,"con":21,"dex":10,"int":14,"str":23,"wis":11}""";
        assertEquals(expected, object.getAbilityScores().toString(),
                "incorrect field value: " + RPGLObjectTO.ABILITY_SCORES_ALIAS
        );
        expected = """
                {"base":93,"current":178,"hit_dice":[{"determined":[5],"size":10,"spent":false},{"determined":[5],"size":10,"spent":false},{"determined":[5],"size":10,"spent":false},{"determined":[5],"size":10,"spent":false},{"determined":[5],"size":10,"spent":false},{"determined":[5],"size":10,"spent":false},{"determined":[5],"size":10,"spent":false},{"determined":[5],"size":10,"spent":false},{"determined":[5],"size":10,"spent":false},{"determined":[5],"size":10,"spent":false},{"determined":[5],"size":10,"spent":false},{"determined":[5],"size":10,"spent":false},{"determined":[5],"size":10,"spent":false},{"determined":[5],"size":10,"spent":false},{"determined":[5],"size":10,"spent":false},{"determined":[5],"size":10,"spent":false},{"determined":[5],"size":10,"spent":false}],"temporary":0}""";
        assertEquals(expected, object.getHealthData().toString(),
                "incorrect field value: " + RPGLObjectTO.HEALTH_DATA_ALIAS
        );
        assertEquals("{}", object.getEquippedItems().toString(),
                "incorrect field value: " + RPGLObjectTO.EQUIPPED_ITEMS_ALIAS
        );
        assertEquals("[]", object.getInventory().toString(),
                "incorrect field value: " + RPGLObjectTO.INVENTORY_ALIAS
        );
        expected = """
                ["demo:young_red_dragon_bite_attack","demo:young_red_dragon_claw_attack","demo:young_red_dragon_fire_breath"]""";
        assertEquals(expected, object.getEvents().toString(),
                "incorrect field value: " + RPGLObjectTO.EVENTS_ALIAS
        );
        JsonArray effects = object.getEffects();
        for (int i = 0; i < effects.size(); i++) {
            String effectUuid = effects.getString(i);
            assertNotNull(UUIDTable.getEffect(effectUuid),
                    "effect at effects index " + i + " is missing from UUIDTable"
            );
        }
        assertEquals(4, object.getProficiencyBonus(),
                "incorrect field value: " + RPGLObjectTO.PROFICIENCY_BONUS_ALIAS
        );
    }

}
