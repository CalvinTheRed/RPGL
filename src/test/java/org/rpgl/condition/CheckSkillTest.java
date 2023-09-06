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
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.AbilityCheck;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

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
    @DisplayName("evaluate wrong condition")
    void evaluate_wrongCondition_throwsException() {
        Condition condition = new CheckSkill();
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
    @DisplayName("evaluate returns true when Subevent uses indicated skill")
    void evaluate_returnsTrueWhenSubeventUsesIndicatedSkill() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.joinSubeventData(new JsonObject() {{
            this.putString("skill", "athletics");
        }});

        CheckSkill checkSkill = new CheckSkill();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "check_skill",
                "skill": "athletics"
            }*/
            this.putString("condition", "check_skill");
            this.putString("skill", "athletics");
        }};

        assertTrue(checkSkill.evaluate(null, abilityCheck, conditionJson, context),
                "evaluate should return true when subevent uses indicated skill"
        );
    }

    @Test
    @DisplayName("evaluate returns false when Subevent uses indicated skill")
    void evaluate_returnsFalseWhenSubeventDoesNotUseIndicatedSkill() throws Exception {
        RPGLObject source = RPGLFactory.newObject("std:humanoid/commoner");
        RPGLObject target = RPGLFactory.newObject("std:humanoid/commoner");
        DummyContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AbilityCheck abilityCheck = new AbilityCheck();
        abilityCheck.joinSubeventData(new JsonObject() {{
            this.putString("skill", "not_athletics");
        }});

        CheckSkill checkSkill = new CheckSkill();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "check_skill",
                "skill": "athletics"
            }*/
            this.putString("condition", "check_skill");
            this.putString("skill", "athletics");
        }};

        assertFalse(checkSkill.evaluate(null, abilityCheck, conditionJson, context),
                "evaluate should return false when subevent does not use indicated skill"
        );
    }

}
