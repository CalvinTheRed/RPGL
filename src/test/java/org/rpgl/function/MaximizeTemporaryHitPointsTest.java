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
import java.util.List;

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
    @DisplayName("execute wrong function")
    void execute_wrongFunction_throwsException() {
        Function function = new MaximizeTemporaryHitPoints();
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
    @DisplayName("execute maximizes temporary hit points for TemporaryHitPointRoll")
    void execute_maximizesTemporaryHitPointsForTemporaryHitPointRoll() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

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

        temporaryHitPointRoll.setSource(source);
        temporaryHitPointRoll.prepare(context, List.of());
        temporaryHitPointRoll.setTarget(target);

        MaximizeTemporaryHitPoints maximizeTemporaryHitPoints = new MaximizeTemporaryHitPoints();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "maximize_temporary_hit_points"
            }*/
            this.putString("function", "maximize_temporary_hit_points");
        }};

        maximizeTemporaryHitPoints.execute(null, temporaryHitPointRoll, functionJson, context, List.of());

        String expected = """
                [{"bonus":2,"dice":[{"determined":[],"roll":6,"size":6},{"determined":[],"roll":6,"size":6}]}]""";
        assertEquals(expected, temporaryHitPointRoll.getTemporaryHitPoints().toString(),
                "execute should set all temporary hit point dice to their maximum face value"
        );
    }

    @Test
    @DisplayName("execute maximizes temporary hit points for TemporaryHitPointsDelivery")
    void execute_maximizesTemporaryHitPointsForTemporaryHitPointDelivery() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

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

        temporaryHitPointsDelivery.setSource(source);
        temporaryHitPointsDelivery.prepare(context, List.of());
        temporaryHitPointsDelivery.setTarget(target);

        MaximizeTemporaryHitPoints maximizeTemporaryHitPoints = new MaximizeTemporaryHitPoints();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "maximize_temporary_hit_points"
            }*/
            this.putString("function", "maximize_temporary_hit_points");
        }};

        maximizeTemporaryHitPoints.execute(null, temporaryHitPointsDelivery, functionJson, context, List.of());

        assertEquals(12, temporaryHitPointsDelivery.getTemporaryHitPoints(),
                "execute should set all temporary hit point dice to their maximum face value"
        );
    }

}
