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
import org.rpgl.subevent.Roll;
import org.rpgl.subevent.Subevent;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.function.GrantDisadvantage class.
 *
 * @author Calvin Withun
 */
public class GrantDisadvantageTest {

    private Roll roll;

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
        roll = new Roll("roll") {
            @Override
            public String getAbility(RPGLContext context) {
                return null;
            }

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
                () -> new GrantDisadvantage().execute(null, null, new JsonObject() {{
                    /*{
                        "function": "not_a_function"
                    }*/
                    this.putString("function", "not_a_function");
                }}, new DummyContext(), List.of()),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("grants disadvantage")
    void grantsDisadvantage() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        roll.setSource(object);
        roll.prepare(new DummyContext(), List.of());

        new GrantDisadvantage().execute(null, roll, new JsonObject() {{
            /*{
                "function": "grant_disadvantage"
            }*/
            this.putString("function", "grant_disadvantage");
        }}, new DummyContext(), List.of());

        assertTrue(roll.isDisadvantageRoll(),
                "execute should grant disadvantage to roll"
        );
    }

}
