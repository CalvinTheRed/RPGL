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
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.subevent.HealingRoll class.
 *
 * @author Calvin Withun
 */
public class HealingRollTest {

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
    @DisplayName("invoke wrong subevent")
    void invoke_wrongSubevent_throwsException() {
        Subevent subevent = new HealingRoll();
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
    @DisplayName("getHealing returns correct healing dice and bonus")
    void getHealing_returnsCorrectHealingDiceAndBonus() {
        HealingRoll healingRoll = new HealingRoll();
        healingRoll.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "dice": [
                            { "size": 6, "roll": 3 }
                        ],
                        "bonus": 2
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putInteger("roll", 3);
                        }});
                    }});
                    this.putInteger("bonus", 2);
                }});
            }});
        }});

        String expected = """
                [{"bonus":2,"dice":[{"roll":3,"size":6}]}]""";
        assertEquals(expected, healingRoll.getHealing().toString(),
                "getHealing should return the correct healing from the subevent"
        );
    }

    @Test
    @DisplayName("maximizeHealing sets all healing dice to maximum value")
    void maximizeHealing_setsAllHealingDiceToMaximumValue() {
        HealingRoll healingRoll = new HealingRoll();
        healingRoll.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "dice": [
                            { "size": 6, "roll": 3 },
                            { "size": 10, "roll": 5 }
                        ],
                        "bonus": 2
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putInteger("roll", 3);
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 10);
                            this.putInteger("roll", 5);
                        }});
                    }});
                    this.putInteger("bonus", 2);
                }});
            }});
        }});

        healingRoll.maximizeHealingDice();

        String expected = """
                [{"bonus":2,"dice":[{"roll":6,"size":6},{"roll":10,"size":10}]}]""";
        assertEquals(expected, healingRoll.getHealing().toString(),
                "maximizeHealing should set all healing dice to their maximum face value"
        );
    }

    @Test
    @DisplayName("setHealingDiceMatchingOrBelow correct healing dice are set to new value")
    void setHealingDiceMatchingOrBelow_correctHealingDiceAreSetToNewValue() {
        HealingRoll healingRoll = new HealingRoll();
        healingRoll.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "dice": [
                            { "size": 6, "roll": 1, "determined": [ 6 ] },
                            { "size": 6, "roll": 2, "determined": [ 6 ] },
                            { "size": 6, "roll": 3, "determined": [ 6 ] }
                        ],
                        "bonus": 2
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putInteger("roll", 1);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(6);
                            }});
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putInteger("roll", 2);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(6);
                            }});
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putInteger("roll", 3);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(6);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 2);
                }});
            }});
        }});

        healingRoll.setHealingDiceMatchingOrBelow(2, 6);

        String expected = """
                [{"bonus":2,"dice":[{"determined":[6],"roll":6,"size":6},{"determined":[6],"roll":6,"size":6},{"determined":[6],"roll":3,"size":6}]}]""";
        assertEquals(expected, healingRoll.getHealing().toString(),
                "healing dice of 1 and 2 should be set to 6"
        );
    }

    @Test
    @DisplayName("rerollHealingDiceMatchingOrBelow correct healing dice are re-rolled")
    void rerollHealingDiceMatchingOrBelow_correctHealingDiceAreRerolled() {
        HealingRoll healingRoll = new HealingRoll();
        healingRoll.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "dice": [
                            { "size": 6, "roll": 1, "determined": [ 6 ] },
                            { "size": 6, "roll": 2, "determined": [ 6 ] },
                            { "size": 6, "roll": 3, "determined": [ 6 ] }
                        ],
                        "bonus": 2
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putInteger("roll", 1);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(6);
                            }});
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putInteger("roll", 2);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(6);
                            }});
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putInteger("roll", 3);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(6);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 2);
                }});
            }});
        }});

        healingRoll.rerollHealingDiceMatchingOrBelow(2);

        String expected = """
                [{"bonus":2,"dice":[{"determined":[],"roll":6,"size":6},{"determined":[],"roll":6,"size":6},{"determined":[6],"roll":3,"size":6}]}]""";
        assertEquals(expected, healingRoll.getHealing().toString(),
                "healing dice of 1 and 2 should be rerolled to their next determined values of 6"
        );
    }

    @Test
    @DisplayName("roll healing dice are rolled properly")
    void roll_healingDiceAreRolledProperly() {
        HealingRoll healingRoll = new HealingRoll();
        healingRoll.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "dice": [
                            { "size": 6, "determined": [ 1 ] },
                            { "size": 6, "determined": [ 2 ] },
                            { "size": 6, "determined": [ 3 ] }
                        ],
                        "bonus": 2
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
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
                    }});
                    this.putInteger("bonus", 2);
                }});
            }});
        }});

        healingRoll.roll();

        String expected = """
                [{"bonus":2,"dice":[{"determined":[],"roll":1,"size":6},{"determined":[],"roll":2,"size":6},{"determined":[],"roll":3,"size":6}]}]""";
        assertEquals(expected, healingRoll.getHealing().toString(),
                "roll should set the roll field of all healing dice to determined values"
        );
    }

    @Test
    @DisplayName("prepare healing dice are rolled")
    void prepare_healingDiceAreRolled() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);

        HealingRoll healingRoll = new HealingRoll();
        healingRoll.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "dice": [
                            { "size": 6, "determined": [ 1 ] },
                            { "size": 6, "determined": [ 2 ] },
                            { "size": 6, "determined": [ 3 ] }
                        ],
                        "bonus": 2
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
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
                    }});
                    this.putInteger("bonus", 2);
                }});
            }});
        }});

        healingRoll.prepare(context, List.of());

        String expected = """
                [{"bonus":2,"dice":[{"determined":[],"roll":1,"size":6},{"determined":[],"roll":2,"size":6},{"determined":[],"roll":3,"size":6}]}]""";
        assertEquals(expected, healingRoll.getHealing().toString(),
                "prepare should roll healing dice correctly"
        );
    }

}
