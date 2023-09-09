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
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
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
                () -> subevent.invoke(new DummyContext(), List.of()),
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
        RPGLObject source = RPGLFactory.newObject("std:humanoid/knight");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/knight");
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

        assertTrue(attackRoll.isCriticalHit(context, List.of()),
                "attack roll with base of 20 should register as a critical hit"
        );
    }

    @Test
    @DisplayName("isCriticalHit returns false (base roll below 20)")
    void isCriticalHit_returnsFalse_baseRollBelowTwenty() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/knight");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/knight");
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

        assertFalse(attackRoll.isCriticalHit(context, List.of()),
                "attack roll with base below 20 should not register as a critical hit"
        );
    }

    @Test
    @DisplayName("resolveNestedSubevents invoked DummySubevent (on hit)")
    void resolveNestedSubevents_invokesDummySubevent_onHit() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/knight");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/knight");
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
        attackRoll.resolveNestedSubevents("hit", new DummyContext(), List.of());

        assertEquals(1, DummySubevent.counter,
                "DummySubevent counter should increment by 1 from resolving nested subevents on hit"
        );
    }

    @Test
    @DisplayName("resolveNestedSubevents invoked DummySubevent (on miss)")
    void resolveNestedSubevents_invokesDummySubevent_onMiss() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/knight");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/knight");
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
        attackRoll.resolveNestedSubevents("miss", new DummyContext(), List.of());

        assertEquals(1, DummySubevent.counter,
                "DummySubevent counter should increment by 1 from resolving nested subevents on miss"
        );
    }

    @Test
    @DisplayName("deliverDamage object loses health")
    void deliverDamage_objectLosesHealth() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/knight");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/knight");
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
        attackRoll.deliverDamage(context, List.of());

        assertEquals(42, target.getHealthData().getInteger("current"),
                "Knight should have 42 health left (52-10=42)"
        );
    }

    @Test
    @DisplayName("deliverDamage source heals from vampirism")
    void deliverDamage_sourceHealsFromVampirism() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy");
        RPGLObject target = RPGLFactory.newObject("debug:dummy");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.getHealthData().putInteger("current", 1);
        target.getHealthData().putInteger("current", 11);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "damage_type": "necrotic",
                        "dice": [ ],
                        "bonus": 10
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
                }});
            }});
            this.putJsonObject("vampirism", new JsonObject() {{
                this.putString("damage_type", "necrotic");
            }});
        }});

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.deliverDamage(context, List.of());

        assertEquals(1, target.getHealthData().getInteger("current"),
                "target should suffer 10 damage and receive no healing from vampirism"
        );
        assertEquals(6, source.getHealthData().getInteger("current"),
                "source should heal for half damage from vampirism"
        );
    }

    @Test
    @DisplayName("getTargetArmorClass calculate 20 armor class")
    void getTargetArmorClass_calculatesTwentyArmorClass() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/knight");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/knight");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.setSource(source);
        attackRoll.setTarget(target);

        assertEquals(20, attackRoll.getTargetArmorClass(context, List.of()),
                "target armor class should be 20 (plate armor + shield)"
        );
    }

    @Test
    @DisplayName("getBaseDamage calculates correct damage (modifier)")
    void getBaseDamage_calculatesCorrectDamage_modifier() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:dragon/red/young");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/knight");
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
        attackRoll.prepare(context, List.of());
        attackRoll.getBaseDamage(context, List.of());

        String expected = """
                [{"bonus":6,"damage_type":"fire","dice":[]}]""";
        assertEquals(expected, attackRoll.json.getJsonArray("damage").toString(),
                "damage should include str mod (+6)"
        );
    }

    @Test
    @DisplayName("getBaseDamage calculates correct damage (ability)")
    void getBaseDamage_calculatesCorrectDamage_ability() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:dragon/red/young");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/knight");
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
        attackRoll.prepare(context, List.of());
        attackRoll.getBaseDamage(context, List.of());

        String expected = """
                [{"bonus":23,"damage_type":"fire","dice":[]}]""";
        assertEquals(expected, attackRoll.json.getJsonArray("damage").toString(),
                "damage should include str score (23)"
        );
    }

    @Test
    @DisplayName("getBaseDamage calculates correct damage (proficiency)")
    void getBaseDamage_calculatesCorrectDamage_proficiency() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:dragon/red/young");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/knight");
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
        attackRoll.prepare(context, List.of());
        attackRoll.getBaseDamage(context, List.of());

        String expected = """
                [{"bonus":4,"damage_type":"fire","dice":[]}]""";
        assertEquals(expected, attackRoll.json.getJsonArray("damage").toString(),
                "damage should include proficiency bonus (+4)"
        );
    }

    @Test
    @DisplayName("getCriticalHitDamage correctly gathers critical hit damage")
    void getCriticalHitDamage_correctlyGathersCriticalHitDamage() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
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
                    this.putInteger("bonus", 0);
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
                    this.putInteger("bonus", 0);
                }});
            }});
        }});

        attackRoll.getCriticalHitDamage(context, List.of());

        String expected = """
                [{"bonus":0,"damage_formula":"range","damage_type":"slashing","dice":[{"count":2,"determined":[3],"size":6},{"count":2,"determined":[3],"size":6}]},{"bonus":0,"damage_formula":"range","damage_type":"fire","dice":[{"count":2,"determined":[3],"size":6},{"count":2,"determined":[3],"size":6}]}]""";
        assertEquals(expected, attackRoll.json.getJsonArray("damage").toString(),
                "critical hit damage should double the number of dice"
        );
    }

    @Test
    @DisplayName("getBaseDamage gets correct base damage (damage modifier not withheld)")
    void getBaseDamage_getsCorrectBaseDamage_damageModifierNotWithheld() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/knight");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
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
                    this.putInteger("bonus", 0);
                }});
            }});
            this.putBoolean("withhold_damage_modifier", false);
        }});

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.getBaseDamage(context, List.of());

        String expected = """
                [{"bonus":0,"damage_type":"slashing","dice":[{"determined":[3],"size":6},{"determined":[3],"size":6}]},{"bonus":3,"damage_type":"slashing","dice":[]}]""";
        assertEquals(expected, attackRoll.json.getJsonArray("damage").toString(),
                "base damage should be calculated including ability modifier bonus"
        );
    }

    @Test
    @DisplayName("getBaseDamage gets correct base damage (damage modifier withheld)")
    void getBaseDamage_getsCorrectBaseDamage_damageModifierWithheld() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/knight");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
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
                    this.putInteger("bonus", 0);
                }});
            }});
            this.putBoolean("withhold_damage_modifier", true);
        }});

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.getBaseDamage(context, List.of());

        String expected = """
                [{"bonus":0,"damage_type":"slashing","dice":[{"determined":[3],"size":6},{"determined":[3],"size":6}]}]""";
        assertEquals(expected, attackRoll.json.getJsonArray("damage").toString(),
                "base damage should be calculated not including ability modifier bonus"
        );
    }

    @Test
    @DisplayName("getBaseDamage gets correct base damage (origin item damage bonus)")
    void getBaseDamage_getsCorrectBaseDamage_originItemDamageBonus() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/knight");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        RPGLItem item = RPGLFactory.newItem("std:weapon/melee/martial/longsword");
        item.setDamageBonus(1);

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
                    this.putInteger("bonus", 0);
                }});
            }});
            this.putBoolean("withhold_damage_modifier", true);
        }});

        attackRoll.setOriginItem(item.getUuid());
        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.getBaseDamage(context, List.of());

        String expected = """
                [{"bonus":0,"damage_type":"slashing","dice":[{"determined":[3],"size":6},{"determined":[3],"size":6}]},{"bonus":1,"damage_type":"slashing","dice":[]}]""";
        assertEquals(expected, attackRoll.json.getJsonArray("damage").toString(),
                "base damage should be calculated including item damage bonus"
        );
    }

    @Test
    @DisplayName("prepare adds origin item attack bonus")
    void prepare_addsOriginItemAttackBonus() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/knight");
        DummyContext context = new DummyContext();
        context.add(source);

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
        attackRoll.prepare(context, List.of());

        assertEquals(1, attackRoll.getBonus(),
                "attack roll should include bonus from weapon attack bonus"
        );
    }

    @Test
    @DisplayName("resolveDamage delivers correct damage values")
    void resolveDamage_deliversCorrectDamageValues() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy");
        RPGLContext context = new DummyContext();
        context.add(object);

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
                            { "size": 6, "determined": [ 3 ] },
                            { "size": 6, "determined": [ 3 ] }
                        ],
                        "bonus": 0
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
                }});
            }});
        }});

        attackRoll.setSource(object);
        attackRoll.setTarget(object);
        attackRoll.resolveDamage(context, List.of());

        assertEquals(1000-3-3, object.getHealthData().getInteger("current"),
                "dummy should take 6 damage from the attack roll"
        );
    }
    
    @Test
    @DisplayName("run resolves correctly (critical miss always misses)")
    void run_resolvesCorrectly_criticalMissAlwaysMisses() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/knight");
        RPGLObject target = RPGLFactory.newObject("debug:dummy");
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("str", 100); // should land a hit without critical miss rule

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_ability": "str",
                "damage": [
                    {
                        "damage_formula": "range",
                        "damage_type": "fire",
                        "dice": [ ],
                        "bonus": 10
                    }
                ],
                "has_advantage": false,
                "has_disadvantage": false,
                "bonuses": [ ],
                "determined": [ 1 ]
            }*/
            this.putString("attack_ability", "str");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_formula", "range");
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
        }});

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.run(context, List.of());

        assertEquals(1000, target.getHealthData().getInteger("current"),
                "target should not have been hit"
        );
    }

    @Test
    @DisplayName("run resolves correctly (normal miss)")
    void run_resolvesCorrectly_normalMiss() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/knight");
        RPGLObject target = RPGLFactory.newObject("debug:dummy");
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        target.getAbilityScores().putInteger("dex", 100); // should be able to avoid a hit normally

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_ability": "str",
                "damage": [
                    {
                        "damage_formula": "range",
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
                "determined": [ 2 ]
            }*/
            this.putString("attack_ability", "str");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_formula", "range");
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
        }});

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.run(context, List.of());

        assertEquals(1000, target.getHealthData().getInteger("current"),
                "target should not have been hit"
        );
    }

    @Test
    @DisplayName("run resolves correctly (normal hit)")
    void run_resolvesCorrectly_normalHit() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/knight");
        RPGLObject target = RPGLFactory.newObject("debug:dummy");
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_ability": "str",
                "damage": [
                    {
                        "damage_formula": "range",
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
                "withhold_damage_modifier": true
            }*/
            this.putString("attack_ability", "str");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_formula", "range");
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
        }});

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.run(context, List.of());

        assertEquals(1000-3, target.getHealthData().getInteger("current"),
                "target should have been hit and taken damage"
        );
    }

    @Test
    @DisplayName("run resolves correctly (critical hit)")
    void run_resolvesCorrectly_criticalHit() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/knight");
        RPGLObject target = RPGLFactory.newObject("debug:dummy");
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
                        "damage_formula": "range",
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
                "withhold_damage_modifier": true
            }*/
            this.putString("attack_ability", "str");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_formula", "range");
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
        }});

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.run(context, List.of());

        assertEquals(1000-3-3, target.getHealthData().getInteger("current"),
                "target should have been hit and taken critical damage"
        );
    }

    @Test
    @DisplayName("handleVampirism heals source for half damage (specific damage type)")
    void handleVampirism_healsSourceForHalfDamage_specificDamageType() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy");
        RPGLObject target = RPGLFactory.newObject("debug:dummy");
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.getHealthData().putInteger("current", 1);
        target.getHealthData().putInteger("current", 1);

        JsonObject damageByType = new JsonObject() {{
            this.putInteger("necrotic", 10);
            this.putInteger("radiant", 10);
        }};

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "vampirism": {
                    "numerator": 1,
                    "denominator": 2,
                    "round_up": false,
                    "damage_type": "necrotic"
                }
            }*/
            this.putJsonObject("vampirism", new JsonObject() {{
                this.putInteger("numerator", 1);
                this.putInteger("denominator", 2);
                this.putBoolean("round_up", false);
                this.putString("damage_type", "necrotic");
            }});
        }});
        attackRoll.setSource(source);
        attackRoll.setTarget(target);

        VampiricSubevent.handleVampirism(attackRoll, damageByType, context, List.of());

        assertEquals(6, source.getHealthData().getInteger("current"),
                "source should be healed for half necrotic damage via vampirism"
        );
        assertEquals(1, target.getHealthData().getInteger("current"),
                "target should not be healed via vampirism"
        );
    }

    @Test
    @DisplayName("confirmCriticalHit returns true if not canceled")
    void confirmCriticalHit_returnsTrueIfNotCanceled() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy");
        RPGLObject target = RPGLFactory.newObject("debug:dummy");
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AttackRoll attackRoll = new AttackRoll();

        assertTrue(attackRoll.confirmCriticalHit(context, List.of()),
                "critical hit should be confirmed"
        );
    }

    @Test
    @DisplayName("confirmCriticalHit returns false if canceled")
    void confirmCriticalHit_returnsFalseIfCanceled() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy");
        RPGLObject target = RPGLFactory.newObject("debug:dummy");
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        RPGLItem adamantimePlate = RPGLFactory.newItem("std:armor/heavy/plate/adamantine");
        target.giveItem(adamantimePlate.getUuid());
        target.equipItem(adamantimePlate.getUuid(), "armor");

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.setSource(source);
        attackRoll.setTarget(target);

        assertFalse(attackRoll.confirmCriticalHit(context, List.of()),
                "critical hit should not be confirmed"
        );
    }

    @Test
    @DisplayName("run resolves correctly (critical hit not confirmed)")
    void run_resolvesCorrectly_criticalHitNotConfirmed() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/knight");
        RPGLObject target = RPGLFactory.newObject("debug:dummy");
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        RPGLItem adamantimePlate = RPGLFactory.newItem("std:armor/heavy/plate/adamantine");
        target.giveItem(adamantimePlate.getUuid());
        target.equipItem(adamantimePlate.getUuid(), "armor");

        AttackRoll attackRoll = new AttackRoll();
        attackRoll.joinSubeventData(new JsonObject() {{
            /*{
                "attack_ability": "str",
                "damage": [
                    {
                        "damage_formula": "range",
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
                "withhold_damage_modifier": true
            }*/
            this.putString("attack_ability", "str");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_formula", "range");
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
        }});

        attackRoll.setSource(source);
        attackRoll.setTarget(target);
        attackRoll.run(context, List.of());

        assertEquals(1000-3, target.getHealthData().getInteger("current"),
                "target should have been hit but should not have suffered critical damage"
        );
    }

}
