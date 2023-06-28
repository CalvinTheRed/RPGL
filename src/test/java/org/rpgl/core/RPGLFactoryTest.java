package org.rpgl.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackContentTO;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.datapack.RPGLEffectTO;
import org.rpgl.datapack.RPGLItemTO;
import org.rpgl.datapack.RPGLObjectTO;
import org.rpgl.datapack.RPGLTaggableTO;
import org.rpgl.json.JsonArray;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Testing class for the org.rpgl.core.RPGLFactory class.
 *
 * @author Calvin Withun
 */
public class RPGLFactoryTest {

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
    @DisplayName("newEffect using std_effects:fire_immunity")
    void newEffect_fireImmunity() {
        RPGLEffect effect = RPGLFactory.newEffect("std_effects:fire_immunity");
        String expected;

        expected = """
                {"author":"Calvin Withun"}""";
        assertEquals(expected, effect.getMetadata().toString(),
                "incorrect field value: " + DatapackContentTO.METADATA_ALIAS
        );
        assertEquals("Fire Immunity", effect.getName(),
                "incorrect field value: " + DatapackContentTO.NAME_ALIAS
        );
        assertEquals("Creatures with this effect take 0 fire damage.", effect.getDescription(),
                "incorrect field value: " + DatapackContentTO.DESCRIPTION_ALIAS
        );
        assertEquals("std_effects:fire_immunity", effect.getId(),
                "incorrect field value: " + DatapackContentTO.ID_ALIAS
        );

        expected = """
                {"damage_affinity":{"conditions":[{"condition":"objects_match","effect":"target","subevent":"target"}],"functions":[{"damage_type":"fire","function":"grant_immunity"}]}}""";
        assertEquals(expected, effect.getSubeventFilters().toString(),
                "incorrect field value: " + RPGLEffectTO.SUBEVENT_FILTERS_ALIAS
        );
    }

//    @Test
//    @DisplayName("newEvent using std_objects:young_red_dragon_fire_breath")
//    void newEvent_youngRedDragonFireBreath() {
//        RPGLEvent event = RPGLFactory.newEvent("std_objects:young_red_dragon_fire_breath");
//        String expected;
//
//        expected = """
//                {"author":"Calvin Withun"}""";
//        assertEquals(expected, event.getMetadata().toString(),
//                "incorrect field value: " + DatapackContentTO.METADATA_ALIAS
//        );
//        assertEquals("Fire Breath", event.getName(),
//                "incorrect field value: " + DatapackContentTO.NAME_ALIAS
//        );
//        assertEquals("The dragon breathes fire.", event.getDescription(),
//                "incorrect field value: " + DatapackContentTO.DESCRIPTION_ALIAS
//        );
//        assertEquals("std_objects:young_red_dragon_fire_breath", event.getId(),
//                "incorrect field value: " + DatapackContentTO.ID_ALIAS
//        );
//
//        assertEquals("{}", event.getAreaOfEffect().toString(),
//                "incorrect field value: " + RPGLEventTO.AREA_OF_EFFECT_ALIAS
//        );
//        expected = """
//                [{"damage":[{"bonus":0,"damage_formula":"range","damage_type":"fire","dice":[{"count":16,"determined":[3],"size":6}]}],"damage_on_pass":"half","determined":[1],"difficulty_class_ability":"con","save_ability":"dex","subevent":"saving_throw"}]""";
//        assertEquals(expected, event.getSubevents().toString(),
//                "incorrect field value: " + RPGLEventTO.SUBEVENTS_ALIAS
//        );
//    }

    @Test
    @DisplayName("newItem using std_items:frostbrand")
    void newItem_teacup() {
        RPGLItem item = RPGLFactory.newItem("std_items:frostbrand");
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
                {"multiple_hands":["std_items:frostbrand_melee","std_items:frostbrand_melee_finesse","std_items:improvised_thrown"],"one_hand":["std_items:frostbrand_melee","std_items:frostbrand_melee_finesse","std_items:improvised_thrown"],"special":[]}""";
        assertEquals(expected, item.getEvents().toString(),
                "incorrect field value: " + RPGLItemTO.EVENTS_ALIAS
        );

        List<RPGLEffect> equippedEffects = item.getEquippedEffectsObjects();
        assertEquals(2, equippedEffects.size(),
                "2 effects should be created"
        );
        assertEquals("std_effects:cold_resistance", equippedEffects.get(0).getId(),
                "First effect should be std:cold_resistance"
        );
        assertEquals("std_effects:fire_resistance", equippedEffects.get(1).getId(),
                "Second effect should be std:fire_resistance"
        );

        assertEquals(3, item.getAttackBonus(),
                "incorrect field value: " + RPGLItemTO.ATTACK_BONUS_ALIAS
        );
        assertEquals(3, item.getDamageBonus(),
                "incorrect field value: " + RPGLItemTO.DAMAGE_BONUS_ALIAS
        );
    }

    @Test
    @DisplayName("newObject using std_objects:young_red_dragon")
    void newObject_youngRedDragon() {
        RPGLObject object = RPGLFactory.newObject("std_objects:young_red_dragon");
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
        assertEquals("std_objects:young_red_dragon", object.getId(),
                "incorrect field value: " + DatapackContentTO.ID_ALIAS
        );

        expected = """
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
                ["std_objects:young_red_dragon_fire_breath"]""";
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
