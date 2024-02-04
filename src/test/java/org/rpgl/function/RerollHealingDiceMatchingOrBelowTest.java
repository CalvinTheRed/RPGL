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
import org.rpgl.subevent.HealingRoll;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.RerollHealingDiceMatchingOrBelow class.
 *
 * @author Calvin Withun
 */
public class RerollHealingDiceMatchingOrBelowTest {

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
                () -> new RerollHealingDiceMatchingOrBelow().execute(null, null, new JsonObject() {{
                    /*{
                        "function": "not_a_function"
                    }*/
                    this.putString("function", "not_a_function");
                }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("re-rolls all dice at or below value")
    void rerollsAllDiceAtOrBelowValue() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        HealingRoll healingRoll = new HealingRoll();
        healingRoll.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
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
            this.putJsonArray("healing", new JsonArray() {{
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
        healingRoll.setSource(object);
        healingRoll.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        new RerollHealingDiceMatchingOrBelow().execute(null, healingRoll, new JsonObject() {{
            /*{
                "function": "reroll_healing_dice_matching_or_below",
                "threshold": 2
            }*/
            this.putString("function", "reroll_healing_dice_matching_or_below");
            this.putInteger("threshold", 2);
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        String expected = """
                [{"bonus":0,"dice":[{"determined":[],"roll":6,"size":6}]},{"bonus":0,"dice":[{"determined":[],"roll":6,"size":6}]},{"bonus":0,"dice":[{"determined":[6],"roll":3,"size":6}]},{"bonus":0,"dice":[{"determined":[6],"roll":4,"size":6}]}]""";
        assertEquals(expected, healingRoll.getHealing().toString(),
                "execute should re-roll all dice which rolled 2 or lower to 3"
        );
    }

}
