package org.rpgl.condition;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DamageAffinity;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.condition.CheckDamageType class.
 *
 * @author Calvin Withun
 */
public class CheckDamageTypeTest {

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
    @DisplayName("evaluate wrong condition")
    void evaluate_wrongCondition_throwsException() {
        Condition condition = new CheckDamageType();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "not_a_condition"
            }*/
            this.putString("condition", "not_a_condition");
        }};

        RPGLContext context = new RPGLContext();

        assertThrows(ConditionMismatchException.class,
                () -> condition.evaluate(null, null, conditionJson, context),
                "Condition should throw a ConditionMismatchException if the specified condition doesn't match"
        );
    }

    @Test
    @DisplayName("evaluate returns true for affinity if desired damage type")
    void evaluate_returnsTrueForAffinityIfDesiredDamageType() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.joinSubeventData(new JsonObject() {{
            this.putString("type", "fire");
        }});

        CheckDamageType checkDamageType = new CheckDamageType();

        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "check_damage_type",
                "type": "fire"
            }*/
            this.putString("condition", "check_damage_type");
            this.putString("type", "fire");
        }};

        assertTrue(checkDamageType.evaluate(null, damageAffinity, conditionJson, context),
                "evaluate should return true when damage affinity is for the desired damage type"
        );
    }

    @Test
    @DisplayName("evaluate returns false for affinity if undesired damage type")
    void evaluate_returnsFalseForAffinityIfUndesiredDamageType() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.joinSubeventData(new JsonObject() {{
            this.putString("type", "fire");
        }});

        CheckDamageType checkDamageType = new CheckDamageType();

        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "check_damage_type",
                "type": "cold"
            }*/
            this.putString("condition", "check_damage_type");
            this.putString("type", "cold");
        }};

        assertFalse(checkDamageType.evaluate(null, damageAffinity, conditionJson, context),
                "evaluate should return false when damage affinity is for an undesired damage type"
        );
    }

}
