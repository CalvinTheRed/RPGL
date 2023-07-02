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
import java.util.Objects;

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
        Function function = new GrantImmunity();
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
    @DisplayName("execute grants immunity to a specific damage type")
    void execute_grantsImmunity_specificDamageType() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:commoner");
        RPGLObject target = RPGLFactory.newObject("std:commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        String damageTypeFire = "fire";
        String damageTypeCold = "cold";

        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.addDamageType(damageTypeFire);
        damageAffinity.addDamageType(damageTypeCold);
        damageAffinity.setSource(source);
        damageAffinity.prepare(context);
        damageAffinity.setTarget(target);

        GrantImmunity grantImmunity = new GrantImmunity();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "grant_immunity",
                "damage_type": "fire"
            }*/
            this.putString("function", "grant_immunity");
            this.putString("damage_type", damageTypeFire);
        }};

        grantImmunity.execute(null, damageAffinity, functionJson, context);

        assertTrue(damageAffinity.isImmune(damageTypeFire),
                "execute should grant immunity to fire damage"
        );
        assertFalse(damageAffinity.isImmune(damageTypeCold),
                "execute should not grant immunity to other damage types"
        );
    }

    @Test
    @DisplayName("execute grants immunity to all damage types with unlisted damage type")
    void execute_grantsImmunityToAllDamageTypes_unlistedDamageType() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:commoner");
        RPGLObject target = RPGLFactory.newObject("std:commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        String damageTypeFire = "fire";
        String damageTypeCold = "cold";

        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.addDamageType(damageTypeFire);
        damageAffinity.addDamageType(damageTypeCold);
        damageAffinity.setSource(source);
        damageAffinity.prepare(context);
        damageAffinity.setTarget(target);

        GrantImmunity grantImmunity = new GrantImmunity();
        JsonObject functionJson = new JsonObject() {{
            /*{
                "function": "grant_immunity"
            }*/
            this.putString("function", "grant_immunity");
        }};

        grantImmunity.execute(null, damageAffinity, functionJson, context);

        assertTrue(damageAffinity.isImmune(damageTypeFire),
                "execute should grant immunity to fire damage"
        );
        assertTrue(damageAffinity.isImmune(damageTypeCold),
                "execute should grant immunity to cold damage"
        );
    }

}
