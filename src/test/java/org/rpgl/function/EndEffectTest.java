package org.rpgl.function;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DummySubevent;
import org.rpgl.subevent.Subevent;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.function.EndEffect class.
 *
 * @author Calvin Withun
 */
public class EndEffectTest {

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

    @AfterEach
    void afterEach() {
        UUIDTable.clear();
    }

    @Test
    @DisplayName("execute wrong function")
    void execute_wrongFunction_throwsException() {
        Function function = new EndEffect();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "not_a_function"
            }*/
            this.putString("function", "not_a_function");
        }};

        RPGLContext context = new RPGLContext();

        assertThrows(FunctionMismatchException.class,
                () -> function.execute(null, null, functionJson, context),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("")
    void test() throws Exception {
        RPGLObject commoner = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(commoner);

        RPGLEffect fireImmunity = RPGLFactory.newEffect("demo:fire_immunity");
        fireImmunity.setSource(commoner);
        fireImmunity.setTarget(commoner);

        commoner.addEffect(fireImmunity);
        assertTrue(commoner.getEffects().asList().contains(fireImmunity.getUuid()),
                "condition should be successfully assigned to commoner before it is ended"
        );

        Subevent subevent = new DummySubevent();

        EndEffect endEffect = new EndEffect();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "end_effect"
            }*/
            this.putString("function", "end_effect");
        }};

        endEffect.execute(fireImmunity, subevent, functionJson, context);

        assertFalse(commoner.getEffects().asList().contains(fireImmunity.getUuid()),
                "commoner should no longer have effect after it is ended"
        );
    }

}
