package org.rpgl.subevent;

import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.exception.SubeventMismatchException;

import static org.junit.jupiter.api.Assertions.*;

public class ContestRerollChanceTest {

    @Test
    @DisplayName("ContestRerollChance Subevent throws SubeventMismatchException when subevent type doesn't match")
    void test0() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new ContestRerollChance();
        String subeventJsonString = "{" +
                "\"subevent\": \"not_a_subevent\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);

        /*
         * Verify subevent behaves as expected
         */
        assertThrows(SubeventMismatchException.class,
                () -> subevent.clone(subeventJson).invoke(null, null),
                "ContestRerollChance Subevent should throw a SubeventMismatchException if the specified subevent doesn't match."
        );
    }

    @Test
    @DisplayName("ContestRerollChance Subevent defaults to false")
    void test1() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new ContestRerollChance();
        String subeventJsonString = "{" +
                "\"subevent\": \"contest_reroll_chance\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        ContestRerollChance contestRerollChance = (ContestRerollChance) subevent.clone(subeventJson);
        contestRerollChance.prepare(null);

        /*
         * Verify subevent behaves as expected
         */
        assertFalse(contestRerollChance.wasRerollRequested(),
                "ContestRerollChance Subevent should default to false."
        );
    }

    @Test
    @DisplayName("ContestRerollChance Subevent can have reroll requested")
    void test2() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new ContestRerollChance();
        String subeventJsonString = "{" +
                "\"subevent\": \"contest_reroll_chance\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        ContestRerollChance contestRerollChance = (ContestRerollChance) subevent.clone(subeventJson);
        contestRerollChance.prepare(null);

        /*
         * Invoke subevent method
         */
        contestRerollChance.requestReroll("reroll_mode");

        /*
         * Verify subevent behaves as expected
         */
        assertTrue(contestRerollChance.wasRerollRequested(),
                "ContestRerollChance Subevent did not properly report when a reroll was requested."
        );
        assertEquals("reroll_mode", contestRerollChance.getRerollMode(),
                "ContestRerollChance Subevent did not properly report reroll mode."
        );
    }

}