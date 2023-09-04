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
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.FunctionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DamageAffinity;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;
import java.util.Objects;

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
        Function function = new GrantResistance();
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
    @DisplayName("execute grants resistance to a specific damage type")
    void execute_grantsResistance_specificDamageType() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
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

        GrantResistance grantResistance = new GrantResistance();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "grant_resistance",
                "damage_type": "fire"
            }*/
            this.putString("function", "grant_resistance");
            this.putString("damage_type", damageTypeFire);
        }};

        grantResistance.execute(null, damageAffinity, functionJson, context, List.of());

        assertTrue(damageAffinity.isResistant(damageTypeFire),
                "execute should grant resistance to fire damage"
        );
        assertFalse(damageAffinity.isResistant(damageTypeCold),
                "execute should not grant resistance to other damage types"
        );
    }

    @Test
    @DisplayName("execute grants resistance to all damage types with unlisted damage type")
    void execute_grantsResistanceToAllDamageTypes_unlistedDamageType() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
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

        GrantResistance grantResistance = new GrantResistance();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "grant_resistance"
            }*/
            this.putString("function", "grant_resistance");
        }};

        grantResistance.execute(null, damageAffinity, functionJson, context, List.of());

        assertTrue(damageAffinity.isResistant(damageTypeFire),
                "execute should grant resistance to fire damage"
        );
        assertTrue(damageAffinity.isResistant(damageTypeCold),
                "execute should grant resistance to cold damage"
        );
    }

}
