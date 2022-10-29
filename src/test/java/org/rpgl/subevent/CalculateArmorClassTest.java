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
import org.rpgl.uuidtable.UUIDTable;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CalculateArmorClassTest {

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
    }

    @Test
    @DisplayName("CalculateArmorClass Subevent throws SubeventMismatchException when subevent type doesn't match")
    void test0() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateArmorClass();
        String subeventJsonString = "{" +
                "\"subevent\": \"not_a_subevent\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        RPGLContext context = new RPGLContext(null);

        /*
         * Verify subevent behaves as expected
         */
        assertThrows(SubeventMismatchException.class,
                () -> subevent.clone(subeventJson).invoke(context),
                "CalculateArmorClass Subevent should throw a SubeventMismatchException if the specified subevent doesn't match."
        );
    }

    @Test
    @DisplayName("CalculateArmorClass Subevent setup method & getter work")
    void test1() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateArmorClass();
        String subeventJsonString = "{ \"subevent\": \"calculate_armor_class\" }";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateArmorClass calculateArmorClass = (CalculateArmorClass) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateArmorClass.setSource(object);
        calculateArmorClass.prepare(context);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(9L, calculateArmorClass.get(),
                "CalculateArmorClass Subevent did not report raw ability score correctly."
        );
    }

    @Test
    @DisplayName("CalculateArmorClass Subevent can set ability")
    void test2() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateArmorClass();
        String subeventJsonString = "{ \"subevent\": \"calculate_armor_class\" }";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateArmorClass calculateArmorClass = (CalculateArmorClass) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateArmorClass.setSource(object);
        calculateArmorClass.prepare(context);
        calculateArmorClass.set(10L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(10L, calculateArmorClass.get(),
                "CalculateArmorClass Subevent did not set ability score correctly."
        );
    }

    @Test
    @DisplayName("CalculateArmorClass Subevent can set ability (override prior set with higher)")
    void test3() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateArmorClass();
        String subeventJsonString = "{ \"subevent\": \"calculate_armor_class\" }";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateArmorClass calculateArmorClass = (CalculateArmorClass) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateArmorClass.setSource(object);
        calculateArmorClass.prepare(context);
        calculateArmorClass.set(10L);
        calculateArmorClass.set(12L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(12L, calculateArmorClass.get(),
                "CalculateArmorClass Subevent should be able to override ability score set value with higher value."
        );
    }

    @Test
    @DisplayName("CalculateArmorClass Subevent can not set ability (override prior set with lower)")
    void test4() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateArmorClass();
        String subeventJsonString = "{ \"subevent\": \"calculate_armor_class\" }";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateArmorClass calculateArmorClass = (CalculateArmorClass) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateArmorClass.setSource(object);
        calculateArmorClass.prepare(context);
        calculateArmorClass.set(10L);
        calculateArmorClass.set(8L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(10L, calculateArmorClass.get(),
                "CalculateArmorClass Subevent should not be able to override ability score set value with lower value."
        );
    }

    @Test
    @DisplayName("CalculateArmorClass Subevent can add bonus to ability score")
    void test5() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateArmorClass();
        String subeventJsonString = "{ \"subevent\": \"calculate_armor_class\" }";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateArmorClass calculateArmorClass = (CalculateArmorClass) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateArmorClass.setSource(object);
        calculateArmorClass.prepare(context);
        calculateArmorClass.addBonus(3L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(12L, calculateArmorClass.get(),
                "CalculateArmorClass Subevent did not add bonus to ability score properly."
        );
    }

    @Test
    @DisplayName("CalculateArmorClass Subevent can add bonus to a set ability score")
    void test6() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateArmorClass();
        String subeventJsonString = "{ \"subevent\": \"calculate_armor_class\" }";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateArmorClass calculateArmorClass = (CalculateArmorClass) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateArmorClass.setSource(object);
        calculateArmorClass.prepare(context);
        calculateArmorClass.addBonus(3L);
        calculateArmorClass.set(12L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(15L, calculateArmorClass.get(),
                "CalculateArmorClass Subevent did not add bonus to set ability score properly."
        );
    }

}
