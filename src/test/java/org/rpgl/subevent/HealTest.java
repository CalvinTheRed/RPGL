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
 * Testing class for the org.rpgl.subevent.HealTest class.
 *
 * @author Calvin Withun
 */
public class HealTest {

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
        Subevent subevent = new Heal();
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
    @DisplayName("delivers healing")
    void deliversHealing() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        object.getHealthData().putInteger("current", 10);

        Heal heal = new Heal();
        heal.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "dice": [
                            { "roll": 5 }
                        ],
                        "bonus": 5
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("roll", 5);
                        }});
                    }});
                    this.putInteger("bonus", 5);
                }});
            }});
        }});
        heal.setSource(object);
        heal.setTarget(object);

        heal.deliverHealing(new DummyContext());

        assertEquals(10 /*base*/ +10 /*healing*/, object.getHealthData().getInteger("current"),
                "object should recover 10 hit points"
        );
    }

    @Test
    @DisplayName("gets base healing")
    void getsBaseHealing() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        Heal heal = new Heal();
        heal.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "formula": "range",
                        "dice": [
                            { "count":2, "size": 6, "determined": [ 1 ] }
                        ],
                        "bonus": 2
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("count", 2);
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(1);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 2);
                }});
            }});
        }});

        heal.setSource(object);
        heal.getBaseHealing(new DummyContext());

        String expected = """
                [{"bonus":2,"dice":[{"determined":[],"roll":1,"size":6},{"determined":[],"roll":1,"size":6}],"scale":{"denominator":1,"numerator":1,"round_up":false}}]""";
        assertEquals(expected, heal.json.getJsonArray("healing").toString(),
                "base healing should be rolled and in accordance with subevent-specified healing"
        );
    }

    @Test
    @DisplayName("heals")
    void heals() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        object.getHealthData().putInteger("current", 10);

        Heal heal = new Heal();
        heal.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "formula": "range",
                        "dice": [
                            { "count": 2, "size": 6, "determined": [ 1 ] }
                        ],
                        "bonus": 2
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("count", 2);
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(1);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 2);
                }});
            }});
        }});

        heal.setSource(object);
        heal.prepare(new DummyContext());
        heal.setTarget(object);
        heal.invoke(new DummyContext());

        assertEquals(10 /*base*/ +4 /*healing*/, object.getHealthData().getInteger("current"),
                "invoking heal should restore 4 hit points"
        );
    }

    @Test
    @DisplayName("prepares and rolls healing")
    void preparesAndRollsHealing() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        Heal heal = new Heal();
        heal.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "formula": "range",
                        "dice": [
                            { "count": 2, "size": 6, "determined": [ 1 ] }
                        ],
                        "bonus": 2
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "range");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("count", 2);
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(1);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 2);
                }});
            }});
        }});

        heal.setSource(source);
        heal.prepare(new DummyContext());

        String expected = """
                [{"bonus":2,"dice":[{"determined":[],"roll":1,"size":6},{"determined":[],"roll":1,"size":6}],"scale":{"denominator":1,"numerator":1,"round_up":false}}]""";
        assertEquals(expected, heal.json.getJsonArray("healing").toString(),
                "base healing should be rolled and stored after calling prepare"
        );
    }

    @Test
    @DisplayName("interprets healing formulae")
    void interpretsHealingFormulae() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        object.getAbilityScores().putInteger("str", 20);
        object.getHealthData().putInteger("current", 10);

        Heal heal = new Heal();
        heal.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "formula": "modifier",
                        "ability": "str",
                        "object": {
                            "from": "subevent",
                            "object": "source"
                        }
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("formula", "modifier");
                    this.putString("ability", "str");
                    this.putJsonObject("object", new JsonObject() {{
                        this.putString("from", "subevent");
                        this.putString("object", "source");
                    }});
                }});
            }});
        }});

        heal.setSource(object);
        heal.getBaseHealing(new DummyContext());

        String expected = """
                [{"bonus":5,"dice":[],"scale":{"denominator":1,"numerator":1,"round_up":false}}]""";
        assertEquals(expected, heal.json.getJsonArray("healing").toString(),
                "healing should equal str mod"
        );
    }

}
