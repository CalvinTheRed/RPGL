package org.rpgl.subevent;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.junit.jupiter.api.*;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.math.Die;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing class for subevent.AttackRoll class.
 *
 * @author Calvin Withun
 */
public class AttackRollTest {

    @BeforeAll
    static void beforeAll() throws Exception {
        DatapackLoader.loadDatapacks(
                new File(Objects.requireNonNull(DatapackTest.class.getClassLoader().getResource("datapacks")).toURI())
        );
        Die.setTesting(true);
    }

    @AfterAll
    static void afterAll() {
        DatapackLoader.DATAPACKS.clear();
    }

    @AfterEach
    void afterEach() {
        UUIDTable.clear();
        DummySubevent.resetCounter();
    }

    @Test
    @DisplayName("AttackRoll Subevent throws SubeventMismatchException when subevent type doesn't match")
    void test0() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String subeventJsonString = """
                {
                    "subevent": "not_a_subevent"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        RPGLContext context = new RPGLContext(null);

        /*
         * Verify subevent behaves as expected
         */
        assertThrows(SubeventMismatchException.class,
                () -> subevent.clone(subeventJson).invoke(context),
                "AttackRoll Subevent should throw a SubeventMismatchException if the specified subevent doesn't match."
        );
    }

    /*
     * #################################################################################################################
     *                                             Attack roll stuff
     * #################################################################################################################
     */

    @Test
    @DisplayName("AttackRoll Subevent defaults to normal roll")
    void test1() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String subeventJsonString = """
                {
                    "subevent": "attack_roll"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);

        /*
         * Verify subevent behaves as expected
         */
        assertTrue(attackRoll.normalRoll(),
                "Attack should default to normal roll."
        );
        assertFalse(attackRoll.advantageRoll(),
                "AttackRoll should not default to advantage roll."
        );
        assertFalse(attackRoll.disadvantageRoll(),
                "AttackRoll should not default to disadvantage roll."
        );
    }

    @Test
    @DisplayName("AttackRoll Subevent can be granted advantage")
    void test2() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String subeventJsonString = """
                {
                    "subevent": "attack_roll"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent methods
         */
        attackRoll.grantAdvantage();

        /*
         * Verify subevent behaves as expected
         */
        assertFalse(attackRoll.normalRoll(),
                "Attack should not be a normal roll after being granted advantage."
        );
        assertTrue(attackRoll.advantageRoll(),
                "AttackRoll should be an advantage roll after being granted advantage."
        );
        assertFalse(attackRoll.disadvantageRoll(),
                "AttackRoll should not be a disadvantage roll after being granted advantage."
        );
    }

    @Test
    @DisplayName("AttackRoll Subevent can be granted disadvantage")
    void test3() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String subeventJsonString = """
                {
                    "subevent": "attack_roll"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent methods
         */
        attackRoll.grantDisadvantage();

        /*
         * Verify subevent behaves as expected
         */
        assertFalse(attackRoll.normalRoll(),
                "Attack should not be a normal roll after being granted disadvantage."
        );
        assertFalse(attackRoll.advantageRoll(),
                "AttackRoll should not be an advantage roll after being granted disadvantage."
        );
        assertTrue(attackRoll.disadvantageRoll(),
                "AttackRoll should be a disadvantage roll after being granted disadvantage."
        );
    }

    @Test
    @DisplayName("AttackRoll Subevent can be granted advantage and disadvantage")
    void test4() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String subeventJsonString = """
                {
                    "subevent": "attack_roll"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent methods
         */
        attackRoll.grantAdvantage();
        attackRoll.grantDisadvantage();

        /*
         * Verify subevent behaves as expected
         */
        assertTrue(attackRoll.normalRoll(),
                "Attack should be a normal roll after being granted advantage and disadvantage."
        );
        assertFalse(attackRoll.advantageRoll(),
                "AttackRoll should not be an advantage roll after being granted advantage and disadvantage."
        );
        assertFalse(attackRoll.disadvantageRoll(),
                "AttackRoll should not be a disadvantage roll after being granted advantage and disadvantage."
        );
    }

    @Test
    @DisplayName("AttackRoll Subevent can roll a flat die (no advantage or disadvantage)")
    void test5() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String subeventJsonString = """
                {
                    "subevent": "attack_roll",
                    "determined": 10
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent methods
         */
        attackRoll.roll();

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(10L, attackRoll.get(),
                "AttackRoll failed to roll a flat die."
        );
    }

    @Test
    @DisplayName("AttackRoll Subevent can roll a flat die (with advantage)")
    void test6() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String subeventJsonString = """
                {
                    "subevent": "attack_roll",
                    "determined": 1,
                    "determined_second": 20
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent methods
         */
        attackRoll.grantAdvantage();
        attackRoll.roll();

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(20L, attackRoll.get(),
                "AttackRoll failed to correctly roll a flat die with advantage."
        );
    }

    @Test
    @DisplayName("AttackRoll Subevent can roll a flat die (with advantage) (reverse order)")
    void test7() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String subeventJsonString = """
                {
                    "subevent": "attack_roll",
                    "determined": 20,
                    "determined_second": 1
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent methods
         */
        attackRoll.grantAdvantage();
        attackRoll.roll();

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(20L, attackRoll.get(),
                "AttackRoll failed to correctly roll a flat die with advantage."
        );
    }

    @Test
    @DisplayName("AttackRoll Subevent can roll a flat die (with disadvantage)")
    void test8() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String subeventJsonString = """
                {
                    "subevent": "attack_roll",
                    "determined": 1,
                    "determined_second": 20
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent methods
         */
        attackRoll.grantDisadvantage();
        attackRoll.roll();

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(1L, attackRoll.get(),
                "AttackRoll failed to correctly roll a flat die with disadvantage."
        );
    }

    @Test
    @DisplayName("AttackRoll Subevent can roll a flat die (with disadvantage) (reverse order)")
    void test9() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String subeventJsonString = """
                {
                    "subevent": "attack_roll",
                    "determined": 20,
                    "determined_second": 1
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent methods
         */
        attackRoll.grantDisadvantage();
        attackRoll.roll();

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(1L, attackRoll.get(),
                "AttackRoll failed to correctly roll a flat die with advantage."
        );
    }

    @Test
    @DisplayName("AttackRoll Subevent can set its die roll")
    void test10() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String subeventJsonString = """
                {
                    "subevent": "attack_roll",
                    "determined": 10
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent methods
         */
        attackRoll.roll();
        attackRoll.set(1L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(1L, attackRoll.get(),
                "AttackRoll failed to set its die roll."
        );
    }

    @Test
    @DisplayName("AttackRoll Subevent can set a set die roll (second set is greater)")
    void test11() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String subeventJsonString = """
                {
                    "subevent": "attack_roll",
                    "determined": 10
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent methods
         */
        attackRoll.roll();
        attackRoll.set(1L);
        attackRoll.set(20L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(20L, attackRoll.get(),
                "AttackRoll failed to set its die roll properly."
        );
    }

    @Test
    @DisplayName("AttackRoll Subevent can add bonus to roll")
    void test13() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String subeventJsonString = """
                {
                    "subevent": "attack_roll",
                    "determined": 10
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent methods
         */
        attackRoll.roll();
        attackRoll.addBonus(2L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(12L, attackRoll.get(),
                "AttackRoll failed to add bonus properly."
        );
    }

    @Test
    @DisplayName("AttackRoll Subevent can add bonus to set roll")
    void test14() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String subeventJsonString = """
                {
                    "subevent": "attack_roll",
                    "determined": 1
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent methods
         */
        attackRoll.roll();
        attackRoll.set(10L);
        attackRoll.addBonus(2L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(12L, attackRoll.get(),
                "AttackRoll failed to add bonus to set roll properly."
        );
    }

    /*
     * #################################################################################################################
     *                                  AttackRoll preparing different attack types
     * #################################################################################################################
     */

    @Test
    @DisplayName("AttackRoll Subevent can prepare attack without weapon")
    void test15() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String subeventJsonString = """
                {
                    "subevent": "attack_roll",
                    "attack_ability": "cha"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent methods
         */
        attackRoll.setSource(object);
        attackRoll.prepareAttackWithoutWeapon(context);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(3L, attackRoll.subeventJson.get("bonus"),
                "AttackRoll Subevent should have a bonus of 3 (CHA+1, PROF+2)."
        );
        assertEquals("[]", attackRoll.subeventJson.get("damage").toString(),
                "AttackRoll should create empty damage array if none is provided by template."
        );
    }

    @Test
    @DisplayName("AttackRoll Subevent can prepare natural weapon attack")
    void test16() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String weaponId = "dummy:dummy_hollow";
        String subeventJsonString = String.format("""
                {
                    "subevent": "attack_roll",
                    "weapon": "%s",
                    "attack_type": "melee"
                }
                """,
                weaponId
        );
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent methods
         */
        attackRoll.setSource(object);
        attackRoll.prepareNaturalWeaponAttack(context, weaponId);

        /*
         * Verify subevent behaves as expected
         */
        String naturalWeaponDamageArrayString = """
                [
                    {
                        "type": "bludgeoning",
                        "dice": [
                            { "size": 4, "determined": 1 }
                        ],
                        "bonus": 0
                    }
                ]
                """;
        JsonArray naturalWeaponDamageArray = JsonParser.parseArrayString(naturalWeaponDamageArrayString);
        assertEquals(0L, attackRoll.subeventJson.get("bonus"),
                "AttackRoll Subevent should have a bonus of 0 (STR-2, PROF+2)."
        );
        assertEquals(naturalWeaponDamageArray.toString(), attackRoll.subeventJson.get("damage").toString(),
                "AttackRoll should have natural weapon damage stored (improvised damage for the purposes of this test)."
        );
        assertNotNull(UUIDTable.getItem((String) attackRoll.subeventJson.get("weapon")),
                "AttackRoll should record the natural weapon's UUID."
        );
    }

    @Test
    @DisplayName("AttackRoll Subevent natural weapons do not persist after invoke")
    void test17() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String subeventJsonString = """
                {
                    "subevent": "attack_roll",
                    "weapon": "dummy:dummy_hollow",
                    "attack_type": "melee"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent methods
         */
        attackRoll.setSource(object);
        attackRoll.prepare(context);
        attackRoll.setTarget(object);
        attackRoll.invoke(context);

        /*
         * Verify subevent behaves as expected
         */
        assertNull(UUIDTable.getItem((String) attackRoll.subeventJson.get("weapon")),
                "The recorded natural weapon UUID should no longer be present in UUIDTable after AttackRoll is invoked."
        );
    }

    @Test
    @DisplayName("AttackRoll Subevent can prepare item weapon attack")
    void test18() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String weaponEquipmentSlot = "hand_1";
        String subeventJsonString = String.format("""
                {
                    "subevent": "attack_roll",
                    "weapon": "%s",
                    "attack_type": "melee"
                }
                """,
                weaponEquipmentSlot
        );
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        RPGLItem hand1Weapon = RPGLFactory.newItem("dummy:dummy_hollow");
        assert object != null;
        assert hand1Weapon != null;
        object.giveItem((String) hand1Weapon.get("uuid"));
        object.equipItem((String) hand1Weapon.get("uuid"), weaponEquipmentSlot);
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent methods
         */
        attackRoll.setSource(object);
        attackRoll.prepareItemWeaponAttack(context, weaponEquipmentSlot);

        /*
         * Verify subevent behaves as expected
         */
        String naturalWeaponDamageArrayString = """
                [
                    {
                        "type": "bludgeoning",
                        "dice": [
                            { "size": 4, "determined": 1 }
                        ],
                        "bonus": 0
                    }
                ]
                """;
        JsonArray naturalWeaponDamageArray = JsonParser.parseArrayString(naturalWeaponDamageArrayString);
        assertEquals(-2L, attackRoll.subeventJson.get("bonus"),
                "AttackRoll Subevent should have a bonus of 0 (STR-2, no PROF)."
                // TODO refactor this to account for weapon proficiency once it is implemented
        );
        assertEquals(naturalWeaponDamageArray.toString(), attackRoll.subeventJson.get("damage").toString(),
                "AttackRoll should have item weapon damage stored (improvised damage for the purposes of this test)."
        );
        assertNotNull(UUIDTable.getItem((String) attackRoll.subeventJson.get("weapon")),
                "AttackRoll should record the item weapon's UUID."
        );
    }

    /*
     * #################################################################################################################
     *                                                  Damage Stuff
     * #################################################################################################################
     */

    @Test
    @DisplayName("AttackRoll Subevent can calculate base damage dice collection")
    void test19() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String weaponId = "dummy:dummy_hollow";
        String subeventJsonString = String.format("""
                {
                    "subevent": "attack_roll",
                    "weapon": "%s",
                    "attack_type": "melee"
                }
                """,
                weaponId
        );
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent methods
         */
        attackRoll.setSource(object);
        attackRoll.prepareNaturalWeaponAttack(context, weaponId);
        attackRoll.setTarget(object);
        BaseDamageDiceCollection baseDamageDiceCollection = attackRoll.getBaseDamageDiceCollection(context);

        /*
         * Verify subevent behaves as expected
         */
        String baseDamageArrayString = """
                [
                    {
                        "type": "bludgeoning",
                        "dice": [
                            { "size": 4, "determined": 1 }
                        ],
                        "bonus": 0
                    }
                ]
                """;
        JsonArray expectedBaseDamageDiceCollection = JsonParser.parseArrayString(baseDamageArrayString);
        assertEquals(expectedBaseDamageDiceCollection.toString(), baseDamageDiceCollection.getDamageDiceCollection().toString(),
                ""
        );
    }

    @Test
    @DisplayName("AttackRoll Subevent can resolve damage")
    void test20() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String weaponId = "dummy:dummy_hollow";
        String subeventJsonString = String.format("""
                {
                    "subevent": "attack_roll",
                    "weapon": "%s",
                    "attack_type": "melee"
                }
                """,
                weaponId
        );
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent methods
         */
        attackRoll.setSource(object);
        attackRoll.prepareNaturalWeaponAttack(context, weaponId);
        attackRoll.setTarget(object);
        attackRoll.resolveDamage(context);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(9L, object.seek("health_data.current"),
                "AttackRoll subevent should be able to resolve damage (10-1=9)"
        );
    }

    @Test
    @DisplayName("AttackRoll Subevent can resolve nested subevents (hit)")
    void test21() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String weaponId = "dummy:dummy_hollow";
        String subeventJsonString = String.format("""
                {
                    "subevent": "attack_roll",
                    "weapon": "%s",
                    "attack_type": "melee",
                    "hit": [
                        { "subevent": "dummy_subevent" }
                    ],
                    "miss": [
                        { "subevent": "dummy_subevent" }
                    ],
                    "determined": 20
                }
                """,
                weaponId
        );
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent methods
         */
        attackRoll.setSource(object);
        attackRoll.prepare(context);
        attackRoll.setTarget(object);
        attackRoll.resolveNestedSubevents(context, "hit");

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(1L, DummySubevent.counter,
                "AttackRoll Subevent should increment DummySubevent.counter by 1 on hit."
        );
    }

    @Test
    @DisplayName("AttackRoll Subevent can resolve nested subevents (miss)")
    void test22() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String weaponId = "dummy:dummy_hollow";
        String subeventJsonString = String.format("""
                {
                    "subevent": "attack_roll",
                    "weapon": "%s",
                    "attack_type": "melee",
                    "hit": [
                        { "subevent": "dummy_subevent" }
                    ],
                    "miss": [
                        { "subevent": "dummy_subevent" }
                    ],
                    "determined": 20
                }
                """,
                weaponId
        );
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent methods
         */
        attackRoll.setSource(object);
        attackRoll.prepare(context);
        attackRoll.setTarget(object);
        attackRoll.resolveNestedSubevents(context, "miss");

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(1L, DummySubevent.counter,
                "AttackRoll Subevent should increment DummySubevent.counter by 1 on hit."
        );
    }

    @Test
    @DisplayName("AttackRoll Subevent complete scenario (no weapon attack)")
    void test23() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String subeventJsonString = """
                {
                    "subevent": "attack_roll",
                    "attack_ability": "int",
                    "damage": [
                        {
                            "type": "fire",
                            "dice": [
                                { "size": 10, "determined": 1 },
                                { "size": 10, "determined": 1 },
                            ],
                            "bonus": 1
                        }
                    ],
                    "hit": [
                        { "subevent": "dummy_subevent" }
                    ],
                    "miss": [
                        { "subevent": "dummy_subevent" }
                    ],
                    "determined": 19
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent methods
         */
        attackRoll.setSource(object);
        attackRoll.prepare(context);
        attackRoll.setTarget(object);
        attackRoll.invoke(context);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(7L, object.seek("health_data.current"),
                "AttackRoll Subevent should deal 3 damage on hit (10-3=7)"
        );
        assertEquals(1L, DummySubevent.counter,
                "AttackRoll Subevent should increment DummySubevent.counter by 1 on hit."
        );
    }

    @Test
    @DisplayName("AttackRoll Subevent complete scenario (natural weapon attack)")
    void test24() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String weaponId = "dummy:dummy_hollow";
        String subeventJsonString = String.format("""
                {
                    "subevent": "attack_roll",
                    "weapon": "%s",
                    "attack_type": "melee",
                    "hit": [
                        { "subevent": "dummy_subevent" }
                    ],
                    "miss": [
                        { "subevent": "dummy_subevent" }
                    ],
                    "determined": 19
                }
                """,
                weaponId
        );
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent methods
         */
        attackRoll.setSource(object);
        attackRoll.prepare(context);
        attackRoll.setTarget(object);
        attackRoll.invoke(context);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(9L, object.seek("health_data.current"),
                "AttackRoll Subevent should deal 1 damage on hit (10-1=9)"
        );
        assertEquals(1L, DummySubevent.counter,
                "AttackRoll Subevent should increment DummySubevent.counter by 1 on hit."
        );
    }

    @Test
    @DisplayName("AttackRoll Subevent complete scenario (item weapon attack)")
    void test25() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String weaponEquipmentSlot = "hand_1";
        String subeventJsonString = String.format("""
                {
                    "subevent": "attack_roll",
                    "weapon": "%s",
                    "attack_type": "melee",
                    "hit": [
                        { "subevent": "dummy_subevent" }
                    ],
                    "miss": [
                        { "subevent": "dummy_subevent" }
                    ],
                    "determined": 19
                }
                """,
                weaponEquipmentSlot
        );
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        RPGLItem hand1Weapon = RPGLFactory.newItem("dummy:dummy_hollow");
        assert object != null;
        assert hand1Weapon != null;
        object.giveItem((String) hand1Weapon.get("uuid"));
        object.equipItem((String) hand1Weapon.get("uuid"), weaponEquipmentSlot);
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent methods
         */
        attackRoll.setSource(object);
        attackRoll.prepare(context);
        attackRoll.setTarget(object);
        attackRoll.invoke(context);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(9L, object.seek("health_data.current"),
                "AttackRoll Subevent should deal 1 damage on hit (10-1=9)"
        );
        assertEquals(1L, DummySubevent.counter,
                "AttackRoll Subevent should increment DummySubevent.counter by 1 on hit."
        );
    }

    /*
     * #################################################################################################################
     *                                            Critical Hits and Misses
     * #################################################################################################################
     */

    @Test
    @DisplayName("AttackRoll Subevent deals extra damage on critical hit")
    void test26() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String subeventJsonString = """
                {
                    "subevent": "attack_roll",
                    "attack_ability": "int",
                    "damage": [
                        {
                            "type": "fire",
                            "dice": [
                                { "size": 10, "determined": 1 },
                                { "size": 10, "determined": 1 },
                            ],
                            "bonus": 1
                        }
                    ],
                    "determined": 20
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent methods
         */
        attackRoll.setSource(object);
        attackRoll.prepare(context);
        attackRoll.setTarget(object);
        attackRoll.invoke(context);

        /*
         * Verify subevent behaves as expected
         * NOTE: critical hit bonus dice share the same determined values as the original dice
         */
        assertEquals(5L, object.seek("health_data.current"),
                "AttackRoll Subevent should deal 5 damage on hit including critical hit damage (10-5=5)"
        );
    }

    @Test
    @DisplayName("AttackRoll Subevent critical hit guarantees hit")
    void test27() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String subeventJsonString = """
                {
                    "subevent": "attack_roll",
                    "attack_ability": "int",
                    "damage": [ ],
                    "hit": [
                        { "subevent": "dummy_subevent" }
                    ],
                    "determined": 20
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent methods
         */
        attackRoll.setSource(object);
        attackRoll.prepare(context);
        attackRoll.setTarget(object);
        attackRoll.addBonus(-20L); // should miss normally
        attackRoll.invoke(context);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(1, DummySubevent.counter,
                "AttackRoll Subevent should satisfy hit condition on a critical hit."
        );
    }

    @Test
    @DisplayName("AttackRoll Subevent critical miss guarantees miss")
    void test28() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new AttackRoll();
        String subeventJsonString = """
                {
                    "subevent": "attack_roll",
                    "attack_ability": "int",
                    "damage": [ ],
                    "miss": [
                        { "subevent": "dummy_subevent" }
                    ],
                    "determined": 1
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        AttackRoll attackRoll = (AttackRoll) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent methods
         */
        attackRoll.setSource(object);
        attackRoll.prepare(context);
        attackRoll.setTarget(object);
        attackRoll.addBonus(20L); // should hit normally
        attackRoll.invoke(context);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(1, DummySubevent.counter,
                "AttackRoll Subevent should satisfy miss condition on a critical miss."
        );
    }

}
