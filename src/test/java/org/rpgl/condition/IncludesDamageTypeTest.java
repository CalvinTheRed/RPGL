package org.rpgl.condition;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DamageAffinity;
import org.rpgl.testUtils.DummyContext;

import java.util.List;

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
    static void beforeAll() {
        RPGLCore.initializeTesting();
    }

    @Test
    @DisplayName("errors on wrong condition")
    void errorsOnWrongCondition() {
        assertThrows(ConditionMismatchException.class,
                () -> new IncludesDamageType().evaluate(null, null, new JsonObject() {{
                    /*{
                        "condition": "not_a_condition"
                    }*/
                    this.putString("condition", "not_a_condition");
                }}, new DummyContext()),
                "Condition should throw a ConditionMismatchException if the specified condition doesn't match"
        );
    }

    @Test
    @DisplayName("evaluates true (damage type included)")
    void evaluatesTrue_damageTypeIncluded() throws Exception {
        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.addDamageType("fire");

        assertTrue(new IncludesDamageType().evaluate(null, damageAffinity, new JsonObject() {{
            /*{
                "condition": "includes_damage_type",
                "damage_type": "fire"
            }*/
            this.putString("condition", "includes_damage_type");
            this.putString("damage_type", "fire");
        }}, new DummyContext()),
                "should evaluate true when damage type included"
        );
    }

    @Test
    @DisplayName("evaluates false (damage type not included)")
    void evaluatesFalse_damageTypeNotIncluded() throws Exception {
        DamageAffinity damageAffinity = new DamageAffinity();
        damageAffinity.prepare(new DummyContext(), List.of());

        assertFalse(new IncludesDamageType().evaluate(null, damageAffinity, new JsonObject() {{
            /*{
                "condition": "includes_damage_type",
                "damage_type": "fire"
            }*/
            this.putString("condition", "includes_damage_type");
            this.putString("damage_type", "fire");
        }}, new DummyContext()),
                "should evaluate false when damage type not included"
        );
    }

}
