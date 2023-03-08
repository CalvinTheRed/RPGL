package org.rpgl.function;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.HealingRoll;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.MaximizeHealing class.
 *
 * @author Calvin Withun
 */
public class MaximizeHealingTest {

    private HealingRoll healingRoll;

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

    @BeforeEach
    void beforeEach() {
        healingRoll = new HealingRoll();
        healingRoll.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "healing_roll",
                "dice": [
                    { "size": 6, "determined": [ 1 ] },
                    { "size": 6, "determined": [ 1 ] }
                ],
                "bonus": 2
            }*/
            this.putString("subevent", "healing_roll");
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
    }

    @AfterEach
    void afterEach() {
        UUIDTable.clear();
    }

    @Test
    @DisplayName("execute wrong function")
    void execute_wrongFunction_throwsException() {
        Function function = new MaximizeHealing();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "not_a_function"
            }*/
            this.putString("function", "not_a_function");
        }};

        RPGLContext context = new RPGLContext();

        assertThrows(FunctionMismatchException.class,
                () -> function.execute(null, null, null, functionJson, context),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("execute maximizes specific damage type")
    void execute_maximizesSpecificDamageType() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        healingRoll.setSource(source);
        healingRoll.prepare(context);
        healingRoll.setTarget(target);

        MaximizeHealing maximizeHealing = new MaximizeHealing();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "maximize_healing"
            }*/
            this.putString("function", "maximize_healing");
        }};

        maximizeHealing.execute(source, target, healingRoll, functionJson, context);

        String expected = """
                {"bonus":2,"dice":[{"determined":[],"roll":6,"size":6},{"determined":[],"roll":6,"size":6}]}""";
        assertEquals(expected, healingRoll.getHealing().toString(),
                "execute should set all healing dice to their maximum face value"
        );
    }

}
