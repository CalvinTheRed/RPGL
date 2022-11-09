package org.rpgl.subevent;

import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.exception.SubeventMismatchException;

import static org.junit.jupiter.api.Assertions.*;

public class GetSaveProficiencyTest {

    @AfterEach
    void afterEach() {
        DummySubevent.resetCounter();
    }

    @Test
    @DisplayName("GetSaveProficiency Subevent throws SubeventMismatchException when subevent type doesn't match")
    void test0() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new GetSaveProficiency();
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
                "GetSaveProficiency Subevent should throw a SubeventMismatchException if the specified subevent doesn't match."
        );
    }

    @Test
    @DisplayName("GetSaveProficiency Subevent defaults to not indicating proficiency")
    void test1() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new GetSaveProficiency();
        String subeventJsonString = """
                {
                    "subevent": "get_save_proficiency",
                    "save_ability": "str"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        GetSaveProficiency getSaveProficiency = (GetSaveProficiency) subevent.clone(subeventJson);
        RPGLContext context = new RPGLContext(null);

        /*
         * Invoke subevent methods
         */
        getSaveProficiency.prepare(context);

        /*
         * Verify subevent behaves as expected
         */
        assertFalse(getSaveProficiency.getIsProficient(),
                "GetSaveProficiency Subevent should default to not indicate proficiency."
        );
    }

    @Test
    @DisplayName("GetSaveProficiency Subevent can update to indicate proficiency")
    void test2() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new GetSaveProficiency();
        String subeventJsonString = """
                {
                    "subevent": "get_save_proficiency",
                    "save_ability": "str"
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        GetSaveProficiency getSaveProficiency = (GetSaveProficiency) subevent.clone(subeventJson);
        RPGLContext context = new RPGLContext(null);

        /*
         * Invoke subevent methods
         */
        getSaveProficiency.prepare(context);
        getSaveProficiency.grantProficiency();

        /*
         * Verify subevent behaves as expected
         */
        assertTrue(getSaveProficiency.getIsProficient(),
                "GetSaveProficiency Subevent should indicate proficiency after having it granted."
        );
    }

}
