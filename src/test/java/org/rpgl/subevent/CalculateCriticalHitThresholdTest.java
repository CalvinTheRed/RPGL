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
 * Testing class for subevent.CalculateCriticalHitThreshold class.
 *
 * @author Calvin Withun
 */
public class CalculateCriticalHitThresholdTest {

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
    @DisplayName("CalculateCriticalHitThreshold Subevent throws SubeventMismatchException when subevent type doesn't match")
    void test0() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateCriticalHitThreshold();
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
                "CalculateCriticalHitThreshold Subevent should throw a SubeventMismatchException if the specified subevent doesn't match."
        );
    }

    @Test
    @DisplayName("CalculateCriticalHitThreshold Subevent prepare method defaults critical hit threshold correctly")
    void test1() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateCriticalHitThreshold();
        String subeventJsonString = """
                {
                    "subevent": "calculate_critical_hit_threshold"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateCriticalHitThreshold calculateCriticalHitThreshold = (CalculateCriticalHitThreshold) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("test:blank");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.getUuid());
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateCriticalHitThreshold.setSource(object);
        calculateCriticalHitThreshold.prepare(context);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(20L, calculateCriticalHitThreshold.get(),
                "CalculateCriticalHitThreshold Subevent should default to 20."
        );
    }

    @Test
    @DisplayName("CalculateCriticalHitThreshold Subevent can set critical hit threshold")
    void test2() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateCriticalHitThreshold();
        String subeventJsonString = """
                {
                    "subevent": "calculate_critical_hit_threshold"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateCriticalHitThreshold calculateCriticalHitThreshold = (CalculateCriticalHitThreshold) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("test:blank");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.getUuid());
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateCriticalHitThreshold.setSource(object);
        calculateCriticalHitThreshold.prepare(context);
        calculateCriticalHitThreshold.set(10L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(10L, calculateCriticalHitThreshold.get(),
                "CalculateCriticalHitThreshold Subevent did not set critical hit threshold correctly."
        );
    }

    @Test
    @DisplayName("CalculateCriticalHitThreshold Subevent can set critical hit threshold (override prior set with higher)")
    void test3() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateCriticalHitThreshold();
        String subeventJsonString = """
                {
                    "subevent": "calculate_critical_hit_threshold"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateCriticalHitThreshold calculateCriticalHitThreshold = (CalculateCriticalHitThreshold) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("test:blank");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.getUuid());
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateCriticalHitThreshold.setSource(object);
        calculateCriticalHitThreshold.prepare(context);
        calculateCriticalHitThreshold.set(10L);
        calculateCriticalHitThreshold.set(12L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(12L, calculateCriticalHitThreshold.get(),
                "CalculateCriticalHitThreshold Subevent should be able to override critical hit threshold set value with higher value."
        );
    }

    @Test
    @DisplayName("CalculateCriticalHitThreshold Subevent can add bonus to critical hit threshold")
    void test5() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateCriticalHitThreshold();
        String subeventJsonString = """
                {
                    "subevent": "calculate_critical_hit_threshold"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateCriticalHitThreshold calculateCriticalHitThreshold = (CalculateCriticalHitThreshold) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("test:blank");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.getUuid());
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateCriticalHitThreshold.setSource(object);
        calculateCriticalHitThreshold.prepare(context);
        calculateCriticalHitThreshold.addBonus(-1L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(19L, calculateCriticalHitThreshold.get(),
                "CalculateCriticalHitThreshold Subevent did not add bonus to critical hit threshold properly."
        );
    }

    @Test
    @DisplayName("CalculateCriticalHitThreshold Subevent can add bonus to a set critical hit threshold")
    void test6() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateCriticalHitThreshold();
        String subeventJsonString = """
                {
                    "subevent": "calculate_critical_hit_threshold"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateCriticalHitThreshold calculateCriticalHitThreshold = (CalculateCriticalHitThreshold) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("test:blank");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.getUuid());
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateCriticalHitThreshold.setSource(object);
        calculateCriticalHitThreshold.prepare(context);
        calculateCriticalHitThreshold.addBonus(-1L);
        calculateCriticalHitThreshold.set(19L);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(18L, calculateCriticalHitThreshold.get(),
                "CalculateCriticalHitThreshold Subevent did not add bonus to set critical hit threshold properly."
        );
    }

}
