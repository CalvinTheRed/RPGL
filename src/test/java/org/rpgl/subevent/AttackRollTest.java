package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.datapack.RPGLObjectTO;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for class AttackRoll.
 *
 * @author Calvin Withun
 */
public class AttackRollTest {

    @BeforeAll
    static void beforeAll() throws Exception {
        DatapackLoader.loadDatapacks(
                new File(Objects.requireNonNull(DatapackTest.class.getClassLoader().getResource("datapacks")).toURI())
        );
        RPGLCore.initializeTesting();
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
    @DisplayName("invoke wrong subevent")
    void invoke_wrongSubevent_throwsException() {
        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "not_a_subevent"
            }*/
            this.putString("subevent", "not_a_subevent");
        }});

        assertThrows(SubeventMismatchException.class,
                () -> attackRoll.invoke(new RPGLContext()),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("applyWeaponAttackBonus adds bonus to roll (longsword +1)")
    void applyWeaponAttackBonus_addsBonusToRoll_longswordPlusOne() {
        AttackRoll attackRoll = new AttackRoll();
        RPGLItem item = RPGLFactory.newItem("demo:longsword_plus_one");
        attackRoll.applyWeaponAttackBonus(item);

        assertEquals(1, attackRoll.getBonus(),
                "weapon bonus of 1 should be applied to the attack roll"
        );
    }

    @Test
    @DisplayName("isCriticalMiss returns true (base roll of 1)")
    void isCriticalMiss_returnsTrue_baseRollOne() {
        AttackRoll attackRoll = new AttackRoll();
        attackRoll.setBase(1);

        assertTrue(attackRoll.isCriticalMiss(),
                "attack roll with base of 1 should register as a critical miss"
        );
    }

    @Test
    @DisplayName("isCriticalMiss returns false (base roll exceeding 1)")
    void isCriticalMiss_returnsFalse_baseRollExceedingOne() {
        AttackRoll attackRoll = new AttackRoll();
        attackRoll.setBase(10);

        assertFalse(attackRoll.isCriticalMiss(),
                "attack roll with base exceeding 1 should not register as a critical miss"
        );
    }

    @Test
    @DisplayName("isCriticalHit returns true (base roll of 20)")
    void isCriticalHit_returnsTrue_baseRollTwenty() throws Exception {
        AttackRoll attackRoll = new AttackRoll();
        attackRoll.setBase(20);

        assertTrue(attackRoll.isCriticalHit(new RPGLContext()),
                "attack roll with base of 20 should register as a critical hit"
        );
    }

    @Test
    @DisplayName("isCriticalHit returns false (base roll below 20)")
    void isCriticalHit_returnsFalse_baseRollBelowTwenty() throws Exception {
        AttackRoll attackRoll = new AttackRoll();
        attackRoll.setBase(10);

        assertFalse(attackRoll.isCriticalHit(new RPGLContext()),
                "attack roll with base below 20 should not register as a critical hit"
        );
    }

    @Test
    @DisplayName("resolveNestedSubevents invoked DummySubevent (on hit)")
    void resolveNestedSubevents_invokesDummySubevent_onHit() throws Exception {
        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "hit": [
                    {
                        "subevent": "dummy_subevent"
                    }
                ]
            }*/
            this.putJsonArray("hit", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
        }});
        attackRoll.resolveNestedSubevents("hit", new RPGLContext());

        assertEquals(1, DummySubevent.counter,
                "DummySubevent counter should increment by 1 from resolving nested subevents on hit"
        );
    }

    @Test
    @DisplayName("resolveNestedSubevents invoked DummySubevent (on miss)")
    void resolveNestedSubevents_invokesDummySubevent_onMiss() throws Exception {
        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "miss": [
                    {
                        "subevent": "dummy_subevent"
                    }
                ]
            }*/
            this.putJsonArray("miss", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
        }});
        attackRoll.resolveNestedSubevents("miss", new RPGLContext());

        assertEquals(1, DummySubevent.counter,
                "DummySubevent counter should increment by 1 from resolving nested subevents on miss"
        );
    }

    @Test
    @DisplayName("deliverDamage object loses health")
    void deliverDamage_objectLosesHealth() throws Exception {
        RPGLObject object = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(object);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "damage": {
                    "fire": 10
                }
            }*/
            this.putJsonObject("damage", new JsonObject() {{
                this.putInteger("fire", 10);
            }});
        }});
        attackRoll.setSource(object);
        attackRoll.setTarget(object);
        attackRoll.deliverDamage(context);

        assertEquals(42, object.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS).getInteger("current"),
                "Knight should have 42 health left (52-10=42)"
        );
    }

    @Test
    @DisplayName("getAttackDamage damage is rolled")
    void getAttackDamage_damageIsRolled() throws Exception {
        RPGLObject object = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(object);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.setSource(object);
        attackRoll.setTarget(object);
        JsonObject attackDamage = attackRoll.getAttackDamage(new JsonArray() {{
            /*[
                {
                    "type": "fire",
                    "dice": [
                        { "size": 6, "determined": [ 3 ] },
                        { "size": 6, "determined": [ 3 ] },
                        { "size": 6, "determined": [ 3 ] }
                    ],
                    "bonus": 1
                },{
                    "type": "cold",
                    "dice": [
                        { "size": 6, "determined": [ 3 ] },
                        { "size": 6, "determined": [ 3 ] },
                        { "size": 6, "determined": [ 3 ] }
                    ],
                    "bonus": 1
                }
            ]*/
            this.addJsonObject(new JsonObject() {{
                this.putString("type", "fire");
                this.putJsonArray("dice", new JsonArray() {{
                    this.addJsonObject(new JsonObject() {{
                        this.putInteger("size", 6);
                        this.putJsonArray("determined", new JsonArray() {{
                            this.addInteger(3);
                        }});
                    }});
                    this.addJsonObject(new JsonObject() {{
                        this.putInteger("size", 6);
                        this.putJsonArray("determined", new JsonArray() {{
                            this.addInteger(3);
                        }});
                    }});
                    this.addJsonObject(new JsonObject() {{
                        this.putInteger("size", 6);
                        this.putJsonArray("determined", new JsonArray() {{
                            this.addInteger(3);
                        }});
                    }});
                }});
                this.putInteger("bonus", 1);
            }});
            this.addJsonObject(new JsonObject() {{
                this.putString("type", "cold");
                this.putJsonArray("dice", new JsonArray() {{
                    this.addJsonObject(new JsonObject() {{
                        this.putInteger("size", 6);
                        this.putJsonArray("determined", new JsonArray() {{
                            this.addInteger(3);
                        }});
                    }});
                    this.addJsonObject(new JsonObject() {{
                        this.putInteger("size", 6);
                        this.putJsonArray("determined", new JsonArray() {{
                            this.addInteger(3);
                        }});
                    }});
                    this.addJsonObject(new JsonObject() {{
                        this.putInteger("size", 6);
                        this.putJsonArray("determined", new JsonArray() {{
                            this.addInteger(3);
                        }});
                    }});
                }});
                this.putInteger("bonus", 1);
            }});
        }}, context);

        String expected = """
                {"cold":10,"fire":10}""";
        assertEquals(expected, attackDamage.toString(),
                "getAttackDamage should yield 10 cold and fire damage"
        );
    }

    @Test
    @DisplayName("getCriticalHitDamageCollection doubles dice")
    void getCriticalHitDamageCollection_doublesDice() throws Exception {
        AttackRoll attackRoll = new AttackRoll();

        BaseDamageCollection baseDamageCollection = new BaseDamageCollection();
        baseDamageCollection.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "type": "fire",
                        "dice": [
                            { "size": 6, "determined": [ 3 ] }
                        ],
                        "bonus": 1
                    }
                ]
            }*/
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("type", "fire");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(3);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 1);
                }});
            }});
        }});

        CriticalHitDamageCollection criticalHitDamageCollection = attackRoll.getCriticalHitDamageCollection(
                baseDamageCollection,
                new TargetDamageCollection(),
                new RPGLContext()
        );

        String expected = """
                [{"bonus":1,"dice":[{"determined":[3],"size":6},{"determined":[3],"size":6}],"type":"fire"}]""";
        assertEquals(expected, criticalHitDamageCollection.getDamageCollection().toString(),
                "the number of dice should be doubled after calling getCriticalHitDamageCollection"
        );
    }

    @Test
    @DisplayName("getTargetDamageCollection returns empty array (default behavior)")
    void getTargetDamageCollection_returnsEmptyArray_default() throws Exception {
        AttackRoll attackRoll = new AttackRoll();
        TargetDamageCollection targetDamageCollection = attackRoll.getTargetDamageCollection(new RPGLContext());

        assertEquals("[]", targetDamageCollection.getDamageCollection().toString(),
                "getTargetDamageCollection should return empty array by default"
        );
    }

    @Test
    @DisplayName("getBaseDamageCollection calculates base damage collection")
    void getBaseDamageCollection_calculatesBaseDamageCollection() throws Exception {
        RPGLObject object = RPGLFactory.newObject("demo:knight");
        RPGLItem item = UUIDTable.getItem(object.getJsonObject(RPGLObjectTO.EQUIPPED_ITEMS_ALIAS).getString("mainhand"));
        RPGLContext context = new RPGLContext();
        context.add(object);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "weapon": <RPGLItem UUID>,
                "attack_type": "melee",
                "damage": [
                    {
                        "type": "slashing",
                        "dice": [
                            { "size": 8, "determined": [ 4 ] }
                        ],
                        "bonus": 0
                    }
                ]
            }*/
            this.putString("weapon", item.getUuid());
            this.putString("attack_type", "melee");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("type", "slashing");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 8);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(4);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
        }});
        attackRoll.setSource(object);
        attackRoll.setTarget(object);

        BaseDamageCollection baseDamageCollection = attackRoll.getBaseDamageCollection(context);

        String expected = """
                [{"bonus":3,"dice":[{"determined":[4],"size":8}],"type":"slashing"}]""";
        assertEquals(expected, baseDamageCollection.getDamageCollection().toString(),
                "base damage should be collected properly including ability score modifier damage bonus"
        );
    }

    @Test
    @DisplayName("resolveCriticalHitDamage deals critical damage")
    void resolveCriticalHitDamage_dealsCriticalDamage() throws Exception {
        RPGLObject object = RPGLFactory.newObject("demo:knight");
        RPGLItem item = UUIDTable.getItem(object.getJsonObject(RPGLObjectTO.EQUIPPED_ITEMS_ALIAS).getString("mainhand"));
        RPGLContext context = new RPGLContext();
        context.add(object);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "weapon": <RPGLItem UUID>,
                "attack_type": "melee",
                "damage": [
                    {
                        "type": "slashing",
                        "dice": [
                            { "size": 8, "determined": [ 4 ] }
                        ],
                        "bonus": 0
                    }
                ]
            }*/
            this.putString("weapon", item.getUuid());
            this.putString("attack_type", "melee");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("type", "slashing");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 8);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(4);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
        }});
        attackRoll.setSource(object);
        attackRoll.setTarget(object);

        attackRoll.resolveCriticalHitDamage(context);

        assertEquals(41, object.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS).getInteger("current"),
                "resolveCriticalHitDamage should deduct 11 ((4x2)+3) hit points from the knight to leave 41 (52-11=41)"
        );
    }

    @Test
    @DisplayName("resolveDamage deals damage")
    void resolveDamage_dealsDamage() throws Exception {
        RPGLObject object = RPGLFactory.newObject("demo:knight");
        RPGLItem item = UUIDTable.getItem(object.getJsonObject(RPGLObjectTO.EQUIPPED_ITEMS_ALIAS).getString("mainhand"));
        RPGLContext context = new RPGLContext();
        context.add(object);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "weapon": <RPGLItem UUID>,
                "attack_type": "melee",
                "damage": [
                    {
                        "type": "slashing",
                        "dice": [
                            { "size": 8, "determined": [ 4 ] }
                        ],
                        "bonus": 0
                    }
                ]
            }*/
            this.putString("weapon", item.getUuid());
            this.putString("attack_type", "melee");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("type", "slashing");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 8);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(4);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
        }});
        attackRoll.setSource(object);
        attackRoll.setTarget(object);

        attackRoll.resolveDamage(context);

        assertEquals(45, object.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS).getInteger("current"),
                "resolveCriticalHitDamage should deduct 7 (4+3) hit points from the knight to leave 45 (52-7=45)"
        );
    }

    @Test
    @DisplayName("getTargetArmorClass calculate 20 armor class")
    void getTargetArmorClass_calculatesTwentyArmorClass() throws Exception {
        RPGLObject object = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(object);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.setSource(object);
        attackRoll.setTarget(object);

        assertEquals(20, attackRoll.getTargetArmorClass(context),
                "target armor class should be 20 (plate armor + shield)"
        );
    }

    @Test
    @DisplayName("prepareItemWeaponAttack stores weapon damage and stores weapon UUID")
    void prepareItemWeaponAttack_storesWeaponDamageAndStoresWeaponUUID() throws Exception {
        RPGLObject object = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(object);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            this.putString("attack_type", "melee");
        }});
        attackRoll.setSource(object);
        attackRoll.setTarget(object);

        attackRoll.prepareItemWeaponAttack("mainhand", context);

        String expected = """
                [{"bonus":0,"dice":[{"determined":[4],"size":8}],"type":"slashing"}]""";
        assertEquals(expected, attackRoll.subeventJson.getJsonArray("damage").toString(),
                "weapon damage should be stored in the subevent following prepareItemWeaponAttack() call"
        );
        assertNotNull(UUIDTable.getItem(attackRoll.subeventJson.getString("weapon")),
                "weapon UUID should be present in UUIDTable"
        );
        // TODO assertion for attack bonus accounting for proficiency...
    }

    @Test
    @DisplayName("prepareNaturalWeaponAttack stores weapon damage and stores weapon UUID")
    void prepareNaturalWeaponAttack_storesWeaponDamageAndStoresWeaponUUID() throws Exception {
        RPGLObject object = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLContext context = new RPGLContext();
        context.add(object);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            this.putString("attack_type", "melee");
        }});
        attackRoll.setSource(object);
        attackRoll.setTarget(object);

        attackRoll.prepareNaturalWeaponAttack("demo:young_red_dragon_bite", context);

        String expected = """
                [{"bonus":0,"dice":[{"determined":[5],"size":10},{"determined":[5],"size":10}],"type":"piercing"},{"bonus":0,"dice":[{"determined":[3],"size":6}],"type":"fire"}]""";
        assertEquals(expected, attackRoll.subeventJson.getJsonArray("damage").toString(),
                "weapon damage should be stored in the subevent following prepareItemWeaponAttack() call"
        );
        assertNotNull(UUIDTable.getItem(attackRoll.subeventJson.getString("weapon")),
                "weapon UUID should be present in UUIDTable (it gets deleted at a different point in the code)"
        );
        assertEquals(10, attackRoll.getBonus(),
                "attack roll should have a bonus of 10 (proficiency bonus of 4 + str modifier of 6)"
        );
    }

    @Test
    @DisplayName("prepareAttackWithoutWeapon stores damage")
    void prepareAttackWithoutWeapon_storesDamage() throws Exception {
        RPGLObject object = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLContext context = new RPGLContext();
        context.add(object);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_type": "ranged",
                "attack_ability": "int",
                "damage": [
                    {
                        "type": "fire",
                        "dice": [
                            { "size": 10, "determined": [ 5 ] }
                        ],
                        "bonus": 0
                    }
                ]
            }*/
            this.putString("attack_type", "ranged");
            this.putString("attack_ability","int");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("type", "fire");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 10);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(5);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
        }});
        attackRoll.setSource(object);
        attackRoll.setTarget(object);

        attackRoll.prepareAttackWithoutWeapon(context);

        String expected = """
                [{"bonus":0,"dice":[{"determined":[5],"size":10}],"type":"fire"}]""";
        assertEquals(expected, attackRoll.subeventJson.getJsonArray("damage").toString(),
                "weapon damage should be stored in the subevent following prepareItemWeaponAttack() call"
        );
        assertEquals(6, attackRoll.getBonus(),
                "attack roll should have a bonus of 6 (proficiency of 4 + int modifier of 2)"
        );
    }

    @Test
    @DisplayName("prepare stores weapon damage and stores weapon UUID (item weapon)")
    void prepare_storesWeaponDamageAndStoresWeaponUUID_itemWeapon() throws Exception {
        RPGLObject object = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(object);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            this.putString("weapon", "mainhand");
            this.putString("attack_type", "melee");
        }});
        attackRoll.setSource(object);
        attackRoll.setTarget(object);

        attackRoll.prepare(context);

        String expected = """
                [{"bonus":0,"dice":[{"determined":[4],"size":8}],"type":"slashing"}]""";
        assertEquals(expected, attackRoll.subeventJson.getJsonArray("damage").toString(),
                "weapon damage should be stored in the subevent following prepare() call"
        );
        assertNotNull(UUIDTable.getItem(attackRoll.subeventJson.getString("weapon")),
                "weapon UUID should be present in UUIDTable"
        );
        // TODO assertion for attack bonus accounting for proficiency...
    }

    @Test
    @DisplayName("prepare stores weapon damage and stores weapon UUID (natural weapon)")
    void prepare_storesWeaponDamageAndStoresWeaponUUID_naturalWeapon() throws Exception {
        RPGLObject object = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLContext context = new RPGLContext();
        context.add(object);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            this.putString("weapon", "demo:young_red_dragon_bite");
            this.putString("attack_type", "melee");
        }});
        attackRoll.setSource(object);
        attackRoll.setTarget(object);

        attackRoll.prepare(context);

        String expected = """
                [{"bonus":0,"dice":[{"determined":[5],"size":10},{"determined":[5],"size":10}],"type":"piercing"},{"bonus":0,"dice":[{"determined":[3],"size":6}],"type":"fire"}]""";
        assertEquals(expected, attackRoll.subeventJson.getJsonArray("damage").toString(),
                "weapon damage should be stored in the subevent following prepare() call"
        );
        assertNotNull(UUIDTable.getItem(attackRoll.subeventJson.getString("weapon")),
                "weapon UUID should be present in UUIDTable (it gets deleted at a different point in the code)"
        );
        assertEquals(10, attackRoll.getBonus(),
                "attack roll should have a bonus of 10 (proficiency bonus of 4 + str modifier of 6)"
        );
    }

    @Test
    @DisplayName("prepare stores damage (no weapon)")
    void prepare_storesDamage_noWeapon() throws Exception {
        RPGLObject object = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLContext context = new RPGLContext();
        context.add(object);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_type": "ranged",
                "attack_ability": "int",
                "damage": [
                    {
                        "type": "fire",
                        "dice": [
                            { "size": 10, "determined": [ 5 ] }
                        ],
                        "bonus": 0
                    }
                ]
            }*/
            this.putString("attack_type", "ranged");
            this.putString("attack_ability","int");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("type", "fire");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 10);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(5);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
        }});
        attackRoll.setSource(object);
        attackRoll.setTarget(object);

        attackRoll.prepare(context);

        String expected = """
                [{"bonus":0,"dice":[{"determined":[5],"size":10}],"type":"fire"}]""";
        assertEquals(expected, attackRoll.subeventJson.getJsonArray("damage").toString(),
                "weapon damage should be stored in the subevent following prepare() call"
        );
        assertEquals(6, attackRoll.getBonus(),
                "attack roll should have a bonus of 6 (proficiency of 4 + int modifier of 2)"
        );
    }

    @Test
    @DisplayName("invoke stores weapon damage (hit)")
    void invoke_storesWeaponDamage_hit() throws Exception {
        RPGLObject object = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(object);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "attack_roll");
            this.putString("weapon", "mainhand");
            this.putString("attack_type", "melee");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(19);
            }});
        }});
        attackRoll.setSource(object);
        attackRoll.prepare(context);
        attackRoll.setTarget(object);
        attackRoll.invoke(context);

        assertEquals(45, object.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS).getInteger("current"),
                "target should take 7 damage after hit (52-7=45)"
        );
    }

    @Test
    @DisplayName("invoke stores weapon damage (critical hit)")
    void invoke_storesWeaponDamage_criticalHit() throws Exception {
        RPGLObject object = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(object);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "attack_roll");
            this.putString("weapon", "mainhand");
            this.putString("attack_type", "melee");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(20);
            }});
        }});
        attackRoll.setSource(object);
        attackRoll.prepare(context);
        attackRoll.setTarget(object);
        attackRoll.invoke(context);

        assertEquals(41, object.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS).getInteger("current"),
                "target should take 11 damage after hit (52-11=41)"
        );
    }

    @Test
    @DisplayName("invoke stores weapon damage (miss)")
    void invoke_storesWeaponDamage_miss() throws Exception {
        RPGLObject object = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(object);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "attack_roll");
            this.putString("weapon", "mainhand");
            this.putString("attack_type", "melee");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(1);
            }});
        }});
        attackRoll.setSource(object);
        attackRoll.prepare(context);
        attackRoll.setTarget(object);
        attackRoll.invoke(context);

        assertEquals(52, object.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS).getInteger("current"),
                "target should take 0 damage on a miss"
        );
    }

    @Test
    @DisplayName("invoke natural weapons do not persist after subevent")
    void invoke_naturalWeaponDoesNotPersist() throws Exception {
        RPGLObject object = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLContext context = new RPGLContext();
        context.add(object);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "attack_roll");
            this.putString("weapon", "demo:young_red_dragon_bite");
            this.putString("attack_type", "melee");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(10);
            }});
        }});
        attackRoll.setSource(object);
        attackRoll.prepare(context);
        attackRoll.setTarget(object);
        String itemUuid = attackRoll.subeventJson.getString("weapon");
        attackRoll.invoke(context);

        assertNull(UUIDTable.getItem(itemUuid),
                "natural weapon should not persist in UUIDTable after the subevent is invoked"
        );
    }

}
