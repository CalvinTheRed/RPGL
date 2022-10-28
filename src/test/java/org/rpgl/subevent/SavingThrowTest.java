package org.rpgl.subevent;

import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.junit.jupiter.api.*;
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

public class SavingThrowTest {

    @BeforeAll
    static void beforeAll() throws Exception {
        DatapackLoader.loadDatapacks(
                new File(Objects.requireNonNull(DatapackTest.class.getClassLoader().getResource("datapacks")).toURI())
        );
    }

    @AfterAll
    static void afterAll() {
        DatapackLoader.DATAPACKS.clear();
    }

    @AfterEach
    void afterEach() {
        UUIDTable.clear();
        DummySubevent.resetCounter();
        Die.flush();
    }

    @Test
    @DisplayName("SavingThrowTest Subevent throws SubeventMismatchException when subevent type doesn't match")
    void test0() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = "{" +
                "\"subevent\": \"not_a_subevent\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);

        /*
         * Verify subevent behaves as expected
         */
        assertThrows(SubeventMismatchException.class,
                () -> subevent.clone(subeventJson).invoke(null, null),
                "SavingThrow Subevent should throw a SubeventMismatchException if the specified subevent doesn't match."
        );
    }

    @Test
    @DisplayName("SavingThrow Subevent setup method & roll work")
    void test1() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = "{" +
                "\"subevent\": \"saving_throw\"," +
                "\"save_ability\": \"int\"," +
                "\"difficulty_class_ability\":\"int\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        savingThrow.prepare(object);
        Die.queue(10L);
        savingThrow.roll();

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(10L, savingThrow.get(),
                "SavingThrow Subevent did not report raw roll correctly."
        );
    }

    @Test
    @DisplayName("SavingThrow Subevent can set roll")
    void test2() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = "{" +
                "\"subevent\": \"saving_throw\"," +
                "\"save_ability\": \"int\"," +
                "\"difficulty_class_ability\":\"int\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        savingThrow.prepare(object);
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
        String subeventJsonString = "{" +
                "\"subevent\": \"saving_throw\"," +
                "\"save_ability\": \"int\"," +
                "\"difficulty_class_ability\":\"int\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        savingThrow.prepare(object);
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
    @DisplayName("SavingThrow Subevent can not set roll (override prior set with lower)")
    void test4() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = "{" +
                "\"subevent\": \"saving_throw\"," +
                "\"save_ability\": \"int\"," +
                "\"difficulty_class_ability\":\"int\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        savingThrow.prepare(object);
        savingThrow.set(10L);
        savingThrow.set(8L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(10L, savingThrow.get(),
                "SavingThrow Subevent should not be able to override roll set value with lower value."
        );
    }

    @Test
    @DisplayName("SavingThrow Subevent can add bonus to a set roll")
    void test5() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = "{" +
                "\"subevent\": \"saving_throw\"," +
                "\"save_ability\": \"int\"," +
                "\"difficulty_class_ability\":\"int\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        savingThrow.prepare(object);
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
        String subeventJsonString = "{" +
                "\"subevent\": \"saving_throw\"," +
                "\"save_ability\": \"int\"," +
                "\"difficulty_class_ability\":\"int\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        savingThrow.prepare(object);
        Die.queue(20L);
        savingThrow.invoke(object, object);

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
        String subeventJsonString = "{" +
                "\"subevent\": \"saving_throw\"," +
                "\"save_ability\": \"int\"," +
                "\"difficulty_class_ability\":\"int\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        savingThrow.prepare(object);
        Die.queue(1L);
        savingThrow.invoke(object, object);

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
        String subeventJsonString = "{" +
                "\"subevent\": \"saving_throw\"," +
                "\"save_ability\": \"int\"," +
                "\"difficulty_class_ability\":\"int\"," +
                "\"damage\": [" +
                "   {" +
                "   \"type\": \"fire\"," +
                "   \"dice\": [" +
                "       { \"size\": 10 }," +
                "       { \"size\": 10 }" +
                "   ]," +
                "   \"bonus\": 10" +
                "   },{" +
                "   \"type\": \"cold\"," +
                "   \"dice\": [" +
                "       { \"size\": 10 }," +
                "       { \"size\": 10 }" +
                "   ]," +
                "   \"bonus\": 10" +
                "   }" +
                "]," +
                "\"damage_on_pass\": \"half\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        Die.queue(10L);
        Die.queue(10L);
        Die.queue(10L);
        Die.queue(10L);
        savingThrow.prepare(object);

        /*
         * Verify subevent behaves as expected
         */
        String expectedJsonString = "{" +
                "\"fire\": 30," +
                "\"cold\": 30" +
                "}";
        JsonObject expectedJson = JsonParser.parseObjectString(expectedJsonString);
        assertEquals(expectedJson.toString(), savingThrow.subeventJson.get("damage").toString(),
                "SavingThrow Subevent calculated base damage incorrectly."
        );
    }

    @Test
    @DisplayName("SavingThrow Subevent can invoke nested subevents on pass")
    void test9() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = "{" +
                "\"subevent\": \"saving_throw\"," +
                "\"save_ability\": \"int\"," +
                "\"difficulty_class_ability\":\"int\"," +
                "\"pass\": [" +
                "   {" +
                "   \"subevent\": \"dummy_subevent\"" +
                "   }" +
                "]," +
                "\"fail\": [" +
                "   {" +
                "   \"subevent\": \"dummy_subevent\"" +
                "   }" +
                "]" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        savingThrow.prepare(object);
        savingThrow.set(20L);
        savingThrow.invoke(object, object);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(1, DummySubevent.counter,
                "SavingThrow Subevent did not invoke nested subevents on pass."
        );
    }

    @Test
    @DisplayName("SavingThrow Subevent can invoke nested subevents on fail")
    void test10() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = "{" +
                "\"subevent\": \"saving_throw\"," +
                "\"save_ability\": \"int\"," +
                "\"difficulty_class_ability\":\"int\"," +
                "\"pass\": [" +
                "   {" +
                "   \"subevent\": \"dummy_subevent\"" +
                "   }" +
                "]," +
                "\"fail\": [" +
                "   {" +
                "   \"subevent\": \"dummy_subevent\"" +
                "   }" +
                "]" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        savingThrow.prepare(object);
        savingThrow.set(1L);
        savingThrow.invoke(object, object);

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
        String subeventJsonString = "{" +
                "\"subevent\": \"saving_throw\"," +
                "\"save_ability\": \"int\"," +
                "\"difficulty_class_ability\":\"int\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        savingThrow.prepare(object);
        savingThrow.grantDisadvantage();
        assertFalse(savingThrow.advantageRoll(),
                "SavingThrow Subevent should not be at advantage."
        );
        assertTrue(savingThrow.disadvantageRoll(),
                "SavingThrow Subevent should be at disadvantage."
        );
        assertFalse(savingThrow.normalRoll(),
                "SavingThrow Subevent should not be a normal roll."
        );

        /*
         * Verify subevent behaves as expected
         */
        Die.queue(20L);
        Die.queue(1L);
        savingThrow.roll();
        assertEquals(1L, savingThrow.get(),
                "SavingThrow Subevent did not roll with disadvantage correctly."
        );
        Die.queue(1L);
        Die.queue(20L);
        savingThrow.roll();
        assertEquals(1L, savingThrow.get(),
                "SavingThrow Subevent did not roll with disadvantage correctly."
        );
    }

    @Test
    @DisplayName("SavingThrow Subevent can roll with advantage")
    void test12() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = "{" +
                "\"subevent\": \"saving_throw\"," +
                "\"save_ability\": \"int\"," +
                "\"difficulty_class_ability\":\"int\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        savingThrow.prepare(object);
        savingThrow.grantAdvantage();
        assertTrue(savingThrow.advantageRoll(),
                "SavingThrow Subevent should be at advantage."
        );
        assertFalse(savingThrow.disadvantageRoll(),
                "SavingThrow Subevent should not be at disadvantage."
        );
        assertFalse(savingThrow.normalRoll(),
                "SavingThrow Subevent should not be a normal roll."
        );

        /*
         * Verify subevent behaves as expected
         */
        Die.queue(20L);
        Die.queue(1L);
        savingThrow.roll();
        assertEquals(20L, savingThrow.get(),
                "SavingThrow Subevent did not roll with advantage correctly."
        );
        Die.queue(1L);
        Die.queue(20L);
        savingThrow.roll();
        assertEquals(20L, savingThrow.get(),
                "SavingThrow Subevent did not roll with advantage correctly."
        );
    }

    @Test
    @DisplayName("SavingThrow Subevent can roll with normal roll (advantage and disadvantage)")
    void test13() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new SavingThrow();
        String subeventJsonString = "{" +
                "\"subevent\": \"saving_throw\"," +
                "\"save_ability\": \"int\"," +
                "\"difficulty_class_ability\":\"int\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        SavingThrow savingThrow = (SavingThrow) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        savingThrow.prepare(object);
        savingThrow.grantAdvantage();
        savingThrow.grantDisadvantage();
        assertFalse(savingThrow.advantageRoll(),
                "SavingThrow Subevent should not be at advantage."
        );
        assertFalse(savingThrow.disadvantageRoll(),
                "SavingThrow Subevent should not be at disadvantage."
        );
        assertTrue(savingThrow.normalRoll(),
                "SavingThrow Subevent should be a normal roll."
        );

        /*
         * Verify subevent behaves as expected
         */
        Die.queue(20L);
        Die.queue(1L);
        savingThrow.roll();
        assertEquals(20L, savingThrow.get(),
                "SavingThrow Subevent did not roll with both advantage and disadvantage correctly."
        );
        Die.flush();
        Die.queue(1L);
        Die.queue(20L);
        savingThrow.roll();
        assertEquals(1L, savingThrow.get(),
                "SavingThrow Subevent did not roll with both advantage and disadvantage correctly."
        );
    }

}
