package org.rpgl.condition;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.AbilityCheck;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.condition.CheckSkill class.
 *
 * @author Calvin Withun
 */
public class CheckSkillTest {

    @BeforeAll
    static void beforeAll() {
        RPGLCore.initializeTesting();
    }

    @Test
    @DisplayName("errors on wrong condition")
    void errorsOnWrongCondition() {
        assertThrows(ConditionMismatchException.class,
                () -> new CheckSkill().evaluate(null, null, new JsonObject() {{
                    /*{
                        "condition": "not_a_condition"
                    }*/
                    this.putString("condition", "not_a_condition");
                }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "Condition should throw a ConditionMismatchException if the specified condition doesn't match"
        );
    }

    @Test
    @DisplayName("evaluates true")
    void evaluatesTrue() throws Exception {
        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.joinSubeventData(new JsonObject() {{
            this.putString("skill", "athletics");
        }});

        assertTrue(new CheckSkill().evaluate(null, abilityCheck, new JsonObject() {{
            /*{
                "condition": "check_skill",
                "skill": "athletics"
            }*/
            this.putString("condition", "check_skill");
            this.putString("skill", "athletics");
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "evaluate should return true when subevent uses indicated skill"
        );
    }

    @Test
    @DisplayName("evaluates false")
    void evaluatesFalse() throws Exception {
        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.joinSubeventData(new JsonObject() {{
            this.putString("skill", "not_athletics");
        }});

        assertFalse(new CheckSkill().evaluate(null, abilityCheck, new JsonObject() {{
            /*{
                "condition": "check_skill",
                "skill": "athletics"
            }*/
            this.putString("condition", "check_skill");
            this.putString("skill", "athletics");
        }}, new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "evaluate should return false when subevent does not use indicated skill"
        );
    }

}
