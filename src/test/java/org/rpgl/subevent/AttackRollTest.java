package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.subevent.AttackRoll class.
 *
 * @author Calvin Withun
 */
public class AttackRollTest {

    @BeforeAll
    static void beforeAll() {
        DatapackLoader.loadDatapacks(
                new File("src/test/resources/datapacks".replace("/", File.separator))
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
    @DisplayName("errors on wrong subevent")
    void errorsOnWrongSubevent() {
        Subevent subevent = new AttackRoll();
        subevent.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "not_a_subevent"
            }*/
            this.putString("subevent", "not_a_subevent");
        }});

        assertThrows(SubeventMismatchException.class,
                () -> subevent.invoke(new DummyContext()),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("prepares tags")
    void preparesTags() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_ability": "str",
                "attack_type": "melee"
            }*/
            this.putString("attack_ability", "str");
            this.putString("attack_type", "melee");
        }});

        attackRoll.setSource(object);
        attackRoll.prepare(new DummyContext());

        assertTrue(attackRoll.getTags().asList().containsAll(List.of("str", "melee")),
                "should have tags for attack ability and attack type"
        );
    }

    @Test
    @DisplayName("recognizes critical misses")
    void recognizesCriticalMisses() {
        AttackRoll attackRoll = new AttackRoll();

        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "base": {
                    "value": 1
                }
            }*/
            this.putJsonObject("base", new JsonObject() {{
                this.putInteger("value", 1);
            }});
        }});
        assertTrue(attackRoll.isCriticalMiss(),
                "attack roll with base of 1 should register as a critical miss"
        );

        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "base": {
                    "value": 2
                }
            }*/
            this.putJsonObject("base", new JsonObject() {{
                this.putInteger("value", 2);
            }});
        }});
        assertFalse(attackRoll.isCriticalMiss(),
                "attack roll with base of 2 should not register as a critical miss"
        );
    }

    @Test
    @DisplayName("calculates critical hit threshold")
    void calculatesCriticalHitThreshold() throws Exception {
        AttackRoll attackRoll = new AttackRoll();
        attackRoll.calculateCriticalHitThreshold(new DummyContext());
        assertEquals(20, attackRoll.getCriticalHitThreshold(),
                "critical hit threshold should default to 20"
        );
    }

    @Test
    @DisplayName("invokes hit subevents")
    void invokesHitSubevents() throws Exception {
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

        attackRoll.resolveNestedSubevents("hit", new DummyContext());

        assertEquals(1, DummySubevent.counter,
                "DummySubevent counter should increment by 1 from resolving nested subevents on hit"
        );
    }

    @Test
    @DisplayName("invokes miss subevents")
    void invokesMissSubevents() throws Exception {
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

        attackRoll.resolveNestedSubevents("miss", new DummyContext());

        assertEquals(1, DummySubevent.counter,
                "DummySubevent counter should increment by 1 from resolving nested subevents on miss"
        );
    }

    @Test
    @DisplayName("delivers damage")
    void deliversDamage() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "damage_type": "fire",
                        "dice": [
                            { "roll": 5 }
                        ],
                        "bonus": 5,
                        "scale": {
                            "numerator": 1,
                            "denominator": 1,
                            "round_up": false
                        }
                    }
                ]
            }*/
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_type", "fire");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("roll", 5);
                        }});
                    }});
                    this.putInteger("bonus", 5);
                    this.putJsonObject("scale", new JsonObject() {{
                        this.putInteger("numerator", 1);
                        this.putInteger("denominator", 1);
                        this.putBoolean("round_up", false);
                    }});
                }});
            }});
        }});

        attackRoll.setSource(object);
        attackRoll.setTarget(object);
        attackRoll.deliverDamage(new DummyContext());

        assertEquals(1000 /*base*/ -10 /*damage*/, object.getHealthData().getInteger("current"),
                "target should lose 10 health"
        );
    }

    @Test
    @DisplayName("calculates target armor class")
    void calculatesTargetArmorClass() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/knight", TestUtils.TEST_USER);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.calculateTargetArmorClass(new DummyContext());

        assertEquals(18 /*plate armor*/ +2 /*shield*/, attackRoll.getTargetArmorClass(),
                "target armor class should total to 20"
        );
    }

    @Test
    @DisplayName("gets critical hit damage")
    void getsCriticalHitDamage() throws Exception {
        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_type": "melee",
                "attack_ability": "str",
                "damage": [
                    {
                        "formula": "range",
                        "damage_type": "slashing",
                        "dice": [
                            { "count": 2, "size": 6, "determined": [ 3 ] }
                        ],
                        "bonus": 0
                    },
                    {
                        "formula": "range",
                        "damage_type": "fire",
                        "dice": [
                            { "count": 2, "size": 6, "determined": [ 3 ] }
                        ],
                        "bonus": 0
                    }
                ]
            }*/
            this.putString("attack_type", "melee");
            this.putString("attack_ability", "str");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putString("damage_type", "slashing");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("count", 2);
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(3);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putString("damage_type", "fire");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("count", 2);
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(3);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
        }});

        attackRoll.getCriticalHitDamage(new DummyContext());

        String expected = """
                [{"bonus":0,"damage_type":"slashing","dice":[{"count":2,"determined":[3],"size":6},{"count":2,"determined":[3],"size":6}],"formula":"range"},{"bonus":0,"damage_type":"fire","dice":[{"count":2,"determined":[3],"size":6},{"count":2,"determined":[3],"size":6}],"formula":"range"}]""";
        assertEquals(expected, attackRoll.json.getJsonArray("damage").toString(),
                "critical hit damage should double the number of dice"
        );
    }

    @Test
    @DisplayName("gets base damage with damage modifier")
    void getsBaseDamageWithDamageModifier() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        object.getAbilityScores().putInteger("str", 20);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_type": "melee",
                "attack_ability": "str",
                "damage": [
                    {
                        "formula": "range",
                        "damage_type": "slashing",
                        "dice": [
                            { "count": 2, "size": 6, "determined": [ 3 ] }
                        ],
                        "bonus": 0
                    }
                ],
                "withhold_damage_modifier": false
            }*/
            this.putString("attack_type", "melee");
            this.putString("attack_ability", "str");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putString("damage_type", "slashing");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("count", 2);
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(3);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
            this.putBoolean("withhold_damage_modifier", false);
        }});

        attackRoll.setSource(object);
        attackRoll.setTarget(object);
        attackRoll.getBaseDamage(new DummyContext());

        String expected = """
                [{"bonus":0,"damage_type":"slashing","dice":[{"determined":[3],"size":6},{"determined":[3],"size":6}],"scale":{"denominator":1,"numerator":1,"round_up":false}},{"bonus":5,"damage_type":"slashing","dice":[],"scale":{"denominator":1,"numerator":1,"round_up":false}}]""";
        assertEquals(expected, attackRoll.json.getJsonArray("damage").toString(),
                "base damage should be calculated including ability modifier bonus"
        );
    }

    @Test
    @DisplayName("gets base damage without damage modifier")
    void getsBaseDamageWithoutDamageModifier() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_type": "melee",
                "attack_ability": "str",
                "damage": [
                    {
                        "formula": "range",
                        "damage_type": "slashing",
                        "dice": [
                            { "count": 2, "size": 6, "determined": [ 3 ] }
                        ],
                        "bonus": 0
                    }
                ],
                "withhold_damage_modifier": true
            }*/
            this.putString("attack_type", "melee");
            this.putString("attack_ability", "str");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putString("damage_type", "slashing");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("count", 2);
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(3);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
            this.putBoolean("withhold_damage_modifier", true);
        }});

        attackRoll.setSource(object);
        attackRoll.setTarget(object);
        attackRoll.getBaseDamage(new DummyContext());

        String expected = """
                [{"bonus":0,"damage_type":"slashing","dice":[{"determined":[3],"size":6},{"determined":[3],"size":6}],"scale":{"denominator":1,"numerator":1,"round_up":false}}]""";
        assertEquals(expected, attackRoll.json.getJsonArray("damage").toString(),
                "base damage should be calculated not including ability modifier bonus"
        );
    }

    @Test
    @DisplayName("gets base damage with origin item bonus")
    void getsBaseDamageWithOriginItemBonus() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        RPGLItem item = RPGLFactory.newItem("std:weapon/melee/martial/longsword");
        item.setDamageBonus(1);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_type": "melee",
                "attack_ability": "str",
                "damage": [
                    {
                        "formula": "range",
                        "damage_type": "slashing",
                        "dice": [
                            { "count": 2, "size": 6, "determined": [ 3 ] }
                        ],
                        "bonus": 0
                    }
                ],
                "withhold_damage_modifier": true
            }*/
            this.putString("attack_type", "melee");
            this.putString("attack_ability", "str");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putString("damage_type", "slashing");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("count", 2);
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(3);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
            this.putBoolean("withhold_damage_modifier", true);
        }});

        attackRoll.setOriginItem(item.getUuid());
        attackRoll.setSource(object);
        attackRoll.setTarget(object);
        attackRoll.getBaseDamage(new DummyContext());

        String expected = """
                [{"bonus":0,"damage_type":"slashing","dice":[{"determined":[3],"size":6},{"determined":[3],"size":6}],"scale":{"denominator":1,"numerator":1,"round_up":false}},{"bonus":1,"damage_type":"slashing","dice":[],"scale":{"denominator":1,"numerator":1,"round_up":false}}]""";
        assertEquals(expected, attackRoll.json.getJsonArray("damage").toString(),
                "base damage should be calculated including item damage bonus"
        );
    }

    @Test
    @DisplayName("gets base damage using origin object stats")
    void getsBaseDamageUsingOriginObjectStats() throws Exception {
        RPGLObject originObject = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        originObject.getAbilityScores().putInteger("int", 20);
        object.setOriginObject(originObject.getUuid());

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_type": "melee",
                "attack_ability": "int",
                "use_origin_attack_ability": true,
                "damage": [
                    {
                        "formula": "range",
                        "damage_type": "slashing",
                        "dice": [
                            { "count": 2, "size": 6, "determined": [ 3 ] }
                        ],
                        "bonus": 0
                    }
                ],
                "withhold_damage_modifier": false
            }*/
            this.putString("attack_type", "melee");
            this.putString("attack_ability", "int");
            this.putBoolean("use_origin_attack_ability", true);
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putString("damage_type", "slashing");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("count", 2);
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(3);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
            this.putBoolean("withhold_damage_modifier", false);
        }});

        attackRoll.setSource(object);
        attackRoll.prepare(new DummyContext());
        attackRoll.setTarget(object);
        attackRoll.getBaseDamage(new DummyContext());

        String expected = """
                [{"bonus":0,"damage_type":"slashing","dice":[{"determined":[3],"size":6},{"determined":[3],"size":6}],"scale":{"denominator":1,"numerator":1,"round_up":false}},{"bonus":5,"damage_type":"slashing","dice":[],"scale":{"denominator":1,"numerator":1,"round_up":false}}]""";
        assertEquals(expected, attackRoll.json.getJsonArray("damage").toString(),
                "base damage should be calculated including ability modifier bonus"
        );
    }

    @Test
    @DisplayName("adds origin item attack bonus")
    void addsOriginItemAttackBonus() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        RPGLItem item = RPGLFactory.newItem("std:weapon/melee/martial/longsword");
        item.setAttackBonus(1);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_type": "melee",
                "attack_ability": "str"
            }*/
            this.putString("attack_type", "melee");
            this.putString("attack_ability", "str");
        }});

        attackRoll.setOriginItem(item.getUuid());
        attackRoll.setSource(source);
        attackRoll.prepare(new DummyContext());

        assertEquals(1, attackRoll.getBonus(),
                "attack roll should include bonus from weapon attack bonus"
        );
    }

    @Test
    @DisplayName("rolls and delivers damage")
    void rollsAndDeliversDamage() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_type": "melee",
                "attack_ability": "str",
                "damage": [
                    {
                        "formula": "range",
                        "damage_type": "slashing",
                        "dice": [
                            { "size": 6, "determined": [ 3 ] },
                            { "size": 6, "determined": [ 3 ] }
                        ],
                        "bonus": 0,
                        "scale": {
                            "numerator": 1,
                            "denominator": 1,
                            "round_up": false
                        }
                    }
                ]
            }*/
            this.putString("attack_type", "melee");
            this.putString("attack_ability", "str");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_type", "slashing");
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
                    }});
                    this.putInteger("bonus", 0);
                    this.putJsonObject("scale", new JsonObject() {{
                        this.putInteger("numerator", 1);
                        this.putInteger("denominator", 1);
                        this.putBoolean("round_up", false);
                    }});
                }});
            }});
        }});

        attackRoll.setSource(object);
        attackRoll.setTarget(object);
        attackRoll.resolveDamage(new DummyContext());

        assertEquals(1000 /*base*/ -6 /*damage*/, object.getHealthData().getInteger("current"),
                "dummy should take 6 damage"
        );
    }
    
    @Test
    @DisplayName("critically misses")
    void criticallyMisses() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        source.getAbilityScores().putInteger("str", 100); // should land a hit without critical miss rule

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_ability": "str",
                "damage": [
                    {
                        "formula": "range",
                        "damage_type": "fire",
                        "dice": [ ],
                        "bonus": 10
                    }
                ],
                "has_advantage": false,
                "has_disadvantage": false,
                "bonuses": [ ],
                "determined": [ 1 ],
                "canceled": false,
                "use_origin_attack_ability": false
            }*/
            this.putString("attack_ability", "str");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putString("damage_type", "fire");
                    this.putJsonArray("dice", new JsonArray());
                    this.putInteger("bonus", 10);
                }});
            }});
            this.putBoolean("has_advantage", false);
            this.putBoolean("has_disadvantage", false);
            this.putJsonArray("bonuses", new JsonArray());
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(1);
            }});
            this.putBoolean("canceled", false);
            this.putBoolean("use_origin_attack_ability", false);
        }});

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.run(new DummyContext());

        assertEquals(1000, target.getHealthData().getInteger("current"),
                "target should not have been hit"
        );
    }

    @Test
    @DisplayName("misses")
    void misses() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        target.getAbilityScores().putInteger("dex", 100); // should be able to avoid a hit normally

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_ability": "str",
                "damage": [
                    {
                        "formula": "range",
                        "damage_type": "fire",
                        "dice": [ ],
                        "bonus": 10
                    }
                ],
                "has_advantage": false,
                "has_disadvantage": false,
                "bonuses": [ ],
                "minimum": {
                    "value": Integer.MIN_VALUE
                },
                "determined": [ 2 ],
                "canceled": false,
                "use_origin_attack_ability": false
            }*/
            this.putString("attack_ability", "str");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putString("damage_type", "fire");
                    this.putJsonArray("dice", new JsonArray());
                    this.putInteger("bonus", 10);
                }});
            }});
            this.putBoolean("has_advantage", false);
            this.putBoolean("has_disadvantage", false);
            this.putJsonArray("bonuses", new JsonArray());
            this.putJsonObject("minimum", new JsonObject() {{
                this.putInteger("value", Integer.MIN_VALUE);
            }});
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(2);
            }});
            this.putBoolean("canceled", false);
            this.putBoolean("use_origin_attack_ability", false);
        }});

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.run(new DummyContext());

        assertEquals(1000, target.getHealthData().getInteger("current"),
                "target should not have been hit"
        );
    }

    @Test
    @DisplayName("hits")
    void hits() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_ability": "str",
                "damage": [
                    {
                        "formula": "range",
                        "damage_type": "fire",
                        "dice": [
                            { "size": 6, "determined": [ 3 ] }
                        ],
                        "bonus": 0
                    }
                ],
                "has_advantage": false,
                "has_disadvantage": false,
                "bonuses": [ ],
                "minimum": {
                    "value": Integer.MIN_VALUE
                },
                "determined": [ 19 ],
                "withhold_damage_modifier": true,
                "canceled": false,
                "use_origin_attack_ability": false
            }*/
            this.putString("attack_ability", "str");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putString("damage_type", "fire");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(3);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
            this.putBoolean("has_advantage", false);
            this.putBoolean("has_disadvantage", false);
            this.putJsonArray("bonuses", new JsonArray());
            this.putJsonObject("minimum", new JsonObject() {{
                this.putInteger("value", Integer.MIN_VALUE);
            }});
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(19);
            }});
            this.putBoolean("withhold_damage_modifier", true);
            this.putBoolean("canceled", false);
            this.putBoolean("use_origin_attack_ability", false);
        }});

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.run(new DummyContext());

        assertEquals(1000 /*base*/ -3 /*damage*/, target.getHealthData().getInteger("current"),
                "target should have been hit and taken damage"
        );
    }

    @Test
    @DisplayName("critically hits")
    void criticallyHits() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/knight", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        target.getAbilityScores().putInteger("dex", 100);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_ability": "str",
                "damage": [
                    {
                        "formula": "range",
                        "damage_type": "fire",
                        "dice": [
                            { "size": 6, "determined": [ 3 ] }
                        ],
                        "bonus": 0
                    }
                ],
                "has_advantage": false,
                "has_disadvantage": false,
                "bonuses": [ ],
                "minimum": {
                    "value": Integer.MIN_VALUE
                },
                "determined": [ 20 ],
                "withhold_damage_modifier": true,
                "canceled": false,
                "use_origin_attack_ability": false
            }*/
            this.putString("attack_ability", "str");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putString("damage_type", "fire");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(3);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
            this.putBoolean("has_advantage", false);
            this.putBoolean("has_disadvantage", false);
            this.putJsonArray("bonuses", new JsonArray());
            this.putJsonObject("minimum", new JsonObject() {{
                this.putInteger("value", Integer.MIN_VALUE);
            }});
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(20);
            }});
            this.putBoolean("withhold_damage_modifier", true);
            this.putBoolean("canceled", false);
            this.putBoolean("use_origin_attack_ability", false);
        }});

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.run(context);

        assertEquals(1000 /*base*/ -3 /*damage*/ -3 /*critical bonus*/, target.getHealthData().getInteger("current"),
                "target should have been hit and taken critical damage"
        );
    }

    @Test
    @DisplayName("heals source via vampirism (all damage types")
    void healsSourceViaVampirism_allDamageTypes() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        source.getHealthData().putInteger("current", 1);
        target.getHealthData().putInteger("current", 11);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "damage_type": "necrotic",
                        "dice": [ ],
                        "bonus": 10,
                        "scale": {
                            "numerator": 1,
                            "denominator": 1,
                            "round_up": false
                        }
                    }
                ],
                "vampirism": {
                    "damage_type": "necrotic"
                }
            }*/
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_type", "necrotic");
                    this.putJsonArray("dice", new JsonArray());
                    this.putInteger("bonus", 10);
                    this.putJsonObject("scale", new JsonObject() {{
                        this.putInteger("numerator", 1);
                        this.putInteger("denominator", 1);
                        this.putBoolean("round_up", false);
                    }});
                }});
            }});
            this.putJsonObject("vampirism", new JsonObject() {{
                this.putString("damage_type", "necrotic");
            }});
        }});

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.deliverDamage(new DummyContext());

        assertEquals(11 /*base*/ -10 /*damage*/, target.getHealthData().getInteger("current"),
                "target should suffer 10 damage and receive no healing from vampirism"
        );
        assertEquals(1 /*base*/ +5 /*healing*/, source.getHealthData().getInteger("current"),
                "source should heal for half damage from vampirism"
        );
    }

    @Test
    @DisplayName("confirms critical damage")
    void confirmsCriticalDamage() throws Exception {
        assertTrue(new AttackRoll().confirmCriticalDamage(new DummyContext()),
                "critical damage should be confirmed"
        );
    }

    @Test
    @DisplayName("cancels critical damage")
    void cancelsCriticalDamage() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        RPGLEffect adamantineArmorEffect = RPGLFactory.newEffect("std:item/armor/adamantine");
        adamantineArmorEffect.setSource(target);
        adamantineArmorEffect.setTarget(target);
        target.addEffect(adamantineArmorEffect);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.setSource(source);
        attackRoll.setTarget(target);

        assertFalse(attackRoll.confirmCriticalDamage(context),
                "critical damage should not be confirmed"
        );
    }

    @Test
    @DisplayName("cancels critical damage on hit")
    void cancelsCriticalDamageOnHit() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        RPGLEffect adamantineArmorEffect = RPGLFactory.newEffect("std:item/armor/adamantine");
        adamantineArmorEffect.setSource(target);
        adamantineArmorEffect.setTarget(target);
        target.addEffect(adamantineArmorEffect);

        target.getAbilityScores().putInteger("dex", 100);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_ability": "str",
                "damage": [
                    {
                        "formula": "range",
                        "damage_type": "fire",
                        "dice": [
                            { "size": 6, "determined": [ 3 ] }
                        ],
                        "bonus": 0
                    }
                ],
                "has_advantage": false,
                "has_disadvantage": false,
                "bonuses": [ ],
                "minimum": {
                    "value": Integer.MIN_VALUE
                },
                "determined": [ 20 ],
                "withhold_damage_modifier": true,
                "canceled": false,
                "use_origin_attack_ability": false
            }*/
            this.putString("attack_ability", "str");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putString("damage_type", "fire");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(3);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
            this.putBoolean("has_advantage", false);
            this.putBoolean("has_disadvantage", false);
            this.putJsonArray("bonuses", new JsonArray());
            this.putJsonObject("minimum", new JsonObject() {{
                this.putInteger("value", Integer.MIN_VALUE);
            }});
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(20);
            }});
            this.putBoolean("withhold_damage_modifier", true);
            this.putBoolean("canceled", false);
            this.putBoolean("use_origin_attack_ability", false);
        }});

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.run(context);

        assertEquals(1000 /*base*/ -3 /*damage*/, target.getHealthData().getInteger("current"),
                "target should have been hit but should not have suffered critical damage"
        );
    }

}
