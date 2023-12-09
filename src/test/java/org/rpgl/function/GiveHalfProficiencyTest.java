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
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.AbilityCheck;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.function.GiveHalfProficiency class.
 *
 * @author Calvin Withun
 */
public class GiveHalfProficiencyTest {

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
        Function function = new GiveHalfProficiency();
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
    @DisplayName("execute gives half proficiency")
    void execute_givesHalfProficiency() throws Exception {
        RPGLObject object = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(object);

        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.setSource(object);
        abilityCheck.prepare(context, List.of());
        abilityCheck.setTarget(object);

        GiveHalfProficiency giveHalfProficiency = new GiveHalfProficiency();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "give_half_proficiency"
            }*/
            this.putString("function", "give_half_proficiency");
        }};

        giveHalfProficiency.execute(null, abilityCheck, functionJson, context, List.of());

        assertTrue(abilityCheck.hasHalfProficiency(),
                "execute should give half proficiency to roll"
        );
    }

}
