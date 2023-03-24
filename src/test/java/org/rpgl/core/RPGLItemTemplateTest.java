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
import org.rpgl.json.JsonArray;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    @DisplayName("setDefaultItemDamage default damage is added if absent (no melee, no thrown)")
    void setDefaultItemDamage_defaultDamageIsAddedIfAbsent_noMeleeNoThrown() {
        RPGLItemTemplate itemTemplate = DatapackLoader.DATAPACKS.get("demo").getItemTemplate("teacup");
        RPGLItem item = new RPGLItem();
        item.join(itemTemplate);

        RPGLItemTemplate.setDefaultItemDamage(item);

        String expected = """
                {"melee":[{"bonus":0,"damage_formula":"range","damage_type":"bludgeoning","dice":[{"count":1,"determined":[2],"size":4}]}],"thrown":[{"bonus":0,"damage_formula":"range","damage_type":"bludgeoning","dice":[{"count":1,"determined":[2],"size":4}]}]}""";
        assertEquals(expected, item.getDamage().toString(),
                "incorrect field value: " + RPGLItemTO.DAMAGE_ALIAS
        );
    }

    @Test
    @DisplayName("setDefaultItemDamage default damage is added if absent (no thrown)")
    void setDefaultItemDamage_defaultDamageIsAddedIfAbsent_noThrown() {
        RPGLItemTemplate itemTemplate = DatapackLoader.DATAPACKS.get("demo").getItemTemplate("longsword");
        RPGLItem item = new RPGLItem();
        item.join(itemTemplate);

        RPGLItemTemplate.setDefaultItemDamage(item);

        String expected = """
                {"melee":[{"bonus":0,"damage_formula":"range","damage_type":"slashing","dice":[{"count":1,"determined":[4],"size":8}]}],"thrown":[{"bonus":0,"damage_formula":"range","damage_type":"bludgeoning","dice":[{"count":1,"determined":[2],"size":4}]}]}""";
        assertEquals(expected, item.getDamage().toString(),
                "incorrect field value: " + RPGLItemTO.DAMAGE_ALIAS
        );
    }

    @Test
    @DisplayName("processEquippedEffects effects are created and loaded in UUIDTable")
    void processEquippedEffects_effectsCreatedAndLoadedInUUIDTable() {
        RPGLItemTemplate itemTemplate = DatapackLoader.DATAPACKS.get("demo").getItemTemplate("frostbrand");
        RPGLItem item = new RPGLItem();
        item.join(itemTemplate);

        RPGLItemTemplate.processEquippedEffects(item);

        JsonArray whileEquipped = item.getWhileEquippedEffects();
        assertEquals(1, whileEquipped.size(),
                "there should be 1 element in the while_equipped field after calling processEquippedEffects() method"
        );
        for (int i = 0; i < whileEquipped.size(); i++) {
            String effectUuid = whileEquipped.getString(i);
            assertNotNull(UUIDTable.getEffect(effectUuid),
                    "effect (index " + i + ") absent from UUIDTable"
            );
        }
    }

    @Test
    @DisplayName("processImprovisedTags improvised weapon property tags added (not melee, not thrown)")
    void processImprovisedTags_improvisedWeaponPropertyTagsAdded_notMeleeNotThrown() {
        RPGLItemTemplate itemTemplate = DatapackLoader.DATAPACKS.get("demo").getItemTemplate("teacup");
        RPGLItem item = new RPGLItem();
        item.join(itemTemplate);

        RPGLItemTemplate.processImprovisedTags(item);

        JsonArray weaponProperties = item.getWeaponProperties();
        assertTrue(weaponProperties.asList().contains("improvised_melee"),
                "weapon properties array missing improvised_melee tag"
        );
        assertTrue(weaponProperties.asList().contains("improvised_thrown"),
                "weapon properties array missing improvised_thrown tag"
        );
    }

    @Test
    @DisplayName("processImprovisedTags improvised weapon property tags added (not thrown)")
    void processImprovisedTags_improvisedWeaponPropertyTagsAdded_notThrown() {
        RPGLItemTemplate itemTemplate = DatapackLoader.DATAPACKS.get("demo").getItemTemplate("longsword");
        RPGLItem item = new RPGLItem();
        item.join(itemTemplate);

        RPGLItemTemplate.processImprovisedTags(item);

        JsonArray weaponProperties = item.getWeaponProperties();
        assertFalse(weaponProperties.asList().contains("improvised_melee"),
                "weapon properties array should not contain improvised_melee tag"
        );
        assertTrue(weaponProperties.asList().contains("improvised_thrown"),
                "weapon properties array missing improvised_thrown tag"
        );
    }

    @Test
    @DisplayName("newInstance comprehensive test using demo:teacup template")
    void newInstance_teacupTemplate() {
        RPGLItemTemplate itemTemplate = DatapackLoader.DATAPACKS.get("demo").getItemTemplate("teacup");
        RPGLItem item = itemTemplate.newInstance();
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
                "incorrect field value: " + RPGLItemTO.TAGS_ALIAS
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
                {"melee":[{"bonus":0,"damage_formula":"range","damage_type":"bludgeoning","dice":[{"count":1,"determined":[2],"size":4}]}],"thrown":[{"bonus":0,"damage_formula":"range","damage_type":"bludgeoning","dice":[{"count":1,"determined":[2],"size":4}]}]}""";
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

}
