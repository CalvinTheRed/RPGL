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
    @DisplayName("errors on wrong subevent")
    void errorsOnWrongSubevent() {
        Subevent subevent = new SavingThrow()
                .joinSubeventData(new JsonObject() {{
                    /*{
                        "subevent": "not_a_subevent"
                    }*/
                    this.putString("subevent", "not_a_subevent");
                }});

        assertThrows(SubeventMismatchException.class,
                () -> subevent.invoke(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("delivers damage")
    void deliversDamage() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        SavingThrow savingThrow = new SavingThrow()
                .joinSubeventData(new JsonObject() {{
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
                }})
                .setSource(object)
                .setTarget(object);

        savingThrow.deliverDamage("all", new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertEquals(1000 /*base*/ -10 /*damage*/, object.getHealthData().getInteger("current"),
                "target should take 10 cold damage (52-10=42)"
        );
    }

    @Test
    @DisplayName("resolves nested subevents")
    void resolvesNestedSubevents() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        SavingThrow savingThrow = new SavingThrow()
                .setSource(object)
                .setTarget(object);

        // resolves nested subevents for passing

        savingThrow.json.insertJsonArray("pass", new JsonArray() {{
            this.addJsonObject(new JsonObject() {{
                this.putString("subevent", "dummy_subevent");
            }});
        }});
        savingThrow.resolveNestedSubevents("pass", new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertEquals(1, DummySubevent.counter,
                "counter should be incremented once from invoking nested pass subevent"
        );

        // resolves nested subevents for failing

        DummySubevent.resetCounter();

        savingThrow.json.insertJsonArray("fail", new JsonArray() {{
            this.addJsonObject(new JsonObject() {{
                this.putString("subevent", "dummy_subevent");
            }});
        }});
        savingThrow.resolveNestedSubevents("fail", new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertEquals(1, DummySubevent.counter,
                "counter should be incremented once from invoking nested fail subevent"
        );
    }

    @Test
    @DisplayName("gets base damage")
    void getsBaseDamage() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        SavingThrow savingThrow = new SavingThrow()
                .joinSubeventData(new JsonObject() {{
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
                }}).setSource(object);

        savingThrow.getBaseDamage(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        String expected = """
                [{"bonus":0,"damage_type":"cold","dice":[{"determined":[],"roll":5,"size":10},{"determined":[],"roll":5,"size":10}],"scale":{"denominator":1,"numerator":1,"round_up":false}}]""";
        assertEquals(expected, savingThrow.json.getJsonArray("damage").toString(),
                "getBaseDamage should store 10 cold damage"
        );
    }

    @Test
    @DisplayName("prepares difficulty class and base damage")
    void preparesDifficultyClassAndBaseDamage() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER)
                .setProficiencyBonus(2);
        object.getAbilityScores().putInteger("int", 20);

        SavingThrow savingThrow = new SavingThrow()
                .joinSubeventData(new JsonObject() {{
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
                }})
                .setSource(object)
                .prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertEquals(8 /*base*/ +2 /*proficiency*/ +5 /*modifier*/, savingThrow.getDifficultyClass(),
                "save DC was calculated incorrectly"
        );
        String expected = """
                [{"bonus":0,"damage_type":"cold","dice":[{"determined":[],"roll":5,"size":10},{"determined":[],"roll":5,"size":10}],"scale":{"denominator":1,"numerator":1,"round_up":false}}]""";
        assertEquals(expected, savingThrow.json.getJsonArray("damage").toString(),
                "prepare should store 10 cold damage"
        );
    }

    @Test
    @DisplayName("prepares assigned difficulty class")
    void preparesAssignedDifficultyClass() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        SavingThrow savingThrow = new SavingThrow()
                .joinSubeventData(new JsonObject() {{
                    /*{
                        "difficulty_class": 20
                    }*/
                    this.putInteger("difficulty_class", 20);
                }})
                .setSource(object)
                .prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertEquals(20, savingThrow.getDifficultyClass(),
                "should preserve the assigned difficulty class"
        );
    }

    @Test
    @DisplayName("prepares difficulty class as origin")
    void preparesDifficultyClassAsOrigin() throws Exception {
        RPGLObject origin = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        origin.getAbilityScores().putInteger("int", 20);

        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER)
                .setOriginObject(origin.getUuid())
                .setProficiencyBonus(2);

        SavingThrow savingThrow = new SavingThrow()
                .joinSubeventData(new JsonObject() {{
                    this.putString("difficulty_class_ability", "int");
                    this.putBoolean("use_origin_difficulty_class_ability", true);
                }})
                .setSource(object)
                .prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertEquals(8 /*base*/ +2 /*proficiency*/ +5 /*modifier*/, savingThrow.getDifficultyClass(),
                "save DC should calculate using origin object's ability scores"
        );
    }

    @Test
    @DisplayName("deals full damage on fail")
    void dealsFullDamageOnFail() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        new SavingThrow()
                .joinSubeventData(new JsonObject() {{
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
                }})
                .setSource(object)
                .prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0)
                .setTarget(object)
                .invoke(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertEquals(1000 /*base*/ -10 /*damage*/, object.getHealthData().getInteger("current"),
                "invoke should deal full damage on a fail"
        );
    }

    @Test
    @DisplayName("deals half damage on pass")
    void dealsHalfDamageOnPass() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        new SavingThrow()
                .joinSubeventData(new JsonObject() {{
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
                }})
                .setSource(object)
                .prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0)
                .setTarget(object)
                .invoke(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertEquals(1000 /*base*/ -5 /*damage*/, object.getHealthData().getInteger("current"),
                "invoke should deal half damage on a pass"
        );
    }

    @Test
    @DisplayName("deals no damage on pass")
    void dealsNoDamageOnPass() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        new SavingThrow()
                .joinSubeventData(new JsonObject() {{
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
                        "damage_on_pass": "none",
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
                    this.putString("damage_on_pass", "none");
                    this.putJsonArray("determined", new JsonArray() {{
                        this.addInteger(20);
                    }});
                }})
                .setSource(object)
                .prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0)
                .setTarget(object)
                .invoke(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertEquals(1000, object.getHealthData().getInteger("current"),
                "invoke should deal no damage on a pass"
        );
    }

    @Test
    @DisplayName("deals vampiric damage")
    void dealsVampiricDamage() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        source.getHealthData().putInteger("current", 1);
        target.getHealthData().putInteger("current", 11);

        new SavingThrow()
                .joinSubeventData(new JsonObject() {{
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
                }})
                .setSource(source)
                .prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0)
                .setTarget(target)
                .invoke(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertEquals(6, source.getHealthData().getInteger("current"),
                "source should be healed for half damage from vampirism"
        );
        assertEquals(1, target.getHealthData().getInteger("current"),
                "target should take damage and not be healed from vampirism"
        );
    }

}
