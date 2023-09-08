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
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.subevent.AbilitySave class.
 *
 * @author Calvin Withun
 */
public class AbilitySaveTest {

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
        Subevent subevent = new AbilitySave();
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
        RPGLObject source = RPGLFactory.newObject("debug:dummy");
        RPGLObject target = RPGLFactory.newObject("debug:dummy");
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AbilitySave abilitySave = new AbilitySave();
        abilitySave.joinSubeventData(new JsonObject() {{
            this.putJsonArray("pass", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
        }});
        abilitySave.setSource(source);
        abilitySave.setTarget(target);

        abilitySave.resolveNestedSubevents("pass", context, List.of());

        assertEquals(1, DummySubevent.counter,
                "dummy subevent should be invoked on pass"
        );
    }

    @Test
    @DisplayName("resolveNestedSubevents does not invoke fail subevents (on pass)")
    void resolveNestedSubevents_doesNotInvokeFailSubevents_onPass() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy");
        RPGLObject target = RPGLFactory.newObject("debug:dummy");
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AbilitySave abilitySave = new AbilitySave();
        abilitySave.joinSubeventData(new JsonObject() {{
            this.putJsonArray("fail", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
        }});
        abilitySave.setSource(source);
        abilitySave.setTarget(target);

        abilitySave.resolveNestedSubevents("pass", context, List.of());

        assertEquals(0, DummySubevent.counter,
                "dummy subevent should not be invoked on fail"
        );
    }

    @Test
    @DisplayName("resolveNestedSubevents invokes fail subevents (on fail)")
    void resolveNestedSubevents_invokesFailSubevents_onFail() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy");
        RPGLObject target = RPGLFactory.newObject("debug:dummy");
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AbilitySave abilitySave = new AbilitySave();
        abilitySave.joinSubeventData(new JsonObject() {{
            this.putJsonArray("fail", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
        }});
        abilitySave.setSource(source);
        abilitySave.setTarget(target);

        abilitySave.resolveNestedSubevents("fail", context, List.of());

        assertEquals(1, DummySubevent.counter,
                "dummy subevent should be invoked on fail"
        );
    }

    @Test
    @DisplayName("resolveNestedSubevents does not invoke pass subevents (on fail)")
    void resolveNestedSubevents_doesNotInvokePassSubevents_onFail() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy");
        RPGLObject target = RPGLFactory.newObject("debug:dummy");
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AbilitySave abilitySave = new AbilitySave();
        abilitySave.joinSubeventData(new JsonObject() {{
            this.putJsonArray("fail", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
        }});
        abilitySave.setSource(source);
        abilitySave.setTarget(target);

        abilitySave.resolveNestedSubevents("pass", context, List.of());

        assertEquals(0, DummySubevent.counter,
                "dummy subevent should not be invoked on pass"
        );
    }

    @Test
    @DisplayName("calculateDifficultyClass calculates difficulty class")
    void calculateDifficultyClass_calculatesDifficultyClass() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy");
        RPGLObject target = RPGLFactory.newObject("debug:dummy");
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        source.getAbilityScores().putInteger("wis", 20);
        source.setProficiencyBonus(6);

        AbilitySave abilitySave = new AbilitySave();
        abilitySave.joinSubeventData(new JsonObject() {{
            this.putString("difficulty_class_ability", "wis");
        }});
        abilitySave.setSource(source);
        abilitySave.setTarget(target);

        abilitySave.calculateDifficultyClass(context, List.of());

        assertEquals(8+5+6, abilitySave.json.getInteger("save_difficulty_class"),
                "difficulty class should be calculated to 19 (8+5+6)"
        );
    }

    @Test
    @DisplayName("prepare prepares subevent")
    void prepare_preparesSubevent() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy");
        RPGLContext context = new DummyContext();
        context.add(source);

        source.getAbilityScores().putInteger("wis", 20);
        source.setProficiencyBonus(6);

        AbilitySave abilitySave = new AbilitySave();
        abilitySave.joinSubeventData(new JsonObject() {{
            /*{
                "difficulty_class_ability": "wis"
            }*/
            this.putString("difficulty_class_ability", "wis");
        }});
        abilitySave.setSource(source);
        abilitySave.prepare(context, List.of());

        assertEquals(8+5+6, abilitySave.json.getInteger("save_difficulty_class"),
                "difficulty class should be calculated to 19 (8+5+6)"
        );
        assertTrue(abilitySave.getTags().asList().contains("ability_save"),
                "subevent should be given the ability_save tag upon preparation"
        );
    }

    @Test
    @DisplayName("invoke executes correctly (on pass)")
    void invoke_executesCorrectly_onPass() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy");
        RPGLObject target = RPGLFactory.newObject("debug:dummy");
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AbilitySave abilitySave = new AbilitySave();
        abilitySave.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "ability_save",
                "ability": "con",
                "difficulty_class_ability": "wis",
                "determined": [ 20 ],
                "pass": [
                    { "subevent": "dummy_subevent" }
                ]
            }*/
            this.putString("subevent", "ability_save");
            this.putString("ability", "con");
            this.putString("difficulty_class_ability", "wis");
            this.putJsonArray("pass", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(20);
            }});
        }});
        abilitySave.setSource(source);
        abilitySave.prepare(context, List.of());
        abilitySave.setTarget(target);
        abilitySave.invoke(context, List.of());

        assertEquals(1, DummySubevent.counter,
                "dummy subevent should be invoked on pass"
        );
    }

    @Test
    @DisplayName("invoke executes correctly (on fail)")
    void invoke_executesCorrectly_onFail() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy");
        RPGLObject target = RPGLFactory.newObject("debug:dummy");
        RPGLContext context = new DummyContext();
        context.add(source);
        context.add(target);

        AbilitySave abilitySave = new AbilitySave();
        abilitySave.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "ability_save",
                "ability": "con",
                "difficulty_class_ability": "wis",
                "determined": [ 1 ],
                "fail": [
                    { "subevent": "dummy_subevent" }
                ]
            }*/
            this.putString("subevent", "ability_save");
            this.putString("ability", "con");
            this.putString("difficulty_class_ability", "wis");
            this.putJsonArray("fail", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(1);
            }});
        }});
        abilitySave.setSource(source);
        abilitySave.prepare(context, List.of());
        abilitySave.setTarget(target);
        abilitySave.invoke(context, List.of());

        assertEquals(1, DummySubevent.counter,
                "dummy subevent should be invoked on fail"
        );
    }

}
