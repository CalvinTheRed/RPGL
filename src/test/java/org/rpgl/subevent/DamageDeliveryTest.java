package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.subevent.DamageDelivery class.
 *
 * @author Calvin Withun
 */
public class DamageDeliveryTest {

    @BeforeAll
    static void beforeAll() {
        DatapackLoader.loadDatapacks(
                new File("src/test/resources/datapacks".replace("/", File.separator))
        );
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
    @DisplayName("errors on wrong subevent")
    void errorsOnWrongSubevent() {
        Subevent subevent = new DamageDelivery()
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
    @DisplayName("returns damage")
    void returnsDamage() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        DamageDelivery damageDelivery = new DamageDelivery()
                .joinSubeventData(new JsonObject() {{
                    /*{
                        "damage": [
                            {
                                "damage_type": "fire",
                                "bonus": 10,
                                "dice": [ ],
                                "scale": {
                                    "numerator": 1,
                                    "denominator": 1,
                                    "round_up": false
                                }
                            },
                            {
                                "damage_type": "cold",
                                "bonus": 10,
                                "dice": [ ],
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
                            this.putInteger("bonus", 10);
                            this.putJsonArray("dice", new JsonArray());
                            this.putJsonObject("scale", new JsonObject() {{
                                this.putInteger("numerator", 1);
                                this.putInteger("denominator", 1);
                                this.putBoolean("round_up", false);
                            }});
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putString("damage_type", "cold");
                            this.putInteger("bonus", 10);
                            this.putJsonArray("dice", new JsonArray());
                            this.putJsonObject("scale", new JsonObject() {{
                                this.putInteger("numerator", 1);
                                this.putInteger("denominator", 1);
                                this.putBoolean("round_up", false);
                            }});
                        }});
                    }});
                }})
                .setTarget(object)
                .run(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        String expected = """
                {"cold":10,"fire":10}""";
        assertEquals(expected, damageDelivery.getDamage().toString(),
                "getDamage should report final damage quantities of each damage type being dealt at once"
        );
    }

    @Test
    @DisplayName("returns scaled damage")
    void returnsScaledDamage() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        DamageDelivery damageDelivery = new DamageDelivery()
                .joinSubeventData(new JsonObject() {{
                    /*{
                        "damage": [
                            {
                                "damage_type": "fire",
                                "bonus": 11,
                                "dice": [ ],
                                "scale": {
                                    "numerator": 1,
                                    "denominator": 2,
                                    "round_up": false
                                }
                            },
                            {
                                "damage_type": "cold",
                                "bonus": 10,
                                "dice": [ ],
                                "scale": {
                                    "numerator": 2,
                                    "denominator": 1,
                                    "round_up": false
                                }
                            }
                        ]
                    }*/
                    this.putJsonArray("damage", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putString("damage_type", "fire");
                            this.putInteger("bonus", 11);
                            this.putJsonArray("dice", new JsonArray());
                            this.putJsonObject("scale", new JsonObject() {{
                                this.putInteger("numerator", 1);
                                this.putInteger("denominator", 2);
                                this.putBoolean("round_up", false);
                            }});
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putString("damage_type", "cold");
                            this.putInteger("bonus", 10);
                            this.putJsonArray("dice", new JsonArray());
                            this.putJsonObject("scale", new JsonObject() {{
                                this.putInteger("numerator", 2);
                                this.putInteger("denominator", 1);
                                this.putBoolean("round_up", false);
                            }});
                        }});
                    }});
                }})
                .setTarget(object)
                .run(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        String expected = """
                {"cold":20,"fire":5}""";
        assertEquals(expected, damageDelivery.getDamage().toString(),
                "getDamage should report correctly scaled damage"
        );
    }

    @Test
    @DisplayName("rounds damage up")
    void roundsDamageUp() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        DamageDelivery damageDelivery = new DamageDelivery()
                .joinSubeventData(new JsonObject() {{
                    /*{
                        "damage": [
                            {
                                "damage_type": "fire",
                                "bonus": 11,
                                "dice": [ ],
                                "scale": {
                                    "numerator": 1,
                                    "denominator": 2,
                                    "round_up": true
                                }
                            },
                            {
                                "damage_type": "cold",
                                "bonus": 10,
                                "dice": [ ],
                                "scale": {
                                    "numerator": 2,
                                    "denominator": 1,
                                    "round_up": true
                                }
                            }
                        ]
                    }*/
                    this.putJsonArray("damage", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putString("damage_type", "fire");
                            this.putInteger("bonus", 11);
                            this.putJsonArray("dice", new JsonArray());
                            this.putJsonObject("scale", new JsonObject() {{
                                this.putInteger("numerator", 1);
                                this.putInteger("denominator", 2);
                                this.putBoolean("round_up", true);
                            }});
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putString("damage_type", "cold");
                            this.putInteger("bonus", 10);
                            this.putJsonArray("dice", new JsonArray());
                            this.putJsonObject("scale", new JsonObject() {{
                                this.putInteger("numerator", 2);
                                this.putInteger("denominator", 1);
                                this.putBoolean("round_up", true);
                            }});
                        }});
                    }});
                }})
                .setTarget(object)
                .run(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        String expected = """
                {"cold":20,"fire":6}""";
        assertEquals(expected, damageDelivery.getDamage().toString(),
                "getDamage should report correctly scaled damage"
        );
    }

    @Test
    @DisplayName("maximizes damage (specific damage type)")
    void maximizesDamage_specificDamageType() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        DamageDelivery damageDelivery = new DamageDelivery()
                .joinSubeventData(new JsonObject() {{
                    /*{
                        "damage": [
                            {
                                "damage_type": "fire",
                                "dice": [
                                    { "roll": 1, "size": 4 }
                                ],
                                "bonus": 0,
                                "scale": {
                                    "numerator": 1,
                                    "denominator": 1,
                                    "round_up": false
                                }
                            },
                            {
                                "damage_type": "cold",
                                "dice": [
                                    { "roll": 1, "size": 4 }
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
                                    this.putInteger("size", 4);
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
                                    this.putInteger("size", 4);
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
                }})
                .maximizeTypedDamageDice("fire")
                .setTarget(object)
                .run(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        String expected = """
                {"cold":1,"fire":4}""";
        assertEquals(expected, damageDelivery.getDamage().toString(),
                "maximizeDamage should only maximize damage for the passed damage type"
        );
    }

    @Test
    @DisplayName("maximizes damage (all damage types)")
    void maximizesDamage_allDamageTypes() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        DamageDelivery damageDelivery = new DamageDelivery()
                .joinSubeventData(new JsonObject() {{
                    /*{
                        "damage": [
                            {
                                "damage_type": "fire",
                                "dice": [
                                    { "roll": 1, "size": 4 }
                                ],
                                "bonus": 0,
                                "scale": {
                                    "numerator": 1,
                                    "denominator": 1,
                                    "round_up": false
                                }
                            },
                            {
                                "damage_type": "cold",
                                "dice": [
                                    { "roll": 1, "size": 4 }
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
                                    this.putInteger("size", 4);
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
                                    this.putInteger("size", 4);
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
                }})
                .maximizeTypedDamageDice(null)
                .setTarget(object)
                .run(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        String expected = """
                {"cold":4,"fire":4}""";
        assertEquals(expected, damageDelivery.getDamage().toString(),
                "maximizeDamage should maximize damage for all damage types when null damage type is passed"
        );
    }

    @Test
    @DisplayName("recognizes present damage type")
    void recognizesPresentDamageType() {
        DamageDelivery damageDelivery = new DamageDelivery()
                .joinSubeventData(new JsonObject() {{
                    /*{
                        "damage": [
                            {
                                "damage_type": "fire"
                            }
                        ]
                    }*/
                    this.putJsonArray("damage", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putString("damage_type", "fire");
                        }});
                    }});
                }});

        assertTrue(damageDelivery.includesDamageType("fire"),
                "should return true when damage type is included"
        );
    }

    @Test
    @DisplayName("recognizes absent damage type")
    void recognizesAbsentDamageType() {
        DamageDelivery damageDelivery = new DamageDelivery()
                .joinSubeventData(new JsonObject() {{
                    /*{
                        "damage": [
                            {
                                "damage_type": "fire"
                            }
                        ]
                    }*/
                    this.putJsonArray("damage", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putString("damage_type", "fire");
                        }});
                    }});
                }});

        assertFalse(damageDelivery.includesDamageType("cold"),
                "should return false when damage type is not included"
        );
    }

}
