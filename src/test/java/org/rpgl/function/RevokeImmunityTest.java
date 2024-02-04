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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.function.RevokeImmunity class.
 *
 * @author Calvin Withun
 */
public class RevokeImmunityTest {

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
                () -> new RevokeImmunity().execute(null, null, new JsonObject() {{
                    /*{
                        "function": "not_a_function"
                    }*/
                    this.putString("function", "not_a_function");
                }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("revokes immunity (specific damage type)")
    void revokesImmunity_specificDamageType() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.setSource(object);
        damageAffinity.addDamageType("fire");
        damageAffinity.addDamageType("cold");
        damageAffinity.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);
        damageAffinity.grantImmunity("fire");
        damageAffinity.grantImmunity("cold");

        new RevokeImmunity().execute(null, damageAffinity, new JsonObject() {{
            /*{
                "function": "revoke_immunity",
                "damage_type": "fire"
            }*/
            this.putString("function", "revoke_immunity");
            this.putString("damage_type", "fire");
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertFalse(damageAffinity.isImmune("fire"),
                "execute should revoke immunity to counter the granted fire immunity"
        );
        assertTrue(damageAffinity.isImmune("cold"),
                "execute should not revoke immunity to counter the granted cold immunity"
        );
    }

    @Test
    @DisplayName("revokes immunity (all damage types)")
    void revokesImmunity_allDamageTypes() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.setSource(object);
        damageAffinity.addDamageType("fire");
        damageAffinity.addDamageType("cold");
        damageAffinity.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);
        damageAffinity.grantImmunity("fire");
        damageAffinity.grantImmunity("cold");

        new RevokeImmunity().execute(null, damageAffinity, new JsonObject() {{
            /*{
                "function": "revoke_immunity"
            }*/
            this.putString("function", "revoke_immunity");
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertFalse(damageAffinity.isImmune("fire"),
                "execute should revoke immunity to counter the granted fire immunity"
        );
        assertFalse(damageAffinity.isImmune("cold"),
                "execute should revoke immunity to counter the granted cold immunity"
        );
    }

}
