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

public class CalculateProficiencyModifierTest {

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
    @DisplayName("CalculateProficiencyModifier Subevent throws SubeventMismatchException when subevent type doesn't match")
    void test0() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateProficiencyModifier();
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
                "CalculateProficiencyModifier Subevent should throw a SubeventMismatchException if the specified subevent doesn't match."
        );
    }

    @Test
    @DisplayName("CalculateProficiencyModifier Subevent setup method & getter work")
    void test1() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateProficiencyModifier();
        String subeventJsonString = """
                {
                    "subevent": "calculate_proficiency_modifier"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateProficiencyModifier calculateProficiencyModifier = (CalculateProficiencyModifier) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateProficiencyModifier.setSource(object);
        calculateProficiencyModifier.prepare(context);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(2L, calculateProficiencyModifier.get(),
                "CalculateAbilityScore Subevent did not report raw proficiency correctly."
        );
    }

    @Test
    @DisplayName("CalculateAbilityScore Subevent can set proficiency")
    void test2() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateProficiencyModifier();
        String subeventJsonString = """
                {
                    "subevent": "calculate_proficiency_modifier"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateProficiencyModifier calculateProficiencyModifier = (CalculateProficiencyModifier) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateProficiencyModifier.setSource(object);
        calculateProficiencyModifier.prepare(context);
        calculateProficiencyModifier.set(3L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(3L, calculateProficiencyModifier.get(),
                "CalculateAbilityScore Subevent did not set proficiency correctly."
        );
    }

    @Test
    @DisplayName("CalculateAbilityScore Subevent can set proficiency (override prior set with higher)")
    void test3() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateProficiencyModifier();
        String subeventJsonString = """
                {
                    "subevent": "calculate_proficiency_modifier"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateProficiencyModifier calculateProficiencyModifier = (CalculateProficiencyModifier) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateProficiencyModifier.setSource(object);
        calculateProficiencyModifier.prepare(context);
        calculateProficiencyModifier.set(3L);
        calculateProficiencyModifier.set(4L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(4L, calculateProficiencyModifier.get(),
                "CalculateAbilityScore Subevent should be able to override proficiency set value with higher value."
        );
    }

    @Test
    @DisplayName("CalculateAbilityScore Subevent can not set proficiency (override prior set with lower)")
    void test4() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateProficiencyModifier();
        String subeventJsonString = """
                {
                    "subevent": "calculate_proficiency_modifier"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateProficiencyModifier calculateProficiencyModifier = (CalculateProficiencyModifier) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateProficiencyModifier.setSource(object);
        calculateProficiencyModifier.prepare(context);
        calculateProficiencyModifier.set(4L);
        calculateProficiencyModifier.set(3L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(4L, calculateProficiencyModifier.get(),
                "CalculateAbilityScore Subevent should not be able to override proficiency set value with lower value."
        );
    }

    @Test
    @DisplayName("CalculateAbilityScore Subevent can add bonus to proficiency")
    void test5() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateProficiencyModifier();
        String subeventJsonString = """
                {
                    "subevent": "calculate_proficiency_modifier"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateProficiencyModifier calculateProficiencyModifier = (CalculateProficiencyModifier) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateProficiencyModifier.setSource(object);
        calculateProficiencyModifier.prepare(context);
        calculateProficiencyModifier.addBonus(1L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(3L, calculateProficiencyModifier.get(),
                "CalculateAbilityScore Subevent did not add bonus to proficiency properly."
        );
    }

    @Test
    @DisplayName("CalculateAbilityScore Subevent can add bonus to a set proficiency")
    void test6() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateProficiencyModifier();
        String subeventJsonString = """
                {
                    "subevent": "calculate_proficiency_modifier"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateProficiencyModifier calculateProficiencyModifier = (CalculateProficiencyModifier) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateProficiencyModifier.setSource(object);
        calculateProficiencyModifier.prepare(context);
        calculateProficiencyModifier.addBonus(1L);
        calculateProficiencyModifier.set(3L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(4L, calculateProficiencyModifier.get(),
                "CalculateAbilityScore Subevent did not add bonus to set proficiency properly."
        );
    }

}
