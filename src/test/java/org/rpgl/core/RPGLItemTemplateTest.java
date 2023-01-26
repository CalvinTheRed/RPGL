package org.rpgl.core;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonObject;
import org.junit.jupiter.api.*;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing class for core.RPGLItemTemplate class.
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
    @DisplayName("Template sets default cost")
    void test1() {
        RPGLItem dummyItem = RPGLFactory.newItem("test:blank");
        assert dummyItem != null;

        String cost = (String) dummyItem.get("cost");
        assertNotNull(cost,
                "RPGLItemTemplate should create a default cost for a new RPGLItem if one isn't specified."
        );
        assertEquals("0g", cost,
                "The default cost should be 0."
        );
    }

    @Test
    @DisplayName("Template sets default weight")
    void test2() {
        RPGLItem dummyItem = RPGLFactory.newItem("test:blank");
        assert dummyItem != null;

        Long weight = (Long) dummyItem.get("weight");
        assertNotNull(weight,
                "RPGLItemTemplate should create a default weight for a new RPGLItem if one isn't specified."
        );
        assertEquals(0, weight,
                "The default weight should be 0."
        );
    }

    @Test
    @DisplayName("Template sets default attack bonus")
    void test3() {
        RPGLItem dummyItem = RPGLFactory.newItem("test:blank");
        assert dummyItem != null;

        Long attackBonus = (Long) dummyItem.get("attack_bonus");
        assertNotNull(attackBonus,
                "RPGLItemTemplate should create a default attack bonus for a new RPGLItem if one isn't specified."
        );
        assertEquals(0, attackBonus,
                "The default attack bonus should be 0."
        );
    }

    @Test
    @DisplayName("Template sets default weapon properties")
    void test4() {
        RPGLItem dummyItem = RPGLFactory.newItem("test:blank");
        assert dummyItem != null;

        JsonArray weaponProperties = (JsonArray) dummyItem.get("weapon_properties");
        assertNotNull(weaponProperties,
                "RPGLItemTemplate should create a default weapon properties array for a new RPGLItem if one isn't specified."
        );
        assertEquals(1, weaponProperties.size(),
                "The default weapon properties array should have 1 element."
        );
        assertEquals("improvised", weaponProperties.get(0),
                "The element in the default weapon properties array should be improvised."
        );
    }

    @Test
    @DisplayName("Template sets default proficiency tags")
    void test5() {
        RPGLItem dummyItem = RPGLFactory.newItem("test:blank");
        assert dummyItem != null;

        JsonArray proficiencyTags = (JsonArray) dummyItem.get("proficiency_tags");
        assertNotNull(proficiencyTags,
                "RPGLItemTemplate should create a default proficiency tags array for a new RPGLItem if one isn't specified."
        );
        assertEquals(1, proficiencyTags.size(),
                "The default proficiency tags array should have 1 element."
        );
        assertEquals("improvised", proficiencyTags.get(0),
                "The element in the default proficiency tags array should be improvised."
        );
    }

    @Test
    @DisplayName("Template sets default tags")
    void test6() {
        RPGLItem dummyItem = RPGLFactory.newItem("test:blank");
        assert dummyItem != null;

        JsonArray tags = (JsonArray) dummyItem.get("tags");
        assertNotNull(tags,
                "RPGLItemTemplate should create a default tags array for a new RPGLItem if one isn't specified."
        );
        assertEquals(1, tags.size(),
                "The default tags array should have 1 element."
        );
        assertEquals("improvised", tags.get(0),
                "The element in the default tags array should be improvised."
        );
    }

    @Test
    @DisplayName("Template sets default thrown range")
    void test7() {
        RPGLItem dummyItem = RPGLFactory.newItem("test:blank");
        assert dummyItem != null;

        JsonObject thrownRange = (JsonObject) dummyItem.get("thrown_range");
        assertNotNull(thrownRange,
                "RPGLItemTemplate should create a default thrown range for a new RPGLItem if one isn't specified."
        );
        assertEquals(2, thrownRange.size(),
                "Thrown range should have 2 key-value pairs (normal and long)."
        );
        Long normalRange = (Long) thrownRange.get("normal");
        Long longRange = (Long) thrownRange.get("long");
        assertNotNull(normalRange,
                "Normal thrown range should not be null."
        );
        assertNotNull(longRange,
                "Long thrown range should not be null."
        );
        assertEquals(20L, normalRange,
                "Normal thrown range should be 20."
        );
        assertEquals(60L, longRange,
                "Long thrown range should be 60."
        );
    }

    @Test
    @DisplayName("Template sets default attack abilities")
    void test8() {
        RPGLItem dummyItem = RPGLFactory.newItem("test:blank");
        assert dummyItem != null;

        JsonObject attackAbilities = (JsonObject) dummyItem.get("attack_abilities");
        assertNotNull(attackAbilities,
                "Template should insert attack abilities."
        );
        assertEquals(2, attackAbilities.size(),
                "Attack ability should have 2 key-value pairs (melee and thrown)."
        );
        String meleeAttackAbility = (String) attackAbilities.get("melee");
        String thrownAttackAbility = (String) attackAbilities.get("thrown");
        assertNotNull(meleeAttackAbility,
                "Melee attack ability should not be null."
        );
        assertNotNull(thrownAttackAbility,
                "Thrown attack ability should not be null."
        );
        assertEquals("str", meleeAttackAbility,
                "Melee attack ability should be str."
        );
        assertEquals("str", thrownAttackAbility,
                "Thrown attack ability should be str."
        );
    }

    @Test
    @DisplayName("Template sets default attack abilities (finesse)")
    void test9() {
        RPGLItem dummyItem = RPGLFactory.newItem("test:blank_with_finesse");
        assert dummyItem != null;

        JsonObject attackAbilities = (JsonObject) dummyItem.get("attack_abilities");
        assertNotNull(attackAbilities,
                "Template should insert attack abilities."
        );
        assertEquals(2, attackAbilities.size(),
                "Attack ability should have 2 key-value pairs (melee and thrown)."
        );
        String meleeAttackAbility = (String) attackAbilities.get("melee");
        String thrownAttackAbility = (String) attackAbilities.get("thrown");
        assertNotNull(meleeAttackAbility,
                "Melee attack ability should not be null."
        );
        assertNotNull(thrownAttackAbility,
                "Thrown attack ability should not be null."
        );
        assertEquals("dex", meleeAttackAbility,
                "Melee attack ability should be dex."
        );
        assertEquals("dex", thrownAttackAbility,
                "Thrown attack ability should be dex."
        );
    }

    @Test
    @DisplayName("Template sets default equipped effects")
    void test10() {
        RPGLItem dummyItem = RPGLFactory.newItem("test:blank");
        assert dummyItem != null;

        JsonArray equippedEffects = (JsonArray) dummyItem.get("equipped_effects");
        assertNotNull(equippedEffects,
                "Template should add a default equipped effects array for a new RPGLItem if one isn't specified."
        );
        assertTrue(equippedEffects.isEmpty(),
                "Default equipped effects array should be empty."
        );
    }

    @Test
    @DisplayName("Template sets default damage values")
    void test11() {
        RPGLItem dummyItem = RPGLFactory.newItem("test:blank");
        assert dummyItem != null;

        JsonObject damage = (JsonObject) dummyItem.get("damage");
        assertNotNull(damage,
                "Template should add default damage for a new RPGLItem if one isn't specified."
        );
        assertEquals(2, damage.size(),
                "Damage should have 2 key-value pairs (melee, thrown)."
        );
        JsonArray meleeDamage = (JsonArray) damage.get("melee");
        JsonArray thrownDamage = (JsonArray) damage.get("thrown");
        assertNotNull(meleeDamage,
                "Melee damage value should not be null."
        );
        assertNotNull(thrownDamage,
                "Thrown damage value should not be null."
        );
        assertEquals(1, meleeDamage.size(),
                "Melee damage array should only have 1 entry (bludgeoning)."
        );
        assertEquals(1, thrownDamage.size(),
                "Thrown damage array should only have 1 entry (bludgeoning)."
        );
        JsonObject meleeDamageDiceCollection = (JsonObject) meleeDamage.get(0);
        JsonObject thrownDamageDiceCollection = (JsonObject) thrownDamage.get(0);
        assertNotNull(meleeDamageDiceCollection,
                "Melee damage dice collection should not be null."
        );
        assertNotNull(thrownDamageDiceCollection,
                "Thrown damage dice collection should not be null."
        );
        String meleeDamageType = (String) meleeDamageDiceCollection.get("type");
        String thrownDamageType = (String) thrownDamageDiceCollection.get("type");
        assertNotNull(meleeDamageType,
                "Melee damage type should not be null."
        );
        assertNotNull(thrownDamageType,
                "Thrown damage type should not be null."
        );
        assertEquals("bludgeoning", meleeDamageType,
                "Melee damage type should be bludgeoning."
        );
        assertEquals("bludgeoning", thrownDamageType,
                "Thrown damage type should be bludgeoning."
        );
        Long meleeBonus = (Long) meleeDamageDiceCollection.get("bonus");
        Long thrownBonus = (Long) thrownDamageDiceCollection.get("bonus");
        assertNotNull(meleeBonus,
                "Melee damage bonus should not be null."
        );
        assertNotNull(thrownBonus,
                "Thrown damage bonus should not be null."
        );
        assertEquals(0L, meleeBonus,
                "Melee damage bonus should be 0."
        );
        assertEquals(0L, thrownBonus,
                "Thrown damage bonus should be 0."
        );
        JsonArray meleeDice = (JsonArray) meleeDamageDiceCollection.get("dice");
        JsonArray thrownDice = (JsonArray) thrownDamageDiceCollection.get("dice");
        assertNotNull(meleeDice,
                "Melee dice should not be null."
        );
        assertNotNull(thrownDice,
                "Thrown dice should not be null."
        );
        JsonObject meleeDie = (JsonObject) meleeDice.get(0);
        JsonObject thrownDie = (JsonObject) meleeDice.get(0);
        assertNotNull(meleeDie,
                "Melee die should not be null."
        );
        assertNotNull(thrownDie,
                "Thrown die should not be null."
        );
        assertEquals(4L, meleeDie.get("size"),
                "Melee die should be a d4."
        );
        assertEquals(4L, thrownDie.get("size"),
                "Thrown die should be a d4."
        );
    }

    @Test
    @DisplayName("Template processes equipped effects")
    void test12() {
        RPGLItem dummyItem = RPGLFactory.newItem("test:blank_with_equipped_effects");
        assert dummyItem != null;

        JsonArray equippedEffects = (JsonArray) dummyItem.get("equipped_effects");
        assertNotNull(equippedEffects,
                "Template should preserve the default equipped effects array for a new RPGLItem."
        );
        assertEquals(1, equippedEffects.size(),
                "Equipped effects array should have 1 element."
        );
        String effectUuid = (String) equippedEffects.get(0);
        assertNotNull(effectUuid,
                "Equipped effect UUID should not be null."
        );
        assertNotNull(UUIDTable.getEffect(effectUuid),
                "Equipped Effect should be registered in the UUIDTable."
        );
    }

}
