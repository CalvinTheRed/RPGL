package org.rpgl.condition;

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
import org.rpgl.exception.ConditionMismatchException;
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
 * Testing class for the org.rpgl.condition.IncludesDamageType class.
 *
 * @author Calvin Withun
 */
public class IncludesDamageTypeTest {

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
        Condition condition = new IncludesDamageType();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "not_a_condition"
            }*/
            this.putString("condition", "not_a_condition");
        }};

        DummyContext context = new DummyContext();

        assertThrows(ConditionMismatchException.class,
                () -> condition.evaluate(null, null, conditionJson, context),
                "Condition should throw a ConditionMismatchException if the specified condition doesn't match"
        );
    }

    @Test
    @DisplayName("evaluate returns true if damage type is present")
    void evaluate_returnsTrueIfDamageTypeIsPresent() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        String damageTypeFire = "fire";

        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.addDamageType(damageTypeFire);

        IncludesDamageType includesDamageType = new IncludesDamageType();

        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "includes_damage_type",
                "damage_type": "fire"
            }*/
            this.putString("condition", "includes_damage_type");
            this.putString("damage_type", damageTypeFire);
        }};

        assertTrue(includesDamageType.evaluate(null, damageAffinity, conditionJson, context),
                "evaluate should return true when damage affinity includes the specified damage type"
        );
    }

    @Test
    @DisplayName("evaluate returns false if damage type is not present")
    void evaluate_returnsFalseIfDamageTypeIsNotPresent() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.addDamageType("cold");

        IncludesDamageType includesDamageType = new IncludesDamageType();

        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "includes_damage_type",
                "damage_type": "fire"
            }*/
            this.putString("condition", "includes_damage_type");
            this.putString("damage_type", "fire");
        }};

        assertFalse(includesDamageType.evaluate(null, damageAffinity, conditionJson, context),
                "evaluate should return false when damage affinity does not include the specified damage type"
        );
    }

}
