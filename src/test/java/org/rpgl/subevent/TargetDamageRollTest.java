package org.rpgl.subevent;

import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.exception.SubeventMismatchException;
import org.rpgl.math.Die;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TargetDamageRollTest {

    @BeforeAll
    static void beforeAll() {
        Die.setTesting(true);
    }

    @Test
    @DisplayName("TargetDamageRoll Subevent throws SubeventMismatchException when subevent type doesn't match")
    void test0() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new TargetDamageRoll();
        String subeventJsonString = "{" +
                "\"subevent\": \"not_a_subevent\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);

        /*
         * Verify subevent behaves as expected
         */
        assertThrows(SubeventMismatchException.class,
                () -> subevent.clone(subeventJson).invoke(null, null),
                "TargetDamageRoll Subevent should throw a SubeventMismatchException if the specified subevent doesn't match."
        );
    }

    @Test
    @DisplayName("TargetDamageRoll Subevent returns the correct final damage values")
    void test1() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new TargetDamageRoll();
        String subeventJsonString = "{" +
                "\"subevent\": \"target_damage_roll\"," +
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
        TargetDamageRoll targetDamageRoll = (TargetDamageRoll) subevent.clone(subeventJson);

        /*
         * Verify subevent behaves as expected
         */
        String expectedJsonString = "{ \"fire\": 22, \"cold\": 22 }";
        JsonObject expectedJson = JsonParser.parseObjectString(expectedJsonString);
        assertEquals(expectedJson.toString(), targetDamageRoll.getBaseDamage().toString(),
                "TargetDamageRoll Subevent did not accurately report rolled damage"
        );
    }

    @Test
    @DisplayName("TargetDamageRoll Subevent can roll dice for damage")
    void test2() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new TargetDamageRoll();
        String subeventJsonString = "{" +
                "\"subevent\": \"target_damage_roll\"," +
                "\"damage\": [" +
                "{" +
                "   \"type\": \"fire\"," +
                "   \"dice\": [" +
                "       { \"size\": 10, \"determined\": 10 }" +
                "   ]," +
                "   \"bonus\": 2" +
                "},{" +
                "   \"type\": \"cold\"," +
                "   \"dice\": [" +
                "       { \"size\": 10, \"determined\": 10 }" +
                "   ]," +
                "   \"bonus\": 2" +
                "}]}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        TargetDamageRoll targetDamageRoll = (TargetDamageRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        targetDamageRoll.roll();

        /*
         * Verify subevent behaves as expected
         */
        String expectedJsonString = "{ \"fire\": 12, \"cold\": 12 }";
        JsonObject expectedJson = JsonParser.parseObjectString(expectedJsonString);
        assertEquals(expectedJson.toString(), targetDamageRoll.getBaseDamage().toString(),
                "TargetDamageRoll Subevent did not roll dice correctly"
        );
    }

    @Test
    @DisplayName("TargetDamageRoll Subevent can re-roll typed dice below a given value")
    void test3() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new TargetDamageRoll();
        String subeventJsonString = "{" +
                "\"subevent\": \"target_damage_roll\"," +
                "\"damage\": [" +
                "{" +
                "   \"type\": \"fire\"," +
                "   \"dice\": [" +
                "       { \"size\": 10, \"roll\": 1, \"determined_reroll\": 10 }," +
                "       { \"size\": 10, \"roll\": 2, \"determined_reroll\": 10 }," +
                "   ]" +
                "},{" +
                "   \"type\": \"cold\"," +
                "   \"dice\": [" +
                "       { \"size\": 10, \"roll\": 1, \"determined_reroll\": 10 }," +
                "       { \"size\": 10, \"roll\": 2, \"determined_reroll\": 10 }," +
                "   ]" +
                "}]}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        TargetDamageRoll targetDamageRoll = (TargetDamageRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        targetDamageRoll.rerollTypedDiceLessThanOrEqualTo(1L, "fire");

        /*
         * Verify subevent behaves as expected
         */
        String expectedJsonString = "{ \"fire\": 12, \"cold\": 3 }";
        JsonObject expectedJson = JsonParser.parseObjectString(expectedJsonString);
        assertEquals(expectedJson.toString(), targetDamageRoll.getBaseDamage().toString(),
                "TargetDamageRoll Subevent did not re-roll dice correctly"
        );
    }

    @Test
    @DisplayName("TargetDamageRoll Subevent can re-roll all dice below a given value")
    void test4() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new TargetDamageRoll();
        String subeventJsonString = "{" +
                "\"subevent\": \"target_damage_roll\"," +
                "\"damage\": [" +
                "{" +
                "   \"type\": \"fire\"," +
                "   \"dice\": [" +
                "       { \"size\": 10, \"roll\": 1, \"determined_reroll\": 10 }," +
                "       { \"size\": 10, \"roll\": 2, \"determined_reroll\": 10 }," +
                "   ]" +
                "},{" +
                "   \"type\": \"cold\"," +
                "   \"dice\": [" +
                "       { \"size\": 10, \"roll\": 1, \"determined_reroll\": 10 }," +
                "       { \"size\": 10, \"roll\": 2, \"determined_reroll\": 10 }," +
                "   ]" +
                "}]}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        TargetDamageRoll targetDamageRoll = (TargetDamageRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        targetDamageRoll.rerollTypedDiceLessThanOrEqualTo(1L, null);

        /*
         * Verify subevent behaves as expected
         */
        String expectedJsonString = "{ \"fire\": 12, \"cold\": 12 }";
        JsonObject expectedJson = JsonParser.parseObjectString(expectedJsonString);
        assertEquals(expectedJson.toString(), targetDamageRoll.getBaseDamage().toString(),
                "TargetDamageRoll Subevent did not re-roll dice correctly"
        );
    }

    @Test
    @DisplayName("TargetDamageRoll Subevent can set typed dice below a given value")
    void test5() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new TargetDamageRoll();
        String subeventJsonString = "{" +
                "\"subevent\": \"target_damage_roll\"," +
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
        TargetDamageRoll targetDamageRoll = (TargetDamageRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        targetDamageRoll.setTypedDiceLessThanOrEqualTo(1L, 10L, "fire");

        /*
         * Verify subevent behaves as expected
         */
        String expectedJsonString = "{ \"fire\": 12, \"cold\": 3 }";
        JsonObject expectedJson = JsonParser.parseObjectString(expectedJsonString);
        assertEquals(expectedJson.toString(), targetDamageRoll.getBaseDamage().toString(),
                "TargetDamageRoll Subevent did not set dice correctly"
        );
    }

    @Test
    @DisplayName("TargetDamageRoll Subevent can set all dice below a given value")
    void test6() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new TargetDamageRoll();
        String subeventJsonString = "{" +
                "\"subevent\": \"target_damage_roll\"," +
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
        TargetDamageRoll targetDamageRoll = (TargetDamageRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        targetDamageRoll.setTypedDiceLessThanOrEqualTo(1L, 10L, null);

        /*
         * Verify subevent behaves as expected
         */
        String expectedJsonString = "{ \"fire\": 12, \"cold\": 12 }";
        JsonObject expectedJson = JsonParser.parseObjectString(expectedJsonString);
        assertEquals(expectedJson.toString(), targetDamageRoll.getBaseDamage().toString(),
                "TargetDamageRoll Subevent did not set dice correctly"
        );
    }

    @Test
    @DisplayName("TargetDamageRoll Subevent prepare method works")
    void test7() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new TargetDamageRoll();
        String subeventJsonString = "{" +
                "\"subevent\": \"target_damage_roll\"," +
                "\"damage\": [" +
                "{" +
                "   \"type\": \"fire\"," +
                "   \"dice\": [" +
                "       { \"size\": 10, \"determined\": 10 }" +
                "   ]," +
                "   \"bonus\": 2" +
                "},{" +
                "   \"type\": \"cold\"," +
                "   \"dice\": [" +
                "       { \"size\": 10, \"determined\": 10 }" +
                "   ]," +
                "   \"bonus\": 2" +
                "}]}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        TargetDamageRoll targetDamageRoll = (TargetDamageRoll) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        targetDamageRoll.prepare(null);

        /*
         * Verify subevent behaves as expected
         */
        String expectedJsonString = "{ \"fire\": 12, \"cold\": 12 }";
        JsonObject expectedJson = JsonParser.parseObjectString(expectedJsonString);
        assertEquals(expectedJson.toString(), targetDamageRoll.getBaseDamage().toString(),
                "TargetDamageRoll Subevent did not roll dice correctly"
        );
    }

}
