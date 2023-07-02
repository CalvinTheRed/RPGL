package org.rpgl.datapack;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLEffectTemplate;
import org.rpgl.core.RPGLEventTemplate;
import org.rpgl.core.RPGLItemTemplate;
import org.rpgl.core.RPGLObjectTemplate;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.datapack.Datapack class.
 *
 * @author Calvin Withun
 */
public class DatapackTest {

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
    @DisplayName("verify RPGLEffect templates can be loaded")
    void rpglEffectTemplate_loadsDatapackContent() {
        RPGLEffectTemplate effectTemplate = DatapackLoader.DATAPACKS.get("std").getEffectTemplate("fire_immunity");
        assertNotNull(effectTemplate,
                "Effect template std:fire_immunity failed to load."
        );

        String expected;

        /*
        DatapackContentTO field keys
         */
        assertTrue(effectTemplate.asMap().containsKey(DatapackContentTO.METADATA_ALIAS),
                "template missing field: " + DatapackContentTO.METADATA_ALIAS
        );
        assertTrue(effectTemplate.asMap().containsKey(DatapackContentTO.NAME_ALIAS),
                "template missing field: " + DatapackContentTO.NAME_ALIAS
        );
        assertTrue(effectTemplate.asMap().containsKey(DatapackContentTO.DESCRIPTION_ALIAS),
                "template missing field: " + DatapackContentTO.DESCRIPTION_ALIAS
        );

        /*
        RPGLEffectTO field keys
         */
        assertTrue(effectTemplate.asMap().containsKey(RPGLEffectTO.SUBEVENT_FILTERS_ALIAS),
                "template missing field: " + RPGLEffectTO.SUBEVENT_FILTERS_ALIAS
        );

        /*
        DatapackContentTO field values
         */
        expected = """
                {"author":"Calvin Withun"}""";
        assertEquals(expected, effectTemplate.getJsonObject(DatapackContentTO.METADATA_ALIAS).toString(),
                "incorrect field value: " + DatapackContentTO.METADATA_ALIAS
        );
        assertEquals("Fire Immunity", effectTemplate.getString(RPGLObjectTO.NAME_ALIAS),
                "incorrect field value: " + DatapackContentTO.NAME_ALIAS
        );
        assertEquals("Creatures with this effect take 0 fire damage.", effectTemplate.getString(RPGLObjectTO.DESCRIPTION_ALIAS),
                "incorrect field value: " + DatapackContentTO.DESCRIPTION_ALIAS
        );

        /*
        RPGLEffectTO field values
         */
        expected = """
                {"damage_affinity":[{"conditions":[{"condition":"objects_match","effect":"target","subevent":"target"}],"functions":[{"damage_type":"fire","function":"grant_immunity"}]}]}""";
        assertEquals(expected, effectTemplate.getJsonObject(RPGLEffectTO.SUBEVENT_FILTERS_ALIAS).toString(),
                "incorrect field value: " + RPGLEffectTO.SUBEVENT_FILTERS_ALIAS
        );
    }

    @Test
    @DisplayName("verify RPGLEvent templates can be loaded")
    void rpglEventTemplate_loadsDatapackContent() {
        RPGLEventTemplate eventTemplate = DatapackLoader.DATAPACKS.get("std").getEventTemplate("young_red_dragon_fire_breath");
        assertNotNull(eventTemplate,
                "Event template std:young_red_dragon_fire_breath failed to load."
        );

        String expected;

        /*
        DatapackContentTO field keys
         */
        assertTrue(eventTemplate.asMap().containsKey(DatapackContentTO.METADATA_ALIAS),
                "template missing field: " + DatapackContentTO.METADATA_ALIAS
        );
        assertTrue(eventTemplate.asMap().containsKey(DatapackContentTO.NAME_ALIAS),
                "template missing field: " + DatapackContentTO.NAME_ALIAS
        );
        assertTrue(eventTemplate.asMap().containsKey(DatapackContentTO.DESCRIPTION_ALIAS),
                "template missing field: " + DatapackContentTO.DESCRIPTION_ALIAS
        );

        /*
        RPGLEventTO field keys
         */
        assertTrue(eventTemplate.asMap().containsKey(RPGLEventTO.AREA_OF_EFFECT_ALIAS),
                "template missing field: " + RPGLEventTO.AREA_OF_EFFECT_ALIAS
        );
        assertTrue(eventTemplate.asMap().containsKey(RPGLEventTO.SUBEVENTS_ALIAS),
                "template missing field: " + RPGLEventTO.SUBEVENTS_ALIAS
        );

        /*
        DatapackContentTO field values
         */
        expected = """
                {"author":"Calvin Withun"}""";
        assertEquals(expected, eventTemplate.getJsonObject(DatapackContentTO.METADATA_ALIAS).toString(),
                "incorrect field value: " + DatapackContentTO.METADATA_ALIAS
        );
        assertEquals("Fire Breath", eventTemplate.getString(RPGLObjectTO.NAME_ALIAS),
                "incorrect field value: " + DatapackContentTO.NAME_ALIAS
        );
        assertEquals("The dragon breathes fire.", eventTemplate.getString(RPGLObjectTO.DESCRIPTION_ALIAS),
                "incorrect field value: " + DatapackContentTO.DESCRIPTION_ALIAS
        );

        /*
        RPGLEventTO field values
         */
        assertEquals("{}", eventTemplate.getJsonObject(RPGLEventTO.AREA_OF_EFFECT_ALIAS).toString(),
                "incorrect field value: " + RPGLEventTO.AREA_OF_EFFECT_ALIAS
        );
        expected = """
                [{"damage":[{"bonus":0,"damage_formula":"range","damage_type":"fire","dice":[{"count":16,"determined":[3],"size":6}]}],"damage_on_pass":"half","determined":[1],"difficulty_class_ability":"con","save_ability":"dex","subevent":"saving_throw"}]""";
        assertEquals(expected, eventTemplate.getJsonArray(RPGLEventTO.SUBEVENTS_ALIAS).toString(),
                "incorrect field value: " + RPGLEventTO.SUBEVENTS_ALIAS
        );
    }

    @Test
    @DisplayName("verify RPGLItem templates can be loaded (frostbrand)")
    void rpglItemTemplate_loadsDatapackContent_meleeWeapon() {
        RPGLItemTemplate itemTemplate = DatapackLoader.DATAPACKS.get("std").getItemTemplate("frostbrand");
        assertNotNull(itemTemplate,
                "Event template std:frostbrand failed to load."
        );

        String expected;

        /*
        DatapackContentTO field keys
         */
        assertTrue(itemTemplate.asMap().containsKey(DatapackContentTO.METADATA_ALIAS),
                "template missing field: " + DatapackContentTO.METADATA_ALIAS
        );
        assertTrue(itemTemplate.asMap().containsKey(DatapackContentTO.NAME_ALIAS),
                "template missing field: " + DatapackContentTO.NAME_ALIAS
        );
        assertTrue(itemTemplate.asMap().containsKey(DatapackContentTO.DESCRIPTION_ALIAS),
                "template missing field: " + DatapackContentTO.DESCRIPTION_ALIAS
        );

        /*
        RPGLItemTO field keys
         */
        assertTrue(itemTemplate.asMap().containsKey(RPGLItemTO.TAGS_ALIAS),
                "template missing field: " + RPGLItemTO.TAGS_ALIAS
        );
        assertTrue(itemTemplate.asMap().containsKey(RPGLItemTO.WEIGHT_ALIAS),
                "template missing field: " + RPGLItemTO.WEIGHT_ALIAS
        );
        assertTrue(itemTemplate.asMap().containsKey(RPGLItemTO.COST_ALIAS),
                "template missing field: " + RPGLItemTO.COST_ALIAS
        );
        assertTrue(itemTemplate.asMap().containsKey(RPGLItemTO.EQUIPPED_EFFECTS_ALIAS),
                "template missing field: " + RPGLItemTO.EQUIPPED_EFFECTS_ALIAS
        );
        assertTrue(itemTemplate.asMap().containsKey(RPGLItemTO.EQUIPPED_RESOURCES_ALIAS),
                "template missing field: " + RPGLItemTO.EQUIPPED_RESOURCES_ALIAS
        );
        assertTrue(itemTemplate.asMap().containsKey(RPGLItemTO.ATTACK_BONUS_ALIAS),
                "template missing field: " + RPGLItemTO.ATTACK_BONUS_ALIAS
        );
        assertTrue(itemTemplate.asMap().containsKey(RPGLItemTO.DAMAGE_BONUS_ALIAS),
                "template missing field: " + RPGLItemTO.DAMAGE_BONUS_ALIAS
        );
        assertTrue(itemTemplate.asMap().containsKey(RPGLItemTO.ARMOR_CLASS_BASE_ALIAS),
                "template missing field: " + RPGLItemTO.ARMOR_CLASS_BASE_ALIAS
        );
        assertTrue(itemTemplate.asMap().containsKey(RPGLItemTO.ARMOR_CLASS_DEX_LIMIT_ALIAS),
                "template missing field: " + RPGLItemTO.ARMOR_CLASS_DEX_LIMIT_ALIAS
        );
        assertTrue(itemTemplate.asMap().containsKey(RPGLItemTO.ARMOR_CLASS_BONUS_ALIAS),
                "template missing field: " + RPGLItemTO.ARMOR_CLASS_BASE_ALIAS
        );

        /*
        DatapackContentTO field values
         */
        expected = """
                {"author":"Calvin Withun"}""";
        assertEquals(expected, itemTemplate.getJsonObject(DatapackContentTO.METADATA_ALIAS).toString(),
                "incorrect field value: " + DatapackContentTO.METADATA_ALIAS
        );
        assertEquals("Frostbrand", itemTemplate.getString(RPGLObjectTO.NAME_ALIAS),
                "incorrect field value: " + DatapackContentTO.NAME_ALIAS
        );
        assertEquals("A legendary scimitar wielded by Drizzt Do'Urden.", itemTemplate.getString(RPGLObjectTO.DESCRIPTION_ALIAS),
                "incorrect field value: " + DatapackContentTO.DESCRIPTION_ALIAS
        );

        /*
        RPGLItemTO field values
         */
        expected = """
                ["scimitar","metal","magic","martial_melee","finesse"]""";
        assertEquals(expected, itemTemplate.getJsonArray(RPGLItemTO.TAGS_ALIAS).toString(),
                "incorrect field value: " + RPGLItemTO.TAGS_ALIAS
        );
        assertEquals(3, itemTemplate.getInteger(RPGLItemTO.WEIGHT_ALIAS),
                "incorrect field value: " + RPGLItemTO.WEIGHT_ALIAS
        );
        assertEquals(10000, itemTemplate.getInteger(RPGLItemTO.COST_ALIAS),
                "incorrect field value: " + RPGLItemTO.COST_ALIAS
        );
        assertEquals(2, itemTemplate.getJsonArray(RPGLItemTO.EQUIPPED_EFFECTS_ALIAS).size(),
                "incorrect field value: " + RPGLItemTO.EQUIPPED_EFFECTS_ALIAS
        );
        assertEquals(0, itemTemplate.getJsonArray(RPGLItemTO.EQUIPPED_RESOURCES_ALIAS).size(),
                "incorrect field value: " + RPGLItemTO.EQUIPPED_RESOURCES_ALIAS
        );
        assertEquals(3, itemTemplate.getInteger(RPGLItemTO.ATTACK_BONUS_ALIAS),
                "incorrect field value: " + RPGLItemTO.ATTACK_BONUS_ALIAS
        );
        assertEquals(3, itemTemplate.getInteger(RPGLItemTO.DAMAGE_BONUS_ALIAS),
                "incorrect field value: " + RPGLItemTO.DAMAGE_BONUS_ALIAS
        );
        assertNull(itemTemplate.getInteger(RPGLItemTO.ARMOR_CLASS_BASE_ALIAS),
                "incorrect field value: " + RPGLItemTO.TAGS_ALIAS
        );
        assertNull(itemTemplate.getInteger(RPGLItemTO.ARMOR_CLASS_DEX_LIMIT_ALIAS),
                "incorrect field value: " + RPGLItemTO.ARMOR_CLASS_DEX_LIMIT_ALIAS
        );
        assertNull(itemTemplate.getInteger(RPGLItemTO.ARMOR_CLASS_BONUS_ALIAS),
                "incorrect field value: " + RPGLItemTO.ARMOR_CLASS_BONUS_ALIAS
        );
    }

    @Test
    @DisplayName("verify RPGLItem templates can be loaded (armor)")
    void rpglItemTemplate_loadsDatapackContent_armor() {
        RPGLItemTemplate itemTemplate = DatapackLoader.DATAPACKS.get("std").getItemTemplate("breastplate_armor");
        assertNotNull(itemTemplate,
                "Event template std:breastplate_armor failed to load."
        );

        String expected;

        /*
        DatapackContentTO field keys
         */
        assertTrue(itemTemplate.asMap().containsKey(DatapackContentTO.METADATA_ALIAS),
                "template missing field: " + DatapackContentTO.METADATA_ALIAS
        );
        assertTrue(itemTemplate.asMap().containsKey(DatapackContentTO.NAME_ALIAS),
                "template missing field: " + DatapackContentTO.NAME_ALIAS
        );
        assertTrue(itemTemplate.asMap().containsKey(DatapackContentTO.DESCRIPTION_ALIAS),
                "template missing field: " + DatapackContentTO.DESCRIPTION_ALIAS
        );

        /*
        RPGLEventTO field keys
         */
        assertTrue(itemTemplate.asMap().containsKey(RPGLItemTO.TAGS_ALIAS),
                "template missing field: " + RPGLItemTO.TAGS_ALIAS
        );
        assertTrue(itemTemplate.asMap().containsKey(RPGLItemTO.WEIGHT_ALIAS),
                "template missing field: " + RPGLItemTO.WEIGHT_ALIAS
        );
        assertTrue(itemTemplate.asMap().containsKey(RPGLItemTO.COST_ALIAS),
                "template missing field: " + RPGLItemTO.COST_ALIAS
        );
        assertTrue(itemTemplate.asMap().containsKey(RPGLItemTO.EQUIPPED_EFFECTS_ALIAS),
                "template missing field: " + RPGLItemTO.EQUIPPED_EFFECTS_ALIAS
        );
        assertTrue(itemTemplate.asMap().containsKey(RPGLItemTO.EQUIPPED_RESOURCES_ALIAS),
                "template missing field: " + RPGLItemTO.EQUIPPED_RESOURCES_ALIAS
        );
        assertTrue(itemTemplate.asMap().containsKey(RPGLItemTO.ARMOR_CLASS_BASE_ALIAS),
                "template missing field: " + RPGLItemTO.ARMOR_CLASS_BASE_ALIAS
        );
        assertTrue(itemTemplate.asMap().containsKey(RPGLItemTO.ARMOR_CLASS_DEX_LIMIT_ALIAS),
                "template missing field: " + RPGLItemTO.ARMOR_CLASS_DEX_LIMIT_ALIAS
        );

        /*
        DatapackContentTO field values
         */
        expected = """
                {"author":"Calvin Withun"}""";
        assertEquals(expected, itemTemplate.getJsonObject(DatapackContentTO.METADATA_ALIAS).toString(),
                "incorrect field value: " + DatapackContentTO.METADATA_ALIAS
        );
        assertEquals("Breastplate Armor", itemTemplate.getString(RPGLObjectTO.NAME_ALIAS),
                "incorrect field value: " + DatapackContentTO.NAME_ALIAS
        );
        assertEquals("A suit of breastplate armor.", itemTemplate.getString(RPGLObjectTO.DESCRIPTION_ALIAS),
                "incorrect field value: " + DatapackContentTO.DESCRIPTION_ALIAS
        );

        /*
        RPGLEventTO field values
         */
        expected = """
                ["metal","armor","medium_armor"]""";
        assertEquals(expected, itemTemplate.getJsonArray(RPGLItemTO.TAGS_ALIAS).toString(),
                "incorrect field value: " + RPGLItemTO.TAGS_ALIAS
        );
        assertEquals(20, itemTemplate.getInteger(RPGLItemTO.WEIGHT_ALIAS),
                "incorrect field value: " + RPGLItemTO.WEIGHT_ALIAS
        );
        assertEquals(400, itemTemplate.getInteger(RPGLItemTO.COST_ALIAS),
                "incorrect field value: " + RPGLItemTO.COST_ALIAS
        );
        assertEquals(0, itemTemplate.getJsonArray(RPGLItemTO.EQUIPPED_EFFECTS_ALIAS).size(),
                "incorrect field value: " + RPGLItemTO.EQUIPPED_EFFECTS_ALIAS
        );
        assertEquals(0, itemTemplate.getJsonArray(RPGLItemTO.EQUIPPED_RESOURCES_ALIAS).size(),
                "incorrect field value: " + RPGLItemTO.EQUIPPED_RESOURCES_ALIAS
        );
        assertNull(itemTemplate.getInteger(RPGLItemTO.ATTACK_BONUS_ALIAS),
                "incorrect field value: " + RPGLItemTO.ATTACK_BONUS_ALIAS
        );
        assertEquals(14, itemTemplate.getInteger(RPGLItemTO.ARMOR_CLASS_BASE_ALIAS),
                "incorrect field value: " + RPGLItemTO.TAGS_ALIAS
        );
        assertEquals(2, itemTemplate.getInteger(RPGLItemTO.ARMOR_CLASS_DEX_LIMIT_ALIAS),
                "incorrect field value: " + RPGLItemTO.ARMOR_CLASS_DEX_LIMIT_ALIAS
        );
        assertNull(itemTemplate.getInteger(RPGLItemTO.ARMOR_CLASS_BONUS_ALIAS),
                "incorrect field value: " + RPGLItemTO.ARMOR_CLASS_BONUS_ALIAS
        );
    }

    @Test
    @DisplayName("verify RPGLItem templates can be loaded (shield)")
    void rpglItemTemplate_loadsDatapackContent_shield() {
        RPGLItemTemplate itemTemplate = DatapackLoader.DATAPACKS.get("std").getItemTemplate("metal_shield");
        assertNotNull(itemTemplate,
                "Event template std:metal_shield failed to load."
        );

        String expected;

        /*
        DatapackContentTO field keys
         */
        assertTrue(itemTemplate.asMap().containsKey(DatapackContentTO.METADATA_ALIAS),
                "template missing field: " + DatapackContentTO.METADATA_ALIAS
        );
        assertTrue(itemTemplate.asMap().containsKey(DatapackContentTO.NAME_ALIAS),
                "template missing field: " + DatapackContentTO.NAME_ALIAS
        );
        assertTrue(itemTemplate.asMap().containsKey(DatapackContentTO.DESCRIPTION_ALIAS),
                "template missing field: " + DatapackContentTO.DESCRIPTION_ALIAS
        );

        /*
        RPGLEventTO field keys
         */
        assertTrue(itemTemplate.asMap().containsKey(RPGLItemTO.TAGS_ALIAS),
                "template missing field: " + RPGLItemTO.TAGS_ALIAS
        );
        assertTrue(itemTemplate.asMap().containsKey(RPGLItemTO.WEIGHT_ALIAS),
                "template missing field: " + RPGLItemTO.WEIGHT_ALIAS
        );
        assertTrue(itemTemplate.asMap().containsKey(RPGLItemTO.COST_ALIAS),
                "template missing field: " + RPGLItemTO.COST_ALIAS
        );
        assertTrue(itemTemplate.asMap().containsKey(RPGLItemTO.EQUIPPED_EFFECTS_ALIAS),
                "template missing field: " + RPGLItemTO.EQUIPPED_EFFECTS_ALIAS
        );
        assertTrue(itemTemplate.asMap().containsKey(RPGLItemTO.EQUIPPED_RESOURCES_ALIAS),
                "template missing field: " + RPGLItemTO.EQUIPPED_RESOURCES_ALIAS
        );
        assertTrue(itemTemplate.asMap().containsKey(RPGLItemTO.ARMOR_CLASS_BONUS_ALIAS),
                "template missing field: " + RPGLItemTO.ARMOR_CLASS_BONUS_ALIAS
        );

        /*
        DatapackContentTO field values
         */
        expected = """
                {"author":"Calvin Withun"}""";
        assertEquals(expected, itemTemplate.getJsonObject(DatapackContentTO.METADATA_ALIAS).toString(),
                "incorrect field value: " + DatapackContentTO.METADATA_ALIAS
        );
        assertEquals("Metal Shield", itemTemplate.getString(RPGLObjectTO.NAME_ALIAS),
                "incorrect field value: " + DatapackContentTO.NAME_ALIAS
        );
        assertEquals("A metal shield.", itemTemplate.getString(RPGLObjectTO.DESCRIPTION_ALIAS),
                "incorrect field value: " + DatapackContentTO.DESCRIPTION_ALIAS
        );

        /*
        RPGLEventTO field values
         */
        expected = """
                ["metal","shield"]""";
        assertEquals(expected, itemTemplate.getJsonArray(RPGLItemTO.TAGS_ALIAS).toString(),
                "incorrect field value: " + RPGLItemTO.TAGS_ALIAS
        );
        assertEquals(6, itemTemplate.getInteger(RPGLItemTO.WEIGHT_ALIAS),
                "incorrect field value: " + RPGLItemTO.WEIGHT_ALIAS
        );
        assertEquals(10, itemTemplate.getInteger(RPGLItemTO.COST_ALIAS),
                "incorrect field value: " + RPGLItemTO.COST_ALIAS
        );
        assertEquals(0, itemTemplate.getJsonArray(RPGLItemTO.EQUIPPED_EFFECTS_ALIAS).size(),
                "incorrect field value: " + RPGLItemTO.EQUIPPED_EFFECTS_ALIAS
        );
        assertEquals(0, itemTemplate.getJsonArray(RPGLItemTO.EQUIPPED_RESOURCES_ALIAS).size(),
                "incorrect field value: " + RPGLItemTO.EQUIPPED_RESOURCES_ALIAS
        );
        assertNull(itemTemplate.getInteger(RPGLItemTO.ATTACK_BONUS_ALIAS),
                "incorrect field value: " + RPGLItemTO.ATTACK_BONUS_ALIAS
        );
        assertNull(itemTemplate.getInteger(RPGLItemTO.ARMOR_CLASS_BASE_ALIAS),
                "incorrect field value: " + RPGLItemTO.TAGS_ALIAS
        );
        assertNull(itemTemplate.getInteger(RPGLItemTO.ARMOR_CLASS_DEX_LIMIT_ALIAS),
                "incorrect field value: " + RPGLItemTO.ARMOR_CLASS_DEX_LIMIT_ALIAS
        );
        assertEquals(2, itemTemplate.getInteger(RPGLItemTO.ARMOR_CLASS_BONUS_ALIAS),
                "incorrect field value: " + RPGLItemTO.ARMOR_CLASS_BONUS_ALIAS
        );
    }

    @Test
    @DisplayName("verify RPGLObject templates can be loaded")
    void rpglObjectTemplate_loadsDatapackContent() {
        RPGLObjectTemplate objectTemplate = DatapackLoader.DATAPACKS.get("std").getObjectTemplate("young_red_dragon");
        assertNotNull(objectTemplate,
                "Object template std:young_red_dragon failed to load."
        );

        String expected;

        /*
        DatapackContentTO field keys
         */
        assertTrue(objectTemplate.asMap().containsKey(DatapackContentTO.METADATA_ALIAS),
                "template missing field: " + DatapackContentTO.METADATA_ALIAS
        );
        assertTrue(objectTemplate.asMap().containsKey(DatapackContentTO.NAME_ALIAS),
                "template missing field: " + DatapackContentTO.NAME_ALIAS
        );
        assertTrue(objectTemplate.asMap().containsKey(DatapackContentTO.DESCRIPTION_ALIAS),
                "template missing field: " + DatapackContentTO.DESCRIPTION_ALIAS
        );

        /*
        RPGLObjectTO field keys
         */
        assertTrue(objectTemplate.asMap().containsKey(RPGLObjectTO.ABILITY_SCORES_ALIAS),
                "template missing field: " + RPGLObjectTO.ABILITY_SCORES_ALIAS
        );
        assertTrue(objectTemplate.asMap().containsKey(RPGLObjectTO.HEALTH_DATA_ALIAS),
                "template missing field: " + RPGLObjectTO.HEALTH_DATA_ALIAS
        );
        assertTrue(objectTemplate.asMap().containsKey(RPGLObjectTO.EQUIPPED_ITEMS_ALIAS),
                "template missing field: " + RPGLObjectTO.EQUIPPED_ITEMS_ALIAS
        );
        assertTrue(objectTemplate.asMap().containsKey(RPGLObjectTO.INVENTORY_ALIAS),
                "template missing field: " + RPGLObjectTO.INVENTORY_ALIAS
        );
        assertTrue(objectTemplate.asMap().containsKey(RPGLObjectTO.EVENTS_ALIAS),
                "template missing field: " + RPGLObjectTO.EVENTS_ALIAS
        );
        assertTrue(objectTemplate.asMap().containsKey(RPGLObjectTO.EFFECTS_ALIAS),
                "template missing field: " + RPGLObjectTO.EFFECTS_ALIAS
        );
        assertTrue(objectTemplate.asMap().containsKey(RPGLObjectTO.PROFICIENCY_BONUS_ALIAS),
                "template missing field: " + RPGLObjectTO.PROFICIENCY_BONUS_ALIAS
        );

        /*
        DatapackContentTO field values
         */
        expected = """
                {"author":"Calvin Withun"}""";
        assertEquals(expected, objectTemplate.getJsonObject(DatapackContentTO.METADATA_ALIAS).toString(),
                "incorrect field value: " + DatapackContentTO.METADATA_ALIAS
        );
        assertEquals("Young Red Dragon", objectTemplate.getString(RPGLObjectTO.NAME_ALIAS),
                "incorrect field value: " + DatapackContentTO.NAME_ALIAS
        );
        assertEquals("A young red dragon.", objectTemplate.getString(RPGLObjectTO.DESCRIPTION_ALIAS),
                "incorrect field value: " + DatapackContentTO.DESCRIPTION_ALIAS
        );

        /*
        RPGLObjectTO field values
         */
        expected = """
                {"cha":19,"con":21,"dex":10,"int":14,"str":23,"wis":11}""";
        assertEquals(expected, objectTemplate.getJsonObject(RPGLObjectTO.ABILITY_SCORES_ALIAS).toString(),
                "incorrect field value: " + RPGLObjectTO.ABILITY_SCORES_ALIAS
        );
        expected = """
                {"base":93,"current":178,"hit_dice":[{"count":17,"determined":[5],"size":10}],"temporary":0}""";
        assertEquals(expected, objectTemplate.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS).toString(),
                "incorrect field value: " + RPGLObjectTO.HEALTH_DATA_ALIAS
        );
        assertEquals("{}", objectTemplate.getJsonObject(RPGLObjectTO.EQUIPPED_ITEMS_ALIAS).toString(),
                "incorrect field value: " + RPGLObjectTO.EQUIPPED_ITEMS_ALIAS
        );
        assertEquals("[]", objectTemplate.getJsonArray(RPGLObjectTO.INVENTORY_ALIAS).toString(),
                "incorrect field value: " + RPGLObjectTO.INVENTORY_ALIAS
        );
        expected = """
                ["std:young_red_dragon_fire_breath"]""";
        assertEquals(expected, objectTemplate.getJsonArray(RPGLObjectTO.EVENTS_ALIAS).toString(),
                "incorrect field value: " + RPGLObjectTO.EVENTS_ALIAS
        );
        expected = """
                ["std:fire_immunity"]""";
        assertEquals(expected, objectTemplate.getJsonArray(RPGLObjectTO.EFFECTS_ALIAS).toString(),
                "incorrect field value: " + RPGLObjectTO.EFFECTS_ALIAS
        );
        assertEquals(4, objectTemplate.getInteger(RPGLObjectTO.PROFICIENCY_BONUS_ALIAS),
                "incorrect field value: " + RPGLObjectTO.PROFICIENCY_BONUS_ALIAS
        );
    }

}
