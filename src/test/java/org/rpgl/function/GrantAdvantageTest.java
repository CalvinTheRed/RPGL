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
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.Roll;
import org.rpgl.subevent.Subevent;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.function.GrantDisadvantage class.
 *
 * @author Calvin Withun
 */
public class GrantAdvantageTest {

    private Roll roll;

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
    @DisplayName("execute wrong function")
    void execute_wrongFunction_throwsException() {
        Function function = new GrantAdvantage();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "not_a_function"
            }*/
            this.putString("function", "not_a_function");
        }};

        DummyContext context = new DummyContext();

        assertThrows(FunctionMismatchException.class,
                () -> function.execute(null, null, functionJson, context),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("execute grants advantage to roll")
    void execute_grantsAdvantageToRoll() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std_objects:commoner");
        RPGLObject target = RPGLFactory.newObject("std_objects:commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        roll.setSource(source);
        roll.prepare(context);
        roll.setTarget(target);

        GrantAdvantage grantAdvantage = new GrantAdvantage();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "grant_advantage"
            }*/
            this.putString("function", "grant_advantage");
        }};

        grantAdvantage.execute(null, roll, functionJson, context);

        assertTrue(roll.isAdvantageRoll(),
                "execute should grant advantage to roll"
        );
    }

}
