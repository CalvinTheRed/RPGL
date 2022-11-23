package org.rpgl.subevent;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.junit.jupiter.api.*;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLObject;
import org.rpgl.datapack.DatapackLoader;
import org.rpgl.datapack.DatapackTest;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.math.Die;
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing class for subevent.SavingThrow class.
 *
 * @author Calvin Withun
 */
public class SavingThrowTest {

    @BeforeAll
    static void beforeAll() throws Exception {
        DatapackLoader.loadDatapacks(
                new File(Objects.requireNonNull(DatapackTest.class.getClassLoader().getResource("datapacks")).toURI())
        );
        Die.setTesting(true);
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
    @DisplayName("SavingThrow Subevent throws SubeventMismatchException when subevent type doesn't match")
    void test0() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = """
                {
                    "subevent": "not_a_subevent"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        RPGLContext context = new RPGLContext(null);

        /*
         * Verify subevent behaves as expected
         */
        assertThrows(SubeventMismatchException.class,
                () -> subevent.clone(subeventJson).invoke(context),
                "SavingThrow Subevent should throw a SubeventMismatchException if the specified subevent doesn't match."
        );
    }

    @Test
    @DisplayName("SavingThrow Subevent prepare method & roll work")
    void test1() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = """
                {
                    "subevent": "saving_throw",
                    "save_ability": "int",
                    "difficulty_class_ability": "int",
                    "determined": 10
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        savingThrow.setSource(object);
        savingThrow.prepare(context);
        savingThrow.roll();

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(10L, savingThrow.get(),
                "SavingThrow Subevent did not report base roll correctly."
        );
    }

    @Test
    @DisplayName("SavingThrow Subevent can set roll")
    void test2() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = """
                {
                    "subevent": "saving_throw",
                    "save_ability": "int",
                    "difficulty_class_ability": "int"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        savingThrow.setSource(object);
        savingThrow.prepare(context);
        savingThrow.set(10L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(10L, savingThrow.get(),
                "SavingThrow Subevent did not set roll correctly."
        );
    }

    @Test
    @DisplayName("SavingThrow Subevent can set roll (override prior set with higher)")
    void test3() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = """
                {
                    "subevent": "saving_throw",
                    "save_ability": "int",
                    "difficulty_class_ability": "int"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        savingThrow.setSource(object);
        savingThrow.prepare(context);
        savingThrow.set(10L);
        savingThrow.set(12L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(12L, savingThrow.get(),
                "SavingThrow Subevent should be able to override roll set value with higher value."
        );
    }

    @Test
    @DisplayName("SavingThrow Subevent can add bonus to a set roll")
    void test5() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = """
                {
                    "subevent": "saving_throw",
                    "save_ability": "int",
                    "difficulty_class_ability": "int"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        savingThrow.setSource(object);
        savingThrow.prepare(context);
        savingThrow.set(10L);
        savingThrow.addBonus(3L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(13L, savingThrow.get(),
                "SavingThrow Subevent did not add bonus to set roll properly."
        );
    }

    @Test
    @DisplayName("SavingThrow Subevent invoke works on pass (hollow)")
    void test6() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = """
                {
                    "subevent": "saving_throw",
                    "save_ability": "int",
                    "difficulty_class_ability": "int",
                    "determined": 20
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        savingThrow.setSource(object);
        savingThrow.prepare(context);
        savingThrow.setTarget(object);
        savingThrow.invoke(context);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(20L, savingThrow.get(),
                "SavingThrow Subevent should report a roll of 20."
        );
    }

    @Test
    @DisplayName("SavingThrow Subevent invoke works on fail (hollow)")
    void test7() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = """
                {
                    "subevent": "saving_throw",
                    "save_ability": "int",
                    "difficulty_class_ability": "int",
                    "determined": 1
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        savingThrow.setSource(object);
        savingThrow.prepare(context);
        savingThrow.setTarget(object);
        savingThrow.invoke(context);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(1L, savingThrow.get(),
                "SavingThrow Subevent should report a roll of 1."
        );
    }

    @Test
    @DisplayName("SavingThrow Subevent can prepare base damage value")
    void test8() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = """
                {
                    "subevent": "saving_throw",
                    "save_ability": "int",
                    "difficulty_class_ability": "int",
                    "damage": [
                        {
                            "type": "fire",
                            "dice": [
                                { "size": 10, "determined": 10 },
                                { "size": 10, "determined": 10 }
                            ],
                            "bonus": 10
                        },
                        {
                            "type": "cold",
                            "dice": [
                                { "size": 10, "determined": 10 },
                                { "size": 10, "determined": 10 }
                            ],
                            "bonus": 10
                        }
                    ],
                    "damage_on_pass": "half"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        savingThrow.setSource(object);
        savingThrow.prepare(context);

        /*
         * Verify subevent behaves as expected
         */
        String expectedJsonString = """
                {
                    "fire": 30,
                    "cold": 30
                }
                """;
        JsonObject expectedJson = JsonParser.parseObjectString(expectedJsonString);
        assertEquals(expectedJson.toString(), savingThrow.subeventJson.get("damage").toString(),
                "SavingThrow Subevent calculated base damage incorrectly."
        );
    }

    @Test
    @DisplayName("SavingThrow Subevent invokes pass nested subevents and not fail nested subevents on pass")
    void test9() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = """
                {
                    "subevent": "saving_throw",
                    "save_ability": "int",
                    "difficulty_class_ability": "int",
                    "pass": [
                        { "subevent": "dummy_subevent" }
                    ],
                    "fail": [
                        { "subevent": "dummy_subevent" }
                    ]
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        savingThrow.setSource(object);
        savingThrow.prepare(context);
        savingThrow.setTarget(object);
        savingThrow.set(20L);
        savingThrow.invoke(context);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(1, DummySubevent.counter,
                "SavingThrow Subevent did not invoke nested subevents on pass."
        );
    }

    @Test
    @DisplayName("SavingThrow Subevent invokes fail nested subevents and not pass nested subevents on fail")
    void test10() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = """
                {
                    "subevent": "saving_throw",
                    "save_ability": "int",
                    "difficulty_class_ability": "int",
                    "pass": [
                        { "subevent": "dummy_subevent" }
                    ],
                    "fail": [
                        { "subevent": "dummy_subevent" }
                    ]
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        savingThrow.setSource(object);
        savingThrow.prepare(context);
        savingThrow.setTarget(object);
        savingThrow.set(1L);
        savingThrow.invoke(context);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(1, DummySubevent.counter,
                "SavingThrow Subevent did not invoke nested subevents on fail."
        );
    }

    @Test
    @DisplayName("SavingThrow Subevent can roll with disadvantage")
    void test11() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = """
                {
                    "subevent": "saving_throw",
                    "save_ability": "int",
                    "difficulty_class_ability": "int",
                    "determined": 20,
                    "determined_second": 1
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        savingThrow.setSource(object);
        savingThrow.prepare(context);
        savingThrow.grantDisadvantage();
        savingThrow.roll();

        /*
         * Verify subevent behaves as expected
         */
        assertFalse(savingThrow.advantageRoll(),
                "SavingThrow Subevent should not be at advantage."
        );
        assertTrue(savingThrow.disadvantageRoll(),
                "SavingThrow Subevent should be at disadvantage."
        );
        assertFalse(savingThrow.normalRoll(),
                "SavingThrow Subevent should not be a normal roll."
        );
        assertEquals(1L, savingThrow.get(),
                "SavingThrow Subevent did not roll with disadvantage correctly."
        );
    }

    @Test
    @DisplayName("SavingThrow Subevent can roll with disadvantage (reversed order)")
    void test12() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = """
                {
                    "subevent": "saving_throw",
                    "save_ability": "int",
                    "difficulty_class_ability": "int",
                    "determined": 1,
                    "determined_second": 20
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        savingThrow.setSource(object);
        savingThrow.prepare(context);
        savingThrow.grantDisadvantage();
        savingThrow.roll();

        /*
         * Verify subevent behaves as expected
         */
        assertFalse(savingThrow.advantageRoll(),
                "SavingThrow Subevent should not be at advantage."
        );
        assertTrue(savingThrow.disadvantageRoll(),
                "SavingThrow Subevent should be at disadvantage."
        );
        assertFalse(savingThrow.normalRoll(),
                "SavingThrow Subevent should not be a normal roll."
        );
        assertEquals(1L, savingThrow.get(),
                "SavingThrow Subevent did not roll with disadvantage correctly."
        );
    }

    @Test
    @DisplayName("SavingThrow Subevent can roll with advantage")
    void test13() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = """
                {
                    "subevent": "saving_throw",
                    "save_ability": "int",
                    "difficulty_class_ability": "int",
                    "determined": 20,
                    "determined_second": 1
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        savingThrow.setSource(object);
        savingThrow.prepare(context);
        savingThrow.grantAdvantage();
        savingThrow.roll();

        /*
         * Verify subevent behaves as expected
         */
        assertTrue(savingThrow.advantageRoll(),
                "SavingThrow Subevent should be at advantage."
        );
        assertFalse(savingThrow.disadvantageRoll(),
                "SavingThrow Subevent should not be at disadvantage."
        );
        assertFalse(savingThrow.normalRoll(),
                "SavingThrow Subevent should not be a normal roll."
        );
        assertEquals(20L, savingThrow.get(),
                "SavingThrow Subevent did not roll with advantage correctly."
        );
    }

    @Test
    @DisplayName("SavingThrow Subevent can roll with advantage (reversed order)")
    void test14() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = """
                {
                    "subevent": "saving_throw",
                    "save_ability": "int",
                    "difficulty_class_ability": "int",
                    "determined": 1,
                    "determined_second": 20
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        savingThrow.setSource(object);
        savingThrow.prepare(context);
        savingThrow.grantAdvantage();
        savingThrow.roll();

        /*
         * Verify subevent behaves as expected
         */
        assertTrue(savingThrow.advantageRoll(),
                "SavingThrow Subevent should be at advantage."
        );
        assertFalse(savingThrow.disadvantageRoll(),
                "SavingThrow Subevent should not be at disadvantage."
        );
        assertFalse(savingThrow.normalRoll(),
                "SavingThrow Subevent should not be a normal roll."
        );
        assertEquals(20L, savingThrow.get(),
                "SavingThrow Subevent did not roll with advantage correctly."
        );
    }

    @Test
    @DisplayName("SavingThrow Subevent can roll with normal roll (advantage and disadvantage)")
    void test15() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = """
                {
                    "subevent": "saving_throw",
                    "save_ability": "int",
                    "difficulty_class_ability": "int",
                    "determined": 20,
                    "determined_second": 1
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        savingThrow.setSource(object);
        savingThrow.prepare(context);
        savingThrow.grantAdvantage();
        savingThrow.grantDisadvantage();
        savingThrow.roll();

        /*
         * Verify subevent behaves as expected
         */
        assertFalse(savingThrow.advantageRoll(),
                "SavingThrow Subevent should not be at advantage."
        );
        assertFalse(savingThrow.disadvantageRoll(),
                "SavingThrow Subevent should not be at disadvantage."
        );
        assertTrue(savingThrow.normalRoll(),
                "SavingThrow Subevent should be a normal roll."
        );
        assertEquals(20L, savingThrow.get(),
                "SavingThrow Subevent did not roll with both advantage and disadvantage correctly."
        );
    }

    @Test
    @DisplayName("SavingThrow Subevent can roll with normal roll (advantage and disadvantage) (reversed order)")
    void test16() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = """
                {
                    "subevent": "saving_throw",
                    "save_ability": "int",
                    "difficulty_class_ability": "int",
                    "determined": 1,
                    "determined_second": 20
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        savingThrow.setSource(object);
        savingThrow.prepare(context);
        savingThrow.grantAdvantage();
        savingThrow.grantDisadvantage();
        savingThrow.roll();

        /*
         * Verify subevent behaves as expected
         */
        assertFalse(savingThrow.advantageRoll(),
                "SavingThrow Subevent should not be at advantage."
        );
        assertFalse(savingThrow.disadvantageRoll(),
                "SavingThrow Subevent should not be at disadvantage."
        );
        assertTrue(savingThrow.normalRoll(),
                "SavingThrow Subevent should be a normal roll."
        );
        assertEquals(1L, savingThrow.get(),
                "SavingThrow Subevent did not roll with both advantage and disadvantage correctly."
        );
    }

    @Test
    @DisplayName("SavingThrow Subevent can deal (full) damage on a fail")
    void test17() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = """
                {
                    "subevent": "saving_throw",
                    "save_ability": "int",
                    "difficulty_class_ability": "int",
                    "damage": [
                        {
                            "type": "fire",
                            "dice": [
                                { "size": 10, "determined": 1 }
                            ]
                        },
                        {
                            "type": "cold",
                            "dice": [
                                { "size": 10, "determined": 1 }
                            ]
                        }
                    ],
                    "damage_on_pass": "none",
                    "determined": 1
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        savingThrow.setSource(object);
        savingThrow.prepare(context);
        savingThrow.setTarget(object);
        savingThrow.invoke(context);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(8L, object.seek("health_data.current"),
                "SavingThrow Subevent should have dealt 2 damage (10-2=8)."
        );
    }

    @Test
    @DisplayName("SavingThrow Subevent can deal no damage on a pass")
    void test18() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = """
                {
                    "subevent": "saving_throw",
                    "save_ability": "int",
                    "difficulty_class_ability": "int",
                    "damage": [
                        {
                            "type": "fire",
                            "dice": [
                                { "size": 10, "determined": 1 }
                            ]
                        },
                        {
                            "type": "cold",
                            "dice": [
                                { "size": 10, "determined": 1 }
                            ]
                        }
                    ],
                    "damage_on_pass": "none",
                    "determined": 20
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        savingThrow.setSource(object);
        savingThrow.prepare(context);
        savingThrow.setTarget(object);
        savingThrow.invoke(context);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(10L, object.seek("health_data.current"),
                "SavingThrow Subevent should have dealt no damage (10-0=10)."
        );
    }

    @Test
    @DisplayName("SavingThrow Subevent can deal half damage on a pass")
    void test19() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = """
                {
                    "subevent": "saving_throw",
                    "save_ability": "int",
                    "difficulty_class_ability": "int",
                    "damage": [
                        {
                            "type": "fire",
                            "dice": [
                                { "size": 10, "determined": 2 }
                            ]
                        },
                        {
                            "type": "cold",
                            "dice": [
                                { "size": 10, "determined": 2 }
                            ]
                        }
                    ],
                    "damage_on_pass": "half",
                    "determined": 20
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        savingThrow.setSource(object);
        savingThrow.prepare(context);
        savingThrow.setTarget(object);
        savingThrow.invoke(context);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(8L, object.seek("health_data.current"),
                "SavingThrow Subevent should have dealt no damage (10-(4/2)=8)."
        );
    }

}
