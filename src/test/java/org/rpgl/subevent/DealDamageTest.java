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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.subevent.DealDamage class.
 *
 * @author Calvin Withun
 */
public class DealDamageTest {

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
    @DisplayName("errors on wrong subevent")
    void errorsOnWrongSubevent() {
        Subevent subevent = new DealDamage()
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
    @DisplayName("gets base damage")
    void getsBaseDamage() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        DealDamage dealDamage = new DealDamage()
                .joinSubeventData(new JsonObject() {{
                    /*{
                        "tags": [ ],
                        "damage": [
                            {
                                "formula": "range",
                                "damage_type": "force",
                                "dice": [
                                    { "count": 1, "size": 4, "determined": [ 2 ] }
                                ],
                                "bonus": 1
                            }
                        ]
                    }*/
                    this.putJsonArray("tags", new JsonArray());
                    this.putJsonArray("damage", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putString("formula", "range");
                            this.putString("damage_type", "force");
                            this.putJsonArray("dice", new JsonArray() {{
                                this.addJsonObject(new JsonObject() {{
                                    this.putInteger("count", 1);
                                    this.putInteger("size", 4);
                                    this.putJsonArray("determined", new JsonArray() {{
                                        this.addInteger(2);
                                    }});
                                }});
                            }});
                            this.putInteger("bonus", 1);
                        }});
                    }});
                }})
                .setSource(source)
                .setTarget(target);

        dealDamage.getBaseDamage(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        String expected = """
                [{"bonus":1,"damage_type":"force","dice":[{"determined":[],"roll":2,"size":4}],"scale":{"denominator":1,"numerator":1,"round_up":false}}]""";
        assertEquals(expected, dealDamage.json.getJsonArray("damage").toString(),
                "base damage should be 3 force damage"
        );
    }

    @Test
    @DisplayName("deals damage")
    void dealsDamage() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        new DealDamage()
                .joinSubeventData(new JsonObject() {{
                    /*{
                        "damage": [
                            {
                                "formula": "range",
                                "damage_type": "force",
                                "dice": [
                                    { "count": 1, "size": 4, "determined": [ 2 ] }
                                ],
                                "bonus": 1
                            }
                        ]
                    }*/
                    this.putJsonArray("damage", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putString("formula", "range");
                            this.putString("damage_type", "force");
                            this.putJsonArray("dice", new JsonArray() {{
                                this.addJsonObject(new JsonObject() {{
                                    this.putInteger("count", 1);
                                    this.putInteger("size", 4);
                                    this.putJsonArray("determined", new JsonArray() {{
                                        this.addInteger(2);
                                    }});
                                }});
                            }});
                            this.putInteger("bonus", 1);
                        }});
                    }});
                }})
                .setSource(object)
                .prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0).setTarget(object)
                .setTarget(object)
                .invoke(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertEquals(1000 /*base*/ -3 /*damage*/, object.getHealthData().getInteger("current"),
                "invoking DealDamage should deal 3 points of damage"
        );
    }

    @Test
    @DisplayName("deals vampiric damage")
    void dealsVampiricDamage() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.getHealthData().putInteger("current", 1);
        target.getHealthData().putInteger("current", 11);

        new DealDamage()
                .joinSubeventData(new JsonObject() {{
                    /*{
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
                        }
                    }*/
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
                }})
                .setSource(source)
                .prepare(context, target.getPosition())
                .setTarget(target)
                .invoke(context, target.getPosition());

        assertEquals(6, source.getHealthData().getInteger("current"),
                "source should be healed for half damage from vampirism"
        );
        assertEquals(1, target.getHealthData().getInteger("current"),
                "target should take damage and not be healed from vampirism"
        );
    }

    @Test
    @DisplayName("recognizes present damage type")
    void recognizesPresentDamageType() {
        DealDamage dealDamage = new DealDamage()
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

        assertTrue(dealDamage.includesDamageType("fire"),
                "should return true when damage type is present"
        );
    }

    @Test
    @DisplayName("recognizes absent damage type")
    void recognizesAbsentDamageType() {
        DealDamage dealDamage = new DealDamage()
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

        assertFalse(dealDamage.includesDamageType("cold"),
                "should return false when damage type is not present"
        );
    }

}
