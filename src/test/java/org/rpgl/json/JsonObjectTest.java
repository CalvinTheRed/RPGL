package org.rpgl.json;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for JsonObject class.
 *
 * @author Calvin Withun
 */
public class JsonObjectTest {

    // TODO a lot of places use new JsonObject(new HashMap<>() {{... but they could just use new JsonObject() {{... right?

    /**
     * The JsonObject whose join methods are being called for join(...) tests.
     */
    private static JsonObject join_jsonObject;

    /**
     * The JsonObject passed as a parameter to another JsonObject's join(...) method.
     */
    private static JsonObject join_parameterJsonObject;

    @BeforeEach
    public void beforeEach() {
        // before-each for join(...) tests
        join_jsonObject = new JsonObject();
        join_parameterJsonObject = new JsonObject();
    }

    @Test
    @DisplayName("join: adding novel key")
    void join_addingNovelKey() {
        /*
        Set up JsonObject objects for testing
         */
        JsonObjectTest.join_jsonObject.asMap().put("key1", 1);
        JsonObjectTest.join_parameterJsonObject.asMap().put("key2", 2);

        /*
        Invoke join(...) method for testing
         */
        JsonObjectTest.join_jsonObject.join(JsonObjectTest.join_parameterJsonObject);

        /*
        Assertions to confirm expected behavior
         */
        assertEquals(2, JsonObjectTest.join_jsonObject.asMap().keySet().size(),
                "second key should be present after joining JsonObject with a new key"
        );
        assertTrue(JsonObjectTest.join_jsonObject.asMap().containsKey("key1"),
                "original key should persist after join"
        );
        assertTrue(JsonObjectTest.join_jsonObject.asMap().containsKey("key2"),
                "new key should transfer over after join"
        );
        assertEquals(1, JsonObjectTest.join_jsonObject.asMap().get("key1"),
                "original key's value should persist after join"
        );
        assertEquals(2, JsonObjectTest.join_jsonObject.asMap().get("key2"),
                "new key's value should transfer after join"
        );
    }

    @Test
    @DisplayName("join: primitive overrides primitive, new value")
    void join_primitiveOverridesPrimitiveNewValue() {
        /*
        Set up JsonObject objects for testing
         */
        JsonObjectTest.join_jsonObject.asMap().put("key1", 1);
        JsonObjectTest.join_parameterJsonObject.asMap().put("key1", 2);

        /*
        Invoke join(...) method for testing
         */
        JsonObjectTest.join_jsonObject.join(JsonObjectTest.join_parameterJsonObject);

        /*
        Assertions to confirm expected behavior
         */
        assertEquals(1, JsonObjectTest.join_jsonObject.asMap().keySet().size(),
                "only 1 key should be present after joining a JsonObject with a shared key"
        );
        assertTrue(JsonObjectTest.join_jsonObject.asMap().containsKey("key1"),
                "original key should not change after joining a JsonObject with a shared key"
        );
        assertEquals(2, JsonObjectTest.join_jsonObject.asMap().get("key1"),
                "shared key should store the new value after join"
        );
    }

    @Test
    @DisplayName("join: map overrides primitive")
    void join_mapOverridesPrimitive() {
        /*
        Set up JsonObject objects for testing
         */
        JsonObjectTest.join_jsonObject.asMap().put("key1", 1);
        JsonObjectTest.join_parameterJsonObject.asMap().put("key1", new HashMap<String, Object>() {{
            this.put("key2", 2);
        }});

        /*
        Invoke join(...) method for testing
         */
        JsonObjectTest.join_jsonObject.join(JsonObjectTest.join_parameterJsonObject);

        /*
        Assertions to confirm expected behavior
         */
        assertEquals(1, JsonObjectTest.join_jsonObject.asMap().keySet().size(),
                "only 1 key should be present after joining a JsonObject with a shared key"
        );
        assertTrue(JsonObjectTest.join_jsonObject.asMap().containsKey("key1"),
                "original key should not change after joining a JsonObject with a shared key"
        );
        assertTrue(JsonObjectTest.join_jsonObject.asMap().get("key1") instanceof Map,
                "new value should be a Map rather than an Integer after join"
        );
        assertTrue(JsonObjectTest.join_jsonObject.getJsonObject("key1").asMap().containsKey("key2"),
                "new map value should contain the correct key"
        );
        assertEquals(2, JsonObjectTest.join_jsonObject.getJsonObject("key1").asMap().get("key2"),
                "new map value should contain the correct value"
        );
    }

    @Test
    @DisplayName("join: map overrides map, same outer keys")
    void join_mapOverridesMapSameContent() {
        /*
        Set up JsonObject objects for testing
         */
        JsonObjectTest.join_jsonObject.asMap().put("key1", new HashMap<String, Object>() {{
            this.put("key2", 2);
        }});
        JsonObjectTest.join_parameterJsonObject.asMap().put("key1", new HashMap<String, Object>() {{
            this.put("key3", 3);
        }});

        /*
        Invoke join(...) method for testing
         */
        JsonObjectTest.join_jsonObject.join(JsonObjectTest.join_parameterJsonObject);

        /*
        Assertions to confirm expected behavior
         */
        assertEquals(1, JsonObjectTest.join_jsonObject.asMap().keySet().size(),
                "JsonObject join should have 1 key"
        );
        assertTrue(JsonObjectTest.join_jsonObject.asMap().containsKey("key1"),
                "object 1 should hold on to its original key"
        );
        JsonObject value = JsonObjectTest.join_jsonObject.getJsonObject("key1");
        assertEquals(2, value.asMap().keySet().size(),
                "nested map should have 2 keys"
        );
        assertTrue(value.asMap().containsKey("key2"),
                "map missing original key"
        );
        assertTrue(value.asMap().containsKey("key3"),
                "map missing new key"
        );
    }

    @Test
    @DisplayName("toString: test f functionality")
    void toString_test() {
        JsonObject toStringJsonObject = new JsonObject() {{
            this.putJsonObject("empty object", new JsonObject());
            this.putJsonObject("populated object", new JsonObject() {{
                this.putString("nested string", "nested value");
            }});
            this.putJsonArray("empty array", new JsonArray());
            this.putJsonArray("populated array", new JsonArray() {{
                this.addString("element");
            }});
            this.putString("string", "value");
            this.putInteger("integer", 123);
            this.putDouble("double", 12.34);
            this.putBoolean("boolean", false);
        }};
        // System.out.println(toStringJsonObject);

        // TODO this is not printed in an intuitive ordering...
    }

}
