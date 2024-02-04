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
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

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
    @DisplayName("errors on wrong function")
    void errorsOnWrongFunction() {
        assertThrows(FunctionMismatchException.class,
                () -> new EndEffect().execute(null, null, new JsonObject() {{
                    /*{
                        "function": "not_a_function"
                    }*/
                    this.putString("function", "not_a_function");
                }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("removes effect from object")
    void removesEffectFromObject() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        RPGLEffect fireImmunity = RPGLFactory.newEffect("std:common/damage/immunity/fire");
        fireImmunity.setSource(object);
        fireImmunity.setTarget(object);

        object.addEffect(fireImmunity);
        assertTrue(object.getEffects().asList().contains(fireImmunity.getUuid()),
                "condition should be successfully assigned to commoner before it is ended"
        );

        new EndEffect().execute(fireImmunity, null, new JsonObject() {{
            /*{
                "function": "end_effect"
            }*/
            this.putString("function", "end_effect");
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertFalse(object.getEffects().asList().contains(fireImmunity.getUuid()),
                "object should no longer have effect after it is ended"
        );
    }

}
