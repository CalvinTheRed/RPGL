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
import org.rpgl.subevent.DummySubevent;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.condition.IsObjectsTurn class.
 *
 * @author Calvin Withun
 */
public class IsObjectsTurnTest {

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
        Condition condition = new IsObjectsTurn();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "not_a_condition"
            }*/
            this.putString("condition", "not_a_condition");
        }};

        RPGLContext context = new RPGLContext();

        assertThrows(ConditionMismatchException.class,
                () -> condition.evaluate(null, null, null, conditionJson, context),
                "Condition should throw a ConditionMismatchException if the specified condition doesn't match"
        );
    }

    @Test
    @DisplayName("evaluate returns true when it is objects turn")
    void evaluate_returnsTrueWhenItIsObjectsTurn() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source, 12);
        context.add(target, 10);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);
        dummySubevent.setTarget(target);

        IsObjectsTurn isObjectsTurn = new IsObjectsTurn();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "is_objects_turn",
                "object": {
                    "from": "subevent",
                    "object": "source"
                }
            }*/
            this.putString("condition", "is_objects_turn");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
        }};

        context.currentObject();

        assertTrue(isObjectsTurn.evaluate(source, target, dummySubevent, conditionJson, context),
                "evaluate should return true when it is object's turn"
        );
    }

    @Test
    @DisplayName("evaluate returns false when it is not objects turn")
    void evaluate_returnsFalseWhenItIsNotObjectsTurn() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source, 12);
        context.add(target, 10);

        DummySubevent dummySubevent = new DummySubevent();
        dummySubevent.setSource(source);
        dummySubevent.setTarget(target);

        IsObjectsTurn isObjectsTurn = new IsObjectsTurn();
        JsonObject conditionJson = new JsonObject() {{
            /*{
                "condition": "is_objects_turn",
                "object": {
                    "from": "subevent",
                    "object": "source"
                }
            }*/
            this.putString("condition", "is_objects_turn");
            this.putJsonObject("object", new JsonObject() {{
                this.putString("from", "subevent");
                this.putString("object", "source");
            }});
        }};

        context.currentObject();
        context.nextObject();

        assertFalse(isObjectsTurn.evaluate(source, target, dummySubevent, conditionJson, context),
                "evaluate should return false when it is not object's turn"
        );
    }

}
