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
 * Testing class for subevent.CalculateAbilityScore class.
 *
 * @author Calvin Withun
 */
public class CalculateAbilityScoreTest {

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
    @DisplayName("CalculateAbilityScore Subevent throws SubeventMismatchException when subevent type doesn't match")
    void test0() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateAbilityScore();
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
                "CalculateAbilityScore Subevent should throw a SubeventMismatchException if the specified subevent doesn't match."
        );
    }

    @Test
    @DisplayName("CalculateAbilityScore Subevent prepare method sets base ability score")
    void test1() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateAbilityScore();
        String subeventJsonString = """
                {
                    "subevent": "calculate_ability_score",
                    "ability": "str"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateAbilityScore calculateAbilityScore = (CalculateAbilityScore) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateAbilityScore.setSource(object);
        calculateAbilityScore.prepare(context);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(7L, calculateAbilityScore.get(),
                "CalculateAbilityScore Subevent did not prepare base ability score correctly."
        );
    }

    @Test
    @DisplayName("CalculateAbilityScore Subevent can set ability score")
    void test2() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateAbilityScore();
        String subeventJsonString = """
                {
                    "subevent": "calculate_ability_score",
                    "ability": "str"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateAbilityScore calculateAbilityScore = (CalculateAbilityScore) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateAbilityScore.setSource(object);
        calculateAbilityScore.prepare(context);
        calculateAbilityScore.set(10L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(10L, calculateAbilityScore.get(),
                "CalculateAbilityScore Subevent did not set ability score correctly."
        );
    }

    @Test
    @DisplayName("CalculateAbilityScore Subevent can set ability score (override prior set with higher)")
    void test3() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateAbilityScore();
        String subeventJsonString = """
                {
                    "subevent": "calculate_ability_score",
                    "ability": "str"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateAbilityScore calculateAbilityScore = (CalculateAbilityScore) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateAbilityScore.setSource(object);
        calculateAbilityScore.prepare(context);
        calculateAbilityScore.set(10L);
        calculateAbilityScore.set(12L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(12L, calculateAbilityScore.get(),
                "CalculateAbilityScore Subevent should be able to override ability score set value with higher value."
        );
    }

    @Test
    @DisplayName("CalculateAbilityScore Subevent can add bonus to ability score")
    void test5() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateAbilityScore();
        String subeventJsonString = """
                {
                    "subevent": "calculate_ability_score",
                    "ability": "str"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateAbilityScore calculateAbilityScore = (CalculateAbilityScore) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateAbilityScore.setSource(object);
        calculateAbilityScore.prepare(context);
        calculateAbilityScore.addBonus(3L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(10L, calculateAbilityScore.get(),
                "CalculateAbilityScore Subevent did not add bonus to ability score properly."
        );
    }

    @Test
    @DisplayName("CalculateAbilityScore Subevent can add bonus to a set ability score")
    void test6() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateAbilityScore();
        String subeventJsonString = """
                {
                    "subevent": "calculate_ability_score",
                    "ability": "str"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateAbilityScore calculateAbilityScore = (CalculateAbilityScore) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.get("uuid"));
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateAbilityScore.setSource(object);
        calculateAbilityScore.prepare(context);
        calculateAbilityScore.addBonus(3L);
        calculateAbilityScore.set(12L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(15L, calculateAbilityScore.get(),
                "CalculateAbilityScore Subevent did not add bonus to set ability score properly."
        );
    }

}
