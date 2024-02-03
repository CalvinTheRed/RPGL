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
import org.rpgl.subevent.DamageRoll;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.SetDamageDiceMatchingOrBelow class.
 *
 * @author Calvin Withun
 */
public class SetDamageDiceMatchingOrBelowTest {

    private DamageRoll damageRoll;

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
                            { "size": 6, "determined": [ 2 ] },
                            { "size": 6, "determined": [ 3 ] },
                            { "size": 6, "determined": [ 4 ] }
                        ],
                        "bonus": 0
                    },{
                        "damage_type": "cold",
                        "dice": [
                            { "size": 6, "determined": [ 1 ] },
                            { "size": 6, "determined": [ 2 ] },
                            { "size": 6, "determined": [ 3 ] },
                            { "size": 6, "determined": [ 4 ] }
                        ],
                        "bonus": 0
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
                                this.addInteger(2);
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
                                this.addInteger(4);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
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
                                this.addInteger(2);
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
                                this.addInteger(4);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
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
                () -> new SetDamageDiceMatchingOrBelow().execute(null, null, new JsonObject() {{
                    /*{
                        "function": "not_a_function"
                    }*/
                    this.putString("function", "not_a_function");
                }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("sets dice at or below value (specific damage type)")
    void setsDiceAtOrBelowValue_specificDamageType() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        damageRoll.setSource(object);
        damageRoll.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        new SetDamageDiceMatchingOrBelow().execute(null, damageRoll, new JsonObject() {{
            /*{
                "function": "set_damage_dice_matching_or_below",
                "threshold": 2,
                "set": 3,
                "damage_type": "fire"
            }*/
            this.putString("function", "set_damage_dice_matching_or_below");
            this.putInteger("threshold", 2);
            this.putInteger("set", 3);
            this.putString("damage_type", "fire");
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        String expected = """
                [{"bonus":0,"damage_type":"fire","dice":[{"determined":[],"roll":3,"size":6},{"determined":[],"roll":3,"size":6},{"determined":[],"roll":3,"size":6},{"determined":[],"roll":4,"size":6}]},{"bonus":0,"damage_type":"cold","dice":[{"determined":[],"roll":1,"size":6},{"determined":[],"roll":2,"size":6},{"determined":[],"roll":3,"size":6},{"determined":[],"roll":4,"size":6}]}]""";
        assertEquals(expected, damageRoll.getDamage().toString(),
                "execute should set all fire damage dice which rolled 2 or lower to 3 (cold=1+2+3+4=10, fire=3+3+3+4=13)"
        );
    }

    @Test
    @DisplayName("sets dice at or below value (all damage types)")
    void setsDiceAtOrBelowValue_allDamageTypes() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        damageRoll.setSource(object);
        damageRoll.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        new SetDamageDiceMatchingOrBelow().execute(null, damageRoll, new JsonObject() {{
            /*{
                "function": "set_damage_dice_matching_or_below",
                "threshold": 2,
                "set": 3,
                "damage_type": ""
            }*/
            this.putString("function", "set_damage_dice_matching_or_below");
            this.putInteger("threshold", 2);
            this.putInteger("set", 3);
            this.putString("damage_type", "");
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        String expected = """
                [{"bonus":0,"damage_type":"fire","dice":[{"determined":[],"roll":3,"size":6},{"determined":[],"roll":3,"size":6},{"determined":[],"roll":3,"size":6},{"determined":[],"roll":4,"size":6}]},{"bonus":0,"damage_type":"cold","dice":[{"determined":[],"roll":3,"size":6},{"determined":[],"roll":3,"size":6},{"determined":[],"roll":3,"size":6},{"determined":[],"roll":4,"size":6}]}]""";
        assertEquals(expected, damageRoll.getDamage().toString(),
                "execute should set all damage dice which rolled 2 or lower to 3 (cold=3+3+3+4=13, fire=3+3+3+4=13)"
        );
    }

}
