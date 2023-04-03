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
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.ConditionMismatchException;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DummySubevent;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.condition.ObjectsMatch class.
 *
 * @author Calvin Withun
 */
public class ObjectsMatchTest {

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
        Condition condition = new ObjectsMatch();
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
    @DisplayName("evaluate returns true (effect source same as subevent source)")
    void evaluate_returnsTrue_effectSourceSameAsSubeventSource() throws Exception {
        RPGLObject effectSource = RPGLFactory.newObject("demo:commoner");
        RPGLObject effectTarget = RPGLFactory.newObject("demo:commoner");
        RPGLObject subeventSource = effectSource;
        RPGLObject subeventTarget = RPGLFactory.newObject("demo:commoner");
        DummyContext context = new DummyContext();
        context.add(effectSource);
        context.add(effectTarget);
        context.add(subeventSource);
        context.add(subeventTarget);

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(effectSource);
        effect.setTarget(effectTarget);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(subeventSource);
        dummySubevent.setTarget(subeventTarget);

        ObjectsMatch objectsMatch = new ObjectsMatch();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "objects_match",
                "effect": "source",
                "subevent": "source"
            }*/
            this.putString("condition", "objects_match");
            this.putString("effect", "source");
            this.putString("subevent", "source");
        }};

        assertTrue(objectsMatch.evaluate(effect, dummySubevent, conditionJson, context),
                "evaluate should return true when objects match"
        );
    }

    @Test
    @DisplayName("evaluate returns false (effect source different than subevent source)")
    void evaluate_returnsFalse_effectSourceDifferentThanSubeventSource() throws Exception {
        RPGLObject effectSource = RPGLFactory.newObject("demo:commoner");
        RPGLObject effectTarget = RPGLFactory.newObject("demo:commoner");
        RPGLObject subeventSource = RPGLFactory.newObject("demo:commoner");
        RPGLObject subeventTarget = RPGLFactory.newObject("demo:commoner");
        DummyContext context = new DummyContext();
        context.add(effectSource);
        context.add(effectTarget);
        context.add(subeventSource);
        context.add(subeventTarget);

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(effectSource);
        effect.setTarget(effectTarget);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(subeventSource);
        dummySubevent.setTarget(subeventTarget);

        ObjectsMatch objectsMatch = new ObjectsMatch();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "objects_match",
                "effect": "source",
                "subevent": "source"
            }*/
            this.putString("condition", "objects_match");
            this.putString("effect", "source");
            this.putString("subevent", "source");
        }};

        assertFalse(objectsMatch.evaluate(effect, dummySubevent, conditionJson, context),
                "evaluate should return false when objects don't match"
        );
    }

    @Test
    @DisplayName("evaluate returns true (effect source same as subevent target)")
    void evaluate_returnsTrue_effectSourceSameAsSubeventTarget() throws Exception {
        RPGLObject effectSource = RPGLFactory.newObject("demo:commoner");
        RPGLObject effectTarget = RPGLFactory.newObject("demo:commoner");
        RPGLObject subeventSource = RPGLFactory.newObject("demo:commoner");
        RPGLObject subeventTarget = effectSource;
        DummyContext context = new DummyContext();
        context.add(effectSource);
        context.add(effectTarget);
        context.add(subeventSource);
        context.add(subeventTarget);

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(effectSource);
        effect.setTarget(effectTarget);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(subeventSource);
        dummySubevent.setTarget(subeventTarget);

        ObjectsMatch objectsMatch = new ObjectsMatch();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "objects_match",
                "effect": "source",
                "subevent": "target"
            }*/
            this.putString("condition", "objects_match");
            this.putString("effect", "source");
            this.putString("subevent", "target");
        }};

        assertTrue(objectsMatch.evaluate(effect, dummySubevent, conditionJson, context),
                "evaluate should return true when objects match"
        );
    }

    @Test
    @DisplayName("evaluate returns false (effect source different than subevent target)")
    void evaluate_returnsFalse_effectSourceDifferentThanSubeventTarget() throws Exception {
        RPGLObject effectSource = RPGLFactory.newObject("demo:commoner");
        RPGLObject effectTarget = RPGLFactory.newObject("demo:commoner");
        RPGLObject subeventSource = RPGLFactory.newObject("demo:commoner");
        RPGLObject subeventTarget = RPGLFactory.newObject("demo:commoner");
        DummyContext context = new DummyContext();
        context.add(effectSource);
        context.add(effectTarget);
        context.add(subeventSource);
        context.add(subeventTarget);

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(effectSource);
        effect.setTarget(effectTarget);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(subeventSource);
        dummySubevent.setTarget(subeventTarget);

        ObjectsMatch objectsMatch = new ObjectsMatch();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "objects_match",
                "effect": "source",
                "subevent": "target"
            }*/
            this.putString("condition", "objects_match");
            this.putString("effect", "source");
            this.putString("subevent", "target");
        }};

        assertFalse(objectsMatch.evaluate(effect, dummySubevent, conditionJson, context),
                "evaluate should return false when objects don't match"
        );
    }

    @Test
    @DisplayName("evaluate returns true (effect target same as subevent source)")
    void evaluate_returnsTrue_effectTargetSameAsSubeventSource() throws Exception {
        RPGLObject effectSource = RPGLFactory.newObject("demo:commoner");
        RPGLObject effectTarget = RPGLFactory.newObject("demo:commoner");
        RPGLObject subeventSource = effectTarget;
        RPGLObject subeventTarget = RPGLFactory.newObject("demo:commoner");
        DummyContext context = new DummyContext();
        context.add(effectSource);
        context.add(effectTarget);
        context.add(subeventSource);
        context.add(subeventTarget);

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(effectSource);
        effect.setTarget(effectTarget);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(subeventSource);
        dummySubevent.setTarget(subeventTarget);

        ObjectsMatch objectsMatch = new ObjectsMatch();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "objects_match",
                "effect": "target",
                "subevent": "source"
            }*/
            this.putString("condition", "objects_match");
            this.putString("effect", "target");
            this.putString("subevent", "source");
        }};

        assertTrue(objectsMatch.evaluate(effect, dummySubevent, conditionJson, context),
                "evaluate should return true when objects match"
        );
    }

    @Test
    @DisplayName("evaluate returns false (effect target different than subevent source)")
    void evaluate_returnsFalse_effectTargetDifferentThanSubeventSource() throws Exception {
        RPGLObject effectSource = RPGLFactory.newObject("demo:commoner");
        RPGLObject effectTarget = RPGLFactory.newObject("demo:commoner");
        RPGLObject subeventSource = RPGLFactory.newObject("demo:commoner");
        RPGLObject subeventTarget = RPGLFactory.newObject("demo:commoner");
        DummyContext context = new DummyContext();
        context.add(effectSource);
        context.add(effectTarget);
        context.add(subeventSource);
        context.add(subeventTarget);

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(effectSource);
        effect.setTarget(effectTarget);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(subeventSource);
        dummySubevent.setTarget(subeventTarget);

        ObjectsMatch objectsMatch = new ObjectsMatch();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "objects_match",
                "effect": "target",
                "subevent": "source"
            }*/
            this.putString("condition", "objects_match");
            this.putString("effect", "target");
            this.putString("subevent", "source");
        }};

        assertFalse(objectsMatch.evaluate(effect, dummySubevent, conditionJson, context),
                "evaluate should return false when objects don't match"
        );
    }

    @Test
    @DisplayName("evaluate returns true (effect target same as subevent target)")
    void evaluate_returnsTrue_effectTargetSameAsSubeventTarget() throws Exception {
        RPGLObject effectSource = RPGLFactory.newObject("demo:commoner");
        RPGLObject effectTarget = RPGLFactory.newObject("demo:commoner");
        RPGLObject subeventSource = RPGLFactory.newObject("demo:commoner");
        RPGLObject subeventTarget = effectTarget;
        DummyContext context = new DummyContext();
        context.add(effectSource);
        context.add(effectTarget);
        context.add(subeventSource);
        context.add(subeventTarget);

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(effectSource);
        effect.setTarget(effectTarget);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(subeventSource);
        dummySubevent.setTarget(subeventTarget);

        ObjectsMatch objectsMatch = new ObjectsMatch();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "objects_match",
                "effect": "target",
                "subevent": "target"
            }*/
            this.putString("condition", "objects_match");
            this.putString("effect", "target");
            this.putString("subevent", "target");
        }};

        assertTrue(objectsMatch.evaluate(effect, dummySubevent, conditionJson, context),
                "evaluate should return true when objects match"
        );
    }

    @Test
    @DisplayName("evaluate returns false (effect target different than subevent target)")
    void evaluate_returnsFalse_effectTargetDifferentThanSubeventTarget() throws Exception {
        RPGLObject effectSource = RPGLFactory.newObject("demo:commoner");
        RPGLObject effectTarget = RPGLFactory.newObject("demo:commoner");
        RPGLObject subeventSource = RPGLFactory.newObject("demo:commoner");
        RPGLObject subeventTarget = RPGLFactory.newObject("demo:commoner");
        DummyContext context = new DummyContext();
        context.add(effectSource);
        context.add(effectTarget);
        context.add(subeventSource);
        context.add(subeventTarget);

        RPGLEffect effect = new RPGLEffect();
        effect.setSource(effectSource);
        effect.setTarget(effectTarget);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(subeventSource);
        dummySubevent.setTarget(subeventTarget);

        ObjectsMatch objectsMatch = new ObjectsMatch();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "objects_match",
                "effect": "target",
                "subevent": "target"
            }*/
            this.putString("condition", "objects_match");
            this.putString("effect", "target");
            this.putString("subevent", "target");
        }};

        assertFalse(objectsMatch.evaluate(effect, dummySubevent, conditionJson, context),
                "evaluate should return false when objects don't match"
        );
    }

}
