package org.rpgl.json;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JsonObjectTest {

    @Test
    @DisplayName("subset of primitives")
    void test001() {
        try {
            JsonObject set = JsonParser.parseObjectString("{\"key1\":1,\"key2\":2}");
            JsonObject subset = JsonParser.parseObjectString("{\"key1\":1}");
            assertTrue(subset.subsetOf(set));

            // test fail if values do not match
            subset.put("key2", 3L);
            assertFalse(subset.subsetOf(set));
            subset.remove("key2");

            // test fail if key is not present in set
            subset.put("key3", 3L);
            assertFalse(subset.subsetOf(set));

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("subset of lists")
    void test002() {
        try {
            JsonObject set = JsonParser.parseObjectString("{\"key1\":[1],\"key2\":[2]}");
            JsonObject subset = JsonParser.parseObjectString("{\"key1\":[1]}");
            assertTrue(subset.subsetOf(set));

            JsonArray newList;

            // test fail if values do not match
            newList = new JsonArray();
            newList.add(3L);
            subset.put("key2", newList);
            assertFalse(subset.subsetOf(set));
            subset.remove("key2");

            // test fail if key is not present in set
            newList = new JsonArray();
            newList.add(3L);
            subset.put("key3", newList);
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
            JsonObject set = JsonParser.parseObjectString("{\"key1\":{\"sub1\":1,\"sub2\":2},\"key2\":{\"sub3\":3,\"sub4\":4}}");
            JsonObject subset = JsonParser.parseObjectString("{\"key1\":{\"sub1\":1}}");
            assertTrue(subset.subsetOf(set));

            // test fail if values do not match
            subset.put("key2", 2L);
            assertFalse(subset.subsetOf(set));

            // test fail if key is not present in set
            subset.put("key2", JsonParser.parseObjectString("{\"key5\":5}"));
            assertFalse(subset.subsetOf(set));

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("Seek test (no keypath)")
    void test004() {
        try {
            JsonObject data = JsonParser.parseObjectString("{\"key\":\"value\"}");
            assertEquals(data, data.seek(""));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("Seek test (key)")
    void test005() {
        try {
            JsonObject data = JsonParser.parseObjectString("{\"key\":\"value\"}");
            assertEquals("value", data.seek("key"));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("Seek test (key > key)")
    void test006() {
        try {
            JsonObject data = JsonParser.parseObjectString("{\"key1\":{\"key2\":{\"key3\":\"value\"}}}");
            assertEquals("value", data.seek("key1.key2.key3"));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("Seek test (key > index by number)")
    void test007() {
        try {
            JsonObject data = JsonParser.parseObjectString("{\"key\":[1,2,3]}");
            assertEquals(1L, data.seek("key[0]"));
            assertEquals(2L, data.seek("key[1]"));
            assertEquals(3L, data.seek("key[2]"));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("Seek test (key > index by content)")
    void test008() {
        try {
            JsonObject data = JsonParser.parseObjectString("{\"key\":[{\"subkey\":1},{\"subkey\":2},{\"subkey\":3}]}");
            assertEquals("{\"subkey\":1}", data.seek("key[{\"subkey\":1}]").toString());
            assertEquals("{\"subkey\":2}", data.seek("key[{\"subkey\":2}]").toString());
            assertEquals("{\"subkey\":3}", data.seek("key[{\"subkey\":3}]").toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("Seek test (key > index by number > key)")
    void test009() {
        try {
            JsonObject data = JsonParser.parseObjectString("{\"key\":[{\"subkey\":1},{\"subkey\":2},{\"subkey\":3}]}");
            assertEquals(1L, data.seek("key[0].subkey"));
            assertEquals(2L, data.seek("key[1].subkey"));
            assertEquals(3L, data.seek("key[2].subkey"));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("Seek test (key > index by content > key)")
    void test010() {
        try {
            JsonObject data = JsonParser.parseObjectString("{\"key\":[{\"subkey\":1},{\"subkey\":2},{\"subkey\":3}]}");
            assertEquals(1L, data.seek("key[{\"subkey\":1}].subkey"));
            assertEquals(2L, data.seek("key[{\"subkey\":2}].subkey"));
            assertEquals(3L, data.seek("key[{\"subkey\":3}].subkey"));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("Seek test (key > index by number > index by number)")
    void test011() {
        try {
            JsonObject data = JsonParser.parseObjectString("{\"key\":[[1,2],[3,4]]}");
            assertEquals(1L, data.seek("key[0][0]"));
            assertEquals(2L, data.seek("key[0][1]"));
            assertEquals(3L, data.seek("key[1][0]"));
            assertEquals(4L, data.seek("key[1][1]"));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("seek test (using '\"' in a string)")
    void test012() {
        try {
            JsonObject data = JsonParser.parseObjectString("{\"key\":[{\"subkey\":\"1\\\"0\"}]}");
            assertEquals("1\\\"0", data.seek("key[{\"subkey\":\"1\\\"0\"}].subkey"));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("seek test (using '.' in a string)")
    void test013() {
        try {
            JsonObject data = JsonParser.parseObjectString("{\"key\":[{\"subkey\":\"1.0\"}]}");
            assertEquals("1.0", data.seek("key[{\"subkey\":\"1.0\"}].subkey"));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("seek test (using '{' in a string)")
    void test014() {
        try {
            JsonObject data = JsonParser.parseObjectString("{\"key\":[{\"subkey\":\"1{0\"}]}");
            assertEquals("1{0", data.seek("key[{\"subkey\":\"1{0\"}].subkey"));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("seek test (using '}' in a string)")
    void test015() {
        try {
            JsonObject data = JsonParser.parseObjectString("{\"key\":[{\"subkey\":\"1}0\"}]}");
            assertEquals("1}0", data.seek("key[{\"subkey\":\"1}0\"}].subkey"));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("seek test (using '[' in a string)")
    void test016() {
        try {
            JsonObject data = JsonParser.parseObjectString("{\"key\":[{\"subkey\":\"1[0\"}]}");
            assertEquals("1[0", data.seek("key[{\"subkey\":\"1[0\"}].subkey"));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("seek test (using ']' in a string)")
    void test017() {
        try {
            JsonObject data = JsonParser.parseObjectString("{\"key\":[{\"subkey\":\"1]0\"}]}");
            assertEquals("1]0", data.seek("key[{\"subkey\":\"1]0\"}].subkey"));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("join (novel key)")
    void test018() {
        try {
            JsonObject data = JsonParser.parseObjectString("{}");
            JsonObject toJoin = JsonParser.parseObjectString("{\"key\":1}");
            data.join(toJoin);
            assertEquals(1L, data.seek("key"));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("join (primitive key collision)")
    void test019() {
        try {
            JsonObject data = JsonParser.parseObjectString("{\"key\":1}");
            JsonObject toJoin = JsonParser.parseObjectString("{\"key\":2}");
            data.join(toJoin);
            assertEquals(2L, data.seek("key"));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("join (object-primitive collision)")
    void test020() {
        try {
            JsonObject data = JsonParser.parseObjectString("{\"key\":1}");
            JsonObject toJoin = JsonParser.parseObjectString("{\"key\":{\"subkey\":1}}");
            data.join(toJoin);
            assertEquals("{\"subkey\":1}", data.seek("key").toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("join (object-object collision)")
    void test021() {
        try {
            JsonObject data = JsonParser.parseObjectString("{\"key\":{\"subkey1\":1,\"subkey2\":2}}");
            JsonObject toJoin = JsonParser.parseObjectString("{\"key\":{\"subkey2\":3,\"subkey3\":4}}");
            data.join(toJoin);
            assertEquals(1L, data.seek("key.subkey1"));
            assertEquals(3L, data.seek("key.subkey2"));
            assertEquals(4L, data.seek("key.subkey3"));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Test
    @DisplayName("toString tester")
    void test022() {
        try {
            String jsonString = "{\"key1\":1,\"key2\":2,\"key3\":\"three\"}";
            JsonObject data = JsonParser.parseObjectString(jsonString);
            assertEquals(jsonString, data.toString());
            assertEquals(jsonString, data.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

}
