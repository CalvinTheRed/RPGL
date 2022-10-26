package org.rpgl.subevent;

import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.math.Die;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BaseDamageRollTest {

    @AfterEach
    void afterEach() {
        Die.flush();
    }

    @Test
    @DisplayName("BaseDamageRoll Subevent throws SubeventMismatchException when subevent type doesn't match")
    void test0() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new BaseDamageRoll();
        String subeventJsonString = "{" +
                "\"subevent\": \"not_a_subevent\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);

        /*
         * Verify subevent behaves as expected
         */
        assertThrows(SubeventMismatchException.class,
                () -> subevent.clone(subeventJson).invoke(null, null),
                "BaseDamageRoll Subevent should throw a SubeventMismatchException if the specified subevent doesn't match."
        );
    }

    @Test
    @DisplayName("BaseDamageRollTest Subevent returns the correct final damage values")
    void test1() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new BaseDamageRoll();
        String subeventJsonString = "{" +
                "\"subevent\": \"base_damage_roll\"," +
                "\"damage\": [" +
                "{" +
                "   \"type\": \"fire\"," +
                "   \"dice\": [" +
                "       { \"size\": 10, \"roll\": 10 }," +
                "       { \"size\": 10, \"roll\": 10 }" +
                "   ]," +
                "   \"bonus\": 2" +
                "},{" +
                "   \"type\": \"cold\"," +
                "   \"dice\": [" +
                "       { \"size\": 10, \"roll\": 10 }," +
                "       { \"size\": 10, \"roll\": 10 }" +
                "   ]," +
                "   \"bonus\": 2" +
                "}]}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        BaseDamageRoll baseDamageRoll = (BaseDamageRoll) subevent.clone(subeventJson);

        /*
         * Verify subevent behaves as expected
         */
        String expectedJsonString = "{ \"fire\": 22, \"cold\": 22 }";
        JsonObject expectedJson = JsonParser.parseObjectString(expectedJsonString);
        assertEquals(expectedJson.toString(), baseDamageRoll.getBaseDamage().toString(),
                "BaseDamageRoll Subevent did not accurately report rolled damage"
        );
    }

    @Test
    @DisplayName("BaseDamageRollTest Subevent can roll dice for damage")
    void test2() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new BaseDamageRoll();
        String subeventJsonString = "{" +
                "\"subevent\": \"base_damage_roll\"," +
                "\"damage\": [" +
                "{" +
                "   \"type\": \"fire\"," +
                "   \"dice\": [" +
                "       { \"size\": 10 }" +
                "   ]," +
                "   \"bonus\": 2" +
                "},{" +
                "   \"type\": \"cold\"," +
                "   \"dice\": [" +
                "       { \"size\": 10 }" +
                "   ]," +
                "   \"bonus\": 2" +
                "}]}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        BaseDamageRoll baseDamageRoll = (BaseDamageRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        Die.queue(10L);
        Die.queue(10L);
        baseDamageRoll.roll();

        /*
         * Verify subevent behaves as expected
         */
        String expectedJsonString = "{ \"fire\": 12, \"cold\": 12 }";
        JsonObject expectedJson = JsonParser.parseObjectString(expectedJsonString);
        assertEquals(expectedJson.toString(), baseDamageRoll.getBaseDamage().toString(),
                "BaseDamageRoll Subevent did not roll dice correctly"
        );
    }

    @Test
    @DisplayName("BaseDamageRollTest Subevent can re-roll typed dice below a given value")
    void test3() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new BaseDamageRoll();
        String subeventJsonString = "{" +
                "\"subevent\": \"base_damage_roll\"," +
                "\"damage\": [" +
                "{" +
                "   \"type\": \"fire\"," +
                "   \"dice\": [" +
                "       { \"size\": 10, \"roll\": 1 }," +
                "       { \"size\": 10, \"roll\": 2 }," +
                "   ]" +
                "},{" +
                "   \"type\": \"cold\"," +
                "   \"dice\": [" +
                "       { \"size\": 10, \"roll\": 1 }," +
                "       { \"size\": 10, \"roll\": 2 }," +
                "   ]" +
                "}]}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        BaseDamageRoll baseDamageRoll = (BaseDamageRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        Die.queue(10L);
        Die.queue(10L);
        baseDamageRoll.rerollTypedDiceLessThanOrEqualTo(1L, "fire");

        /*
         * Verify subevent behaves as expected
         */
        String expectedJsonString = "{ \"fire\": 12, \"cold\": 3 }";
        JsonObject expectedJson = JsonParser.parseObjectString(expectedJsonString);
        assertEquals(expectedJson.toString(), baseDamageRoll.getBaseDamage().toString(),
                "BaseDamageRoll Subevent did not re-roll dice correctly"
        );
    }

    @Test
    @DisplayName("BaseDamageRollTest Subevent can re-roll all dice below a given value")
    void test4() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new BaseDamageRoll();
        String subeventJsonString = "{" +
                "\"subevent\": \"base_damage_roll\"," +
                "\"damage\": [" +
                "{" +
                "   \"type\": \"fire\"," +
                "   \"dice\": [" +
                "       { \"size\": 10, \"roll\": 1 }," +
                "       { \"size\": 10, \"roll\": 2 }," +
                "   ]" +
                "},{" +
                "   \"type\": \"cold\"," +
                "   \"dice\": [" +
                "       { \"size\": 10, \"roll\": 1 }," +
                "       { \"size\": 10, \"roll\": 2 }," +
                "   ]" +
                "}]}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        BaseDamageRoll baseDamageRoll = (BaseDamageRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        Die.queue(10L);
        Die.queue(10L);
        baseDamageRoll.rerollTypedDiceLessThanOrEqualTo(1L, null);

        /*
         * Verify subevent behaves as expected
         */
        String expectedJsonString = "{ \"fire\": 12, \"cold\": 12 }";
        JsonObject expectedJson = JsonParser.parseObjectString(expectedJsonString);
        assertEquals(expectedJson.toString(), baseDamageRoll.getBaseDamage().toString(),
                "BaseDamageRoll Subevent did not re-roll dice correctly"
        );
    }

    @Test
    @DisplayName("BaseDamageRollTest Subevent can set typed dice below a given value")
    void test5() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new BaseDamageRoll();
        String subeventJsonString = "{" +
                "\"subevent\": \"base_damage_roll\"," +
                "\"damage\": [" +
                "{" +
                "   \"type\": \"fire\"," +
                "   \"dice\": [" +
                "       { \"size\": 10, \"roll\": 1 }," +
                "       { \"size\": 10, \"roll\": 2 }," +
                "   ]" +
                "},{" +
                "   \"type\": \"cold\"," +
                "   \"dice\": [" +
                "       { \"size\": 10, \"roll\": 1 }," +
                "       { \"size\": 10, \"roll\": 2 }," +
                "   ]" +
                "}]}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        BaseDamageRoll baseDamageRoll = (BaseDamageRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        baseDamageRoll.setTypedDiceLessThanOrEqualTo(1L, 10L, "fire");

        /*
         * Verify subevent behaves as expected
         */
        String expectedJsonString = "{ \"fire\": 12, \"cold\": 3 }";
        JsonObject expectedJson = JsonParser.parseObjectString(expectedJsonString);
        assertEquals(expectedJson.toString(), baseDamageRoll.getBaseDamage().toString(),
                "BaseDamageRoll Subevent did not set dice correctly"
        );
    }

    @Test
    @DisplayName("BaseDamageRollTest Subevent can set all dice below a given value")
    void test6() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new BaseDamageRoll();
        String subeventJsonString = "{" +
                "\"subevent\": \"base_damage_roll\"," +
                "\"damage\": [" +
                "{" +
                "   \"type\": \"fire\"," +
                "   \"dice\": [" +
                "       { \"size\": 10, \"roll\": 1 }," +
                "       { \"size\": 10, \"roll\": 2 }," +
                "   ]" +
                "},{" +
                "   \"type\": \"cold\"," +
                "   \"dice\": [" +
                "       { \"size\": 10, \"roll\": 1 }," +
                "       { \"size\": 10, \"roll\": 2 }," +
                "   ]" +
                "}]}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        BaseDamageRoll baseDamageRoll = (BaseDamageRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        baseDamageRoll.setTypedDiceLessThanOrEqualTo(1L, 10L, null);

        /*
         * Verify subevent behaves as expected
         */
        String expectedJsonString = "{ \"fire\": 12, \"cold\": 12 }";
        JsonObject expectedJson = JsonParser.parseObjectString(expectedJsonString);
        assertEquals(expectedJson.toString(), baseDamageRoll.getBaseDamage().toString(),
                "BaseDamageRoll Subevent did not set dice correctly"
        );
    }

    @Test
    @DisplayName("BaseDamageRollTest Subevent prepare method works")
    void test7() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new BaseDamageRoll();
        String subeventJsonString = "{" +
                "\"subevent\": \"base_damage_roll\"," +
                "\"damage\": [" +
                "{" +
                "   \"type\": \"fire\"," +
                "   \"dice\": [" +
                "       { \"size\": 10 }" +
                "   ]," +
                "   \"bonus\": 2" +
                "},{" +
                "   \"type\": \"cold\"," +
                "   \"dice\": [" +
                "       { \"size\": 10 }" +
                "   ]," +
                "   \"bonus\": 2" +
                "}]}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        BaseDamageRoll baseDamageRoll = (BaseDamageRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        Die.queue(10L);
        Die.queue(10L);
        baseDamageRoll.prepare(null);

        /*
         * Verify subevent behaves as expected
         */
        String expectedJsonString = "{ \"fire\": 12, \"cold\": 12 }";
        JsonObject expectedJson = JsonParser.parseObjectString(expectedJsonString);
        assertEquals(expectedJson.toString(), baseDamageRoll.getBaseDamage().toString(),
                "BaseDamageRoll Subevent did not roll dice correctly"
        );
    }

}
