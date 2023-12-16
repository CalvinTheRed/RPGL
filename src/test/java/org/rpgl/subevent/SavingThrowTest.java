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
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.subevent.SavingThrow class.
 *
 * @author Calvin Withun
 */
public class SavingThrowTest {

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
        Subevent subevent = new SavingThrow();
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
    @DisplayName("deliverDamage delivers damage to target")
    void deliverDamage_deliversDamageToTarget() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:dragon/red/young", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/knight", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        SavingThrow savingThrow = new SavingThrow();
        savingThrow.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "damage_type": "cold",
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
                    this.putString("damage_type", "cold");
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

        savingThrow.setSource(source);
        savingThrow.setTarget(target);
        savingThrow.deliverDamage("all", context, List.of());

        assertEquals(42, target.getHealthData().getInteger("current"),
                "target should take 10 cold damage (52-10=42)"
        );
    }

    @Test
    @DisplayName("resolveNestedSubevents increments counter on pass (DummySubevent)")
    void resolveNestedSubevents_incrementsCounterOnPass_dummySubevent() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:dragon/red/young", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/knight", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        SavingThrow savingThrow = new SavingThrow();
        savingThrow.joinSubeventData(new JsonObject() {{
            /*{
                "pass": [
                    {
                        "subevent": "dummy_subevent"
                    }
                ]
            }*/
            this.putJsonArray("pass", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
        }});

        savingThrow.setSource(source);
        savingThrow.setTarget(target);
        savingThrow.resolveNestedSubevents("pass", context, List.of());

        assertEquals(1, DummySubevent.counter,
                "counter should be incremented once from invoking nested pass subevent"
        );
    }

    @Test
    @DisplayName("resolveNestedSubevents increments counter on fail (DummySubevent)")
    void resolveNestedSubevents_incrementsCounterOnFail_dummySubevent() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:dragon/red/young", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/knight", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        SavingThrow savingThrow = new SavingThrow();
        savingThrow.joinSubeventData(new JsonObject() {{
            /*{
                "fail": [
                    {
                        "subevent": "dummy_subevent"
                    }
                ]
            }*/
            this.putJsonArray("fail", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
        }});

        savingThrow.setSource(source);
        savingThrow.setTarget(target);
        savingThrow.resolveNestedSubevents("fail", context, List.of());

        assertEquals(1, DummySubevent.counter,
                "counter should be incremented once from invoking nested fail subevent"
        );
    }

    @Test
    @DisplayName("getTargetDamage returns empty object (default)")
    void getTargetDamage_returnsEmptyObject_default() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:dragon/red/young", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/knight", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        SavingThrow savingThrow = new SavingThrow();
        savingThrow.joinSubeventData(new JsonObject() {{
            this.putJsonArray("tags", new JsonArray());
            this.putJsonArray("damage", new JsonArray());
        }});

        savingThrow.setSource(source);
        savingThrow.setTarget(target);
        savingThrow.getTargetDamage(context, List.of());

        assertEquals("[]", savingThrow.json.getJsonArray("damage").toString(),
                "target damage should be empty by default"
        );
    }

    @Test
    @DisplayName("getBaseDamage stores base damage value")
    void getBaseDamage_storesBaseDamageValue() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        SavingThrow savingThrow = new SavingThrow();
        savingThrow.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "formula": "range",
                        "damage_type": "cold",
                        "dice": [
                            { "count": 2, "size": 10, "determined": [ 5 ] }
                        ],
                        "bonus": 0
                    }
                ],
                "tags": [ ]
            }*/
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putString("damage_type", "cold");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("count", 2);
                            this.putInteger("size", 10);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(5);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
            this.putJsonArray("tags", new JsonArray());
        }});

        savingThrow.setSource(source);
        savingThrow.getBaseDamage(context, List.of());

        String expected = """
                [{"bonus":0,"damage_type":"cold","dice":[{"determined":[],"roll":5,"size":10},{"determined":[],"roll":5,"size":10}],"scale":{"denominator":1,"numerator":1,"round_up":false}}]""";
        assertEquals(expected, savingThrow.json.getJsonArray("damage").toString(),
                "getBaseDamage should store 10 cold damage"
        );
    }

    @Test
    @DisplayName("calculateDifficultyClass calculates correctly")
    void calculateDifficultyClass_calculatesCorrectly() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("con", 20);
        source.setProficiencyBonus(2);

        SavingThrow savingThrow = new SavingThrow();
        savingThrow.joinSubeventData(new JsonObject() {{
            this.putString("difficulty_class_ability", "con");
            this.putBoolean("use_origin_difficulty_class_ability", false);
        }});

        savingThrow.setSource(source);
        savingThrow.calculateDifficultyClass(context, List.of());

        assertEquals(8 /*base*/ +2 /*proficiency*/ +5 /*modifier*/, savingThrow.json.getInteger("save_difficulty_class"),
                "save DC should calculate according to the formula DC = 8 + proficiency + modifier"
        );
    }

    @Test
    @DisplayName("calculateDifficultyClass uses origin difficulty class ability")
    void calculateDifficultyClass_usesOriginDifficultyClassAbility() throws Exception {
        RPGLObject origin = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        origin.getAbilityScores().putInteger("int", 20);
        source.setOriginObject(origin.getUuid());
        source.setProficiencyBonus(2);

        SavingThrow savingThrow = new SavingThrow();
        savingThrow.joinSubeventData(new JsonObject() {{
            this.putString("difficulty_class_ability", "int");
            this.putBoolean("use_origin_difficulty_class_ability", true);
        }});

        savingThrow.setSource(source);
        savingThrow.calculateDifficultyClass(context, List.of());

        assertEquals(8 /*base*/ +2 /*proficiency*/ +5 /*modifier*/, savingThrow.json.getInteger("save_difficulty_class"),
                "save DC should calculate using origin object's ability scores"
        );
    }

    @Test
    @DisplayName("prepare calculates save DC and stores base damage")
    void prepare_calculatesSaveDifficultyClassAndStoresBaseDamage() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("int", 20);
        source.setProficiencyBonus(2);

        SavingThrow savingThrow = new SavingThrow();
        savingThrow.joinSubeventData(new JsonObject() {{
            /*{
                "difficulty_class_ability": "int",
                "damage": [
                    {
                        :formula": "range",
                        "damage_type": "cold",
                        "dice": [
                            { "count": 2, "size": 10, "determined": [ 5 ] }
                        ],
                        "bonus": 0
                    }
                ]
            }*/
            this.putString("difficulty_class_ability", "int");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putString("damage_type", "cold");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("count", 2);
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

        savingThrow.setSource(source);
        savingThrow.prepare(context, List.of());

        assertEquals(8 /*base*/ +2 /*proficiency*/ +5 /*modifier*/, savingThrow.json.getInteger("save_difficulty_class"),
                "save DC was calculated incorrectly"
        );
        String expected = """
                [{"bonus":0,"damage_type":"cold","dice":[{"determined":[],"roll":5,"size":10},{"determined":[],"roll":5,"size":10}],"scale":{"denominator":1,"numerator":1,"round_up":false}}]""";
        assertEquals(expected, savingThrow.json.getJsonArray("damage").toString(),
                "prepare should store 10 cold damage"
        );
    }

    @Test
    @DisplayName("invoke deals proper damage on fail")
    void invoke_dealsProperDamageOnFail() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:dragon/red/young", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/knight", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        SavingThrow savingThrow = new SavingThrow();
        savingThrow.joinSubeventData(new JsonObject() {{
            /*{
                "difficulty_class_ability": "con",
                "save_ability": "dex",
                "damage": [
                    {
                        "formula": "range",
                        "damage_type": "cold",
                        "dice": [
                            { "count": 2, "size": 10, "determined": [ 5 ] }
                        ],
                        "bonus": 0
                    }
                ],
                "damage_on_pass": "half",
                "determined": [ 1 ]
            }*/
            this.putString("difficulty_class_ability", "con");
            this.putString("save_ability", "dex");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putString("damage_type", "cold");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("count", 2);
                            this.putInteger("size", 10);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(5);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
            this.putString("damage_on_pass", "half");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(1);
            }});
        }});

        savingThrow.setSource(source);
        savingThrow.prepare(context, List.of());
        savingThrow.setTarget(target);
        savingThrow.invoke(context, List.of());

        assertEquals(42, target.getHealthData().getInteger("current"),
                "invoke should deal full damage on a fail (52-10=42)"
        );
    }

    @Test
    @DisplayName("invoke deals half damage on pass")
    void invoke_dealsHalfDamageOnPass() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:dragon/red/young", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/knight", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        SavingThrow savingThrow = new SavingThrow();
        savingThrow.joinSubeventData(new JsonObject() {{
            /*{
                "difficulty_class_ability": "con",
                "save_ability": "dex",
                "damage": [
                    {
                        "formula": "range",
                        "damage_type": "cold",
                        "dice": [
                        { "count": 2, "size": 10, "determined": [ 5 ] }
                        ],
                        "bonus": 0
                    }
                ],
                "damage_on_pass": "half",
                "determined": [ 20 ]
            }*/
            this.putString("difficulty_class_ability", "con");
            this.putString("save_ability", "dex");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putString("damage_type", "cold");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("count", 2);
                            this.putInteger("size", 10);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(5);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
            this.putString("damage_on_pass", "half");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(20);
            }});
        }});
        savingThrow.setSource(source);
        savingThrow.prepare(context, List.of());
        savingThrow.setTarget(target);
        savingThrow.invoke(context, List.of());

        assertEquals(47, target.getHealthData().getInteger("current"),
                "invoke should deal full damage on a fail (52-5=47)"
        );
    }

    @Test
    @DisplayName("invoke accommodates vampirism")
    void invoke_accommodatesVampirism() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.getHealthData().putInteger("current", 1);
        target.getHealthData().putInteger("current", 11);

        SavingThrow savingThrow = new SavingThrow();
        savingThrow.joinSubeventData(new JsonObject() {{
            /*{
                "difficulty_class_ability": "int",
                "save_ability": "con",
                "damage": [
                    {
                        "formula": "range",
                        "damage_type": "necrotic",
                        "dice": [
                        { "count": 2, "size": 10, "determined": [ 5 ] }
                        ],
                        "bonus": 0
                    }
                ],
                "vampirism": {
                    "numerator": 1,
                    "denominator": 2,
                    "round_up": false,
                    "damage_type": "necrotic"
                },
                "damage_on_pass": "half",
                "determined": [ 1 ]
            }*/
            this.putString("difficulty_class_ability", "int");
            this.putString("save_ability", "con");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putString("damage_type", "necrotic");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("count", 2);
                            this.putInteger("size", 10);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(5);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
            this.putJsonObject("vampirism", new JsonObject() {{
                this.putInteger("numerator", 1);
                this.putInteger("denominator", 2);
                this.putBoolean("round_up", false);
                this.putString("damage_type", "necrotic");
            }});
            this.putString("damage_on_pass", "half");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(1);
            }});
        }});
        savingThrow.setSource(source);
        savingThrow.prepare(context, List.of());
        savingThrow.setTarget(target);
        savingThrow.invoke(context, List.of());

        assertEquals(6, source.getHealthData().getInteger("current"),
                "source should be healed for half damage from vampirism"
        );
        assertEquals(1, target.getHealthData().getInteger("current"),
                "target should take damage and not be healed from vampirism"
        );
    }

}
