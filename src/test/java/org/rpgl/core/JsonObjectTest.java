package org.rpgl.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonObjectTest {

    @Test
    @DisplayName("Join 2 different keys with integers")
    void test001() {
        JsonObject object1 = new JsonObject();
        object1.put("key1", 1);
        JsonObject object2 = new JsonObject();
        object2.put("key2", 2);

        object1.join(object2);

        assertEquals(2, object1.keySet().size(),
                "JsonObject join should create keys if object 2 has a key not present in object 1"
        );
        assertTrue(object1.containsKey("key1"),
                "object 1 should hold on to its original key"
        );
        assertTrue(object1.containsKey("key2"),
                "object 1 should have the same key as is in object 1"
        );
    }

    @Test
    @DisplayName("Join 2 identical keys with (different) integers")
    void test002() {
        JsonObject object1 = new JsonObject();
        object1.put("key1", 1);
        JsonObject object2 = new JsonObject();
        object2.put("key1", 2);

        object1.join(object2);

        assertEquals(1, object1.keySet().size(),
                "JsonObject join should only have 1 key after joining an object with the same key"
        );
        assertTrue(object1.containsKey("key1"),
                "object 1 should hold on to its original key"
        );
        assertEquals(2, object1.get("key1"),
                "object 1 should have the value of object 2 after join"
        );
    }

    @Test
    @DisplayName("Join 2 identical keys with primitives")
    void test003() {
        JsonObject object1 = new JsonObject();
        object1.put("key1", 1);
        JsonObject object2 = new JsonObject();
        object2.put("key1", 2);

        object1.join(object2);

        assertEquals(1, object1.keySet().size(),
                "JsonObject join should only have 1 key after joining an object with the same key"
        );
        assertTrue(object1.containsKey("key1"),
                "object 1 should hold on to its original key"
        );
        assertEquals(2, object1.get("key1"),
                "object 1 should have the value of object 2 after join"
        );
    }

    @Test
    @DisplayName("override primitive with map")
    void test004() {
        JsonObject object1 = new JsonObject();
        object1.put("key1", 1);
        JsonObject object2 = new JsonObject();
        object2.put("key1", new HashMap<String, Object>() {{
            this.put("key2", 3);
        }});

        object1.join(object2);

        assertEquals(1, object1.keySet().size(),
                "JsonObject join should only have 1 key"
        );
        assertTrue(object1.containsKey("key1"),
                "object 1 should hold on to its original key"
        );
        assertTrue(object1.get("key1") instanceof Map,
                "object 1 should have the value of object 2 after join"
        );
        Map<String, Object> value = object1.getMap("key1");
        assertTrue(value.containsKey("key2"),
                "map should be copied properly"
        );
    }

    @Test
    @DisplayName("override map with map (different keys)")
    void test005() {
        JsonObject object1 = new JsonObject();
        object1.put("key1", new HashMap<String, Object>() {{
            this.put("key2", 2);
        }});
        JsonObject object2 = new JsonObject();
        object2.put("key1", new HashMap<String, Object>() {{
            this.put("key3", 3);
        }});

        object1.join(object2);

        assertEquals(1, object1.keySet().size(),
                "JsonObject join should have 1 key"
        );
        assertTrue(object1.containsKey("key1"),
                "object 1 should hold on to its original key"
        );
        Map<String, Object> value = object1.getMap("key1");
        assertEquals(2, value.keySet().size(),
                "nested map should have 2 keys"
        );
        assertTrue(value.containsKey("key2"),
                "map missing original key"
        );
        assertTrue(value.containsKey("key3"),
                "map missing new key"
        );
    }

    @Test
    @DisplayName("override map with map (same keys)")
    void test006() {
        JsonObject object1 = new JsonObject();
        object1.put("key1", new HashMap<String, Object>() {{
            this.put("key2", 2);
        }});
        JsonObject object2 = new JsonObject();
        object2.put("key1", new HashMap<String, Object>() {{
            this.put("key2", 3);
        }});

        object1.join(object2);

        assertEquals(1, object1.keySet().size(),
                "JsonObject join should have 1 key"
        );
        assertTrue(object1.containsKey("key1"),
                "object 1 should hold on to its original key"
        );
        Map<String, Object> value = object1.getMap("key1");
        assertEquals(1, value.keySet().size(),
                "nested map should have 1 key"
        );
        assertTrue(value.containsKey("key2"),
                "map missing original key"
        );
        assertEquals(3, value.get("key2"),
                "map should have newer value"
        );
    }

//    @Test
//    @DisplayName("override map with map (same keys)")
//    void test006() {
//        JsonObject object1 = new JsonObject();
//        object1.put("key1", new HashMap<String, Object>() {{
//            this.put("key2", 2);
//        }});
//        JsonObject object2 = new JsonObject();
//        object2.put("key1", new HashMap<String, Object>() {{
//            this.put("key2", 3);
//        }});
//
//        object1.join(object2);
//
//        assertEquals(1, object1.keySet().size(),
//                "JsonObject join should have 1 key"
//        );
//        assertTrue(object1.containsKey("key1"),
//                "object 1 should hold on to its original key"
//        );
//        Map<String, Object> value = object1.getMap("key1");
//        assertEquals(1, value.keySet().size(),
//                "nested map should have 1 key"
//        );
//        assertTrue(value.containsKey("key2"),
//                "map missing original key"
//        );
//        assertEquals(3, value.get("key2"),
//                "map should have newer value"
//        );
//    }

}
