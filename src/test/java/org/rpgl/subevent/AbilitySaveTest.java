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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    @DisplayName("errors on wrong subevent")
    void errorsOnWrongSubevent() {
        Subevent subevent = new AbilitySave()
                .joinSubeventData(new JsonObject() {{
                    /*{
                        "subevent": "not_a_subevent"
                    }*/
                    this.putString("subevent", "not_a_subevent");
                }});

        assertThrows(SubeventMismatchException.class,
                () -> subevent.invoke(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0),
                "Subevent should throw a SubeventMismatchException if the specified subevent doesn't match"
        );
    }

    @Test
    @DisplayName("invokes pass subevents")
    void invokesPassSubevents() throws Exception {
        AbilitySave abilitySave = new AbilitySave()
                .joinSubeventData(new JsonObject() {{
                    /*{
                        "pass": [
                            { "subevent": "dummy_subevent" }
                        ]
                    }*/
                    this.putJsonArray("pass", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putString("subevent", "dummy_subevent");
                        }});
                    }});
                }});

        abilitySave.resolveNestedSubevents("pass", new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertEquals(1, DummySubevent.counter,
                "dummy subevent should be invoked on pass"
        );
    }

    @Test
    @DisplayName("invokes fail subevents")
    void invokesFailSubevents() throws Exception {
        AbilitySave abilitySave = new AbilitySave()
                .joinSubeventData(new JsonObject() {{
                    /*{
                        "fail": [
                            { "subevent": "dummy_subevent" }
                        ]
                    }*/
                    this.putJsonArray("fail", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putString("subevent", "dummy_subevent");
                        }});
                    }});
                }});

        abilitySave.resolveNestedSubevents("fail", new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertEquals(1, DummySubevent.counter,
                "dummy subevent should be invoked on fail"
        );
    }

    @Test
    @DisplayName("prepares difficulty class")
    void preparesDifficultyClass() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER)
                .setProficiencyBonus(2);
        object.getAbilityScores().putInteger("int", 20);

        AbilitySave abilitySave = new AbilitySave()
                .joinSubeventData(new JsonObject() {{
                    /*{
                        "difficulty_class_ability": "int"
                    }*/
                    this.putString("difficulty_class_ability", "int");
                }})
                .setSource(object)
                .prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertEquals(8 /*base*/ +2 /*proficiency*/ +5 /*modifier*/, abilitySave.getDifficultyClass(),
                "save DC was calculated incorrectly"
        );
    }

    @Test
    @DisplayName("prepares assigned difficulty class")
    void preparesAssignedDifficultyClass() throws Exception {
        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        AbilitySave abilitySave = new AbilitySave()
                .joinSubeventData(new JsonObject() {{
                    /*{
                        "difficulty_class": 20
                    }*/
                    this.putInteger("difficulty_class", 20);
                }})
                .setSource(object)
                .prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertEquals(20, abilitySave.getDifficultyClass(),
                "should preserve the assigned difficulty class"
        );
    }

    @Test
    @DisplayName("prepares difficulty class as origin")
    void preparesDifficultyClassAsOrigin() throws Exception {
        RPGLObject origin = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        origin.getAbilityScores().putInteger("int", 20);

        RPGLObject object = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER)
                .setProficiencyBonus(2)
                .setOriginObject(origin.getUuid());

        AbilitySave abilitySave = new AbilitySave()
                .joinSubeventData(new JsonObject() {{
                    this.putString("difficulty_class_ability", "int");
                    this.putBoolean("use_origin_difficulty_class_ability", true);
                }})
                .setSource(object)
                .prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertEquals(8 /*base*/ +2 /*proficiency*/ +5 /*modifier*/, abilitySave.getDifficultyClass(),
                "save DC should calculate using origin object's ability scores"
        );
    }

    @Test
    @DisplayName("invokes pass subevents on pass")
    void invokesPassSubeventsOnPass() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        new AbilitySave()
                .joinSubeventData(new JsonObject() {{
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
                }})
                .setSource(source)
                .prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0)
                .setTarget(target)
                .invoke(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertEquals(1, DummySubevent.counter,
                "dummy subevent should be invoked on pass"
        );
    }

    @Test
    @DisplayName("does not invoke pass subevents on fail")
    void doesNotInvokePassSubeventsOnFail() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        new AbilitySave()
                .joinSubeventData(new JsonObject() {{
                    /*{
                        "subevent": "ability_save",
                        "ability": "con",
                        "difficulty_class_ability": "wis",
                        "determined": [ 1 ],
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
                        this.addInteger(1);
                    }});
                }})
                .setSource(source)
                .prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0)
                .setTarget(target)
                .invoke(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertEquals(0, DummySubevent.counter,
                "dummy subevent should not be invoked on fail"
        );
    }

    @Test
    @DisplayName("invokes fail subevents on fail")
    void invokesFailSubeventsOnFail() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        new AbilitySave()
                .joinSubeventData(new JsonObject() {{
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
                }})
                .setSource(source)
                .prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0)
                .setTarget(target)
                .invoke(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertEquals(1, DummySubevent.counter,
                "dummy subevent should be invoked on fail"
        );
    }

    @Test
    @DisplayName("does not invoke fail subevents on pass")
    void doesNotInvokeFailSubeventsOnPass() throws Exception {
        RPGLObject source = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);
        RPGLObject target = RPGLFactory.newObject("debug:dummy", TestUtils.TEST_USER);

        new AbilitySave()
                .joinSubeventData(new JsonObject() {{
                    /*{
                        "subevent": "ability_save",
                        "ability": "con",
                        "difficulty_class_ability": "wis",
                        "determined": [ 20 ],
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
                        this.addInteger(20);
                    }});
                }})
                .setSource(source)
                .prepare(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0)
                .setTarget(target)
                .invoke(new DummyContext(), TestUtils.TEST_ARRAY_0_0_0);

        assertEquals(0, DummySubevent.counter,
                "dummy subevent should not be invoked on pass"
        );
    }

}
