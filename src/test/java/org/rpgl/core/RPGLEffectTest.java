package org.rpgl.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.function.DummyFunction;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DummySubevent;
import org.rpgl.subevent.Subevent;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.core.RPGLEffect class.
 *
 * @author Calvin Withun
 */
public class RPGLEffectTest {

    private RPGLEffect effect;

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

    @BeforeEach
    void beforeEach() {
        effect = new RPGLEffect();
        effect.join(new JsonObject() {{
            /*{
                "subevent_filters": {
                    "dummy_subevent": {
                        "conditions": [
                            { "condition": "true" },
                            { "condition": "subevent_has_tag", "tag": "dummy_subevent" }
                        ],
                        "functions": [
                            { "function": "dummy_function" },
                            { "function": "dummy_function" }
                        ]
                    }
                }
            }*/
            this.putJsonObject("subevent_filters", new JsonObject() {{
                this.putJsonObject("dummy_subevent", new JsonObject() {{
                    this.putJsonArray("conditions", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putString("condition", "true");
                        }});
                    }});
                    this.putJsonArray("functions", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putString("function", "dummy_function");
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putString("function", "dummy_function");
                        }});
                    }});
                }});
            }});
        }});
    }

    @AfterEach
    void afterEach() {
        UUIDTable.clear();
        DummySubevent.resetCounter();
        DummyFunction.resetCounter();
    }

    @Test
    @DisplayName("executeFunctions executes correct functions")
    void executeFunctions_executesCorrectFunctions() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        effect.setSource(source);
        effect.setTarget(target);

        Subevent subevent = new DummySubevent();
        subevent.setSource(source);
        subevent.prepare(context);
        subevent.setTarget(target);

        effect.executeFunctions(
                subevent,
                effect.getSubeventFilters().getJsonObject("dummy_subevent").getJsonArray("functions"),
                context
        );

        assertEquals(2, DummyFunction.counter,
                "both instances of dummy_function should be executed"
        );
    }

    @Test
    @DisplayName("evaluateConditions evaluates true as appropriate")
    void evaluateConditions_evaluatesTrueAsAppropriate() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        effect.setSource(source);
        effect.setTarget(target);

        Subevent subevent = new DummySubevent();
        subevent.setSource(source);
        subevent.prepare(context);
        subevent.setTarget(target);

        boolean evaluation = effect.evaluateConditions(
                subevent,
                effect.getSubeventFilters().getJsonObject("dummy_subevent").getJsonArray("conditions"),
                context
        );

        assertTrue(evaluation,
                "evaluateConditions() should return true for the provided Subevent"
        );
    }

    @Test
    @DisplayName("processSubevent executes functions")
    void processSubevent_executesFunctions() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:commoner");
        RPGLObject target = RPGLFactory.newObject("demo:commoner");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        effect.setSource(source);
        effect.setTarget(target);

        Subevent subevent = new DummySubevent();
        subevent.setSource(source);
        subevent.prepare(context);
        subevent.setTarget(target);

        effect.processSubevent(subevent, context);

        assertEquals(2, DummyFunction.counter,
                "both instances of dummy_function should be executed"
        );
    }

}