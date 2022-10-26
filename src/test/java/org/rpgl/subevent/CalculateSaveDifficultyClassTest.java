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

public class CalculateSaveDifficultyClassTest {

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
    @DisplayName("CalculateSaveDifficultyClass Subevent throws SubeventMismatchException when subevent type doesn't match")
    void test0() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateSaveDifficultyClass();
        String subeventJsonString = "{" +
                "\"subevent\": \"not_a_subevent\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);

        /*
         * Verify subevent behaves as expected
         */
        assertThrows(SubeventMismatchException.class,
                () -> subevent.clone(subeventJson).invoke(null, null),
                "CalculateSaveDifficultyClass Subevent should throw a SubeventMismatchException if the specified subevent doesn't match."
        );
    }

    @Test
    @DisplayName("CalculateSaveDifficultyClass Subevent setup method works (all test features included)")
    void test1() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateSaveDifficultyClass();
        String subeventJsonString = "{" +
                "\"subevent\": \"calculate_save_difficulty_class\"," +
                "\"difficulty_class_ability\": \"int\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateSaveDifficultyClass calculateSaveDifficultyClass = (CalculateSaveDifficultyClass) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        RPGLObject object = RPGLFactory.newObject("dummy:dummy_hollow");
        calculateSaveDifficultyClass.prepare(object);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(10L, calculateSaveDifficultyClass.get(),
                "CalculateSaveDifficultyClass Subevent did not calculate raw save DC correctly."
        );
    }

}
