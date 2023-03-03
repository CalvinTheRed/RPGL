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
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing class for the org.rpgl.subevent.SavingThrow class.
 *
 * @author Calvin Withun
 */
public class SavingThrowTest {

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
        Subevent subevent = new SavingThrow();
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
    @DisplayName("deliverDamage delivers damage to target")
    void deliverDamage_deliversDamageToTarget() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        SavingThrow savingThrow = new SavingThrow();
        savingThrow.joinSubeventData(new JsonObject() {{
            /*{
                "damage": {
                    "cold": 10
                }
            }*/
            this.putJsonObject("damage", new JsonObject() {{
                this.putInteger("cold", 10);
            }});
        }});

        savingThrow.setSource(source);
        savingThrow.setTarget(target);
        savingThrow.deliverDamage(context);

        assertEquals(42, target.getHealthData().getInteger("current"),
                "target should take 10 cold damage (52-10=42)"
        );
    }

    @Test
    @DisplayName("resolveNestedSubevents increments counter on pass (DummySubevent)")
    void resolveNestedSubevents_incrementsCounterOnPass_dummySubevent() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        SavingThrow savingThrow = new SavingThrow();
        savingThrow.joinSubeventData(new JsonObject() {{
            /*{
                "pass": [
                    {
                        "subevent": "dummy_subevent"
                    }
                ]
            }*/
            this.putJsonArray("pass", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
        }});

        savingThrow.setSource(source);
        savingThrow.setTarget(target);
        savingThrow.resolveNestedSubevents(context, "pass");

        assertEquals(1, DummySubevent.counter,
                "counter should be incremented once from invoking nested pass subevent"
        );
    }

    @Test
    @DisplayName("resolveNestedSubevents increments counter on fail (DummySubevent)")
    void resolveNestedSubevents_incrementsCounterOnFail_dummySubevent() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        SavingThrow savingThrow = new SavingThrow();
        savingThrow.joinSubeventData(new JsonObject() {{
            /*{
                "fail": [
                    {
                        "subevent": "dummy_subevent"
                    }
                ]
            }*/
            this.putJsonArray("fail", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
        }});

        savingThrow.setSource(source);
        savingThrow.setTarget(target);
        savingThrow.resolveNestedSubevents(context, "fail");

        assertEquals(1, DummySubevent.counter,
                "counter should be incremented once from invoking nested fail subevent"
        );
    }

    @Test
    @DisplayName("resolveFailDamage target takes full damage")
    void resolveFailDamage_targetTakesFullDamage() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        SavingThrow savingThrow = new SavingThrow();
        savingThrow.joinSubeventData(new JsonObject() {{
            /*{
                "damage": {
                    "cold": 10
                },
                "tags": [ ]
            }*/
            this.putJsonObject("damage", new JsonObject() {{
                this.putInteger("cold", 10);
            }});
            this.putJsonArray("tags", new JsonArray());
        }});

        savingThrow.setSource(source);
        savingThrow.setTarget(target);
        savingThrow.resolveFailDamage(context);

        assertEquals(42, target.getHealthData().getInteger("current"),
                "target should take 10 cold damage after resolving fail damage (52-10=42)"
        );
    }

    @Test
    @DisplayName("resolvePassDamage target takes no damage on pass")
    void resolvePassDamage_targetTakesNoDamageOnPass() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        SavingThrow savingThrow = new SavingThrow();
        savingThrow.joinSubeventData(new JsonObject() {{
            /*{
                "damage_on_pass": "none",
                "damage": {
                    "cold": 10
                }
            }*/
            this.putString("damage_on_pass", "none");
            this.putJsonObject("damage", new JsonObject() {{
                this.putInteger("cold", 10);
            }});
        }});

        savingThrow.setSource(source);
        savingThrow.setTarget(target);
        savingThrow.resolvePassDamage(context);

        assertEquals(52, target.getHealthData().getInteger("current"),
                "target should take no damage after resolving pass damage (52-0=52)"
        );
    }

    @Test
    @DisplayName("resolvePassDamage target takes half damage on pass")
    void resolvePassDamage_targetTakesHalfDamageOnPass() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        SavingThrow savingThrow = new SavingThrow();
        savingThrow.joinSubeventData(new JsonObject() {{
            /*{
                "damage_on_pass": "half",
                "damage": {
                    "cold": 10
                },
                "tags": [ ]
            }*/
            this.putString("damage_on_pass", "half");
            this.putJsonObject("damage", new JsonObject() {{
                this.putInteger("cold", 10);
            }});
            this.putJsonArray("tags", new JsonArray());
        }});

        savingThrow.setSource(source);
        savingThrow.setTarget(target);
        savingThrow.resolvePassDamage(context);

        assertEquals(47, target.getHealthData().getInteger("current"),
                "target should take half damage after resolving pass damage (52-5=47)"
        );
    }

    @Test
    @DisplayName("getTargetDamage returns empty object (default)")
    void getTargetDamage_returnsEmptyObject_default() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        SavingThrow savingThrow = new SavingThrow();
        savingThrow.joinSubeventData(new JsonObject() {{
            this.putJsonArray("tags", new JsonArray());
        }});

        savingThrow.setSource(source);
        savingThrow.setTarget(target);
        JsonObject targetDamage = savingThrow.getTargetDamage(context);

        assertEquals("{}", targetDamage.toString(),
                "target damage should be empty by default"
        );
    }

    @Test
    @DisplayName("resolveSaveFail target takes full damage")
    void resolveSaveFail_targetTakesFullDamage() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        SavingThrow savingThrow = new SavingThrow();
        savingThrow.joinSubeventData(new JsonObject() {{
            /*{
                "damage": {
                    "cold": 10
                },
                "fail": [
                    {
                        "subevent": "dummy_subevent"
                    }
                ],
                "tags": [ ]
            }*/
            this.putJsonObject("damage", new JsonObject() {{
                this.putInteger("cold", 10);
            }});
            this.putJsonArray("fail", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
            this.putJsonArray("tags", new JsonArray());
        }});

        savingThrow.setSource(source);
        savingThrow.setTarget(target);
        savingThrow.resolveSaveFail(context);

        assertEquals(42, target.getHealthData().getInteger("current"),
                "target should take 10 cold damage after resolving fail damage (52-10=42)"
        );
        assertEquals(1, DummySubevent.counter,
                "counter should increment by 1 from nested pass subevent"
        );
    }

    @Test
    @DisplayName("resolveSavePass target takes half damage")
    void resolveSavePass_targetTakesHalfDamage() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        SavingThrow savingThrow = new SavingThrow();
        savingThrow.joinSubeventData(new JsonObject() {{
            /*{
                "damage_on_pass": "half",
                "damage": {
                    "cold": 10
                },
                "pass": [
                    {
                        "subevent": "dummy_subevent"
                    }
                ],
                "tags": [ ]
            }*/
            this.putString("damage_on_pass", "half");
            this.putJsonObject("damage", new JsonObject() {{
                this.putInteger("cold", 10);
            }});
            this.putJsonArray("pass", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("subevent", "dummy_subevent");
                }});
            }});
            this.putJsonArray("tags", new JsonArray());
        }});

        savingThrow.setSource(source);
        savingThrow.setTarget(target);
        savingThrow.resolveSavePass(context);

        assertEquals(47, target.getHealthData().getInteger("current"),
                "target should take half damage after resolving pass damage (52-5=47)"
        );
        assertEquals(1, DummySubevent.counter,
                "counter should increment by 1 from nested pass subevent"
        );
    }

    @Test
    @DisplayName("getBaseDamage stores base damage value")
    void getBaseDamage_storesBaseDamageValue() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        SavingThrow savingThrow = new SavingThrow();
        savingThrow.joinSubeventData(new JsonObject() {{
            /*{
                "damage": [
                    {
                        "type": "cold",
                        "dice": [
                            { "size": 10, "determined": [ 5 ] },
                            { "size": 10, "determined": [ 5 ] }
                        ],
                        "bonus": 0
                    }
                ],
                "tags": [ ]
            }*/
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("type", "cold");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 10);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(5);
                            }});
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 10);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(5);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
            this.putJsonArray("tags", new JsonArray());
        }});

        savingThrow.setSource(source);
        savingThrow.getBaseDamage(context);

        String expected = """
                {"cold":10}""";
        assertEquals(expected, savingThrow.json.getJsonObject("damage").toString(),
                "getBaseDamage should store 10 cold damage"
        );
    }

    @Test
    @DisplayName("calculateDifficultyClass calculates 17 (young red dragon using con)")
    void calculateDifficultyClass_calculatesSeventeen_youngRedDragonUsingCon() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        SavingThrow savingThrow = new SavingThrow();
        savingThrow.joinSubeventData(new JsonObject() {{
            this.putString("difficulty_class_ability", "con");
        }});

        savingThrow.setSource(source);
        savingThrow.calculateDifficultyClass(context);

        assertEquals(17, savingThrow.json.getInteger("save_difficulty_class"),
                "young red dragon should produce a con-based save DC of 17 (8+4+5=17)"
        );
    }

    @Test
    @DisplayName("prepare calculates save DC and stores base damage")
    void prepare_calculatesSaveDifficultyClassAndStoresBaseDamage() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        SavingThrow savingThrow = new SavingThrow();
        savingThrow.joinSubeventData(new JsonObject() {{
            /*{
                "difficulty_class_ability": "con",
                "damage": [
                    {
                        "type": "cold",
                        "dice": [
                            { "size": 10, "determined": [ 5 ] },
                            { "size": 10, "determined": [ 5 ] }
                        ],
                        "bonus": 0
                    }
                ]
            }*/
            this.putString("difficulty_class_ability", "con");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("type", "cold");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 10);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(5);
                            }});
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 10);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(5);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
        }});

        savingThrow.setSource(source);
        savingThrow.prepare(context);

        assertEquals(17, savingThrow.json.getInteger("save_difficulty_class"),
                "young red dragon should produce a con-based save DC of 17 (8+4+5=17)"
        );
        String expected = """
                {"cold":10}""";
        assertEquals(expected, savingThrow.json.getJsonObject("damage").toString(),
                "prepare should store 10 cold damage"
        );
    }

    @Test
    @DisplayName("invoke deals proper damage on fail")
    void invoke_dealsProperDamageOnFail() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        SavingThrow savingThrow = new SavingThrow();
        savingThrow.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "saving_throw",
                "difficulty_class_ability": "con",
                "save_ability": "dex",
                "damage": [
                    {
                      "type": "cold",
                      "dice": [
                        { "size": 10, "determined": [ 5 ] },
                        { "size": 10, "determined": [ 5 ] }
                      ],
                      "bonus": 0
                    }
                ],
                "damage_on_pass": "half",
                "determined": [ 1 ]
            }*/
            this.putString("subevent", "saving_throw");
            this.putString("difficulty_class_ability", "con");
            this.putString("save_ability", "dex");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("type", "cold");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 10);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(5);
                            }});
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 10);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(5);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
            this.putString("damage_on_pass", "half");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(1);
            }});
        }});

        savingThrow.setSource(source);
        savingThrow.prepare(context);
        savingThrow.setTarget(target);
        savingThrow.invoke(context);

        assertEquals(42, target.getHealthData().getInteger("current"),
                "invoke should deal full damage on a fail (52-10=42)"
        );
    }

    @Test
    @DisplayName("invoke deals half damage on pass")
    void invoke_dealsHalfDamageOnPass() throws Exception {
        RPGLObject source = RPGLFactory.newObject("demo:young_red_dragon");
        RPGLObject target = RPGLFactory.newObject("demo:knight");
        RPGLContext context = new RPGLContext();
        context.add(source);
        context.add(target);

        SavingThrow savingThrow = new SavingThrow();
        savingThrow.joinSubeventData(new JsonObject() {{
            /*{
                "subevent": "saving_throw",
                "difficulty_class_ability": "con",
                "save_ability": "dex",
                "damage": [
                    {
                      "type": "cold",
                      "dice": [
                        { "size": 10, "determined": [ 5 ] },
                        { "size": 10, "determined": [ 5 ] }
                      ],
                      "bonus": 0
                    }
                ],
                "damage_on_pass": "half",
                "determined": [ 20 ]
            }*/
            this.putString("subevent", "saving_throw");
            this.putString("difficulty_class_ability", "con");
            this.putString("save_ability", "dex");
            this.putJsonArray("damage", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("type", "cold");
                    this.putJsonArray("dice", new JsonArray() {{
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 10);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(5);
                            }});
                        }});
                        this.addJsonObject(new JsonObject() {{
                            this.putInteger("size", 10);
                            this.putJsonArray("determined", new JsonArray() {{
                                this.addInteger(5);
                            }});
                        }});
                    }});
                    this.putInteger("bonus", 0);
                }});
            }});
            this.putString("damage_on_pass", "half");
            this.putJsonArray("determined", new JsonArray() {{
                this.addInteger(20);
            }});
        }});
        savingThrow.setSource(source);
        savingThrow.prepare(context);
        savingThrow.setTarget(target);
        savingThrow.invoke(context);

        assertEquals(47, target.getHealthData().getInteger("current"),
                "invoke should deal full damage on a fail (52-5=47)"
        );
    }

}
