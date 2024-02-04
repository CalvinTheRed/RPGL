package org.rpgl.function;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DamageDelivery;
import org.rpgl.subevent.DamageRoll;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.MaximizeDamage class.
 *
 * @author Calvin Withun
 */
public class MaximizeDamageTest {

    private DamageRoll damageRoll;
    private DamageDelivery damageDelivery;

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

    @BeforeEach
    void beforeEach() {
        damageRoll = new DamageRoll();
        damageRoll.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "damage_type": "fire",
                        "dice": [
                            { "size": 6, "determined": [ 1 ] },
                            { "size": 6, "determined": [ 1 ] }
                        ],
                        "bonus": 2,
                        "scale": {
                            "numerator": 1,
                            "denominator": 1,
                            "round_up": false
                        }
                    },{
                        "damage_type": "cold",
                        "dice": [
                            { "size": 6, "determined": [ 1 ] },
                            { "size": 6, "determined": [ 1 ] }
                        ],
                        "bonus": 2,
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
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(1);
                            }});
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(1);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 2);
                    this.putJsonObject("scale", new JsonObject() {{
                        this.putInteger("numerator", 1);
                        this.putInteger("denominator", 1);
                        this.putBoolean("round_up", false);
                    }});
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_type", "cold");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(1);
                            }});
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(1);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 2);
                    this.putJsonObject("scale", new JsonObject() {{
                        this.putInteger("numerator", 1);
                        this.putInteger("denominator", 1);
                        this.putBoolean("round_up", false);
                    }});
                }});
            }});
        }});

        damageDelivery = new DamageDelivery();
        damageDelivery.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "damage_type": "fire",
                        "dice": [
                            { "roll": 1, "size": 6 },
                            { "roll": 1, "size": 6 },
                        ],
                        "bonus": 0,
                        "scale": {
                            "numerator": 1,
                            "denominator": 1,
                            "round_up": false
                        }
                    },{
                        "damage_type": "cold",
                        "dice": [
                            { "roll": 1, "size": 6 },
                            { "roll": 1, "size": 6 },
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
                            this.putInteger("roll", 1);
                            this.putInteger("size", 6);
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("roll", 1);
                            this.putInteger("size", 6);
                        }});
                    }});
                    this.putInteger("bonus", 0);
                    this.putJsonObject("scale", new JsonObject() {{
                        this.putInteger("numerator", 1);
                        this.putInteger("denominator", 1);
                        this.putBoolean("round_up", false);
                    }});
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putString("damage_type", "cold");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("roll", 1);
                            this.putInteger("size", 6);
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("roll", 1);
                            this.putInteger("size", 6);
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
    }

    @AfterEach
    void afterEach() {
        UUIDTable.clear();
    }

    @Test
    @DisplayName("errors on wrong function")
    void errorsOnWrongFunction() {
        assertThrows(FunctionMismatchException.class,
                () -> new MaximizeDamage().execute(null, null, new JsonObject() {{
                    /*{
                        "function": "not_a_function"
                    }*/
                    this.putString("function", "not_a_function");
                }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("maximizes specific damage type (damage roll)")
    void maximizesSpecificDamageType_damageRoll() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        damageRoll.setSource(object);
        damageRoll.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        new MaximizeDamage().execute(null, damageRoll, new JsonObject() {{
            /*{
                "function": "maximize_damage",
                "damage_type": "fire"
            }*/
            this.putString("function", "maximize_damage");
            this.putString("damage_type", "fire");
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        String expected = """
                [{"bonus":2,"damage_type":"fire","dice":[{"determined":[],"roll":6,"size":6},{"determined":[],"roll":6,"size":6}],"scale":{"denominator":1,"numerator":1,"round_up":false}},{"bonus":2,"damage_type":"cold","dice":[{"determined":[],"roll":1,"size":6},{"determined":[],"roll":1,"size":6}],"scale":{"denominator":1,"numerator":1,"round_up":false}}]""";
        assertEquals(expected, damageRoll.getDamage().toString(),
                "execute should maximize fire damage (cold 1+1+2=4 fire 6+6+2=14)"
        );
    }

    @Test
    @DisplayName("maximizes every damage type (damage roll)")
    void maximizesEveryDamageType_damageRoll() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        damageRoll.setSource(object);
        damageRoll.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        new MaximizeDamage().execute(null, damageRoll, new JsonObject() {{
            /*{
                "function": "maximize_damage"
            }*/
            this.putString("function", "maximize_damage");
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        String expected = """
                [{"bonus":2,"damage_type":"fire","dice":[{"determined":[],"roll":6,"size":6},{"determined":[],"roll":6,"size":6}],"scale":{"denominator":1,"numerator":1,"round_up":false}},{"bonus":2,"damage_type":"cold","dice":[{"determined":[],"roll":6,"size":6},{"determined":[],"roll":6,"size":6}],"scale":{"denominator":1,"numerator":1,"round_up":false}}]""";
        assertEquals(expected, damageRoll.getDamage().toString(),
                "execute should maximize all damage (cold 6+6+2=14 fire 6+6+2=14)"
        );
    }

    @Test
    @DisplayName("maximizes specific damage type (damage delivery)")
    void maximizesSpecificDamageType_damageDelivery() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        damageDelivery.setSource(object);
        damageDelivery.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        new MaximizeDamage().execute(null, damageDelivery, new JsonObject() {{
            /*{
                "function": "maximize_damage",
                "damage_type": "fire"
            }*/
            this.putString("function", "maximize_damage");
            this.putString("damage_type", "fire");
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        damageDelivery.setTarget(object);
        damageDelivery.run(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        String expected = """
                {"cold":2,"fire":12}""";
        assertEquals(expected, damageDelivery.getDamage().toString(),
                "execute should maximize fire damage only"
        );
    }

    @Test
    @DisplayName("maximizes every damage type (damage delivery)")
    void maximizesEveryDamageType_damageDelivery() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        damageDelivery.setSource(object);
        damageDelivery.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        new MaximizeDamage().execute(null, damageDelivery, new JsonObject() {{
            /*{
                "function": "maximize_damage"
            }*/
            this.putString("function", "maximize_damage");
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        damageDelivery.setTarget(object);
        damageDelivery.run(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        String expected = """
                {"cold":12,"fire":12}""";
        assertEquals(expected, damageDelivery.getDamage().toString(),
                "execute should maximize all damage"
        );
    }

}
