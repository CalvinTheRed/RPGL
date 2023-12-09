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
import org.rpgl.subevent.DamageAffinity;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.function.RevokeResistance class.
 *
 * @author Calvin Withun
 */
public class RevokeResistanceTest {

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
        Function function = new RevokeResistance();
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
    @DisplayName("execute grants resistance for single damage type")
    void execute_revokesResistanceForSingleDamageType() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        String damageTypeFire = "fire";
        String damageTypeCold = "cold";

        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.addDamageType(damageTypeFire);
        damageAffinity.addDamageType(damageTypeCold);
        damageAffinity.setSource(source);
        damageAffinity.prepare(context, List.of());
        damageAffinity.setTarget(target);
        damageAffinity.grantResistance(damageTypeFire);
        damageAffinity.grantResistance(damageTypeCold);

        RevokeResistance revokeResistance = new RevokeResistance();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "revoke_resistance",
                "damage_type": "fire"
            }*/
            this.putString("function", "revoke_resistance");
            this.putString("damage_type", damageTypeFire);
        }};

        revokeResistance.execute(null, damageAffinity, functionJson, context, List.of());

        assertFalse(damageAffinity.isResistant(damageTypeFire),
                "execute should revoke resistance to counter the granted fire resistance"
        );
        assertTrue(damageAffinity.isResistant(damageTypeCold),
                "execute should not revoke resistance to counter the granted cold resistance"
        );
    }

    @Test
    @DisplayName("execute grants resistance for all damage types")
    void execute_revokesResistanceForAllDamageTypes() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner", TestUtils.TEST_USER);
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        String damageTypeFire = "fire";
        String damageTypeCold = "cold";

        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.addDamageType(damageTypeFire);
        damageAffinity.addDamageType(damageTypeCold);
        damageAffinity.setSource(source);
        damageAffinity.prepare(context, List.of());
        damageAffinity.setTarget(target);
        damageAffinity.grantResistance(damageTypeFire);
        damageAffinity.grantResistance(damageTypeCold);

        RevokeResistance revokeResistance = new RevokeResistance();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "revoke_resistance"
            }*/
            this.putString("function", "revoke_resistance");
        }};

        revokeResistance.execute(null, damageAffinity, functionJson, context, List.of());

        assertFalse(damageAffinity.isResistant(damageTypeFire),
                "execute should revoke resistance to counter the granted fire resistance"
        );
        assertFalse(damageAffinity.isResistant(damageTypeCold),
                "execute should revoke resistance to counter the granted cold resistance"
        );
    }

}
