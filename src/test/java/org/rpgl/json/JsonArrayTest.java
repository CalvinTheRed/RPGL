package org.rpgl.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JsonArrayTest {

    @Test
    @DisplayName("subset of primitives")
    void test001() {
        try {
            String jsonString = "{\"set\":[1,2,3,4],\"subset\":[1,2,3]}";
            JsonObject jsonData = JsonParser.parseObjectString(jsonString);
            JsonArray set = (JsonArray) jsonData.get("set");
            JsonArray subset = (JsonArray) jsonData.get("subset");
            assertTrue(subset.subsetOf(set));
            subset.add(5L);
            assertFalse(subset.subsetOf(set));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("subset of arrays")
    void test002() {
        try {
            String jsonString = "{\"set\":[[1],[2],[3],[4]],\"subset\":[[1],[2],[3]]}";
            JsonObject jsonData = JsonParser.parseObjectString(jsonString);
            JsonArray set = (JsonArray) jsonData.get("set");
            JsonArray subset = (JsonArray) jsonData.get("subset");
            assertTrue(subset.subsetOf(set));
            JsonArray newMember = new JsonArray();
            newMember.add(5L);
            subset.add(newMember);
            assertFalse(subset.subsetOf(set));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("subset of objects")
    void test003() {
        try {
            String sharedArrayElement = "{\"key1\":1}";

            String setArrayString = "[" + sharedArrayElement + ",{\"key2\":2}]";
            String subsetArrayString = "[" + sharedArrayElement + "]";

            JsonArray set = JsonParser.parseArrayString(setArrayString);
            JsonArray subset = JsonParser.parseArrayString(subsetArrayString);
            assertTrue(subset.subsetOf(set));

            JsonObject newMember = new JsonObject();

            // check for fail with differing values
            newMember.put("key1", 3L);
            subset.add(newMember);
            assertFalse(subset.subsetOf(set));

            // check for fail with absent keys
            newMember.remove("key1");
            newMember.put("key3", 3L);
            assertFalse(subset.subsetOf(set));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("toString validation")
    void test004() {
        try {
            String jsonString = "[1,2,3,4,\"five\"]";
            JsonArray jsonArray = JsonParser.parseArrayString(jsonString);

            // verify toString() constructs itself correctly
            assertEquals(jsonString, jsonArray.toString());

            // test again to verify resetting StringBuilder works as intended
            assertEquals(jsonString, jsonArray.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

}