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
 * Testing class for the org.rpgl.function.GrantImmunity class.
 *
 * @author Calvin Withun
 */
public class GrantImmunityTest {

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
                () -> new GrantImmunity().execute(null, null, new JsonObject() {{
                    /*{
                        "function": "not_a_function"
                    }*/
                    this.putString("function", "not_a_function");
                }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("grants immunity (specific damage type)")
    void grantsImmunity_specificDamageType() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.setSource(object);
        damageAffinity.addDamageType("fire");
        damageAffinity.addDamageType("cold");
        damageAffinity.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        new GrantImmunity().execute(null, damageAffinity, new JsonObject() {{
            /*{
                "function": "grant_immunity",
                "damage_type": "fire"
            }*/
            this.putString("function", "grant_immunity");
            this.putString("damage_type", "fire");
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertTrue(damageAffinity.isImmune("fire"),
                "execute should grant immunity to fire damage"
        );
        assertFalse(damageAffinity.isImmune("cold"),
                "execute should not grant immunity to other damage types"
        );
    }

    @Test
    @DisplayName("grants immunity (all damage types)")
    void grantsImmunity_allDamageTypes() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.setSource(object);
        damageAffinity.addDamageType("fire");
        damageAffinity.addDamageType("cold");
        damageAffinity.prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        new GrantImmunity().execute(null, damageAffinity, new JsonObject() {{
            /*{
                "function": "grant_immunity"
            }*/
            this.putString("function", "grant_immunity");
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertTrue(damageAffinity.isImmune("fire"),
                "execute should grant immunity to fire damage"
        );
        assertTrue(damageAffinity.isImmune("cold"),
                "execute should grant immunity to cold damage"
        );
    }

}
