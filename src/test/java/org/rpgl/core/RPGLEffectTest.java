package org.rpgl.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.function.DummyFunction;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.subevent.DummySubevent;
import org.rpgl.subevent.Subevent;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;

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

    @BeforeEach
    void beforeEach() {
        effect = new RPGLEffect();
        effect.join(new JsonObject() {{
            /*{
                "subevent_filters": {
                    "dummy_subevent": [
                        {
                            "conditions": [
                                { "condition": "true" },
                                { "condition": "subevent_has_tag", "tag": "dummy_subevent" }
                            ],
                            "functions": [
                                { "function": "dummy_function" },
                                { "function": "dummy_function" }
                            ]
                        }
                    ]
                }
            }*/
            this.putJsonObject("subevent_filters", new JsonObject() {{
                this.putJsonArray("dummy_subevent", new JsonArray() {{
                    this.addJsonObject(new JsonObject() {{
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
        }});
    }

    @AfterEach
    void afterEach() {
        UUIDTable.clear();
        DummySubevent.resetCounter();
        DummyFunction.resetCounter();
    }

    @Test
    @DisplayName("executes functions")
    void executesFunctions() throws Exception {
        Subevent subevent = new DummySubevent();
        subevent.prepare(new DummyContext());

        effect.executeFunctions(
                subevent,
                effect.getSubeventFilters().getJsonArray("dummy_subevent").getJsonObject(0).getJsonArray("functions"),
                new DummyContext()
        );

        assertEquals(2, DummyFunction.counter,
                "both instances of dummy_function should be executed"
        );
    }

    @Test
    @DisplayName("evaluates conditions")
    void evaluatesConditions() throws Exception {
        Subevent subevent = new DummySubevent();
        subevent.prepare(new DummyContext());

        boolean evaluation = effect.evaluateConditions(
                subevent,
                effect.getSubeventFilters().getJsonArray("dummy_subevent").getJsonObject(0).getJsonArray("conditions"),
                new DummyContext()
        );

        assertTrue(evaluation,
                "conditions should evaluate to true for provided effect"
        );
    }

    @Test
    @DisplayName("processes subevents")
    void processesSubevents() throws Exception {
        Subevent subevent = new DummySubevent();
        subevent.prepare(new DummyContext());

        effect.processSubevent(subevent, new DummyContext());

        assertEquals(2, DummyFunction.counter,
                "both instances of dummy_function should be executed"
        );
    }

}
