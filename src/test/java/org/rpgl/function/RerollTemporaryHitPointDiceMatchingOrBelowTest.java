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
import org.rpgl.subevent.TemporaryHitPointRoll;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.RerollTemporaryHitPointDiceMatchingOrBelow class.
 *
 * @author Calvin Withun
 */
public class RerollTemporaryHitPointDiceMatchingOrBelowTest {

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
                () -> new RerollTemporaryHitPointDiceMatchingOrBelow().execute(null, null, new JsonObject() {{
                    /*{
                        "function": "not_a_function"
                    }*/
                    this.putString("function", "not_a_function");
                }}, new DummyContext()),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("re-rolls all dice at or below value")
    void rerollsAllDiceAtOrBelowValue() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        TemporaryHitPointRoll temporaryHitPointRoll = new TemporaryHitPointRoll();
        temporaryHitPointRoll.joinSubeventData(new JsonObject() {{
            /*{
                "temporary_hit_points": [
                    {
                        "dice": [
                            { "size": 6, "determined": [ 1, 6 ] }
                        ],
                        "bonus": 0
                    },
                    {
                        "dice": [
                            { "size": 6, "determined": [ 2, 6 ] }
                        ],
                        "bonus": 0
                    },
                    {
                        "dice": [
                            { "size": 6, "determined": [ 3, 6 ] }
                        ],
                        "bonus": 0
                    },
                    {
                        "dice": [
                            { "size": 6, "determined": [ 4, 6 ] }
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
                                this.addInteger(6);
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
                                this.addInteger(6);
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
                                this.addInteger(6);
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
                                this.addInteger(6);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
        }});
        temporaryHitPointRoll.setSource(object);
        temporaryHitPointRoll.prepare(new DummyContext());

        new RerollTemporaryHitPointDiceMatchingOrBelow().execute(null, temporaryHitPointRoll, new JsonObject() {{
            /*{
                "function": "reroll_temporary_hit_point_dice_matching_or_below",
                "threshold": 2
            }*/
            this.putString("function", "reroll_temporary_hit_point_dice_matching_or_below");
            this.putInteger("threshold", 2);
        }}, new DummyContext());

        String expected = """
                [{"bonus":0,"dice":[{"determined":[],"roll":6,"size":6}]},{"bonus":0,"dice":[{"determined":[],"roll":6,"size":6}]},{"bonus":0,"dice":[{"determined":[6],"roll":3,"size":6}]},{"bonus":0,"dice":[{"determined":[6],"roll":4,"size":6}]}]""";
        assertEquals(expected, temporaryHitPointRoll.getTemporaryHitPoints().toString(),
                "execute should re-roll all dice which rolled 2 or lower to 3"
        );
    }

}
