package org.rpgl.subevent;

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
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.testUtils.DummyContext;
import org.rpgl.testUtils.TestUtils;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.subevent.AbilityContestTest class.
 *
 * @author Calvin Withun
 */
public class AbilityContestTest {

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
    @DisplayName("invoke wrong subevent")
    void invoke_wrongSubevent_throwsException() {
        Subevent subevent = new AbilityContest();
        subevent.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "not_a_subevent"
            }*/
            this.putString("subevent", "not_a_subevent");
        }});

        assertThrows(SubeventMismatchException.class,
                () -> subevent.invoke(new DummyContext(), List.of()),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("resolveNestedSubevents invokes pass subevents (on pass)")
    void resolveNestedSubevents_invokesPassSubevents_onPass() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AbilityContest abilityContest = new AbilityContest();
        abilityContest.joinSubeventData(new JsonObject() {{
            this.putJsonArray("pass", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
        }});
        abilityContest.setSource(source);
        abilityContest.setTarget(target);

        abilityContest.resolveNestedSubevents("pass", context, List.of());

        assertEquals(1, DummySubevent.counter,
                "dummy subevent should be invoked on pass"
        );
    }

    @Test
    @DisplayName("resolveNestedSubevents does not invoke fail subevents (on pass)")
    void resolveNestedSubevents_doesNotInvokeFailSubevents_onPass() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AbilityContest abilityContest = new AbilityContest();
        abilityContest.joinSubeventData(new JsonObject() {{
            this.putJsonArray("fail", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
        }});
        abilityContest.setSource(source);
        abilityContest.setTarget(target);

        abilityContest.resolveNestedSubevents("pass", context, List.of());

        assertEquals(0, DummySubevent.counter,
                "dummy subevent should not be invoked on fail"
        );
    }

    @Test
    @DisplayName("resolveNestedSubevents invokes fail subevents (on fail)")
    void resolveNestedSubevents_invokesFailSubevents_onFail() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AbilityContest abilityContest = new AbilityContest();
        abilityContest.joinSubeventData(new JsonObject() {{
            this.putJsonArray("fail", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
        }});
        abilityContest.setSource(source);
        abilityContest.setTarget(target);

        abilityContest.resolveNestedSubevents("fail", context, List.of());

        assertEquals(1, DummySubevent.counter,
                "dummy subevent should be invoked on fail"
        );
    }

    @Test
    @DisplayName("resolveNestedSubevents does not invoke pass subevents (on fail)")
    void resolveNestedSubevents_doesNotInvokePassSubevents_onFail() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AbilityContest abilityContest = new AbilityContest();
        abilityContest.joinSubeventData(new JsonObject() {{
            this.putJsonArray("fail", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
        }});
        abilityContest.setSource(source);
        abilityContest.setTarget(target);

        abilityContest.resolveNestedSubevents("pass", context, List.of());

        assertEquals(0, DummySubevent.counter,
                "dummy subevent should not be invoked on pass"
        );
    }

    @Test
    @DisplayName("getSourceAbilityCheck returns source ability check result")
    void getSourceAbilityCheck_returnsSourceAbilityCheckResult() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("wis", 20);

        AbilityContest abilityContest = new AbilityContest();
        abilityContest.joinSubeventData(new JsonObject() {{
            /*{
                "source_check": {
                    "ability": "wis",
                    "skill": "perception",
                    "determined": [ 20 ]
                }
            }*/
            this.putJsonObject("source_check", new JsonObject() {{
                this.putString("ability", "wis");
                this.putString("skill", "perception");
                this.putJsonArray("determined", new JsonArray() {{
                    this.addInteger(20);
                }});
            }});
        }});
        abilityContest.setSource(source);
        abilityContest.setTarget(target);

        assertEquals(25, abilityContest.getSourceAbilityCheck(context, List.of()),
                "source ability check should evaluate to 25 (20+5)"
        );
    }

    @Test
    @DisplayName("getTargetAbilityCheck returns target ability check result")
    void getTargetAbilityCheck_returnsTargetAbilityCheckResult() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        target.getAbilityScores().putInteger("wis", 20);

        AbilityContest abilityContest = new AbilityContest();
        abilityContest.joinSubeventData(new JsonObject() {{
            /*{
                "target_check": {
                    "ability": "wis",
                    "skill": "perception",
                    "determined": [ 20 ]
                }
            }*/
            this.putJsonObject("target_check", new JsonObject() {{
                this.putString("ability", "wis");
                this.putString("skill", "perception");
                this.putJsonArray("determined", new JsonArray() {{
                    this.addInteger(20);
                }});
            }});
        }});
        abilityContest.setSource(source);
        abilityContest.setTarget(target);

        assertEquals(25, abilityContest.getTargetAbilityCheck(context, List.of()),
                "target ability check should evaluate to 25 (20+5)"
        );
    }

    @Test
    @DisplayName("prepare prepares subevent")
    void prepare_preparesSubevent() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLContext context = new DummyContext();
        context.add(source);

        AbilityContest abilityContest = new AbilityContest();
        abilityContest.joinSubeventData(new JsonObject());
        abilityContest.setSource(source);
        abilityContest.prepare(context, List.of());

        assertTrue(abilityContest.getTags().asList().contains("ability_contest"),
                "subevent should be given the ability_contest tag upon preparation"
        );
    }

    @Test
    @DisplayName("invoke invokes pass subevents (on pass)")
    void invoke_invokesPassSubevents_onPass() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AbilityContest abilityContest = new AbilityContest();
        abilityContest.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "ability_contest",
                "source_check": {
                    "ability": "wis",
                    "skill": "perception",
                    "determined": [ 20 ]
                },
                "target_check": {
                    "ability": "wis",
                    "skill": "perception",
                    "determined": [ 1 ]
                },
                "pass": [
                    { "subevent": "dummy_subevent" }
                ]
            }*/
            this.putString("subevent", "ability_contest");
            this.putJsonObject("source_check", new JsonObject() {{
                this.putString("ability", "wis");
                this.putString("skill", "perception");
                this.putJsonArray("determined", new JsonArray() {{
                    this.addInteger(20);
                }});
            }});
            this.putJsonObject("target_check", new JsonObject() {{
                this.putString("ability", "wis");
                this.putString("skill", "perception");
                this.putJsonArray("determined", new JsonArray() {{
                    this.addInteger(1);
                }});
            }});
            this.putJsonArray("pass", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
        }});
        abilityContest.setSource(source);
        abilityContest.prepare(context, List.of());
        abilityContest.setTarget(target);
        abilityContest.invoke(context, List.of());

        assertEquals(1, DummySubevent.counter,
                "dummy subevent should be invoked on pass"
        );
    }

    @Test
    @DisplayName("invoke does not invoke pass subevents (on fail)")
    void invoke_doesNotInvokePassSubevents_onFail() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AbilityContest abilityContest = new AbilityContest();
        abilityContest.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "ability_contest",
                "source_check": {
                    "ability": "wis",
                    "skill": "perception",
                    "determined": [ 1 ]
                },
                "target_check": {
                    "ability": "wis",
                    "skill": "perception",
                    "determined": [ 20 ]
                },
                "pass": [
                    { "subevent": "dummy_subevent" }
                ]
            }*/
            this.putString("subevent", "ability_contest");
            this.putJsonObject("source_check", new JsonObject() {{
                this.putString("ability", "wis");
                this.putString("skill", "perception");
                this.putJsonArray("determined", new JsonArray() {{
                    this.addInteger(1);
                }});
            }});
            this.putJsonObject("target_check", new JsonObject() {{
                this.putString("ability", "wis");
                this.putString("skill", "perception");
                this.putJsonArray("determined", new JsonArray() {{
                    this.addInteger(20);
                }});
            }});
            this.putJsonArray("pass", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
        }});
        abilityContest.setSource(source);
        abilityContest.prepare(context, List.of());
        abilityContest.setTarget(target);
        abilityContest.invoke(context, List.of());

        assertEquals(0, DummySubevent.counter,
                "dummy subevent should not be invoked on fail"
        );
    }

    @Test
    @DisplayName("invoke invokes fail subevents (on fail)")
    void invoke_invokesFailSubevents_onFail() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AbilityContest abilityContest = new AbilityContest();
        abilityContest.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "ability_contest",
                "source_check": {
                    "ability": "wis",
                    "skill": "perception",
                    "determined": [ 1 ]
                },
                "target_check": {
                    "ability": "wis",
                    "skill": "perception",
                    "determined": [ 20 ]
                },
                "fail": [
                    { "subevent": "dummy_subevent" }
                ]
            }*/
            this.putString("subevent", "ability_contest");
            this.putJsonObject("source_check", new JsonObject() {{
                this.putString("ability", "wis");
                this.putString("skill", "perception");
                this.putJsonArray("determined", new JsonArray() {{
                    this.addInteger(1);
                }});
            }});
            this.putJsonObject("target_check", new JsonObject() {{
                this.putString("ability", "wis");
                this.putString("skill", "perception");
                this.putJsonArray("determined", new JsonArray() {{
                    this.addInteger(20);
                }});
            }});
            this.putJsonArray("fail", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
        }});
        abilityContest.setSource(source);
        abilityContest.prepare(context, List.of());
        abilityContest.setTarget(target);
        abilityContest.invoke(context, List.of());

        assertEquals(1, DummySubevent.counter,
                "dummy subevent should be invoked on fail"
        );
    }

    @Test
    @DisplayName("invoke does not invoke fail subevents (on pass)")
    void invoke_doesNotInvokeFailSubevents_onPass() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AbilityContest abilityContest = new AbilityContest();
        abilityContest.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "ability_contest",
                "source_check": {
                    "ability": "wis",
                    "skill": "perception",
                    "determined": [ 20 ]
                },
                "target_check": {
                    "ability": "wis",
                    "skill": "perception",
                    "determined": [ 1 ]
                },
                "fail": [
                    { "subevent": "dummy_subevent" }
                ]
            }*/
            this.putString("subevent", "ability_contest");
            this.putJsonObject("source_check", new JsonObject() {{
                this.putString("ability", "wis");
                this.putString("skill", "perception");
                this.putJsonArray("determined", new JsonArray() {{
                    this.addInteger(20);
                }});
            }});
            this.putJsonObject("target_check", new JsonObject() {{
                this.putString("ability", "wis");
                this.putString("skill", "perception");
                this.putJsonArray("determined", new JsonArray() {{
                    this.addInteger(1);
                }});
            }});
            this.putJsonArray("fail", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
        }});
        abilityContest.setSource(source);
        abilityContest.prepare(context, List.of());
        abilityContest.setTarget(target);
        abilityContest.invoke(context, List.of());

        assertEquals(0, DummySubevent.counter,
                "dummy subevent should not be invoked on pass"
        );
    }

    @Test
    @DisplayName("invoke does not invoke any subevents (on tie)")
    void invoke_doesNotInvokeAnySubevents_onTie() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AbilityContest abilityContest = new AbilityContest();
        abilityContest.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "ability_contest",
                "source_check": {
                    "ability": "wis",
                    "skill": "perception",
                    "determined": [ 20 ]
                },
                "target_check": {
                    "ability": "wis",
                    "skill": "perception",
                    "determined": [ 20 ]
                },
                "pass": [
                    { "subevent": "dummy_subevent" }
                ],
                "fail": [
                    { "subevent": "dummy_subevent" }
                ]
            }*/
            this.putString("subevent", "ability_contest");
            this.putJsonObject("source_check", new JsonObject() {{
                this.putString("ability", "wis");
                this.putString("skill", "perception");
                this.putJsonArray("determined", new JsonArray() {{
                    this.addInteger(20);
                }});
            }});
            this.putJsonObject("target_check", new JsonObject() {{
                this.putString("ability", "wis");
                this.putString("skill", "perception");
                this.putJsonArray("determined", new JsonArray() {{
                    this.addInteger(20);
                }});
            }});
            this.putJsonArray("pass", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
            this.putJsonArray("fail", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
        }});
        abilityContest.setSource(source);
        abilityContest.prepare(context, List.of());
        abilityContest.setTarget(target);
        abilityContest.invoke(context, List.of());

        assertEquals(0, DummySubevent.counter,
                "dummy subevent should not be invoked on tie"
        );
    }

}
