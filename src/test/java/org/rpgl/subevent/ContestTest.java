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
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.json.JsonArray;
import org.rpgl.json.JsonObject;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContestTest {

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
        DummySubevent.resetCounter();
    }

    @Test
    @DisplayName("invoke wrong subevent")
    void invoke_wrongSubevent_throwsException() {
        Subevent subevent = new Contest();
        subevent.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "not_a_subevent"
            }*/
            this.putString("subevent", "not_a_subevent");
        }});

        assertThrows(SubeventMismatchException.class,
                () -> subevent.invoke(new RPGLContext()),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("resolveNestedSubevents subevents resolved correctly (source wins)")
    void resolveNestedSubevents_subeventsResolvedCorrectly_sourceWins() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        Contest contest = new Contest();
        contest.joinSubeventData(new JsonObject() {{
            /*{
                "source_wins": [
                    {
                        "subevent": "dummy_subevent"
                    }
                ],
                "target_wins": [ ]
            }*/
            this.putJsonArray("source_wins", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
            this.putJsonArray("target_wins", new JsonArray());
        }});

        contest.setSource(source);
        contest.setTarget(target);

        contest.resolveNestedSubevents("source_wins", context);

        assertEquals(1, DummySubevent.counter,
                "counter should increment by 1 when source_wins subevents arte resolved"
        );
    }

    @Test
    @DisplayName("resolveNestedSubevents subevents resolved correctly (target wins)")
    void resolveNestedSubevents_subeventsResolvedCorrectly_targetWins() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        Contest contest = new Contest();
        contest.joinSubeventData(new JsonObject() {{
            /*{
                "source_wins": [ ],
                "target_wins": [
                    {
                        "subevent": "dummy_subevent"
                    }
                ]
            }*/
            this.putJsonArray("source_wins", new JsonArray());
            this.putJsonArray("target_wins", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
        }});

        contest.setSource(source);
        contest.setTarget(target);

        contest.resolveNestedSubevents("target_wins", context);

        assertEquals(1, DummySubevent.counter,
                "counter should increment by 1 when target_wins subevents arte resolved"
        );
    }

    @Test
    @DisplayName("getTargetResultAsAbilityCheck returns correct calculation")
    void getTargetResultAsAbilityCheck_returnsCorrectCalculation() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        Contest contest = new Contest();
        JsonObject targetContestJson = new JsonObject() {{
            /*{
                "subevent": "ability_check",
                "ability": "str",
                "determined": [ 10 ]
            }*/
            this.putString("subevent", "ability_check");
            this.putString("ability", "str");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(10);
            }});
        }};
        contest.joinSubeventData(new JsonObject() {{
            /*{
                "target_contest": {
                    "subevent": "ability_check",
                    "ability": "str",
                    "determined": [ 10 ]
                }
            }*/
            this.putJsonObject("target_contest", targetContestJson);
        }});

        contest.setSource(source);
        contest.prepare(context);
        contest.setTarget(target);

        assertEquals(13, contest.getTargetResultAsAbilityCheck(targetContestJson, context),
                "target result as ability check should result in 13 (10+3=13)"
        );
    }

    @Test
    @DisplayName("getTargetResultAsSaveDifficultyClass returns correct calculation")
    void getTargetResultAsSaveDifficultyClass_returnsCorrectCalculation() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        Contest contest = new Contest();
        JsonObject targetContestJson = new JsonObject() {{
            /*{
                "subevent": "calculate_save_difficulty_class",
                "difficulty_class_ability": "str"
            }*/
            this.putString("subevent", "calculate_save_difficulty_class");
            this.putString("difficulty_class_ability", "str");
        }};
        contest.joinSubeventData(new JsonObject() {{
            /*{
                "target_contest": {
                    "subevent": "calculate_save_difficulty_class",
                    "difficulty_class_ability": "str"
                }
            }*/
            this.putJsonObject("target_contest", targetContestJson);
        }});

        contest.setSource(source);
        contest.prepare(context);
        contest.setTarget(target);

        assertEquals(13, contest.getTargetResultAsSaveDifficultyClass(targetContestJson, context),
                "target result as save difficulty class should result in 13 (8+2+3=13)"
        );
    }

    @Test
    @DisplayName("getTargetResult returns correct calculation (ability check)")
    void getTargetResult_returnsCorrectCalculation_abilityCheck() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        Contest contest = new Contest();
        contest.joinSubeventData(new JsonObject() {{
            /*{
                "target_contest": {
                    "subevent": "ability_check",
                    "ability": "str",
                    "determined": [ 10 ]
                }
            }*/
            this.putJsonObject("target_contest", new JsonObject() {{
                this.putString("subevent", "ability_check");
                this.putString("ability", "str");
                this.putJsonArray("determined", new JsonArray() {{
                    this.addInteger(10);
                }});
            }});
        }});

        contest.setSource(source);
        contest.prepare(context);
        contest.setTarget(target);

        assertEquals(13, contest.getTargetResult(context),
                "target result as ability check should result in 13 (10+3=13)"
        );
    }

    @Test
    @DisplayName("getTargetResult returns correct calculation (save difficulty class)")
    void getTargetResult_returnsCorrectCalculation_saveDifficultyClass() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        Contest contest = new Contest();
        contest.joinSubeventData(new JsonObject() {{
            /*{
                "target_contest": {
                    "subevent": "calculate_save_difficulty_class",
                    "difficulty_class_ability": "str"
                }
            }*/
            this.putJsonObject("target_contest", new JsonObject() {{
                this.putString("subevent", "calculate_save_difficulty_class");
                this.putString("difficulty_class_ability", "str");
            }});
        }});

        contest.setSource(source);
        contest.prepare(context);
        contest.setTarget(target);

        assertEquals(13, contest.getTargetResult(context),
                "target result as save difficulty class should result in 13 (8+2+3=13)"
        );
    }

    @Test
    @DisplayName("getTargetResult returns correct calculation (static value)")
    void getTargetResult_returnsCorrectCalculation_staticValue() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        Contest contest = new Contest();
        contest.joinSubeventData(new JsonObject() {{
            /*{
                "target_contest": 13
            }*/
            this.putInteger("target_contest", 13);
        }});

        contest.setSource(source);
        contest.prepare(context);
        contest.setTarget(target);

        assertEquals(13, contest.getTargetResult(context),
                "target result as static value should result in 13 (13=13)"
        );
    }

    @Test
    @DisplayName("getSourceResult returns correct calculation")
    void getSourceResult_returnsCorrectCalculation() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        Contest contest = new Contest();
        contest.joinSubeventData(new JsonObject() {{
            /*{
                "source_contest": {
                    "subevent": "ability_check",
                    "ability": "str",
                    "determined": [ 10 ]
                },
                "target_contest": 10
            }*/
            this.putJsonObject("source_contest", new JsonObject() {{
                this.putString("subevent", "ability_check");
                this.putString("ability", "str");
                this.putJsonArray("determined", new JsonArray() {{
                    this.addInteger(10);
                }});
            }});
            this.putInteger("target_contest", 10);
        }});

        contest.setSource(source);
        contest.prepare(context);
        contest.setTarget(target);

        assertEquals(13, contest.getSourceResult(context),
                "source result as ability check should result in 13 (10+3=13)"
        );
    }

    @Test
    @DisplayName("prepare  set must_exceed_target false (static value target)")
    void prepare_setsMustExceedTargetFalse_staticValueTarget() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        Contest contest = new Contest();
        contest.joinSubeventData(new JsonObject() {{
            /*{
                "target_contest": 10
            }*/
            this.putInteger("target_contest", 10);
        }});

        contest.setSource(source);
        contest.prepare(context);

        assertFalse(contest.json.getBoolean("must_exceed_target"),
                "must_exceed_target should be false for static value targets"
        );
    }

    @Test
    @DisplayName("prepare sets must_exceed_target false (save difficulty class target)")
    void prepare_setsMustExceedTargetFalse_saveDifficultyClassTarget() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        Contest contest = new Contest();
        contest.joinSubeventData(new JsonObject() {{
            /*{
                "target_contest": {
                    "subevent": "calculate_save_difficulty_class",
                    "difficulty_class_ability": "str"
                },
            }*/
            this.putJsonObject("target_contest", new JsonObject() {{
                this.putString("subevent", "calculate_save_difficulty_class");
                this.putString("difficulty_class_ability", "str");
            }});
        }});

        contest.setSource(source);
        contest.prepare(context);

        assertFalse(contest.json.getBoolean("must_exceed_target"),
                "must_exceed_target should be false for save difficulty class targets"
        );
    }

    @Test
    @DisplayName("prepare sets must_exceed_target true (ability check target)")
    void prepare_setsMustExceedTargetTrue_abilityCheckTarget() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        Contest contest = new Contest();
        contest.joinSubeventData(new JsonObject() {{
            /*{
                "target_contest": {
                    "subevent": "ability_check",
                    "ability": "str"
                },
            }*/
            this.putJsonObject("target_contest", new JsonObject() {{
                this.putString("subevent", "ability_check");
                this.putString("ability", "str");
            }});
        }});

        contest.setSource(source);
        contest.prepare(context);

        assertTrue(contest.json.getBoolean("must_exceed_target"),
                "must_exceed_target should be true for ability check targets"
        );
    }

    @Test
    @DisplayName("invoke resolves source_wins subevents (ability check target)")
    void invoke_resolvesSourceWinsSubevents_abilityCheckTarget() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        Contest contest = new Contest();
        contest.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "contest",
                "source_contest": {
                    "subevent": "ability_check",
                    "ability": "dex",
                    "determined": [ 11 ]
                 },
                 "target_contest": {
                    "subevent": "ability_check",
                    "ability": "dex",
                    "determined": [ 10 ]
                 },
                 "source_wins": [
                    {
                        "subevent": "dummy_subevent"
                    }
                 ],
                 "target_wins": [ ]
            }*/
            this.putString("subevent", "contest");
            this.putJsonObject("source_contest", new JsonObject() {{
                this.putString("subevent", "ability_check");
                this.putString("ability", "dex");
                this.putJsonArray("determined", new JsonArray() {{
                    this.addInteger(11);
                }});
            }});
            this.putJsonObject("target_contest", new JsonObject() {{
                this.putString("subevent", "ability_check");
                this.putString("ability", "dex");
                this.putJsonArray("determined", new JsonArray() {{
                    this.addInteger(10);
                }});
            }});
            this.putJsonArray("source_wins", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
            this.putJsonArray("target_wins", new JsonArray());
        }});

        contest.setSource(source);
        contest.prepare(context);
        contest.setTarget(target);
        contest.invoke(context);

        assertEquals(1, DummySubevent.counter,
                "source should win a contest against an ability check when it exceeds the target"
        );
    }

    @Test
    @DisplayName("invoke resolves target_wins subevents (ability check target)")
    void invoke_resolvesTargetWinsSubevents_abilityCheckTarget() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        Contest contest = new Contest();
        contest.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "contest",
                "source_contest": {
                    "subevent": "ability_check",
                    "ability": "dex",
                    "determined": [ 10 ]
                 },
                 "target_contest": {
                    "subevent": "ability_check",
                    "ability": "dex",
                    "determined": [ 10 ]
                 },
                 "source_wins": [ ],
                 "target_wins": [
                    {
                        "subevent": "dummy_subevent"
                    }
                ]
            }*/
            this.putString("subevent", "contest");
            this.putJsonObject("source_contest", new JsonObject() {{
                this.putString("subevent", "ability_check");
                this.putString("ability", "dex");
                this.putJsonArray("determined", new JsonArray() {{
                    this.addInteger(10);
                }});
            }});
            this.putJsonObject("target_contest", new JsonObject() {{
                this.putString("subevent", "ability_check");
                this.putString("ability", "dex");
                this.putJsonArray("determined", new JsonArray() {{
                    this.addInteger(10);
                }});
            }});
            this.putJsonArray("source_wins", new JsonArray());
            this.putJsonArray("target_wins", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
        }});

        contest.setSource(source);
        contest.prepare(context);
        contest.setTarget(target);
        contest.invoke(context);

        assertEquals(1, DummySubevent.counter,
                "source should lose a contest against an ability check when it does not exceed the target"
        );
    }

    @Test
    @DisplayName("invoke resolves source_wins subevents (save difficulty class target)")
    void invoke_resolvesSourceWinsSubevents_saveDifficultyClassTarget() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        Contest contest = new Contest();
        contest.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "contest",
                "source_contest": {
                    "subevent": "ability_check",
                    "ability": "str",
                    "determined": [ 10 ]
                 },
                 "target_contest": {
                    "subevent": "calculate_save_difficulty_class",
                    "difficulty_class_ability": "str"
                 },
                 "source_wins": [
                    {
                        "subevent": "dummy_subevent"
                    }
                 ],
                 "target_wins": [ ]
            }*/
            this.putString("subevent", "contest");
            this.putJsonObject("source_contest", new JsonObject() {{
                this.putString("subevent", "ability_check");
                this.putString("ability", "str");
                this.putJsonArray("determined", new JsonArray() {{
                    this.addInteger(10);
                }});
            }});
            this.putJsonObject("target_contest", new JsonObject() {{
                this.putString("subevent", "calculate_save_difficulty_class");
                this.putString("difficulty_class_ability", "str");
            }});
            this.putJsonArray("source_wins", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
            this.putJsonArray("target_wins", new JsonArray());
        }});

        contest.setSource(source);
        contest.prepare(context);
        contest.setTarget(target);
        contest.invoke(context);

        assertEquals(1, DummySubevent.counter,
                "source should win a contest against a save difficulty class when it matches or exceeds the target"
        );
    }

    @Test
    @DisplayName("invoke resolves target_wins subevents (save difficulty class target)")
    void invoke_resolvesTargetWinsSubevents_saveDifficultyClassTarget() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        Contest contest = new Contest();
        contest.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "contest",
                "source_contest": {
                    "subevent": "ability_check",
                    "ability": "str",
                    "determined": [ 9 ]
                 },
                 "target_contest": {
                    "subevent": "calculate_save_difficulty_class",
                    "difficulty_class_ability": "str"
                 },
                 "source_wins": [ ],
                 "target_wins": [
                    {
                        "subevent": "dummy_subevent"
                    }
                 ]
            }*/
            this.putString("subevent", "contest");
            this.putJsonObject("source_contest", new JsonObject() {{
                this.putString("subevent", "ability_check");
                this.putString("ability", "str");
                this.putJsonArray("determined", new JsonArray() {{
                    this.addInteger(9);
                }});
            }});
            this.putJsonObject("target_contest", new JsonObject() {{
                this.putString("subevent", "calculate_save_difficulty_class");
                this.putString("difficulty_class_ability", "str");
            }});
            this.putJsonArray("source_wins", new JsonArray());
            this.putJsonArray("target_wins", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
        }});

        contest.setSource(source);
        contest.prepare(context);
        contest.setTarget(target);
        contest.invoke(context);

        assertEquals(1, DummySubevent.counter,
                "source should lose a contest against a save difficulty class when it fails to match or exceed the target"
        );
    }

    @Test
    @DisplayName("invoke resolves source_wins subevents (static value target)")
    void invoke_resolvesSourceWinsSubevents_staticValueTarget() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        Contest contest = new Contest();
        contest.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "contest",
                "source_contest": {
                    "subevent": "ability_check",
                    "ability": "str",
                    "determined": [ 10 ]
                 },
                 "target_contest": 13,
                 "source_wins": [
                    {
                        "subevent": "dummy_subevent"
                    }
                 ],
                 "target_wins": [ ]
            }*/
            this.putString("subevent", "contest");
            this.putJsonObject("source_contest", new JsonObject() {{
                this.putString("subevent", "ability_check");
                this.putString("ability", "str");
                this.putJsonArray("determined", new JsonArray() {{
                    this.addInteger(10);
                }});
            }});
            this.putInteger("target_contest", 13);
            this.putJsonArray("source_wins", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
            this.putJsonArray("target_wins", new JsonArray());
        }});

        contest.setSource(source);
        contest.prepare(context);
        contest.setTarget(target);
        contest.invoke(context);

        assertEquals(1, DummySubevent.counter,
                "source should win a contest against a static value when it matches or exceeds the target"
        );
    }

    @Test
    @DisplayName("invoke resolves target_wins subevents (static value target)")
    void invoke_resolvesTargetWinsSubevents_staticValueTarget() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:knight");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        Contest contest = new Contest();
        contest.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "contest",
                "source_contest": {
                    "subevent": "ability_check",
                    "ability": "str",
                    "determined": [ 9 ]
                 },
                 "target_contest": 13,
                 "source_wins": [ ],
                 "target_wins": [
                    {
                        "subevent": "dummy_subevent"
                    }
                 ]
            }*/
            this.putString("subevent", "contest");
            this.putJsonObject("source_contest", new JsonObject() {{
                this.putString("subevent", "ability_check");
                this.putString("ability", "str");
                this.putJsonArray("determined", new JsonArray() {{
                    this.addInteger(9);
                }});
            }});
            this.putInteger("target_contest", 13);
            this.putJsonArray("source_wins", new JsonArray());
            this.putJsonArray("target_wins", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
        }});

        contest.setSource(source);
        contest.prepare(context);
        contest.setTarget(target);
        contest.invoke(context);

        assertEquals(1, DummySubevent.counter,
                "source should lose a contest against a static value when it fails to match or exceed the target"
        );
    }

}
