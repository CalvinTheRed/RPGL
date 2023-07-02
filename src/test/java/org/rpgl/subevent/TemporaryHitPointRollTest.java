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
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.subevent.TemporaryHitPointRoll class.
 *
 * @author Calvin Withun
 */
public class TemporaryHitPointRollTest {

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
        Subevent subevent = new TemporaryHitPointRoll();
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
    @DisplayName("getTemporaryHitPoints returns correct temporary hit points dice and bonus")
    void getTemporaryHitPoints_returnsCorrectTemporaryHitPointsDiceAndBonus() {
        TemporaryHitPointRoll temporaryHitPointRoll = new TemporaryHitPointRoll();
        temporaryHitPointRoll.joinSubeventData(new JsonObject() {{
            /*{
                "temporary_hit_points": [
                    {
                        "dice": [
                            { "size": 6, "roll": 3 }
                        ],
                        "bonus": 2
                    }
                ]
            }*/
            this.putJsonArray("temporary_hit_points", new JsonArray() {{
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
        assertEquals(expected, temporaryHitPointRoll.getTemporaryHitPoints().toString(),
                "getTemporaryHitPoints should return the correct temporary hit points from the subevent"
        );
    }

    @Test
    @DisplayName("roll temporary hit point dice are rolled properly")
    void roll_temporaryHitPointDiceAreRolledProperly() {
        TemporaryHitPointRoll temporaryHitPointRoll = new TemporaryHitPointRoll();
        temporaryHitPointRoll.joinSubeventData(new JsonObject() {{
            /*{
                "temporary_hit_points": [
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
            this.putJsonArray("temporary_hit_points", new JsonArray() {{
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

        temporaryHitPointRoll.roll();

        String expected = """
                [{"bonus":2,"dice":[{"determined":[],"roll":1,"size":6},{"determined":[],"roll":2,"size":6},{"determined":[],"roll":3,"size":6}]}]""";
        assertEquals(expected, temporaryHitPointRoll.getTemporaryHitPoints().toString(),
                "roll should set the roll field of all temporary hit point dice to determined values"
        );
    }

    @Test
    @DisplayName("prepare temporary hit point dice are rolled")
    void prepare_temporaryHitPointDiceAreRolled() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:commoner");
        DummyContext context = new DummyContext();
        context.add(source);

        TemporaryHitPointRoll temporaryHitPointRoll = new TemporaryHitPointRoll();
        temporaryHitPointRoll.joinSubeventData(new JsonObject() {{
            /*{
                "temporary_hit_points": [
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
            this.putJsonArray("temporary_hit_points", new JsonArray() {{
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

        temporaryHitPointRoll.prepare(context);

        String expected = """
                [{"bonus":2,"dice":[{"determined":[],"roll":1,"size":6},{"determined":[],"roll":2,"size":6},{"determined":[],"roll":3,"size":6}]}]""";
        assertEquals(expected, temporaryHitPointRoll.getTemporaryHitPoints().toString(),
                "prepare should roll temporary hit point dice correctly"
        );
    }

}
