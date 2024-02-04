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
import org.rpgl.subevent.TemporaryHitPointsDelivery;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.MaximizeTemporaryHitPoints class.
 *
 * @author Calvin Withun
 */
public class MaximizeTemporaryHitPointsTest {

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
                () -> new MaximizeTemporaryHitPoints().execute(null, null, new JsonObject() {{
                    /*{
                        "function": "not_a_function"
                    }*/
                    this.putString("function", "not_a_function");
                }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("maximizes temporary hit points (temporary hit point roll)")
    void maximizesTemporaryHitPoints_temporaryHitPointRoll() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        TemporaryHitPointRoll temporaryHitPointRoll = new TemporaryHitPointRoll();
        temporaryHitPointRoll.joinSubeventData(new JsonObject() {{
            /*{
                "temporary_hit_points": [
                    {
                        "dice": [
                            { "size": 6, "determined": [ 1 ] },
                            { "size": 6, "determined": [ 1 ] }
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
                                this.addInteger(1);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 2);
                }});
            }});
        }});

        temporaryHitPointRoll.setSource(object);
        temporaryHitPointRoll.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        new MaximizeTemporaryHitPoints().execute(null, temporaryHitPointRoll, new JsonObject() {{
            /*{
                "function": "maximize_temporary_hit_points"
            }*/
            this.putString("function", "maximize_temporary_hit_points");
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        String expected = """
                [{"bonus":2,"dice":[{"determined":[],"roll":6,"size":6},{"determined":[],"roll":6,"size":6}]}]""";
        assertEquals(expected, temporaryHitPointRoll.getTemporaryHitPoints().toString(),
                "execute should set all temporary hit point dice to their maximum face value"
        );
    }

    @Test
    @DisplayName("maximizes temporary hit points (temporary hit points delivery)")
    void maximizesTemporaryHitPoints_temporaryHitPointDelivery() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        TemporaryHitPointsDelivery temporaryHitPointsDelivery = new TemporaryHitPointsDelivery();
        temporaryHitPointsDelivery.joinSubeventData(new JsonObject() {{
            /*{
                "temporary_hit_points": [
                    {
                        "dice": [
                            { "roll": 1, "size": 6 },
                            { "roll": 1, "size": 6 }
                        ],
                        "bonus": 0
                    }
                ]
            }*/
            this.putJsonArray("temporary_hit_points", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("roll", 1);
                            this.putInteger("size", 6);
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("roll", 1);
                            this.putInteger("size", 6);
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
        }});

        temporaryHitPointsDelivery.setSource(object);
        temporaryHitPointsDelivery.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        new MaximizeTemporaryHitPoints().execute(null, temporaryHitPointsDelivery, new JsonObject() {{
            /*{
                "function": "maximize_temporary_hit_points"
            }*/
            this.putString("function", "maximize_temporary_hit_points");
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertEquals(12, temporaryHitPointsDelivery.getTemporaryHitPoints(),
                "execute should set all temporary hit point dice to their maximum face value"
        );
    }

}
