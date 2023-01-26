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
 * Testing class for subevent.CalculateSaveDifficultyClass class.
 *
 * @author Calvin Withun
 */
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
                "CalculateSaveDifficultyClass Subevent should throw a SubeventMismatchException if the specified subevent doesn't match."
        );
    }

    @Test
    @DisplayName("CalculateSaveDifficultyClass Subevent prepare method calculates basic save difficulty class correctly")
    void test1() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new CalculateSaveDifficultyClass();
        String subeventJsonString = """
                {
                    "subevent": "calculate_save_difficulty_class",
                    "difficulty_class_ability": "int"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        CalculateSaveDifficultyClass calculateSaveDifficultyClass = (CalculateSaveDifficultyClass) subevent.clone(subeventJson);
        RPGLObject object = RPGLFactory.newObject("test:all_abilities_12");
        assert object != null;
        JsonArray contextArray = new JsonArray();
        contextArray.add(object.getUuid());
        RPGLContext context = new RPGLContext(contextArray);

        /*
         * Invoke subevent method
         */
        calculateSaveDifficultyClass.setSource(object);
        calculateSaveDifficultyClass.prepare(context);

        /*
         * Verify subevent behaves as expected
         */
        assertEquals(11L, calculateSaveDifficultyClass.get(),
                "CalculateSaveDifficultyClass Subevent did not calculates basic save difficulty class correctly."
        );
    }

}
