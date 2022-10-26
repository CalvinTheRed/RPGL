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
        String subeventJsonString = "{" +
                "\"subevent\": \"not_a_subevent\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);

        /*
         * Verify subevent behaves as expected
         */
        assertThrows(SubeventMismatchException.class,
                () -> subevent.clone(subeventJson).invoke(null, null),
                "CalculateProficiencyModifier Subevent should throw a SubeventMismatchException if the specified subevent doesn't match."
        );
    }

    @Test
    @DisplayName("CalculateProficiencyModifier Subevent setup method & getter work")
    void test1() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateProficiencyModifier();
        String subeventJsonString = "{" +
                "\"subevent\": \"calculate_proficiency_modifier\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateProficiencyModifier calculateAbilityScore = (CalculateProficiencyModifier) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        calculateAbilityScore.prepare(object);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(2L, (Long) calculateAbilityScore.getProficiencyModifier(),
                "CalculateAbilityScore Subevent did not report raw proficiency correctly."
        );
    }

    @Test
    @DisplayName("CalculateAbilityScore Subevent can set proficiency")
    void test2() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateProficiencyModifier();
        String subeventJsonString = "{" +
                "\"subevent\": \"calculate_proficiency_modifier\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateProficiencyModifier calculateProficiencyBonus = (CalculateProficiencyModifier) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        calculateProficiencyBonus.prepare(object);
        calculateProficiencyBonus.set(3L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(3L, (Long) calculateProficiencyBonus.getProficiencyModifier(),
                "CalculateAbilityScore Subevent did not set proficiency correctly."
        );
    }

    @Test
    @DisplayName("CalculateAbilityScore Subevent can set proficiency (override prior set with higher)")
    void test3() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateProficiencyModifier();
        String subeventJsonString = "{" +
                "\"subevent\": \"calculate_proficiency_modifier\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateProficiencyModifier calculateProficiencyBonus = (CalculateProficiencyModifier) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        calculateProficiencyBonus.prepare(object);
        calculateProficiencyBonus.set(3L);
        calculateProficiencyBonus.set(4L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(4L, (Long) calculateProficiencyBonus.getProficiencyModifier(),
                "CalculateAbilityScore Subevent should be able to override proficiency set value with higher value."
        );
    }

    @Test
    @DisplayName("CalculateAbilityScore Subevent can not set proficiency (override prior set with lower)")
    void test4() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateProficiencyModifier();
        String subeventJsonString = "{" +
                "\"subevent\": \"calculate_proficiency_modifier\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateProficiencyModifier calculateProficiencyBonus = (CalculateProficiencyModifier) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        calculateProficiencyBonus.prepare(object);
        calculateProficiencyBonus.set(4L);
        calculateProficiencyBonus.set(3L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(4L, (Long) calculateProficiencyBonus.getProficiencyModifier(),
                "CalculateAbilityScore Subevent should not be able to override proficiency set value with lower value."
        );
    }

    @Test
    @DisplayName("CalculateAbilityScore Subevent can add bonus to proficiency")
    void test5() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateProficiencyModifier();
        String subeventJsonString = "{" +
                "\"subevent\": \"calculate_proficiency_modifier\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateProficiencyModifier calculateProficiencyBonus = (CalculateProficiencyModifier) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        calculateProficiencyBonus.prepare(object);
        calculateProficiencyBonus.addBonus(1L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(3L, (Long) calculateProficiencyBonus.getProficiencyModifier(),
                "CalculateAbilityScore Subevent did not add bonus to proficiency properly."
        );
    }

    @Test
    @DisplayName("CalculateAbilityScore Subevent can add bonus to a set proficiency")
    void test6() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateProficiencyModifier();
        String subeventJsonString = "{" +
                "\"subevent\": \"calculate_proficiency_modifier\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateProficiencyModifier calculateProficiencyBonus = (CalculateProficiencyModifier) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        calculateProficiencyBonus.prepare(object);
        calculateProficiencyBonus.addBonus(1L);
        calculateProficiencyBonus.set(3L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(4L, (Long) calculateProficiencyBonus.getProficiencyModifier(),
                "CalculateAbilityScore Subevent did not add bonus to set proficiency properly."
        );
    }

}
