package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

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
    @DisplayName("isCriticalMiss returns true (base roll of 1)")
    void isCriticalMiss_returnsTrue_baseRollOne() {
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
    }

    @Test
    @DisplayName("isCriticalMiss returns false (base roll exceeding 1)")
    void isCriticalMiss_returnsFalse_baseRollExceedingOne() {
        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "base": {
                    "value": 10
                }
            }*/
            this.putJsonObject("base", new JsonObject() {{
                this.putInteger("value", 10);
            }});
        }});

        assertFalse(attackRoll.isCriticalMiss(),
                "attack roll with base exceeding 1 should not register as a critical miss"
        );
    }

    @Test
    @DisplayName("isCriticalHit returns true (base roll of 20)")
    void isCriticalHit_returnsTrue_baseRollTwenty() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:knight");
        RPGLObject target = RPGLFactory.newObject("std:knight");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "base": {
                    "value": 20
                }
            }*/
            this.putJsonObject("base", new JsonObject() {{
                this.putInteger("value", 20);
            }});
        }});

        attackRoll.setSource(source);
        attackRoll.setTarget(target);

        assertTrue(attackRoll.isCriticalHit(context),
                "attack roll with base of 20 should register as a critical hit"
        );
    }

    @Test
    @DisplayName("isCriticalHit returns false (base roll below 20)")
    void isCriticalHit_returnsFalse_baseRollBelowTwenty() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:knight");
        RPGLObject target = RPGLFactory.newObject("std:knight");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "base": {
                    "value": 10
                }
            }*/
            this.putJsonObject("base", new JsonObject() {{
                this.putInteger("value", 10);
            }});
        }});

        attackRoll.setSource(source);
        attackRoll.setTarget(target);

        assertFalse(attackRoll.isCriticalHit(context),
                "attack roll with base below 20 should not register as a critical hit"
        );
    }

    @Test
    @DisplayName("resolveNestedSubevents invoked DummySubevent (on hit)")
    void resolveNestedSubevents_invokesDummySubevent_onHit() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:knight");
        RPGLObject target = RPGLFactory.newObject("std:knight");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

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

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.resolveNestedSubevents("hit", new DummyContext());

        assertEquals(1, DummySubevent.counter,
                "DummySubevent counter should increment by 1 from resolving nested subevents on hit"
        );
    }

    @Test
    @DisplayName("resolveNestedSubevents invoked DummySubevent (on miss)")
    void resolveNestedSubevents_invokesDummySubevent_onMiss() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:knight");
        RPGLObject target = RPGLFactory.newObject("std:knight");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

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

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.resolveNestedSubevents("miss", new DummyContext());

        assertEquals(1, DummySubevent.counter,
                "DummySubevent counter should increment by 1 from resolving nested subevents on miss"
        );
    }

    @Test
    @DisplayName("deliverDamage object loses health")
    void deliverDamage_objectLosesHealth() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:knight");
        RPGLObject target = RPGLFactory.newObject("std:knight");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "damage_type": "fire",
                        "dice": [
                            { "roll": 5 }
                        ],
                        "bonus": 5
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
                }});
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
    @DisplayName("getTargetArmorClass calculate 20 armor class")
    void getTargetArmorClass_calculatesTwentyArmorClass() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:knight");
        RPGLObject target = RPGLFactory.newObject("std:knight");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.setSource(source);
        attackRoll.setTarget(target);

        assertEquals(20, attackRoll.getTargetArmorClass(context),
                "target armor class should be 20 (plate armor + shield)"
        );
    }

    @Test
    @DisplayName("getBaseDamage calculates correct damage (modifier)")
    void getBaseDamage_calculatesCorrectDamage_modifier() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("std:knight");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_type": "melee",
                "attack_ability": "str",
                "damage": [
                    {
                        "damage_formula": "modifier",
                        "damage_type": "fire",
                        "ability": "str",
                        "object": {
                            "from": "subevent",
                            "object": "source"
                        }
                    }
                ],
                "withhold_damage_modifier": true
            }*/
            this.putString("attack_type", "melee");
            this.putString("attack_ability", "str");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_formula", "modifier");
                    this.putString("damage_type", "fire");
                    this.putString("ability", "str");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "subevent");
                        this.putString("object", "source");
                    }});
                }});
            }});
            this.putBoolean("withhold_damage_modifier", true);
        }});

        attackRoll.setSource(source);
        attackRoll.prepare(context);
        attackRoll.getBaseDamage(context);

        String expected = """
                [{"bonus":6,"damage_type":"fire","dice":[]}]""";
        assertEquals(expected, attackRoll.json.getJsonArray("damage").toString(),
                "damage should include str mod (+6)"
        );
    }

    @Test
    @DisplayName("getBaseDamage calculates correct damage (ability)")
    void getBaseDamage_calculatesCorrectDamage_ability() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("std:knight");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_type": "melee",
                "attack_ability": "str",
                "damage": [
                    {
                        "damage_formula": "ability",
                        "damage_type": "fire",
                        "ability": "str",
                        "object": {
                            "from": "subevent",
                            "object": "source"
                        }
                    }
                ],
                "withhold_damage_modifier": true
            }*/
            this.putString("attack_type", "melee");
            this.putString("attack_ability", "str");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_formula", "ability");
                    this.putString("damage_type", "fire");
                    this.putString("ability", "str");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "subevent");
                        this.putString("object", "source");
                    }});
                }});
            }});
            this.putBoolean("withhold_damage_modifier", true);
        }});

        attackRoll.setSource(source);
        attackRoll.prepare(context);
        attackRoll.getBaseDamage(context);

        String expected = """
                [{"bonus":23,"damage_type":"fire","dice":[]}]""";
        assertEquals(expected, attackRoll.json.getJsonArray("damage").toString(),
                "damage should include str score (23)"
        );
    }

    @Test
    @DisplayName("getBaseDamage calculates correct damage (proficiency)")
    void getBaseDamage_calculatesCorrectDamage_proficiency() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("std:knight");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_type": "melee",
                "attack_ability": "str",
                "damage": [
                    {
                        "damage_formula": "proficiency",
                        "damage_type": "fire",
                        "object": {
                            "from": "subevent",
                            "object": "source"
                        }
                    }
                ],
                "withhold_damage_modifier": true
            }*/
            this.putString("attack_type", "melee");
            this.putString("attack_ability", "str");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_formula", "proficiency");
                    this.putString("damage_type", "fire");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "subevent");
                        this.putString("object", "source");
                    }});
                }});
            }});
            this.putBoolean("withhold_damage_modifier", true);
        }});

        attackRoll.setSource(source);
        attackRoll.prepare(context);
        attackRoll.getBaseDamage(context);

        String expected = """
                [{"bonus":4,"damage_type":"fire","dice":[]}]""";
        assertEquals(expected, attackRoll.json.getJsonArray("damage").toString(),
                "damage should include proficiency bonus (+4)"
        );
    }

    @Test
    @DisplayName("getCriticalHitDamage correctly gathers critical hit damage")
    void getCriticalHitDamage_correctlyGathersCriticalHitDamage() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:commoner");
        RPGLObject target = RPGLFactory.newObject("std:commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_type": "melee",
                "attack_ability": "str",
                "damage": [
                    {
                        "damage_formula": "range",
                        "damage_type": "slashing",
                        "dice": [
                            { "count": 2, "size": 6, "determined": [ 3 ] }
                        ],
                        "bonus": 0
                    },
                    {
                        "damage_formula": "range",
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
                    this.putString("damage_formula", "range");
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
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_formula", "range");
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
                }});
            }});
        }});

        attackRoll.getCriticalHitDamage(context);

        String expected = """
                [{"damage_formula":"range","damage_type":"slashing","dice":[{"count":2,"determined":[3],"size":6},{"count":2,"determined":[3],"size":6}]},{"damage_formula":"range","damage_type":"fire","dice":[{"count":2,"determined":[3],"size":6},{"count":2,"determined":[3],"size":6}]}]""";
        assertEquals(expected, attackRoll.json.getJsonArray("damage").toString(),
                "critical hit damage should double the number of dice"
        );
    }

    @Test
    @DisplayName("getBaseDamage gets correct base damage (damage modifier not withheld)")
    void getBaseDamage_getsCorrectBaseDamage_damageModifierNotWithheld() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:knight");
        RPGLObject target = RPGLFactory.newObject("std:commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_type": "melee",
                "attack_ability": "str",
                "damage": [
                    {
                        "damage_formula": "range",
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
                    this.putString("damage_formula", "range");
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
                }});
            }});
            this.putBoolean("withhold_damage_modifier", false);
        }});

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.getBaseDamage(context);

        String expected = """
                [{"bonus":0,"damage_type":"slashing","dice":[{"determined":[3],"size":6},{"determined":[3],"size":6}]},{"bonus":3,"damage_type":"slashing","dice":[]}]""";
        assertEquals(expected, attackRoll.json.getJsonArray("damage").toString(),
                "base damage should be calculated including ability modifier bonus"
        );
    }

    @Test
    @DisplayName("getBaseDamage gets correct base damage (damage modifier withheld)")
    void getBaseDamage_getsCorrectBaseDamage_damageModifierWithheld() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:knight");
        RPGLObject target = RPGLFactory.newObject("std:commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_type": "melee",
                "attack_ability": "str",
                "damage": [
                    {
                        "damage_formula": "range",
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
                    this.putString("damage_formula", "range");
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
                }});
            }});
            this.putBoolean("withhold_damage_modifier", true);
        }});

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.getBaseDamage(context);

        String expected = """
                [{"bonus":0,"damage_type":"slashing","dice":[{"determined":[3],"size":6},{"determined":[3],"size":6}]}]""";
        assertEquals(expected, attackRoll.json.getJsonArray("damage").toString(),
                "base damage should be calculated not including ability modifier bonus"
        );
    }

}
