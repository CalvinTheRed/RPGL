package org.rpgl.function;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DummySubevent;
import org.rpgl.subevent.Subevent;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;

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
        Function function = new EndEffect();
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
    @DisplayName("execute removes effect from object")
    void execute_removesEffectFromObject() throws Exception {
        RPGLObject commoner = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(commoner);

        RPGLEffect fireImmunity = RPGLFactory.newEffect("std:common/damage/immunity/fire");
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

        endEffect.execute(fireImmunity, subevent, functionJson, context, List.of());

        assertFalse(commoner.getEffects().asList().contains(fireImmunity.getUuid()),
                "commoner should no longer have effect after it is ended"
        );
    }

}
