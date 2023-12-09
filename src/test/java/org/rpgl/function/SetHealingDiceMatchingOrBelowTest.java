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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.SetHealingDiceMatchingOrBelow class.
 *
 * @author Calvin Withun
 */
public class SetHealingDiceMatchingOrBelowTest {

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
    @DisplayName("execute wrong function")
    void execute_wrongFunction_throwsException() {
        Function function = new SetHealingDiceMatchingOrBelow();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "not_a_function"
            }*/
            this.putString("function", "not_a_function");
        }};

        DummyContext context = new DummyContext();

        assertThrows(FunctionMismatchException.class,
                () -> function.execute(null, null, functionJson, context, List.of()),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("execute sets all dice at or below two to three")
    void execute_setsAllDiceAtOrBelowTwoToThree() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        HealingRoll healingRoll = new HealingRoll();
        healingRoll.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
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
            this.putJsonArray("healing", new JsonArray() {{
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
        healingRoll.setSource(source);
        healingRoll.prepare(context, List.of());
        healingRoll.setTarget(target);

        SetHealingDiceMatchingOrBelow setHealingDiceMatchingOrBelow = new SetHealingDiceMatchingOrBelow();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "set_healing_dice_matching_or_below",
                "threshold": 2,
                "set": 3
            }*/
            this.putString("function", "set_healing_dice_matching_or_below");
            this.putInteger("threshold", 2);
            this.putInteger("set", 3);
        }};

        setHealingDiceMatchingOrBelow.execute(null, healingRoll, functionJson, context, List.of());

        String expected = """
                [{"bonus":0,"dice":[{"determined":[],"roll":3,"size":6}]},{"bonus":0,"dice":[{"determined":[],"roll":3,"size":6}]},{"bonus":0,"dice":[{"determined":[],"roll":3,"size":6}]},{"bonus":0,"dice":[{"determined":[],"roll":4,"size":6}]}]""";
        assertEquals(expected, healingRoll.getHealing().toString(),
                "execute should set all dice which rolled 2 or lower to 3"
        );
    }

}
