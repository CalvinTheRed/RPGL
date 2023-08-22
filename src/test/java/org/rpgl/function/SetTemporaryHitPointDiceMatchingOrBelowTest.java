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
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.TemporaryHitPointRoll;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.SetTemporaryHitPointDiceMatchingOrBelow class.
 *
 * @author Calvin Withun
 */
public class SetTemporaryHitPointDiceMatchingOrBelowTest {

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
    @DisplayName("execute wrong function")
    void execute_wrongFunction_throwsException() {
        Function function = new SetTemporaryHitPointDiceMatchingOrBelow();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "not_a_function"
            }*/
            this.putString("function", "not_a_function");
        }};

        DummyContext context = new DummyContext();

        assertThrows(FunctionMismatchException.class,
                () -> function.execute(null, null, functionJson, context),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("execute sets all dice at or below two to three")
    void execute_setsAllDiceAtOrBelowTwoToThree() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        TemporaryHitPointRoll temporaryHitPointRoll = new TemporaryHitPointRoll();
        temporaryHitPointRoll.joinSubeventData(new JsonObject() {{
            /*{
                "temporary_hit_points": [
                    {
                        "dice": [
                            { "size": 6, "determined": [ 1 ] }
                        ],
                        "bonus": 0
                    },
                    {
                        "dice": [
                            { "size": 6, "determined": [ 2 ] }
                        ],
                        "bonus": 0
                    },
                    {
                        "dice": [
                            { "size": 6, "determined": [ 3 ] }
                        ],
                        "bonus": 0
                    },
                    {
                        "dice": [
                            { "size": 6, "determined": [ 4 ] }
                        ],
                        "bonus": 0
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
                    }});
                    this.putInteger("bonus", 0);
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(2);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 6);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(3);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
                this.addJsonObject(new JsonObject() {{
                    this.putJsonArray("dice", new JsonArray() {{
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
        temporaryHitPointRoll.setSource(source);
        temporaryHitPointRoll.prepare(context);
        temporaryHitPointRoll.setTarget(target);

        SetTemporaryHitPointDiceMatchingOrBelow setTemporaryHitPointDiceMatchingOrBelow = new SetTemporaryHitPointDiceMatchingOrBelow();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "set_temporary_hit_point_dice_matching_or_below",
                "threshold": 2,
                "set": 3
            }*/
            this.putString("function", "set_temporary_hit_point_dice_matching_or_below");
            this.putInteger("threshold", 2);
            this.putInteger("set", 3);
        }};

        setTemporaryHitPointDiceMatchingOrBelow.execute(null, temporaryHitPointRoll, functionJson, context);

        String expected = """
                [{"bonus":0,"dice":[{"determined":[],"roll":3,"size":6}]},{"bonus":0,"dice":[{"determined":[],"roll":3,"size":6}]},{"bonus":0,"dice":[{"determined":[],"roll":3,"size":6}]},{"bonus":0,"dice":[{"determined":[],"roll":4,"size":6}]}]""";
        assertEquals(expected, temporaryHitPointRoll.getTemporaryHitPoints().toString(),
                "execute should set all dice which rolled 2 or lower to 3"
        );
    }

}