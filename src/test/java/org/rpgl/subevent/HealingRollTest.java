package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.subevent.HealingRoll class.
 *
 * @author Calvin Withun
 */
public class HealingRollTest {

    @BeforeAll
    static void beforeAll() throws Exception {
        DatapackLoader.loadDatapacks(
                new File(Objects.requireNonNull(DatapackTest.class.getClassLoader().getResource("datapacks")).toURI())
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
                () -> subevent.invoke(new RPGLContext()),
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

        assertEquals(5, healingRoll.getHealing(),
                "getHealing should return the correct healing from the subevent (3+2=5)"
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

        assertEquals(18, healingRoll.getHealing(),
                "maximizeHealing should set all healing dice to their maximum face value (6+10+2=18)"
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

        assertEquals(17, healingRoll.getHealing(),
                "healing dice of 1 and 2 should be set to 6 (6+6+3+2=17)"
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

        assertEquals(17, healingRoll.getHealing(),
                "healing dice of 1 and 2 should be rerolled to their next determined values of 6 (6+6+3+2=17)"
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

        assertEquals(8, healingRoll.getHealing(),
                "roll should set the roll field of all healing dice to determined values (1+2+3+2=8)"
        );
    }

    @Test
    @DisplayName("prepare healing dice are rolled")
    void prepare_healingDiceAreRolled() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
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

        healingRoll.prepare(context);

        assertEquals(8, healingRoll.getHealing(),
                "prepare should roll healing dice correctly (1+2+3+2=8)"
        );
    }

}
