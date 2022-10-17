package org.rpgl.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JsonParserTest {

    @Test
    @DisplayName("Parse object from file")
    void test001() {
        try {
            // create reference file
            String filepath = "test.json";
            String jsonString = "{\"key\":\"value\"}";
            PrintWriter writer = new PrintWriter(filepath, StandardCharsets.UTF_8);
            writer.println(jsonString);
            writer.close();

            // parse data from file
            File file = new File(filepath);
            JsonObject jdata = JsonParser.parseObjectFile(file);

            // assertions
            assertEquals(1, jdata.keySet().size());
            assertEquals("value", jdata.get("key"));

            // delete reference file
            file.delete();

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }

    }

    @Test
    @DisplayName("Parse object from file path")
    void test002() {
        try {
            // create reference file
            String filepath = "test.json";
            String jsonString = "{\"key\":\"value\"}";
            PrintWriter writer = new PrintWriter(filepath, StandardCharsets.UTF_8);
            writer.println(jsonString);
            writer.close();

            // parse data from file
            File file = new File(filepath);
            JsonObject jdata = JsonParser.parseObjectFile(filepath);

            // assertions
            assertEquals(1, jdata.keySet().size());
            assertEquals("value", jdata.get("key"));

            // delete reference file
            file.delete();

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("Parse object from json string")
    void test003() {
        try {
            // create reference string
            String jsonString = "{\"key\":\"value\"}";

            // parse data from string
            JsonObject jdata = JsonParser.parseObjectString(jsonString);

            // assertions
            assertEquals(1, jdata.keySet().size());
            assertEquals("value", jdata.get("key"));

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("Parse object value (string)")
    void test004() {
        try {
            // create reference string
            String jsonString = "{\"key\":\"value\"}";

            // parse data from string
            JsonObject jdata = JsonParser.parseObjectString(jsonString);

            // assertions
            assertEquals(1, jdata.keySet().size());
            assertEquals("value", jdata.get("key"));

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("Parse object value (bool)")
    void test005() {
        try {
            // create reference string
            String jsonString = "{\"key\":true}";

            // parse data from string
            JsonObject jdata = JsonParser.parseObjectString(jsonString);

            // assertions
            assertEquals(1, jdata.keySet().size());
            Object value = jdata.get("key");
            assertTrue(value instanceof Boolean);
            assertEquals(true, jdata.get("key"));

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("Parse object value (long)")
    void test006() {
        try {
            // create reference string
            String jsonString = "{\"key\":20}";

            // parse data from string
            JsonObject jdata = JsonParser.parseObjectString(jsonString);

            // assertions
            assertEquals(1, jdata.keySet().size());
            Object value = jdata.get("key");
            assertTrue(value instanceof Long);
            assertEquals(20L, jdata.get("key"));

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("Parse object value (double)")
    void test007() {
        try {
            // create reference string
            String jsonString = "{\"key\":2.0}";

            // parse data from string
            JsonObject jdata = JsonParser.parseObjectString(jsonString);

            // assertions
            assertEquals(1, jdata.keySet().size());
            Object value = jdata.get("key");
            assertTrue(value instanceof Double);
            assertEquals(2.0, jdata.get("key"));

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("Parse object value (object)")
    void test008() {
        try {
            // create reference string
            String jsonString = "{\"key\":{\"key1\":1,\"key2\":2,\"key3\":3}}";

            // parse data from string
            JsonObject jdata = JsonParser.parseObjectString(jsonString);

            // assertions
            assertEquals(1, jdata.keySet().size());
            Object value = jdata.get("key");
            assertTrue(value instanceof JsonObject);
            JsonObject jobj = (JsonObject) value;
            assertEquals(1L, jobj.get("key1"));
            assertEquals(2L, jobj.get("key2"));
            assertEquals(3L, jobj.get("key3"));

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("Parse object value (list)")
    void test009() {
        try {
            // create reference string
            String jsonString = "{\"key\":[1,2,3]}";

            // parse data from string
            JsonObject jdata = JsonParser.parseObjectString(jsonString);

            // assertions
            assertEquals(1, jdata.keySet().size());
            Object value = jdata.get("key");
            assertTrue(value instanceof JsonArray);
            JsonArray jlist = (JsonArray) value;
            assertEquals(1L, jlist.get(0));
            assertEquals(2L, jlist.get(1));
            assertEquals(3L, jlist.get(2));

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("remove whitespace")
    void test010() {
        try {
            String whitespaceString = "This _   is\t_a _B  IG\t\t\t_ mess\ry\r\r\r _ \" String \"";
            String expectedString = "This_is_a_BIG_messy_\" String \"";
            String condensedString = JsonParser.removeWhitespace(whitespaceString);
            assertEquals(expectedString, condensedString);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

}
