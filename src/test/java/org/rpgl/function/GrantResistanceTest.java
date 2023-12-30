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
 * Testing class for the org.rpgl.function.GrantResistance class.
 *
 * @author Calvin Withun
 */
public class GrantResistanceTest {

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
                () -> new GrantResistance().execute(null, null, new JsonObject() {{
                    /*{
                        "function": "not_a_function"
                    }*/
                    this.putString("function", "not_a_function");
                }}, new DummyContext(), List.of()),
                "Function should throw a FunctionMismatchException if the specified function doesn't match"
        );
    }

    @Test
    @DisplayName("grants resistance (specific damage type)")
    void grantsResistance_specificDamageType() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.setSource(object);
        damageAffinity.addDamageType("fire");
        damageAffinity.addDamageType("cold");
        damageAffinity.prepare(new DummyContext(), List.of());

        new GrantResistance().execute(null, damageAffinity, new JsonObject() {{
            /*{
                "function": "grant_resistance",
                "damage_type": "fire"
            }*/
            this.putString("function", "grant_resistance");
            this.putString("damage_type", "fire");
        }}, new DummyContext(), List.of());

        assertTrue(damageAffinity.isResistant("fire"),
                "execute should grant resistance to fire damage"
        );
        assertFalse(damageAffinity.isResistant("cold"),
                "execute should not grant resistance to other damage types"
        );
    }

    @Test
    @DisplayName("grants resistance (all damage types)")
    void grantsResistance_allDamageTypes() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.setSource(object);
        damageAffinity.addDamageType("fire");
        damageAffinity.addDamageType("cold");
        damageAffinity.prepare(new DummyContext(), List.of());

        new GrantResistance().execute(null, damageAffinity, new JsonObject() {{
            /*{
                "function": "grant_resistance"
            }*/
            this.putString("function", "grant_resistance");
        }}, new DummyContext(), List.of());

        assertTrue(damageAffinity.isResistant("fire"),
                "execute should grant resistance to fire damage"
        );
        assertTrue(damageAffinity.isResistant("cold"),
                "execute should grant resistance to cold damage"
        );
    }

}
