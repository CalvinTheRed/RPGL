package org.rpgl.function;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.CriticalHitDamageCollection;
import org.rpgl.subevent.DamageCollection;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.RepeatDamageDice class.
 *
 * @author Calvin Withun
 */
public class RepeatDamageDiceTest {

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
    }

    @Test
    @DisplayName("errors on wrong function")
    void errorsOnWrongFunction() {
        assertThrows(FunctionMismatchException.class,
                () -> new RepeatDamageDice().execute(null, null, new JsonObject() {{
                    /*{
                        "function": "not_a_function"
                    }*/
                    this.putString("function", "not_a_function");
                }}, new DummyContext()),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("repeats damage dice (no count specified)")
    void repeatsDamageDice_noCountSpecified() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        DamageCollection damageCollection = new DamageCollection();
        damageCollection.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "formula": "range",
                        "damage_type": "fire",
                        "dice": [
                            { "count": 1, "size": 10, "determined": [ 5 ] }
                        ],
                        "bonus": 0
                    }
                ]
            }*/
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putString("damage_type", "fire");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("count", 1);
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
        damageCollection.setSource(object);
        damageCollection.prepare(new DummyContext());

        new RepeatDamageDice().execute(null, damageCollection, new JsonObject() {{
            /*{
                "function": "repeat_damage_dice"
            }*/
            this.putString("function", "repeat_damage_dice");
        }}, new DummyContext());

        String expected = """
                [{"bonus":0,"damage_type":"fire","dice":[{"determined":[5],"size":10},{"determined":[5],"size":10}],"scale":{"denominator":1,"numerator":1,"round_up":false}}]""";
        assertEquals(expected, damageCollection.getDamageCollection().toString(),
                "damage die should be repeated one time by default"
        );
    }

    @Test
    @DisplayName("repeats damage dice (count specified)")
    void repeatsDamageDice_countSpecified() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        DamageCollection damageCollection = new DamageCollection();
        damageCollection.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "formula": "range",
                        "damage_type": "fire",
                        "dice": [
                            { "count": 1, "size": 10, "determined": [ 5 ] }
                        ],
                        "bonus": 0
                    }
                ]
            }*/
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putString("damage_type", "fire");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("count", 1);
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
        damageCollection.setSource(object);
        damageCollection.prepare(new DummyContext());

        new RepeatDamageDice().execute(null, damageCollection, new JsonObject() {{
            /*{
                "function": "repeat_damage_dice",
                "count": 2
            }*/
            this.putString("function", "repeat_damage_dice");
            this.putInteger("count", 2);
        }}, new DummyContext());

        String expected = """
                [{"bonus":0,"damage_type":"fire","dice":[{"determined":[5],"size":10},{"determined":[5],"size":10},{"determined":[5],"size":10}],"scale":{"denominator":1,"numerator":1,"round_up":false}}]""";
        assertEquals(expected, damageCollection.getDamageCollection().toString(),
                "damage die should be repeated the specified number of times"
        );
    }

    @Test
    @DisplayName("repeats damage dice (critical hit damage collection)")
    void repeatsDamageDice_criticalHitDamageCollection() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        CriticalHitDamageCollection criticalHitDamageCollection = new CriticalHitDamageCollection();
        criticalHitDamageCollection.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "damage_type": "fire",
                        "dice": [
                            { "size": 10, "determined": [ 5 ] }
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
                    this.putJsonObject("scale", new JsonObject() {{
                        this.putInteger("numerator", 1);
                        this.putInteger("denominator", 1);
                        this.putBoolean("round_up", false);
                    }});
                }});
            }});
        }});
        criticalHitDamageCollection.setSource(object);
        criticalHitDamageCollection.prepare(new DummyContext());

        new RepeatDamageDice().execute(null, criticalHitDamageCollection, new JsonObject() {{
            /*{
                "function": "repeat_damage_dice",
                "count": 2
            }*/
            this.putString("function", "repeat_damage_dice");
            this.putInteger("count", 2);
        }}, new DummyContext());

        String expected = """
                [{"bonus":0,"damage_type":"fire","dice":[{"determined":[5],"size":10},{"determined":[5],"size":10},{"determined":[5],"size":10}],"scale":{"denominator":1,"numerator":1,"round_up":false}}]""";
        assertEquals(expected, criticalHitDamageCollection.getDamageCollection().toString(),
                "damage die should be repeated the specified number of times"
        );
    }

    @Test
    @DisplayName("does nothing without damage")
    void doesNothingWithoutDamage() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        DamageCollection damageCollection = new DamageCollection();
        damageCollection.setSource(object);
        damageCollection.prepare(new DummyContext());

        new RepeatDamageDice().execute(null, damageCollection, new JsonObject() {{
            /*{
                "function": "repeat_damage_dice",
                "count": 2
            }*/
            this.putString("function", "repeat_damage_dice");
            this.putInteger("count", 2);
        }}, new DummyContext());

        assertEquals("[]", damageCollection.getDamageCollection().toString(),
                "function should do nothing when no initial damage is present"
        );
    }

    @Test
    @DisplayName("does nothing without damage dice")
    void doesNothingWithoutDamageDice() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        DamageCollection damageCollection = new DamageCollection();
        damageCollection.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "formula": "range",
                        "damage_type": "fire",
                        "dice": [ ],
                        "bonus": 2
                    }
                ]
            }*/
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putString("damage_type", "fire");
                    this.putJsonArray("dice", new JsonArray());
                    this.putInteger("bonus", 2);
                }});
            }});
        }});
        damageCollection.setSource(object);
        damageCollection.prepare(new DummyContext());

        new RepeatDamageDice().execute(null, damageCollection, new JsonObject() {{
            /*{
                "function": "repeat_damage_dice",
                "count": 2
            }*/
            this.putString("function", "repeat_damage_dice");
            this.putInteger("count", 2);
        }}, new DummyContext());

        String expected = """
                [{"bonus":2,"damage_type":"fire","dice":[],"scale":{"denominator":1,"numerator":1,"round_up":false}}]""";
        assertEquals(expected, damageCollection.getDamageCollection().toString(),
                "function should do nothing when initial damage does not include dice"
        );
    }

}
