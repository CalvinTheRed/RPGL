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

/**
 * Testing class for subevent.GetWeaponProficiency class.
 *
 * @author Calvin Withun
 */
public class GetWeaponProficiencyTest {

    @AfterEach
    void afterEach() {
        DummySubevent.resetCounter();
    }

    @Test
    @DisplayName("GetWeaponProficiency Subevent throws SubeventMismatchException when subevent type doesn't match")
    void test0() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new GetWeaponProficiency();
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
                "GetWeaponProficiency Subevent should throw a SubeventMismatchException if the specified subevent doesn't match."
        );
    }

    @Test
    @DisplayName("GetWeaponProficiency Subevent defaults to not indicating proficiency")
    void test1() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new GetWeaponProficiency();
        String subeventJsonString = """
                {
                    "subevent": "get_weapon_proficiency",
                    "item": ""
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        GetWeaponProficiency getWeaponProficiency = (GetWeaponProficiency) subevent.clone(subeventJson);

        /*
         * Verify subevent behaves as expected
         */
        assertFalse(getWeaponProficiency.getIsProficient(),
                "GetWeaponProficiency Subevent should default to not indicate proficiency."
        );
    }

    @Test
    @DisplayName("GetWeaponProficiency Subevent can update to indicate proficiency")
    void test2() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new GetWeaponProficiency();
        String subeventJsonString = """
                {
                    "subevent": "get_weapon_proficiency",
                    "item": ""
                }
                """;
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        GetWeaponProficiency getWeaponProficiency = (GetWeaponProficiency) subevent.clone(subeventJson);

        /*
         * Invoke subevent methods
         */
        getWeaponProficiency.grantProficiency();

        /*
         * Verify subevent behaves as expected
         */
        assertTrue(getWeaponProficiency.getIsProficient(),
                "GetWeaponProficiency Subevent should indicate proficiency after having it granted."
        );
    }

}
