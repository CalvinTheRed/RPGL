package org.rpgl.subevent;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.core.RPGLContext;
import org.rpgl.exception.SubeventMismatchException;

import static org.junit.jupiter.api.Assertions.*;

public class TargetDamageDiceCollectionTest {

    @Test
    @DisplayName("TargetDamageDiceCollection Subevent throws SubeventMismatchException when subevent type doesn't match")
    void test0() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new TargetDamageDiceCollection();
        String subeventJsonString = "{" +
                "\"subevent\": \"not_a_subevent\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        RPGLContext context = new RPGLContext(null);

        /*
         * Verify subevent behaves as expected
         */
        assertThrows(SubeventMismatchException.class,
                () -> subevent.clone(subeventJson).invoke(context),
                "TargetDamageDiceCollection Subevent should throw a SubeventMismatchException if the specified subevent doesn't match."
        );
    }

    @Test
    @DisplayName("TargetDamageDiceCollection Subevent accurately reports its typed damage")
    void test1() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new TargetDamageDiceCollection();
        String subeventJsonString = "{" +
                "\"subevent\": \"target_damage_dice_collection\"," +
                "\"damage\": [" +
                "   {" +
                "   \"type\": \"fire\"," +
                "   \"dice\": [ { \"size\": 10 } ]," +
                "   \"bonus\": 10" +
                "   },{" +
                "   \"type\": \"cold\"," +
                "   \"dice\": [ { \"size\": 10 } ]," +
                "   \"bonus\": 10" +
                "   }" +
                "]}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        TargetDamageDiceCollection targetDamageDiceCollection = (TargetDamageDiceCollection) subevent.clone(subeventJson);

        /*
         * Verify subevent behaves as expected
         */
        JsonArray typedDamageArray = targetDamageDiceCollection.getDamageDiceCollection();
        assertEquals(subeventJson.get("damage").toString(), typedDamageArray.toString(),
                "TargetDamageDiceCollection Subevent did not accurately report its damage dice collection."
        );
    }

    @Test
    @DisplayName("TargetDamageDiceCollection Subevent accurately reports the presence of a damage type")
    void test2() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new TargetDamageDiceCollection();
        String subeventJsonString = "{" +
                "\"subevent\": \"target_damage_dice_collection\"," +
                "\"damage\": [" +
                "   { \"type\": \"fire\" }" +
                "]}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        TargetDamageDiceCollection targetDamageDiceCollection = (TargetDamageDiceCollection) subevent.clone(subeventJson);

        /*
         * Verify subevent behaves as expected
         */
        assertTrue(targetDamageDiceCollection.includesDamageType("fire"),
                "TargetDamageDiceCollection Subevent should return true for a damage type which is present."
        );
    }

    @Test
    @DisplayName("TargetDamageDiceCollection Subevent accurately reports the absence of a damage type")
    void test3() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new TargetDamageDiceCollection();
        String subeventJsonString = "{" +
                "\"subevent\": \"target_damage_dice_collection\"," +
                "\"damage\": [ ]" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        TargetDamageDiceCollection targetDamageDiceCollection = (TargetDamageDiceCollection) subevent.clone(subeventJson);

        /*
         * Verify subevent behaves as expected
         */
        assertFalse(targetDamageDiceCollection.includesDamageType("fire"),
                "TargetDamageDiceCollection Subevent should return false for a damage type which is not present."
        );
    }

    @Test
    @DisplayName("TargetDamageDiceCollection Subevent can add existing typed damage (bonus)")
    void test4() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new TargetDamageDiceCollection();
        String subeventJsonString = "{" +
                "\"subevent\": \"target_damage_dice_collection\"," +
                "\"damage\": [" +
                "   { \"type\": \"fire\" }" +
                "]}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        TargetDamageDiceCollection targetDamageDiceCollection = (TargetDamageDiceCollection) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        String typedDamageJsonString = "{ \"type\": \"fire\", \"bonus\": 10 }";
        JsonObject typedDamageJson = JsonParser.parseObjectString(typedDamageJsonString);
        targetDamageDiceCollection.addExistingTypedDamage(typedDamageJson);

        /*
         * Verify subevent behaves as expected
         */
        JsonArray typedDamageArray = targetDamageDiceCollection.getDamageDiceCollection();
        JsonObject typedDamage = (JsonObject) typedDamageArray.get(0);
        Long typedDamageBonus = (Long) typedDamage.get("bonus");
        assertEquals(10L, typedDamageBonus,
                "TargetDamageDiceCollection Subevent didn't add the correct typed damage bonus."
        );
    }

    @Test
    @DisplayName("TargetDamageDiceCollection Subevent can add existing typed damage (dice)")
    void test5() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new TargetDamageDiceCollection();
        String subeventJsonString = "{" +
                "\"subevent\": \"target_damage_dice_collection\"," +
                "\"damage\": [" +
                "   { \"type\": \"fire\" }" +
                "]}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        TargetDamageDiceCollection targetDamageDiceCollection = (TargetDamageDiceCollection) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        String typedDamageJsonString = "{" +
                "\"type\": \"fire\"," +
                "\"dice\": [ { \"size\": 10 } ]" +
                "}";
        JsonObject typedDamageJson = JsonParser.parseObjectString(typedDamageJsonString);
        targetDamageDiceCollection.addExistingTypedDamage(typedDamageJson);

        /*
         * Verify subevent behaves as expected
         */
        JsonArray typedDamageArray = targetDamageDiceCollection.getDamageDiceCollection();
        JsonObject typedDamage = (JsonObject) typedDamageArray.get(0);
        assertEquals(typedDamageJson.get("dice").toString(), typedDamage.get("dice").toString(),
                "TargetDamageDiceCollection Subevent didn't add the correct typed damage dice."
        );
    }



    @Test
    @DisplayName("TargetDamageDiceCollection Subevent can add new typed damage (bonus)")
    void test6() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new TargetDamageDiceCollection();
        String subeventJsonString = "{" +
                "\"subevent\": \"target_damage_dice_collection\"," +
                "\"damage\": [ ]" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        TargetDamageDiceCollection targetDamageDiceCollection = (TargetDamageDiceCollection) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        String typedDamageJsonString = "{ \"type\": \"fire\", \"bonus\": 10 }";
        JsonObject typedDamageJson = JsonParser.parseObjectString(typedDamageJsonString);
        targetDamageDiceCollection.addNewTypedDamage(typedDamageJson);

        /*
         * Verify subevent behaves as expected
         */
        JsonArray typedDamageArray = targetDamageDiceCollection.getDamageDiceCollection();
        JsonObject typedDamage = (JsonObject) typedDamageArray.get(0);
        Long typedDamageBonus = (Long) typedDamage.get("bonus");
        assertEquals(10L, typedDamageBonus,
                "TargetDamageDiceCollection Subevent didn't add the correct typed damage bonus."
        );
    }

    @Test
    @DisplayName("TargetDamageDiceCollection Subevent can add new typed damage (dice)")
    void test7() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new TargetDamageDiceCollection();
        String subeventJsonString = "{" +
                "\"subevent\": \"target_damage_dice_collection\"," +
                "\"damage\": [ ]" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        TargetDamageDiceCollection targetDamageDiceCollection = (TargetDamageDiceCollection) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        String typedDamageJsonString = "{" +
                "\"type\": \"fire\"," +
                "\"dice\": [ { \"size\": 10 } ]" +
                "}";
        JsonObject typedDamageJson = JsonParser.parseObjectString(typedDamageJsonString);
        targetDamageDiceCollection.addNewTypedDamage(typedDamageJson);

        /*
         * Verify subevent behaves as expected
         */
        JsonArray typedDamageArray = targetDamageDiceCollection.getDamageDiceCollection();
        JsonObject typedDamage = (JsonObject) typedDamageArray.get(0);
        assertEquals(typedDamageJson.get("dice").toString(), typedDamage.get("dice").toString(),
                "TargetDamageDiceCollection Subevent didn't add the correct typed damage dice."
        );
    }

    @Test
    @DisplayName("TargetDamageDiceCollection Subevent can add typed damage")
    void test8() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new TargetDamageDiceCollection();
        String subeventJsonString = "{" +
                "\"subevent\": \"target_damage_dice_collection\"," +
                "\"damage\": [" +
                "   { \"type\": \"fire\" }" +
                "]}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        TargetDamageDiceCollection targetDamageDiceCollection = (TargetDamageDiceCollection) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        String typedDamageArrayString = "[" +
                "{" +
                "\"type\": \"fire\"," +
                "\"dice\": [ { \"size\": 10 } ]," +
                "\"bonus\": 10" +
                "},{" +
                "\"type\": \"cold\"," +
                "\"dice\": [ { \"size\": 10 } ]," +
                "\"bonus\": 10" +
                "}]";
        JsonArray typedDamageArray = JsonParser.parseArrayString(typedDamageArrayString);
        targetDamageDiceCollection.addTypedDamage(typedDamageArray);

        /*
         * Verify subevent behaves as expected
         */
        JsonArray finalTypedDamageArray = targetDamageDiceCollection.getDamageDiceCollection();
        assertEquals(typedDamageArray.toString(), finalTypedDamageArray.toString(),
                "TargetDamageDiceCollection Subevent didn't add the correct typed damage dice."
        );
    }

}
