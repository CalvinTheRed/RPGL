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
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Calculation;
import org.rpgl.subevent.Subevent;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

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
    @DisplayName("errors on wrong function")
    void errorsOnWrongFunction() {
        assertThrows(FunctionMismatchException.class,
                () -> new SetMinimum().execute(null, null, new JsonObject() {{
                    /*{
                        "function": "not_a_function"
                    }*/
                    this.putString("function", "not_a_function");
                }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("sets minimum")
    void setsMinimum() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        calculation.setSource(object);
        calculation.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

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
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertEquals(13, calculation.getMinimum(),
                "execute should set calculation minimum to 13"
        );
    }

}
