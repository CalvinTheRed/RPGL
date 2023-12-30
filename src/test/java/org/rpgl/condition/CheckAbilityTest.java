package org.rpgl.condition;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DummySubevent;
import org.rpgl.testUtils.DummyContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.condition.CheckAbility class.
 *
 * @author Calvin Withun
 */
public class CheckAbilityTest {

    @BeforeAll
    static void beforeAll() {
        RPGLCore.initializeTesting();
    }

    @Test
    @DisplayName("errors on wrong condition")
    void errorsOnWrongCondition() {
        assertThrows(ConditionMismatchException.class,
                () -> new CheckAbility().evaluate(null, null, new JsonObject() {{
                    /*{
                        "condition": "not_a_condition"
                    }*/
                    this.putString("condition", "not_a_condition");
                }}, new DummyContext()),
                "Condition should throw a ConditionMismatchException if the specified condition doesn't match"
        );
    }

    @Test
    @DisplayName("evaluates true")
    void evaluatesTrue() throws Exception {
        assertTrue(new CheckAbility().evaluate(null, new DummySubevent().setAbility("str"), new JsonObject() {{
            /*{
                "condition": "check_ability",
                "ability": "str"
            }*/
            this.putString("condition", "check_ability");
            this.putString("ability", "str");
        }}, new DummyContext()),
                "evaluate should return true when subevent uses indicated ability"
        );
    }

    @Test
    @DisplayName("evaluates false")
    void evaluatesFalse() throws Exception {
        assertFalse(new CheckAbility().evaluate(null, new DummySubevent(), new JsonObject() {{
            /*{
                "condition": "check_ability",
                "ability": "not_str"
            }*/
            this.putString("condition", "check_ability");
            this.putString("ability", "not_str"); // <-- DummySubevent is a str AbilitySubevent
        }}, new DummyContext()),
                "evaluate should return false when subevent does not use indicated ability"
        );
    }

}
