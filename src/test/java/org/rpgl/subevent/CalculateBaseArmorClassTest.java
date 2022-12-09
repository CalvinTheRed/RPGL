package org.rpgl.subevent;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.junit.jupiter.api.*;
import org.rpgl.core.RPGLContext;
import org.rpgl.core.RPGLFactory;
import org.rpgl.core.RPGLItem;
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
        RPGLObject object = RPGLFactory.newObject("test:all_abilities_12");
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
        assertEquals(11L, calculateBaseArmorClass.get(),
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
        RPGLObject object = RPGLFactory.newObject("test:all_abilities_12");
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
        RPGLObject object = RPGLFactory.newObject("test:all_abilities_12");
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
        calculateBaseArmorClass.set(14L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(14L, calculateBaseArmorClass.get(),
                "CalculateBaseArmorClass Subevent should be able to override armor class set value with higher value."
        );
    }

    @Test
    @DisplayName("CalculateBaseArmorClass Subevent can add bonus to armor class")
    void test4() throws Exception {
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
        RPGLObject object = RPGLFactory.newObject("test:all_abilities_12");
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
        assertEquals(14L, calculateBaseArmorClass.get(),
                "CalculateBaseArmorClass Subevent did not add bonus to armor class properly."
        );
    }

    @Test
    @DisplayName("CalculateBaseArmorClass Subevent can add bonus to a set armor class")
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
        RPGLObject object = RPGLFactory.newObject("test:all_abilities_12");
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
        calculateBaseArmorClass.set(14L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(17L, calculateBaseArmorClass.get(),
                "CalculateBaseArmorClass Subevent did not add bonus to set armor class properly."
        );
    }

    @Test
    @DisplayName("CalculateBaseArmorClass Subevent can use light armor for armor class")
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
        RPGLObject object = RPGLFactory.newObject("test:all_abilities_12");
        assert object != null;
        RPGLItem armor = RPGLFactory.newItem("test:light_armor");
        assert armor != null;
        object.giveItem((String) armor.get("uuid"));
        object.equipItem((String) armor.get("uuid"), "armor");
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
        assertEquals(13L, calculateBaseArmorClass.get(),
                "Armor Class should be 13 (12+1) using testing light armor."
        );
    }

    @Test
    @DisplayName("CalculateBaseArmorClass Subevent can use medium armor for armor class")
    void test7() throws Exception {
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
        RPGLObject object = RPGLFactory.newObject("test:all_abilities_20");
        assert object != null;
        RPGLItem armor = RPGLFactory.newItem("test:medium_armor");
        assert armor != null;
        object.giveItem((String) armor.get("uuid"));
        object.equipItem((String) armor.get("uuid"), "armor");
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
        assertEquals(16L, calculateBaseArmorClass.get(),
                "Armor Class should be 16 (14+2) using testing medium armor."
        );
    }

    @Test
    @DisplayName("CalculateBaseArmorClass Subevent can use heavy armor for armor class")
    void test8() throws Exception {
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
        RPGLObject object = RPGLFactory.newObject("test:all_abilities_20");
        assert object != null;
        RPGLItem armor = RPGLFactory.newItem("test:heavy_armor");
        assert armor != null;
        object.giveItem((String) armor.get("uuid"));
        object.equipItem((String) armor.get("uuid"), "armor");
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
        assertEquals(18L, calculateBaseArmorClass.get(),
                "Armor Class should be 18 (18+0) using testing medium armor."
        );
    }

    @Test
    @DisplayName("CalculateBaseArmorClass Subevent can use one shield for armor class")
    void test9() throws Exception {
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
        RPGLObject object = RPGLFactory.newObject("test:all_abilities_12");
        assert object != null;
        RPGLItem shield = RPGLFactory.newItem("test:shield");
        assert shield != null;
        object.giveItem((String) shield.get("uuid"));
        object.equipItem((String) shield.get("uuid"), "hand_1");
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
        assertEquals(13L, calculateBaseArmorClass.get(),
                "Armor Class should be 13 (10+1+2) using testing shield."
        );
    }

    @Test
    @DisplayName("CalculateBaseArmorClass Subevent can only use 1 shield at a time for armor class")
    void test10() throws Exception {
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
        RPGLObject object = RPGLFactory.newObject("test:all_abilities_12");
        assert object != null;
        RPGLItem shield1 = RPGLFactory.newItem("test:shield");
        RPGLItem shield2 = RPGLFactory.newItem("test:shield");
        assert shield1 != null;
        assert shield2 != null;
        object.giveItem((String) shield1.get("uuid"));
        object.equipItem((String) shield1.get("uuid"), "hand_1");
        object.giveItem((String) shield2.get("uuid"));
        object.equipItem((String) shield2.get("uuid"), "hand_2");
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
        assertEquals(13L, calculateBaseArmorClass.get(),
                "Armor Class should be 13 (10+1+2) using testing shields."
        );
    }

    @Test
    @DisplayName("CalculateBaseArmorClass Subevent prioritizes the better shield for armor class")
    void test11() throws Exception {
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
        RPGLObject object = RPGLFactory.newObject("test:all_abilities_12");
        assert object != null;
        RPGLItem shield = RPGLFactory.newItem("test:shield");
        RPGLItem shieldPlusOne = RPGLFactory.newItem("test:shield_plus_1");
        assert shield != null;
        assert shieldPlusOne != null;
        object.giveItem((String) shield.get("uuid"));
        object.equipItem((String) shield.get("uuid"), "hand_1");
        object.giveItem((String) shieldPlusOne.get("uuid"));
        object.equipItem((String) shieldPlusOne.get("uuid"), "hand_2");
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
        assertEquals(14L, calculateBaseArmorClass.get(),
                "Armor Class should be 14 (10+1+3) using the better shield."
        );
    }

}
