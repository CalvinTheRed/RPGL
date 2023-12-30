package org.rpgl.subevent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
    @DisplayName("errors on wrong subevent")
    void errorsOnWrongSubevent() {
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
    @DisplayName("invokes passing subevents")
    void invokesPassingSubevents() throws Exception {
        AbilityContest abilityContest = new AbilityContest();
        abilityContest.joinSubeventData(new JsonObject() {{
            this.putJsonArray("pass", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
        }});

        abilityContest.resolveNestedSubevents("pass", new DummyContext(), List.of());

        assertEquals(1, DummySubevent.counter,
                "dummy subevent should be invoked on pass"
        );
    }

    @Test
    @DisplayName("invokes failing subevents")
    void invokesFailingSubevents() throws Exception {
        AbilityContest abilityContest = new AbilityContest();
        abilityContest.joinSubeventData(new JsonObject() {{
            this.putJsonArray("fail", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
        }});

        abilityContest.resolveNestedSubevents("fail", new DummyContext(), List.of());

        assertEquals(1, DummySubevent.counter,
                "dummy subevent should be invoked on fail"
        );
    }

    @Test
    @DisplayName("gets ability check from source")
    void getsAbilityCheckFromSource() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

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

        assertEquals(20 /*base*/ +5 /*ability*/, abilityContest.getSourceAbilityCheck(new DummyContext(), List.of()),
                "source ability check should total to 25"
        );
    }

    @Test
    @DisplayName("gets ability check from target")
    void getsAbilityCheckFromTarget() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

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

        assertEquals(20 /*base*/ +5 /*modifier*/, abilityContest.getTargetAbilityCheck(new DummyContext(), List.of()),
                "target ability check should total to 25"
        );
    }

    @Test
    @DisplayName("prepares")
    void prepares() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        AbilityContest abilityContest = new AbilityContest();
        abilityContest.joinSubeventData(new JsonObject());
        abilityContest.setSource(source);
        abilityContest.prepare(new DummyContext(), List.of());

        assertTrue(abilityContest.getTags().asList().contains("ability_contest"),
                "subevent should be given the ability_contest tag upon preparation"
        );
    }

    @Test
    @DisplayName("invokes pass subevents on pass")
    void invokesPassSubeventsOnPass() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

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
        abilityContest.prepare(new DummyContext(), List.of());
        abilityContest.setTarget(target);
        abilityContest.invoke(new DummyContext(), List.of());

        assertEquals(1, DummySubevent.counter,
                "dummy subevent should be invoked on pass"
        );
    }

    @Test
    @DisplayName("does not invoke pass subevents on fail")
    void doesNotInvokePassSubeventsOnFail() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

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
        abilityContest.prepare(new DummyContext(), List.of());
        abilityContest.setTarget(target);
        abilityContest.invoke(new DummyContext(), List.of());

        assertEquals(0, DummySubevent.counter,
                "dummy subevent should not be invoked on fail"
        );
    }

    @Test
    @DisplayName("invokes fail subevents on fail")
    void invokesFailSubeventsOnFail() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

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
        abilityContest.prepare(new DummyContext(), List.of());
        abilityContest.setTarget(target);
        abilityContest.invoke(new DummyContext(), List.of());

        assertEquals(1, DummySubevent.counter,
                "dummy subevent should be invoked on fail"
        );
    }

    @Test
    @DisplayName("does not invoke fail subevent on pass")
    void doesNotInvokeFailSubeventsOnPass() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

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
        abilityContest.prepare(new DummyContext(), List.of());
        abilityContest.setTarget(target);
        abilityContest.invoke(new DummyContext(), List.of());

        assertEquals(0, DummySubevent.counter,
                "dummy subevent should not be invoked on pass"
        );
    }

    @Test
    @DisplayName("invokes no subevents on tie")
    void invokesNoSubeventsOnTie() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

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
        abilityContest.prepare(new DummyContext(), List.of());
        abilityContest.setTarget(target);
        abilityContest.invoke(new DummyContext(), List.of());

        assertEquals(0, DummySubevent.counter,
                "dummy subevent should not be invoked on tie"
        );
    }

}
