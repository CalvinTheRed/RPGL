package org.rpgl.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackContentTO;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.RPGLObjectTO;
import org.rpgl.datapack.RPGLTaggableTO;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Testing class for the org.rpgl.core.RPGLObjectTemplate class.
 *
 * @author Calvin Withun
 */
public class RPGLObjectTemplateTest {

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
    @DisplayName("creates effects")
    void createsEffects() {
        RPGLObjectTemplate objectTemplate = DatapackLoader.DATAPACKS.get("debug").getObjectTemplate("test");
        RPGLObject object = new RPGLObject();
        objectTemplate.setup(object);

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
    @DisplayName("creates inventory items")
    void createsInventoryItems() {
        RPGLObjectTemplate objectTemplate = DatapackLoader.DATAPACKS.get("std").getObjectTemplate("humanoid/knight");
        RPGLObject object = new RPGLObject();
        objectTemplate.setup(object);

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
    @DisplayName("creates equipped items")
    void createsEquippedItems() {
        RPGLObjectTemplate objectTemplate = DatapackLoader.DATAPACKS.get("std").getObjectTemplate("humanoid/knight");
        RPGLObject object = new RPGLObject();
        objectTemplate.setup(object);

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
    @DisplayName("creates resources")
    void createsResources() {
        RPGLObjectTemplate objectTemplate = DatapackLoader.DATAPACKS.get("debug").getObjectTemplate("test");
        RPGLObject object = new RPGLObject();
        objectTemplate.setup(object);

        RPGLObjectTemplate.processResources(object);

        assertEquals(2, object.getResourceObjects().size(),
                "debug:test should be given 2 resources"
        );
        assertEquals("std:common/action/01", object.getResourceObjects().get(0).getId(),
                "one resource should be std:common/action/01"
        );
        assertEquals("std:common/bonus_action/01", object.getResourceObjects().get(1).getId(),
                "one resource should be std:common/bonus_action/01"
        );
    }

    @Test
    @DisplayName("applies classes")
    void appliesClasses() {
        RPGLObjectTemplate objectTemplate = DatapackLoader.DATAPACKS.get("debug").getObjectTemplate("dummy");
        objectTemplate.putJsonArray(RPGLObjectTO.CLASSES_ALIAS, new JsonArray() {{
            /*[
                {
                    "id": "debug:blank",
                    "level": 1,
                    "choices": { }
                }
            ]*/
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "debug:blank");
                this.putInteger("level", 1);
                this.putJsonObject("choices", new JsonObject());
            }});
        }});
        RPGLObject object = new RPGLObject();
        objectTemplate.setup(object);

        RPGLObjectTemplate.processClasses(object);

        assertEquals(1, object.getLevel("debug:blank"),
                "object should have 1 level in debug:blank"
        );
        assertEquals(1, object.getLevel(),
                "object should be level 1"
        );
    }

    @Test
    @DisplayName("applies classes (with nested classes)")
    void appliesClasses_withNestedClasses() {
        RPGLObjectTemplate objectTemplate = DatapackLoader.DATAPACKS.get("debug").getObjectTemplate("dummy");
        objectTemplate.putJsonArray(RPGLObjectTO.CLASSES_ALIAS, new JsonArray() {{
            /*[
                {
                    "id": "debug:test",
                    "level": 1,
                    "choices": {
                        "Test Effect Choice": [ 0 ]
                    }
                }
            ]*/
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "debug:test");
                this.putInteger("level", 1);
                this.putJsonObject("choices", new JsonObject() {{
                    this.putJsonArray("Test Effect Choice", new JsonArray() {{
                        this.addInteger(0);
                    }});
                }});
            }});
        }});
        RPGLObject object = new RPGLObject();
        objectTemplate.setup(object);

        RPGLObjectTemplate.processClasses(object);

        assertEquals(1, object.getLevel("debug:test"),
                "object should have 1 level in debug:test"
        );
        assertEquals(1, object.getLevel("debug:blank"),
                "object should have 1 level in debug:blank"
        );
        assertEquals(1, object.getLevel(),
                "object should be level 1"
        );
    }

    @Test
    @DisplayName("applies classes (with additional nested classes)")
    void appliesClasses_withAdditionalNestedClasses() {
        RPGLObjectTemplate objectTemplate = DatapackLoader.DATAPACKS.get("debug").getObjectTemplate("dummy");
        objectTemplate.putJsonArray(RPGLObjectTO.CLASSES_ALIAS, new JsonArray() {{
            /*[
                {
                    "id": "std:fighter",
                    "level": 3,
                    "choices": {
                        "Skill Proficiencies": [ 0, 1 ],
                        "Fighting Style": [ 0 ]
                    },
                    "additional_nested_classes": {
                        "std:fighter/champion": {
                            "scale": 1,
                            "round_up": false
                        }
                    }
                }
            ]*/
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "std:fighter");
                this.putInteger("level", 3);
                this.putJsonObject("choices", new JsonObject() {{
                    this.putJsonArray("Skill Proficiencies", new JsonArray() {{
                        this.addInteger(0);
                        this.addInteger(1);
                    }});
                    this.putJsonArray("Fighting Style", new JsonArray() {{
                        this.addInteger(0);
                    }});
                }});
                this.putJsonObject("additional_nested_classes", new JsonObject() {{
                    this.putJsonObject("std:fighter/champion", new JsonObject() {{
                        this.putInteger("scale", 1);
                        this.putBoolean("round_up", false);
                    }});
                }});
            }});
        }});
        RPGLObject object = new RPGLObject();
        objectTemplate.setup(object);

        RPGLObjectTemplate.processClasses(object);

        assertEquals(3, object.getLevel("std:fighter"),
                "object should have 3 levels in std:fighter"
        );
        assertEquals(3, object.getLevel("std:common/base"),
                "object should have 3 levels in std:common/base"
        );
        assertEquals(3, object.getLevel("std:fighter/champion"),
                "object should have 3 levels in std:fighter/champion"
        );
        assertEquals(3, object.getLevel(),
                "object should be level 3"
        );
    }

    @Test
    @DisplayName("applies races")
    void appliesRaces() {
        RPGLObjectTemplate objectTemplate = DatapackLoader.DATAPACKS.get("debug").getObjectTemplate("dummy");
        objectTemplate.putJsonArray(RPGLObjectTO.CLASSES_ALIAS, new JsonArray() {{
            /*[
                {
                    "id": "debug:blank",
                    "level": 1,
                    "choices": { },
                    "additional_nested_classes": { }
                }
            ]*/
            this.addJsonObject(new JsonObject() {{
                this.putString("id", "debug:blank");
                this.putInteger("level", 1);
                this.putJsonObject("choices", new JsonObject());
                this.putJsonObject("additional_nested_classes", new JsonObject());
            }});
        }});
        objectTemplate.putJsonArray(RPGLObjectTO.RACES_ALIAS, new JsonArray() {{
            this.addString("std:human");
        }});
        RPGLObject object = new RPGLObject();
        objectTemplate.setup(object);

        RPGLObjectTemplate.processClasses(object);

        assertEquals(1, object.getLevel("debug:blank"),
                "object should have 3 levels in std:fighter"
        );
        assertEquals(1, object.getEffects().size(),
                "object should have 1 effect from being set to a level 1 character with race std:human"
        );
    }

    @Test
    @DisplayName("creates new objects (knight)")
    void createsNewObjects_knight() {
        RPGLObjectTemplate objectTemplate = DatapackLoader.DATAPACKS.get("std").getObjectTemplate("humanoid/knight");
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
        assertEquals("std:humanoid/knight", object.getId(),
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
                {"base":36,"current":52,"temporary":0}""";
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
        assertEquals("[]", object.getEvents().toString(),
                "incorrect field value: " + RPGLObjectTO.EVENTS_ALIAS
        );
        assertEquals(1, object.getEffects().size(),
                "incorrect field size: " + RPGLObjectTO.EFFECTS_ALIAS
        );
        assertEquals(2, object.getProficiencyBonus(),
                "incorrect field value: " + RPGLObjectTO.PROFICIENCY_BONUS_ALIAS
        );
    }

    @Test
    @DisplayName("creates new objects (young red dragon)")
    void createsNewObjects_youngRedDragon() throws Exception {
        RPGLObjectTemplate objectTemplate = DatapackLoader.DATAPACKS.get("std").getObjectTemplate("dragon/red/young");
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
                {"base":93,"current":178,"temporary":0}""";
        assertEquals(expected, object.getHealthData().toString(),
                "incorrect field value: " + RPGLObjectTO.HEALTH_DATA_ALIAS
        );
        assertEquals("{}", object.getEquippedItems().toString(),
                "incorrect field value: " + RPGLObjectTO.EQUIPPED_ITEMS_ALIAS
        );
        assertEquals("[]", object.getInventory().toString(),
                "incorrect field value: " + RPGLObjectTO.INVENTORY_ALIAS
        );
        assertEquals(4, object.getEventObjects(new DummyContext()).size(),
                "incorrect field size: " + RPGLObjectTO.EVENTS_ALIAS
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
