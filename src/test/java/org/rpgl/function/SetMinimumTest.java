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
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Calculation;
import org.rpgl.subevent.Subevent;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.function.SetMinimum class.
 *
 * @author Calvin Withun
 */
public class SetMinimumTest {

    private Calculation calculation;

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
        calculation = new Calculation("calculation") {
            @Override
            public Subevent clone() {
                return null;
            }

            @Override
            public Subevent clone(JsonObject jsonData) {
                return null;
            }
        };
    }

    @AfterEach
    void afterEach() {
        UUIDTable.clear();
    }

    @Test
    @DisplayName("execute wrong function")
    void execute_wrongFunction_throwsException() {
        Function function = new SetMinimum();
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
    @DisplayName("execute sets calculation minimum to new value (number)")
    void execute_setsCalculationMinimumToNewValue_number() throws Exception {
        RPGLObject dummy = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLContext context = new DummyContext();

        calculation.setSource(dummy);
        calculation.prepare(context, List.of());
        calculation.setTarget(dummy);
        new SetMinimum().execute(null, calculation, new JsonObject() {{
            /*{
                "function": "set_minimum",
                "minimum": {
                    "formula": "number",
                    "number": 13
                }
            }*/
            this.putString("function", "set_minimum");
            this.putJsonObject("minimum", new JsonObject() {{
                this.putString("formula", "number");
                this.putInteger("number", 13);
            }});
        }}, context, List.of());

        assertEquals(13, calculation.getMinimum(),
                "execute should set calculation minimum to 13"
        );
    }

    @Test
    @DisplayName("execute sets calculation minimum to new value only if new minimum is larger")
    void execute_setsCalculationMinimumToNewValueOnlyIfNewMinimumIsLarger() throws Exception {
        RPGLObject dummy = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();

        calculation.setSource(dummy);
        calculation.prepare(context, List.of());
        calculation.setTarget(dummy);

        // first set minimum to 10
        new SetMinimum().execute(null, calculation, new JsonObject() {{
            /*{
                "function": "set_minimum",
                "minimum": {
                    "formula": "number",
                    "number": 10
                }
            }*/
            this.putString("function", "set_minimum");
            this.putJsonObject("minimum", new JsonObject() {{
                this.putString("formula", "number");
                this.putInteger("number", 10);
            }});
        }}, context, List.of());

        assertEquals(10, calculation.getMinimum(),
                "execute should set calculation minimum to 10"
        );

        // second set the minimum to 15
        new SetMinimum().execute(null, calculation, new JsonObject() {{
            /*{
                "function": "set_minimum",
                "minimum": {
                    "formula": "number",
                    "number": 15
                }
            }*/
            this.putString("function", "set_minimum");
            this.putJsonObject("minimum", new JsonObject() {{
                this.putString("formula", "number");
                this.putInteger("number", 15);
            }});
        }}, context, List.of());

        assertEquals(15, calculation.getMinimum(),
                "execute should set calculation minimum to 15 (15 > 10)"
        );

        // third set the minimum to 5 (should not work)
        new SetMinimum().execute(null, calculation, new JsonObject() {{
            /*{
                "function": "set_minimum",
                "minimum": {
                    "formula": "number",
                    "number": 5
                }
            }*/
            this.putString("function", "set_minimum");
            this.putJsonObject("minimum", new JsonObject() {{
                this.putString("formula", "number");
                this.putInteger("number", 5);
            }});
        }}, context, List.of());

        assertEquals(15, calculation.getMinimum(),
                "execute should not change calculation minimum (5 < 15)"
        );
    }

}
