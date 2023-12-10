package org.rpgl.condition;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLCore;
import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DummySubevent;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.condition.UserIdsMatch class.
 *
 * @author Calvin Withun
 */
public class UserIdsMatchTest {

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
        DummySubevent.resetCounter();
    }

    @Test
    @DisplayName("evaluate wrong condition")
    void evaluate_wrongCondition_throwsException() {
        Condition condition = new UserIdsMatch();
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
    @DisplayName("evaluate matching user ids (true)")
    void evaluate_matchingUserIds_true() throws Exception {
        RPGLObject knight = RPGLFactory.newObject("std:humanoid/knight", TestUtils.TEST_USER);
        RPGLObject dragon = RPGLFactory.newObject("std:dragon/red/young", TestUtils.TEST_USER);

        RPGLEffect dummyEffect = new RPGLEffect();
        dummyEffect.setSource(knight);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(dragon);

        Condition condition = new UserIdsMatch();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "user_ids_match",
                "effect": "source",
                "subevent": "source"
            }*/
            this.putString("condition", "user_ids_match");
            this.putString("effect", "source");
            this.putString("subevent", "source");
        }};

        DummyContext context = new DummyContext();

        assertTrue(condition.evaluate(dummyEffect, dummySubevent, conditionJson, context),
                "condition should evaluate true when user ids match"
        );
    }

    @Test
    @DisplayName("evaluate differing user ids (false)")
    void evaluate_differingUserIds_false() throws Exception {
        RPGLObject knight = RPGLFactory.newObject("std:humanoid/knight", "player-one");
        RPGLObject dragon = RPGLFactory.newObject("std:dragon/red/young", "player-two");

        RPGLEffect dummyEffect = new RPGLEffect();
        dummyEffect.setSource(knight);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(dragon);

        Condition condition = new UserIdsMatch();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "user_ids_match",
                "effect": "source",
                "subevent": "source"
            }*/
            this.putString("condition", "user_ids_match");
            this.putString("effect", "source");
            this.putString("subevent", "source");
        }};

        DummyContext context = new DummyContext();

        assertFalse(condition.evaluate(dummyEffect, dummySubevent, conditionJson, context),
                "condition should evaluate false when user ids differ"
        );
    }

}
