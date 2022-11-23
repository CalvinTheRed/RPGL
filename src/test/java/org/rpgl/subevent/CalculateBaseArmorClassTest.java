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

/**
 * Testing class for subevent.CalculateBaseArmorClass class.
 *
 * @author Calvin Withun
 */
public class CalculateBaseArmorClassTest {

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
    @DisplayName("CalculateBaseArmorClass Subevent throws SubeventMismatchException when subevent type doesn't match")
    void test0() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateBaseArmorClass();
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
                "CalculateBaseArmorClass Subevent should throw a SubeventMismatchException if the specified subevent doesn't match."
        );
    }

    @Test
    @DisplayName("CalculateBaseArmorClass Subevent prepare method sets base armor class")
    void test1() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateBaseArmorClass();
        String subeventJsonString = """
                {
                    "subevent": "calculate_base_armor_class"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateBaseArmorClass calculateBaseArmorClass = (CalculateBaseArmorClass) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateBaseArmorClass.setSource(object);
        calculateBaseArmorClass.prepare(context);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(9L, calculateBaseArmorClass.get(),
                "CalculateBaseArmorClass Subevent did not set base armor class correctly."
        );
    }

    @Test
    @DisplayName("CalculateBaseArmorClass Subevent can set armor class")
    void test2() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateBaseArmorClass();
        String subeventJsonString = """
                {
                    "subevent": "calculate_base_armor_class"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateBaseArmorClass calculateBaseArmorClass = (CalculateBaseArmorClass) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateBaseArmorClass.setSource(object);
        calculateBaseArmorClass.prepare(context);
        calculateBaseArmorClass.set(10L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(10L, calculateBaseArmorClass.get(),
                "CalculateBaseArmorClass Subevent did not set armor class correctly."
        );
    }

    @Test
    @DisplayName("CalculateBaseArmorClass Subevent can set armor class (override prior set with higher)")
    void test3() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateBaseArmorClass();
        String subeventJsonString = """
                {
                    "subevent": "calculate_base_armor_class"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateBaseArmorClass calculateBaseArmorClass = (CalculateBaseArmorClass) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateBaseArmorClass.setSource(object);
        calculateBaseArmorClass.prepare(context);
        calculateBaseArmorClass.set(10L);
        calculateBaseArmorClass.set(12L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(12L, calculateBaseArmorClass.get(),
                "CalculateBaseArmorClass Subevent should be able to override armor class set value with higher value."
        );
    }

    @Test
    @DisplayName("CalculateBaseArmorClass Subevent can add bonus to armor class")
    void test5() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateBaseArmorClass();
        String subeventJsonString = """
                {
                    "subevent": "calculate_base_armor_class"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateBaseArmorClass calculateBaseArmorClass = (CalculateBaseArmorClass) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateBaseArmorClass.setSource(object);
        calculateBaseArmorClass.prepare(context);
        calculateBaseArmorClass.addBonus(3L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(12L, calculateBaseArmorClass.get(),
                "CalculateBaseArmorClass Subevent did not add bonus to armor class properly."
        );
    }

    @Test
    @DisplayName("CalculateBaseArmorClass Subevent can add bonus to a set armor class")
    void test6() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateBaseArmorClass();
        String subeventJsonString = """
                {
                    "subevent": "calculate_base_armor_class"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateBaseArmorClass calculateBaseArmorClass = (CalculateBaseArmorClass) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateBaseArmorClass.setSource(object);
        calculateBaseArmorClass.prepare(context);
        calculateBaseArmorClass.addBonus(3L);
        calculateBaseArmorClass.set(12L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(15L, calculateBaseArmorClass.get(),
                "CalculateBaseArmorClass Subevent did not add bonus to set armor class properly."
        );
    }

}
