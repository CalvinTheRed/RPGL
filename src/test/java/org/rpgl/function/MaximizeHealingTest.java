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
import org.rpgl.subevent.HealingDelivery;
import org.rpgl.subevent.HealingRoll;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.MaximizeHealing class.
 *
 * @author Calvin Withun
 */
public class MaximizeHealingTest {

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

    }

    @AfterEach
    void afterEach() {
        UUIDTable.clear();
    }

    @Test
    @DisplayName("errors on wrong function")
    void errorsOnWrongFunction() {
        assertThrows(FunctionMismatchException.class,
                () -> new MaximizeHealing().execute(null, null, new JsonObject() {{
                    /*{
                        "function": "not_a_function"
                    }*/
                    this.putString("function", "not_a_function");
                }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("maximizes healing (healing roll)")
    void maximizesHealing_healingRoll() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        HealingRoll healingRoll = new HealingRoll();
        healingRoll.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "dice": [
                            { "size": 6, "determined": [ 1 ] },
                            { "size": 6, "determined": [ 1 ] }
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
                                this.addInteger(1);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 2);
                }});
            }});
        }});

        healingRoll.setSource(object);
        healingRoll.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        new MaximizeHealing().execute(null, healingRoll, new JsonObject() {{
            /*{
                "function": "maximize_healing"
            }*/
            this.putString("function", "maximize_healing");
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        String expected = """
                [{"bonus":2,"dice":[{"determined":[],"roll":6,"size":6},{"determined":[],"roll":6,"size":6}]}]""";
        assertEquals(expected, healingRoll.getHealing().toString(),
                "execute should set all healing dice to their maximum face value"
        );
    }

    @Test
    @DisplayName("maximizes healing (healing delivery)")
    void maximizesHealing_healingDelivery() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        HealingDelivery healingDelivery = new HealingDelivery();
        healingDelivery.joinSubeventData(new JsonObject() {{
            /*{
                "healing": [
                    {
                        "dice": [
                            { "roll": 1, "size": 4 },
                            { "roll": 1, "size": 6 }
                            { "roll": 1, "size": 8 }
                        ],
                        "bonus": 0
                    }
                ]
            }*/
            this.putJsonArray("healing", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("roll", 1);
                            this.putInteger("size", 4);
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("roll", 1);
                            this.putInteger("size", 6);
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("roll", 1);
                            this.putInteger("size", 8);
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
        }});

        healingDelivery.setSource(object);
        healingDelivery.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        new MaximizeHealing().execute(null, healingDelivery, new JsonObject() {{
            /*{
                "function": "maximize_healing"
            }*/
            this.putString("function", "maximize_healing");
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertEquals(4+6+8, healingDelivery.getHealing(),
                "execute should set all healing dice to their maximum face value"
        );
    }

}
