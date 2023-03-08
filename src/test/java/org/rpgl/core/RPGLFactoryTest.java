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
import org.rpgl.datapack.RPGLEventTO;
import org.rpgl.datapack.RPGLItemTO;
import org.rpgl.datapack.RPGLObjectTO;
import org.rpgl.datapack.RPGLTaggableTO;
import org.rpgl.json.JsonArray;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
    @DisplayName("newEffect using demo:fire_immunity")
    void newEffect_fireImmunity() {
        RPGLEffect effect = RPGLFactory.newEffect("demo:fire_immunity");
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
        assertEquals("demo:fire_immunity", effect.getId(),
                "incorrect field value: " + DatapackContentTO.ID_ALIAS
        );

        expected = """
                {"damage_affinity":{"conditions":[{"condition":"objects_match","effect":"target","subevent":"target"},{"condition":"check_damage_type","type":"fire"}],"functions":[{"function":"grant_immunity"}]}}""";
        assertEquals(expected, effect.getSubeventFilters().toString(),
                "incorrect field value: " + RPGLEffectTO.SUBEVENT_FILTERS_ALIAS
        );
    }

    @Test
    @DisplayName("newEvent using demo:young_red_dragon_fire_breath")
    void newEvent_youngRedDragonFireBreath() {
        RPGLEvent event = RPGLFactory.newEvent("demo:young_red_dragon_fire_breath");
        String expected;

        expected = """
                {"author":"Calvin Withun"}""";
        assertEquals(expected, event.getMetadata().toString(),
                "incorrect field value: " + DatapackContentTO.METADATA_ALIAS
        );
        assertEquals("Fire Breath", event.getName(),
                "incorrect field value: " + DatapackContentTO.NAME_ALIAS
        );
        assertEquals("The dragon breathes fire.", event.getDescription(),
                "incorrect field value: " + DatapackContentTO.DESCRIPTION_ALIAS
        );
        assertEquals("demo:young_red_dragon_fire_breath", event.getId(),
                "incorrect field value: " + DatapackContentTO.ID_ALIAS
        );

        assertEquals("{}", event.getAreaOfEffect().toString(),
                "incorrect field value: " + RPGLEventTO.AREA_OF_EFFECT_ALIAS
        );
        expected = """
                [{"damage":[{"bonus":0,"dice":[{"determined":[3],"size":6},{"determined":[3],"size":6},{"determined":[3],"size":6},{"determined":[3],"size":6},{"determined":[3],"size":6},{"determined":[3],"size":6},{"determined":[3],"size":6},{"determined":[3],"size":6},{"determined":[3],"size":6},{"determined":[3],"size":6},{"determined":[3],"size":6},{"determined":[3],"size":6},{"determined":[3],"size":6},{"determined":[3],"size":6},{"determined":[3],"size":6},{"determined":[3],"size":6}],"type":"fire"}],"damage_on_pass":"half","determined":[1],"difficulty_class_ability":"con","save_ability":"dex","subevent":"saving_throw"}]""";
        assertEquals(expected, event.getSubevents().toString(),
                "incorrect field value: " + RPGLEventTO.SUBEVENTS_ALIAS
        );
    }

    @Test
    @DisplayName("newItem using demo:teacup")
    void newItem_teacup() {
        RPGLItem item = RPGLFactory.newItem("demo:teacup");
        String expected;

        expected = """
                {"author":"Calvin Withun"}""";
        assertEquals(expected, item.getMetadata().toString(),
                "incorrect field value: " + DatapackContentTO.METADATA_ALIAS
        );
        assertEquals("Teacup", item.getName(),
                "incorrect field value: " + DatapackContentTO.NAME_ALIAS
        );
        assertEquals("A teacup.", item.getDescription(),
                "incorrect field value: " + DatapackContentTO.DESCRIPTION_ALIAS
        );
        assertEquals("demo:teacup", item.getId(),
                "incorrect field value: " + DatapackContentTO.ID_ALIAS
        );

        assertEquals("[]", item.getTags().toString(),
                "incorrect field value: " + RPGLTaggableTO.TAGS_ALIAS
        );

        assertEquals(0, item.getWeight(),
                "incorrect field value: " + RPGLItemTO.WEIGHT_ALIAS
        );
        assertEquals(0, item.getCost(),
                "incorrect field value: " + RPGLItemTO.COST_ALIAS
        );
        assertEquals("[]", item.getProficiencyTags().toString(),
                "incorrect field value: " + RPGLItemTO.PROFICIENCY_TAGS_ALIAS
        );
        assertEquals("[]", item.getWhileEquippedEffects().toString(),
                "incorrect field value: " + RPGLItemTO.WHILE_EQUIPPED_ALIAS
        );
        expected = """
                ["improvised_melee","improvised_thrown"]""";
        assertEquals(expected, item.getWeaponProperties().toString(),
                "incorrect field value: " + RPGLItemTO.WEAPON_PROPERTIES_ALIAS
        );
        expected = """
                {"melee":[{"bonus":0,"dice":[{"determined":[2],"size":4}],"type":"bludgeoning"}],"thrown":[{"bonus":0,"dice":[{"determined":[2],"size":4}],"type":"bludgeoning"}]}""";
        assertEquals(expected, item.getDamage().toString(),
                "incorrect field value: " + RPGLItemTO.DAMAGE_ALIAS
        );
        assertEquals(0, item.getAttackBonus(),
                "incorrect field value: " + RPGLItemTO.ATTACK_BONUS_ALIAS
        );
        expected = """
                {"melee":"str","thrown":"str"}""";
        assertEquals(expected, item.getAttackAbilities().toString(),
                "incorrect field value: " + RPGLItemTO.ATTACK_ABILITIES_ALIAS
        );
        expected = """
                {"long":60,"normal":20}""";
        assertEquals(expected, item.getRange().toString(),
                "incorrect field value: " + RPGLItemTO.RANGE_ALIAS
        );
        assertNull(item.getArmorClassBase(),
                "incorrect field value: " + RPGLItemTO.ARMOR_CLASS_BASE_ALIAS
        );
        assertNull(item.getArmorClassDexLimit(),
                "incorrect field value: " + RPGLItemTO.ARMOR_CLASS_DEX_LIMIT_ALIAS
        );
        assertNull(item.getArmorClassBonus(),
                "incorrect field value: " + RPGLItemTO.ARMOR_CLASS_BONUS_ALIAS
        );
    }

    @Test
    @DisplayName("newObject using demo:young_red_dragon")
    void newObject_youngRedDragon() {
        RPGLObject object = RPGLFactory.newObject("demo:young_red_dragon");
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
        assertEquals("demo:young_red_dragon", object.getId(),
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
