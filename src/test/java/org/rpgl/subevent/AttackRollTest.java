package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
 * Testing class for the org.rpgl.subevent.AttackRoll class.
 *
 * @author Calvin Withun
 */
public class AttackRollTest {

    private AttackRoll attackRoll;

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

    @BeforeEach
    void beforeEach() {
        attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "bonuses": [ ],
                "minimum": {
                    "minimum_type": "number",
                    "value": Integer.MIN_VALUE
                }
            }*/
            this.putJsonArray("bonuses", new JsonArray());
            this.putJsonObject("minimum", new JsonObject() {{
                this.putString("minimum_type", "number");
                this.putInteger("value", Integer.MIN_VALUE);
            }});
        }});
    }

    @AfterEach
    void afterEach() {
        UUIDTable.clear();
        DummySubevent.resetCounter();
    }

    @Test
    @DisplayName("invoke wrong subevent")
    void invoke_wrongSubevent_throwsException() {
        Subevent subevent = new AttackRoll();
        subevent.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "not_a_subevent"
            }*/
            this.putString("subevent", "not_a_subevent");
        }});

        assertThrows(SubeventMismatchException.class,
                () -> subevent.invoke(new RPGLContext()),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("applyWeaponAttackBonus adds bonus to roll (longsword +1)")
    void applyWeaponAttackBonus_addsBonusToRoll_longswordPlusOne() {
        RPGLItem item = RPGLFactory.newItem("demo:longsword_plus_one");
        attackRoll.applyWeaponAttackBonus(item);

        assertEquals(1, attackRoll.getBonus(),
                "weapon bonus of 1 should be applied to the attack roll"
        );
    }

    @Test
    @DisplayName("isCriticalMiss returns true (base roll of 1)")
    void isCriticalMiss_returnsTrue_baseRollOne() {
        attackRoll.setBase(new JsonObject() {{
            this.putInteger("value", 1);
        }});

        assertTrue(attackRoll.isCriticalMiss(),
                "attack roll with base of 1 should register as a critical miss"
        );
    }

    @Test
    @DisplayName("isCriticalMiss returns false (base roll exceeding 1)")
    void isCriticalMiss_returnsFalse_baseRollExceedingOne() {
        attackRoll.setBase(new JsonObject() {{
            this.putInteger("value", 10);
        }});

        assertFalse(attackRoll.isCriticalMiss(),
                "attack roll with base exceeding 1 should not register as a critical miss"
        );
    }

    @Test
    @DisplayName("isCriticalHit returns true (base roll of 20)")
    void isCriticalHit_returnsTrue_baseRollTwenty() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.setBase(new JsonObject() {{
            this.putInteger("value", 20);
        }});

        assertTrue(attackRoll.isCriticalHit(context),
                "attack roll with base of 20 should register as a critical hit"
        );
    }

    @Test
    @DisplayName("isCriticalHit returns false (base roll below 20)")
    void isCriticalHit_returnsFalse_baseRollBelowTwenty() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.setBase(new JsonObject() {{
            this.putInteger("value", 10);
        }});

        assertFalse(attackRoll.isCriticalHit(context),
                "attack roll with base below 20 should not register as a critical hit"
        );
    }

    @Test
    @DisplayName("resolveNestedSubevents invoked DummySubevent (on hit)")
    void resolveNestedSubevents_invokesDummySubevent_onHit() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

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

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.resolveNestedSubevents("hit", new RPGLContext());

        assertEquals(1, DummySubevent.counter,
                "DummySubevent counter should increment by 1 from resolving nested subevents on hit"
        );
    }

    @Test
    @DisplayName("resolveNestedSubevents invoked DummySubevent (on miss)")
    void resolveNestedSubevents_invokesDummySubevent_onMiss() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

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

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.resolveNestedSubevents("miss", new RPGLContext());

        assertEquals(1, DummySubevent.counter,
                "DummySubevent counter should increment by 1 from resolving nested subevents on miss"
        );
    }

    @Test
    @DisplayName("deliverDamage object loses health")
    void deliverDamage_objectLosesHealth() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

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

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.deliverDamage(context);

        assertEquals(42, target.getHealthData().getInteger("current"),
                "Knight should have 42 health left (52-10=42)"
        );
    }

    @Test
    @DisplayName("getAttackDamage damage is rolled")
    void getAttackDamage_damageIsRolled() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        attackRoll.joinSubeventData(new JsonObject() {{
            this.putJsonArray("tags", new JsonArray());
        }});

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        JsonObject attackDamage = attackRoll.getAttackDamage(new JsonArray() {{
            /*[
                {
                    "damage_type": "fire",
                    "dice": [
                        { "size": 6, "determined": [ 3 ] },
                        { "size": 6, "determined": [ 3 ] },
                        { "size": 6, "determined": [ 3 ] }
                    ],
                    "bonus": 1
                },{
                    "damage_type": "cold",
                    "dice": [
                        { "size": 6, "determined": [ 3 ] },
                        { "size": 6, "determined": [ 3 ] },
                        { "size": 6, "determined": [ 3 ] }
                    ],
                    "bonus": 1
                }
            ]*/
            this.addJsonObject(new JsonObject() {{
                this.putString("damage_type", "fire");
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
                this.putString("damage_type", "cold");
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
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        DamageCollection damageCollection = new DamageCollection();
        damageCollection.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "damage_type": "fire",
                        "dice": [
                            { "size": 6, "determined": [ 3 ] }
                        ],
                        "bonus": 1
                    }
                ]
            }*/
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_type", "fire");
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

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        CriticalHitDamageCollection criticalHitDamageCollection = attackRoll.getCriticalHitDamageCollection(
                damageCollection,
                new DamageCollection(),
                context
        );

        String expected = """
                [{"bonus":1,"damage_type":"fire","dice":[{"determined":[3],"size":6},{"determined":[3],"size":6}]}]""";
        assertEquals(expected, criticalHitDamageCollection.getDamageCollection().toString(),
                "the number of dice should be doubled after calling getCriticalHitDamageCollection"
        );
    }

    @Test
    @DisplayName("getTargetDamageCollection returns empty array (default behavior)")
    void getTargetDamageCollection_returnsEmptyArray_default() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        attackRoll.joinSubeventData(new JsonObject() {{
            this.putJsonArray("tags", new JsonArray());
        }});

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        DamageCollection targetDamageCollection = attackRoll.getTargetDamageCollection(context);

        assertEquals("[]", targetDamageCollection.getDamageCollection().toString(),
                "getTargetDamageCollection should return empty array by default"
        );
    }

    @Test
    @DisplayName("getBaseDamageCollection calculates base damage collection")
    void getBaseDamageCollection_calculatesBaseDamageCollection() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        RPGLItem item = UUIDTable.getItem(source.getEquippedItems().getString("mainhand"));
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "weapon": <RPGLItem UUID>,
                "attack_type": "melee",
                "damage": [
                    {
                        "damage_type": "slashing",
                        "dice": [
                            { "size": 8, "determined": [ 4 ] }
                        ],
                        "bonus": 0
                    }
                ],
                "tags": [ ]
            }*/
            this.putString("weapon", item.getUuid());
            this.putString("attack_type", "melee");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_type", "slashing");
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
            this.putJsonArray("tags", new JsonArray());
        }});

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        DamageCollection baseDamageCollection = attackRoll.getBaseDamageCollection(context);

        String expected = """
                [{"bonus":3,"damage_type":"slashing","dice":[{"determined":[4],"size":8}]}]""";
        assertEquals(expected, baseDamageCollection.getDamageCollection().toString(),
                "base damage should be collected properly including ability score modifier damage bonus"
        );
    }

    @Test
    @DisplayName("resolveCriticalHitDamage deals critical damage")
    void resolveCriticalHitDamage_dealsCriticalDamage() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        RPGLItem item = UUIDTable.getItem(source.getEquippedItems().getString("mainhand"));
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "weapon": <RPGLItem UUID>,
                "attack_type": "melee",
                "damage": [
                    {
                        "damage_type": "slashing",
                        "dice": [
                            { "size": 8, "determined": [ 4 ] }
                        ],
                        "bonus": 0
                    }
                ],
                "tags": [ ]
            }*/
            this.putString("weapon", item.getUuid());
            this.putString("attack_type", "melee");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_type", "slashing");
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
            this.putJsonArray("tags", new JsonArray());
        }});

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.resolveCriticalHitDamage(context);

        assertEquals(41, target.getHealthData().getInteger("current"),
                "resolveCriticalHitDamage should deduct 11 ((4x2)+3) hit points from the knight to leave 41 (52-11=41)"
        );
    }

    @Test
    @DisplayName("resolveDamage deals damage")
    void resolveDamage_dealsDamage() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        RPGLItem item = UUIDTable.getItem(source.getEquippedItems().getString("mainhand"));
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "weapon": <RPGLItem UUID>,
                "attack_type": "melee",
                "damage": [
                    {
                        "damage_type": "slashing",
                        "dice": [
                            { "size": 8, "determined": [ 4 ] }
                        ],
                        "bonus": 0
                    }
                ],
                "tags": [ ]
            }*/
            this.putString("weapon", item.getUuid());
            this.putString("attack_type", "melee");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_type", "slashing");
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
            this.putJsonArray("tags", new JsonArray());
        }});

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.resolveDamage(context);

        assertEquals(45, target.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS).getInteger("current"),
                "resolveCriticalHitDamage should deduct 7 (4+3) hit points from the knight to leave 45 (52-7=45)"
        );
    }

    @Test
    @DisplayName("getTargetArmorClass calculate 20 armor class")
    void getTargetArmorClass_calculatesTwentyArmorClass() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        attackRoll.setSource(source);
        attackRoll.setTarget(target);

        assertEquals(20, attackRoll.getTargetArmorClass(context),
                "target armor class should be 20 (plate armor + shield)"
        );
    }

    @Test
    @DisplayName("prepareItemWeaponAttack stores weapon damage and stores weapon UUID")
    void prepareItemWeaponAttack_storesWeaponDamageAndStoresWeaponUUID() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        attackRoll.joinSubeventData(new JsonObject() {{
            this.putString("attack_type", "melee");
        }});

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.prepareItemWeaponAttack("mainhand", context);

        String expected = """
                [{"bonus":0,"damage_type":"slashing","dice":[{"determined":[4],"size":8}]}]""";
        assertEquals(expected, attackRoll.json.getJsonArray("damage").toString(),
                "weapon damage should be stored in the subevent following prepareItemWeaponAttack() call"
        );
        assertNotNull(UUIDTable.getItem(attackRoll.json.getString("weapon")),
                "weapon UUID should be present in UUIDTable"
        );
        // TODO assertion for attack bonus accounting for proficiency...
    }

    @Test
    @DisplayName("prepareNaturalWeaponAttack stores weapon damage and stores weapon UUID")
    void prepareNaturalWeaponAttack_storesWeaponDamageAndStoresWeaponUUID() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        attackRoll.joinSubeventData(new JsonObject() {{
            this.putString("attack_type", "melee");
        }});

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.prepareNaturalWeaponAttack("demo:young_red_dragon_bite", context);

        String expected = """
                [{"bonus":0,"damage_type":"piercing","dice":[{"determined":[5],"size":10},{"determined":[5],"size":10}]},{"bonus":0,"damage_type":"fire","dice":[{"determined":[3],"size":6}]}]""";
        assertEquals(expected, attackRoll.json.getJsonArray("damage").toString(),
                "weapon damage should be stored in the subevent following prepareNaturalWeaponAttack() call"
        );
        assertNotNull(UUIDTable.getItem(attackRoll.json.getString("weapon")),
                "weapon UUID should be present in UUIDTable (it gets deleted at a different point in the code)"
        );
        assertEquals(6, attackRoll.getBonus(),
                "attack roll should have a bonus of 6 (str modifier of 6)"
        );
    }

    @Test
    @DisplayName("prepareAttackWithoutWeapon stores damage")
    void prepareAttackWithoutWeapon_storesDamage() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_type": "ranged",
                "attack_ability": "int",
                "damage": [
                    {
                        "damage_type": "fire",
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
                    this.putString("damage_type", "fire");
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

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.prepareAttackWithoutWeapon(context);

        String expected = """
                [{"bonus":0,"damage_type":"fire","dice":[{"determined":[5],"size":10}]}]""";
        assertEquals(expected, attackRoll.json.getJsonArray("damage").toString(),
                "weapon damage should be stored in the subevent following prepareItemWeaponAttack() call"
        );
        assertEquals(2, attackRoll.getBonus(),
                "attack roll should have a bonus of 2 (int modifier of 2)"
        );
    }

    @Test
    @DisplayName("prepare stores weapon damage and stores weapon UUID (item weapon)")
    void prepare_storesWeaponDamageAndStoresWeaponUUID_itemWeapon() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        attackRoll.joinSubeventData(new JsonObject() {{
            this.putString("weapon", "mainhand");
            this.putString("attack_type", "melee");
        }});

        attackRoll.setSource(source);
        attackRoll.prepare(context);

        String expected = """
                [{"bonus":0,"damage_type":"slashing","dice":[{"determined":[4],"size":8}]}]""";
        assertEquals(expected, attackRoll.json.getJsonArray("damage").toString(),
                "weapon damage should be stored in the subevent following prepare() call"
        );
        assertNotNull(UUIDTable.getItem(attackRoll.json.getString("weapon")),
                "weapon UUID should be present in UUIDTable"
        );
        // TODO assertion for attack bonus accounting for proficiency...
    }

    @Test
    @DisplayName("prepare stores weapon damage and stores weapon UUID (natural weapon)")
    void prepare_storesWeaponDamageAndStoresWeaponUUID_naturalWeapon() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        attackRoll.joinSubeventData(new JsonObject() {{
            this.putString("weapon", "demo:young_red_dragon_bite");
            this.putString("attack_type", "melee");
        }});

        attackRoll.setSource(source);
        attackRoll.prepare(context);

        String expected = """
                [{"bonus":0,"damage_type":"piercing","dice":[{"determined":[5],"size":10},{"determined":[5],"size":10}]},{"bonus":0,"damage_type":"fire","dice":[{"determined":[3],"size":6}]}]""";
        assertEquals(expected, attackRoll.json.getJsonArray("damage").toString(),
                "weapon damage should be stored in the subevent following prepare() call"
        );
        assertNotNull(UUIDTable.getItem(attackRoll.json.getString("weapon")),
                "weapon UUID should be present in UUIDTable (it gets deleted at a different point in the code)"
        );
        assertEquals(6, attackRoll.getBonus(),
                "attack roll should have a bonus of 6 (str modifier of 6)"
        );
    }

    @Test
    @DisplayName("prepare stores damage (no weapon)")
    void prepare_storesDamage_noWeapon() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_type": "ranged",
                "attack_ability": "int",
                "damage": [
                    {
                        "damage_type": "fire",
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
                    this.putString("damage_type", "fire");
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

        attackRoll.setSource(source);
        attackRoll.prepare(context);

        String expected = """
                [{"bonus":0,"damage_type":"fire","dice":[{"determined":[5],"size":10}]}]""";
        assertEquals(expected, attackRoll.json.getJsonArray("damage").toString(),
                "weapon damage should be stored in the subevent following prepare() call"
        );
        assertEquals(2, attackRoll.getBonus(),
                "attack roll should have a bonus of 2 (int modifier of 2)"
        );
    }

    @Test
    @DisplayName("invoke stores weapon damage (hit)")
    void invoke_storesWeaponDamage_hit() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        attackRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "attack_roll");
            this.putString("weapon", "mainhand");
            this.putString("attack_type", "melee");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(19);
            }});
        }});

        attackRoll.setSource(source);
        attackRoll.prepare(context);
        attackRoll.setTarget(target);
        attackRoll.invoke(context);

        assertEquals(45, target.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS).getInteger("current"),
                "target should take 7 damage after hit (52-7=45)"
        );
    }

    @Test
    @DisplayName("invoke stores weapon damage (critical hit)")
    void invoke_storesWeaponDamage_criticalHit() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        attackRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "attack_roll");
            this.putString("weapon", "mainhand");
            this.putString("attack_type", "melee");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(20);
            }});
        }});
        attackRoll.setSource(source);
        attackRoll.prepare(context);
        attackRoll.setTarget(target);
        attackRoll.invoke(context);

        assertEquals(41, target.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS).getInteger("current"),
                "target should take 11 damage after hit (52-11=41)"
        );
    }

    @Test
    @DisplayName("invoke stores weapon damage (miss)")
    void invoke_storesWeaponDamage_miss() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        attackRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "attack_roll");
            this.putString("weapon", "mainhand");
            this.putString("attack_type", "melee");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(1);
            }});
        }});

        attackRoll.setSource(source);
        attackRoll.prepare(context);
        attackRoll.setTarget(target);
        attackRoll.invoke(context);

        assertEquals(52, target.getJsonObject(RPGLObjectTO.HEALTH_DATA_ALIAS).getInteger("current"),
                "target should take 0 damage on a miss"
        );
    }

    @Test
    @DisplayName("invoke natural weapons do not persist after subevent")
    void invoke_naturalWeaponDoesNotPersist() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        attackRoll.joinSubeventData(new JsonObject() {{
            this.putString("subevent", "attack_roll");
            this.putString("weapon", "demo:young_red_dragon_bite");
            this.putString("attack_type", "melee");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(10);
            }});
        }});

        attackRoll.setSource(source);
        attackRoll.prepare(context);
        attackRoll.setTarget(target);
        String itemUuid = attackRoll.json.getString("weapon");
        attackRoll.invoke(context);

        assertNull(UUIDTable.getItem(itemUuid),
                "natural weapon should not persist in UUIDTable after the subevent is invoked"
        );
    }

    @Test
    @DisplayName("prepare adds correct tags to attack roll (item weapon attack)")
    void prepare_addsCorrectTagsToAttackRoll_itemWeaponAttack() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "attack_roll",
                "weapon": "mainhand",
                "attack_type": "melee",
            }*/
            this.putString("subevent", "attack_roll");
            this.putString("weapon", "mainhand");
            this.putString("attack_type", "melee");
        }});

        attackRoll.setSource(source);
        attackRoll.prepare(context);

        String expected = """
                ["humanoid","attack_roll","melee","metal","weapon"]""";
        assertEquals(expected, attackRoll.json.getJsonArray("tags").toString(),
                "object tag (humanoid), subevent tag (attack_roll), and item weapon tags (metal, weapon) should all be present"
        );
    }

    @Test
    @DisplayName("prepare adds correct tags to attack roll (natural weapon attack)")
    void prepare_addsCorrectTagsToAttackRoll_naturalWeaponAttack() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "attack_roll",
                "weapon": "demo:young_red_dragon_claw",
                "attack_type": "melee",
            }*/
            this.putString("subevent", "attack_roll");
            this.putString("weapon", "demo:young_red_dragon_claw");
            this.putString("attack_type", "melee");
        }});

        attackRoll.setSource(source);
        attackRoll.prepare(context);

        String expected = """
                ["dragon","attack_roll","melee","claw"]""";
        assertEquals(expected, attackRoll.json.getJsonArray("tags").toString(),
                "object tag (dragon), subevent tag (attack_roll), and natural weapon tag (claw) should all be present"
        );
    }

}
