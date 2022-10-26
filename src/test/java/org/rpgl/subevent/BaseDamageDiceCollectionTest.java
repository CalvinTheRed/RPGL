package org.rpgl.subevent;

import org.jsonutils.JsonArray;
import org.jsonutils.JsonFormatException;
import org.jsonutils.JsonObject;
import org.jsonutils.JsonParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rpgl.exception.SubeventMismatchException;

import static org.junit.jupiter.api.Assertions.*;

public class BaseDamageDiceCollectionTest {

    @Test
    @DisplayName("BaseDamageDiceCollectionTest Subevent throws SubeventMismatchException when subevent type doesn't match")
    void test0() throws JsonFormatException {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new BaseDamageDiceCollection();
        String subeventJsonString = "{" +
                "\"subevent\": \"not_a_subevent\"" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);

        /*
         * Verify subevent behaves as expected
         */
        assertThrows(SubeventMismatchException.class,
                () -> subevent.clone(subeventJson).invoke(null, null),
                "BaseDamageDiceCollection Subevent should throw a SubeventMismatchException if the specified subevent doesn't match."
        );
    }

    @Test
    @DisplayName("BaseDamageDiceCollectionTest Subevent accurately reports its typed damage")
    void test1() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new BaseDamageDiceCollection();
        String subeventJsonString = "{" +
                "\"subevent\": \"base_damage_dice_collection\"," +
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
        BaseDamageDiceCollection baseDamageDiceCollection = (BaseDamageDiceCollection) subevent.clone(subeventJson);

        /*
         * Verify subevent behaves as expected
         */
        JsonArray typedDamageArray = baseDamageDiceCollection.getDamageDiceCollection();
        assertEquals(subeventJson.get("damage").toString(), typedDamageArray.toString(),
                "BaseDamageDiceCollection Subevent did not accurately report its damage dice collection."
        );
    }

    @Test
    @DisplayName("BaseDamageDiceCollectionTest Subevent accurately reports the presence of a damage type")
    void test2() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new BaseDamageDiceCollection();
        String subeventJsonString = "{" +
                "\"subevent\": \"base_damage_dice_collection\"," +
                "\"damage\": [" +
                "   { \"type\": \"fire\" }" +
                "]}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        BaseDamageDiceCollection baseDamageDiceCollection = (BaseDamageDiceCollection) subevent.clone(subeventJson);

        /*
         * Verify subevent behaves as expected
         */
        assertTrue(baseDamageDiceCollection.includesDamageType("fire"),
                "BaseDamageDiceCollection Subevent should return true for a damage type which is present."
        );
    }

    @Test
    @DisplayName("BaseDamageDiceCollectionTest Subevent accurately reports the absence of a damage type")
    void test3() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new BaseDamageDiceCollection();
        String subeventJsonString = "{" +
                "\"subevent\": \"base_damage_dice_collection\"," +
                "\"damage\": [ ]" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        BaseDamageDiceCollection baseDamageDiceCollection = (BaseDamageDiceCollection) subevent.clone(subeventJson);

        /*
         * Verify subevent behaves as expected
         */
        assertFalse(baseDamageDiceCollection.includesDamageType("fire"),
                "BaseDamageDiceCollection Subevent should return false for a damage type which is not present."
        );
    }

    @Test
    @DisplayName("BaseDamageDiceCollectionTest Subevent can add existing typed damage (bonus)")
    void test4() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new BaseDamageDiceCollection();
        String subeventJsonString = "{" +
                "\"subevent\": \"base_damage_dice_collection\"," +
                "\"damage\": [" +
                "   { \"type\": \"fire\" }" +
                "]}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        BaseDamageDiceCollection baseDamageDiceCollection = (BaseDamageDiceCollection) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        String typedDamageJsonString = "{ \"type\": \"fire\", \"bonus\": 10 }";
        JsonObject typedDamageJson = JsonParser.parseObjectString(typedDamageJsonString);
        baseDamageDiceCollection.addExistingTypedDamage(typedDamageJson);

        /*
         * Verify subevent behaves as expected
         */
        JsonArray typedDamageArray = baseDamageDiceCollection.getDamageDiceCollection();
        JsonObject typedDamage = (JsonObject) typedDamageArray.get(0);
        Long typedDamageBonus = (Long) typedDamage.get("bonus");
        assertEquals(10L, typedDamageBonus,
                "BaseDamageDiceCollection Subevent didn't add the correct typed damage bonus."
        );
    }

    @Test
    @DisplayName("BaseDamageDiceCollectionTest Subevent can add existing typed damage (dice)")
    void test5() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new BaseDamageDiceCollection();
        String subeventJsonString = "{" +
                "\"subevent\": \"base_damage_dice_collection\"," +
                "\"damage\": [" +
                "   { \"type\": \"fire\" }" +
                "]}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        BaseDamageDiceCollection baseDamageDiceCollection = (BaseDamageDiceCollection) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        String typedDamageJsonString = "{" +
                "\"type\": \"fire\"," +
                "\"dice\": [ { \"size\": 10 } ]" +
                "}";
        JsonObject typedDamageJson = JsonParser.parseObjectString(typedDamageJsonString);
        baseDamageDiceCollection.addExistingTypedDamage(typedDamageJson);

        /*
         * Verify subevent behaves as expected
         */
        JsonArray typedDamageArray = baseDamageDiceCollection.getDamageDiceCollection();
        JsonObject typedDamage = (JsonObject) typedDamageArray.get(0);
        assertEquals(typedDamageJson.get("dice").toString(), typedDamage.get("dice").toString(),
                "BaseDamageDiceCollection Subevent didn't add the correct typed damage dice."
        );
    }



    @Test
    @DisplayName("BaseDamageDiceCollectionTest Subevent can add new typed damage (bonus)")
    void test6() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new BaseDamageDiceCollection();
        String subeventJsonString = "{" +
                "\"subevent\": \"base_damage_dice_collection\"," +
                "\"damage\": [ ]" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        BaseDamageDiceCollection baseDamageDiceCollection = (BaseDamageDiceCollection) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        String typedDamageJsonString = "{ \"type\": \"fire\", \"bonus\": 10 }";
        JsonObject typedDamageJson = JsonParser.parseObjectString(typedDamageJsonString);
        baseDamageDiceCollection.addNewTypedDamage(typedDamageJson);

        /*
         * Verify subevent behaves as expected
         */
        JsonArray typedDamageArray = baseDamageDiceCollection.getDamageDiceCollection();
        JsonObject typedDamage = (JsonObject) typedDamageArray.get(0);
        Long typedDamageBonus = (Long) typedDamage.get("bonus");
        assertEquals(10L, typedDamageBonus,
                "BaseDamageDiceCollection Subevent didn't add the correct typed damage bonus."
        );
    }

    @Test
    @DisplayName("BaseDamageDiceCollectionTest Subevent can add new typed damage (dice)")
    void test7() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new BaseDamageDiceCollection();
        String subeventJsonString = "{" +
                "\"subevent\": \"base_damage_dice_collection\"," +
                "\"damage\": [ ]" +
                "}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        BaseDamageDiceCollection baseDamageDiceCollection = (BaseDamageDiceCollection) subevent.clone(subeventJson);

        /*
         * Invoke subevent method
         */
        String typedDamageJsonString = "{" +
                "\"type\": \"fire\"," +
                "\"dice\": [ { \"size\": 10 } ]" +
                "}";
        JsonObject typedDamageJson = JsonParser.parseObjectString(typedDamageJsonString);
        baseDamageDiceCollection.addNewTypedDamage(typedDamageJson);

        /*
         * Verify subevent behaves as expected
         */
        JsonArray typedDamageArray = baseDamageDiceCollection.getDamageDiceCollection();
        JsonObject typedDamage = (JsonObject) typedDamageArray.get(0);
        assertEquals(typedDamageJson.get("dice").toString(), typedDamage.get("dice").toString(),
                "BaseDamageDiceCollection Subevent didn't add the correct typed damage dice."
        );
    }

    @Test
    @DisplayName("BaseDamageDiceCollectionTest Subevent can add typed damage")
    void test8() throws Exception {
        /*
         * Set up the subevent context
         */
        Subevent subevent = new BaseDamageDiceCollection();
        String subeventJsonString = "{" +
                "\"subevent\": \"base_damage_dice_collection\"," +
                "\"damage\": [" +
                "   { \"type\": \"fire\" }" +
                "]}";
        JsonObject subeventJson = JsonParser.parseObjectString(subeventJsonString);
        BaseDamageDiceCollection baseDamageDiceCollection = (BaseDamageDiceCollection) subevent.clone(subeventJson);

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
        baseDamageDiceCollection.addTypedDamage(typedDamageArray);

        /*
         * Verify subevent behaves as expected
         */
        JsonArray finalTypedDamageArray = baseDamageDiceCollection.getDamageDiceCollection();
        assertEquals(typedDamageArray.toString(), finalTypedDamageArray.toString(),
                "BaseDamageDiceCollection Subevent didn't add the correct typed damage dice."
        );
    }

}
