package org.rpgl.json;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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

    /**
     * The JsonObject used to test the get(...), put(...), and remove(...) methods
     */
    private static JsonObject getPutRemove_jsonObject;

    @BeforeEach
    public void beforeEach() {
        // join
        JsonObjectTest.join_jsonObject = new JsonObject();
        JsonObjectTest.join_parameterJsonObject = new JsonObject();
        // get, put, remove
        JsonObjectTest.getPutRemove_jsonObject = new JsonObject();
    }

    @Test
    @DisplayName("toString: comprehensive unit test")
    void toString_test() {
        JsonObject toStringJsonObject = new JsonObject();

        JsonObject nestedEmptyJsonObject = new JsonObject();
        JsonObject nestedPopulatedJsonObject = new JsonObject() {{
            this.putString("nested_string_key", "nested_string_value");
        }};

        JsonArray nestedEmptyArray = new JsonArray();
        JsonArray nestedPopulatedArray = new JsonArray() {{
            this.addBoolean(false);
        }};

        toStringJsonObject.putJsonObject("empty_object", nestedEmptyJsonObject);
        toStringJsonObject.putJsonObject("populated_object", nestedPopulatedJsonObject);
        toStringJsonObject.putJsonArray("empty_array", nestedEmptyArray);
        toStringJsonObject.putJsonArray("populated_array", nestedPopulatedArray);
        toStringJsonObject.putString("string_key", "string_value");
        toStringJsonObject.putInteger("integer_key", 123);
        toStringJsonObject.putDouble("double_key", 123.456);
        toStringJsonObject.putBoolean("boolean_key", true);

        String expected = """
                 {"boolean_key":true,"double_key":123.456,"empty_array":[],"empty_object":{},"integer_key":123,"populated_array":[false],"populated_object":{"nested_string_key":"nested_string_value"},"string_key":"string_value"}""";
        assertEquals(expected, toStringJsonObject.toString(),
                "JsonObject toString() method should print the json string representation of the json data, with keys sorted in alphanumerical order"
        );
    }

    @Test
    @DisplayName("join: novel key, new primitive value")
    void join_novelKey_newPrimitive() {
        /*
        Set up JsonObject objects for testing
         */
        JsonObjectTest.join_jsonObject.putInteger("key1", 1);
        JsonObjectTest.join_parameterJsonObject.putInteger("key2", 2);

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
        assertEquals(1, JsonObjectTest.join_jsonObject.getInteger("key1"),
                "original key's value should persist after join"
        );
        assertEquals(2, JsonObjectTest.join_jsonObject.getInteger("key2"),
                "new key's value should transfer after join"
        );
    }

    @Test
    @DisplayName("join: key collision, primitive value overrides primitive value")
    void join_keyCollision_primitiveOverridesPrimitive() {
        /*
        Set up JsonObject objects for testing
         */
        JsonObjectTest.join_jsonObject.putInteger("key1", 1);
        JsonObjectTest.join_parameterJsonObject.putInteger("key1", 2);

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
        assertEquals(2, JsonObjectTest.join_jsonObject.getInteger("key1"),
                "shared key should store the new value after join"
        );
    }

    @Test
    @DisplayName("join: key collision, map value overrides primitive value")
    void join_keyCollision_mapOverridesPrimitive() {
        /*
        Set up JsonObject objects for testing
         */
        JsonObjectTest.join_jsonObject.putInteger("key1", 1);
        JsonObjectTest.join_parameterJsonObject.putJsonObject("key1", new JsonObject() {{
            this.putInteger("key2", 2);
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
        assertNotNull(JsonObjectTest.join_jsonObject.getJsonObject("key1"),
                "non-null JsonObject value should be present after join"
        );
        assertTrue(JsonObjectTest.join_jsonObject.getJsonObject("key1").asMap().containsKey("key2"),
                "new map value should contain the correct key"
        );
        assertEquals(2, JsonObjectTest.join_jsonObject.getJsonObject("key1").asMap().get("key2"),
                "new map value should contain the correct value"
        );
    }

    @Test
    @DisplayName("join: key collision, map value joins to map value")
    void join_keyCollision_mapJoinsToMap() {
        /*
        Set up JsonObject objects for testing
         */
        JsonObjectTest.join_jsonObject.putJsonObject("key1", new JsonObject() {{
            this.putInteger("key2", 2);
        }});
        JsonObjectTest.join_parameterJsonObject.putJsonObject("key1", new JsonObject() {{
            this.putInteger("key3", 3);
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
                "nested map missing original key"
        );
        assertTrue(value.asMap().containsKey("key3"),
                "nested map missing new key"
        );
        assertEquals(2, value.getInteger("key2"),
                "nested map should have value of 2 at key key2"
        );
        assertEquals(3, value.getInteger("key3"),
                "nested map should have value of 3 at key key3"
        );
    }

    @Test
    @DisplayName("join: key collision, list value overrides primitive value")
    void join_keyCollision_listOverridesPrimitive() {
        /*
        Set up JsonObject objects for testing
         */
        JsonObjectTest.join_jsonObject.putInteger("key1", 1);
        JsonObjectTest.join_parameterJsonObject.putJsonArray("key1", new JsonArray() {{
            this.addString("value");
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
        JsonArray value = JsonObjectTest.join_jsonObject.getJsonArray("key1");
        assertEquals(1, value.asList().size(),
                "nested list should have 1 element"
        );
        assertEquals("value", value.getString(0),
                "nested list should have \"value\" as its first (only) element"
        );
    }

    @Test
    @DisplayName("join: key collision, list value joins to list value")
    void join_keyCollision_listJoinsToList() {
        /*
        Set up JsonObject objects for testing
         */
        JsonObjectTest.join_jsonObject.putJsonArray("key1", new JsonArray() {{
            this.addString("value1");
        }});
        JsonObjectTest.join_parameterJsonObject.putJsonArray("key1", new JsonArray() {{
            this.addString("value2");
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
        JsonArray value = JsonObjectTest.join_jsonObject.getJsonArray("key1");
        assertEquals(2, value.asList().size(),
                "nested list should have 2 elements"
        );
        assertTrue(value.asList().contains("value1"),
                "nested list should contain \"value1\" after join"
        );
        assertTrue(value.asList().contains("value2"),
                "nested list should contain \"value2\" after join"
        );
    }

    @Test
    @DisplayName("join: key collision, list value joins to list value, skip repeated elements")
    void join_keyCollision_listJoinsToList_skipRepeatedElements() {
        /*
        Set up JsonObject objects for testing
         */
        JsonObjectTest.join_jsonObject.putJsonArray("key1", new JsonArray() {{
            this.addString("value");
        }});
        JsonObjectTest.join_parameterJsonObject.putJsonArray("key1", new JsonArray() {{
            this.addString("value");
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
        JsonArray value = JsonObjectTest.join_jsonObject.getJsonArray("key1");
        assertEquals(1, value.asList().size(),
                "nested list should have 1 element"
        );
        assertTrue(value.asList().contains("value"),
                "nested list should contain \"value\" after join"
        );
    }

    @Test
    @DisplayName("join: key collision, list value joins to list value (using overridden method call)")
    void join_keyCollision_listJoinsToList_overriddenMethodCall() {
        /*
        Set up JsonObject objects for testing
         */
        JsonObjectTest.join_jsonObject.putJsonArray("key1", new JsonArray() {{
            this.addString("value1");
        }});
        JsonObjectTest.join_parameterJsonObject.putJsonArray("key1", new JsonArray() {{
            this.addString("value2");
        }});

        /*
        Invoke join(...) method for testing
         */
        JsonObjectTest.join_jsonObject.join(JsonObjectTest.join_parameterJsonObject.asMap());

        /*
        Assertions to confirm expected behavior
         */
        assertEquals(1, JsonObjectTest.join_jsonObject.asMap().keySet().size(),
                "JsonObject join should have 1 key"
        );
        assertTrue(JsonObjectTest.join_jsonObject.asMap().containsKey("key1"),
                "object 1 should hold on to its original key"
        );
        JsonArray value = JsonObjectTest.join_jsonObject.getJsonArray("key1");
        assertEquals(2, value.asList().size(),
                "nested list should have 2 elements"
        );
        assertTrue(value.asList().contains("value1"),
                "nested list should contain \"value1\" after join"
        );
        assertTrue(value.asList().contains("value2"),
                "nested list should contain \"value2\" after join"
        );
    }

    @Test
    @DisplayName("join: join uses deep clones of the parameter data")
    void join_usesDeepClones() {
        /*
        populate parameter JsonObject
         */
        JsonObject nestedJsonObject = new JsonObject() {{
            this.putString("nested_string_key", "nested_string_value");
        }};
        JsonObjectTest.join_parameterJsonObject.putJsonObject("object_key", nestedJsonObject);

        /*
        perform join
         */
        JsonObjectTest.join_jsonObject.join(JsonObjectTest.join_parameterJsonObject);

        /*
        make changes to parameter JsonObject
         */
        nestedJsonObject.asMap().clear();
        JsonObjectTest.join_parameterJsonObject.putInteger("integer_key", 123);

        /*
        verify changes made to parameter JsonObject do not impact the calling JsonObject
         */
        assertFalse(JsonObjectTest.join_jsonObject.getJsonObject("object_key").asMap().isEmpty(),
                "changing the parameter JsonObject should not impact the calling JsonObject"
        );
        assertNull(JsonObjectTest.join_jsonObject.getInteger("integer_key"),
                "additional keys added to the parameter JsonObject should not appear in the calling JsonObject"
        );
    }

    @Test
    @DisplayName("get, put, remove: JsonObject values")
    void getPutRemove_JsonObject() {
        JsonObject value = new JsonObject() {{
            this.putString("nested_key", "value");
        }};
        /*
        verify put(...) behavior
         */
        JsonObjectTest.getPutRemove_jsonObject.putJsonObject("key", value);
        assertTrue(JsonObjectTest.getPutRemove_jsonObject.data.get("key") instanceof Map,
                "a Map value should be stored in the JsonObject after calling putJsonObject(...)"
        );
        /*
        verify get(...) behavior
         */
        assertNotNull(JsonObjectTest.getPutRemove_jsonObject.getJsonObject("key"),
                "a non-null JsonObject should be returned by getJsonObject(...) once one is stored at the target key"
        );
        assertNull(JsonObjectTest.getPutRemove_jsonObject.getJsonObject("empty_key"),
                "null should be returned when a JsonObject is not stored in the target key"
        );
        /*
        verify remove(...) behavior
         */
        assertEquals(value, JsonObjectTest.getPutRemove_jsonObject.removeJsonObject("key"),
                "the original value should be returned by removeJsonObject(...)"
        );
        assertNull(JsonObjectTest.getPutRemove_jsonObject.data.get("key"),
                "nothing should remain in the target key after calling removeJsonObject(...)"
        );
        assertNull(JsonObjectTest.getPutRemove_jsonObject.removeJsonObject("key"),
                "nothing should remain to be removed directly following removeJsonObject(...)"
        );
    }

    @Test
    @DisplayName("get, put, remove: JsonArray values")
    void getPutRemove_JsonArray() {
        JsonArray value = new JsonArray() {{
            this.addString("value");
        }};
        /*
        verify put(...) behavior
         */
        JsonObjectTest.getPutRemove_jsonObject.putJsonArray("key", value);
        assertTrue(JsonObjectTest.getPutRemove_jsonObject.data.get("key") instanceof List,
                "a List value should be stored in the JsonObject after calling putJsonArray(...)"
        );
        /*
        verify get(...) behavior
         */
        assertNotNull(JsonObjectTest.getPutRemove_jsonObject.getJsonArray("key"),
                "a non-null JsonArray should be returned by getJsonArray(...) once one is stored at the target key"
        );
        assertNull(JsonObjectTest.getPutRemove_jsonObject.getJsonArray("empty_key"),
                "null should be returned when a JsonArray is not stored in the target key"
        );
        /*
        verify remove(...) behavior
         */
        assertEquals(value, JsonObjectTest.getPutRemove_jsonObject.removeJsonArray("key"),
                "the original value should be returned by removeJsonArray(...)"
        );
        assertNull(JsonObjectTest.getPutRemove_jsonObject.data.get("key"),
                "nothing should remain in the target key after calling removeJsonArray(...)"
        );
        assertNull(JsonObjectTest.getPutRemove_jsonObject.removeJsonArray("key"),
                "nothing should remain to be removed directly following removeJsonArray(...)"
        );
    }

    @Test
    @DisplayName("get, put, remove: String values")
    void getPutRemove_String() {
        String value = "value";
        /*
        verify put(...) behavior
         */
        JsonObjectTest.getPutRemove_jsonObject.putString("key", value);
        assertTrue(JsonObjectTest.getPutRemove_jsonObject.data.get("key") instanceof String,
                "a String value should be stored in the JsonObject after calling putString(...)"
        );
        /*
        verify get(...) behavior
         */
        assertNotNull(JsonObjectTest.getPutRemove_jsonObject.getString("key"),
                "a non-null String should be returned by getString(...) once one is stored at the target key"
        );
        assertNull(JsonObjectTest.getPutRemove_jsonObject.getString("empty_key"),
                "null should be returned when a String is not stored in the target key"
        );
        /*
        verify remove(...) behavior
         */
        assertEquals(value, JsonObjectTest.getPutRemove_jsonObject.removeString("key"),
                "the original value should be returned by removeString(...)"
        );
        assertNull(JsonObjectTest.getPutRemove_jsonObject.data.get("key"),
                "nothing should remain in the target key after calling removeString(...)"
        );
        assertNull(JsonObjectTest.getPutRemove_jsonObject.removeString("key"),
                "nothing should remain to be removed directly following removeString(...)"
        );
    }

    @Test
    @DisplayName("get, put, remove: Integer values")
    void getPutRemove_Integer() {
        Integer value = 1;
        /*
        verify put(...) behavior
         */
        JsonObjectTest.getPutRemove_jsonObject.putInteger("key", value);
        assertTrue(JsonObjectTest.getPutRemove_jsonObject.data.get("key") instanceof Integer,
                "a Integer value should be stored in the JsonObject after calling putInteger(...)"
        );
        /*
        verify get(...) behavior
         */
        assertNotNull(JsonObjectTest.getPutRemove_jsonObject.getInteger("key"),
                "a non-null Integer should be returned by getInteger(...) once one is stored at the target key"
        );
        assertNull(JsonObjectTest.getPutRemove_jsonObject.getInteger("empty_key"),
                "null should be returned when a Integer is not stored in the target key"
        );
        /*
        verify remove(...) behavior
         */
        assertEquals(value, JsonObjectTest.getPutRemove_jsonObject.removeInteger("key"),
                "the original value should be returned by removeInteger(...)"
        );
        assertNull(JsonObjectTest.getPutRemove_jsonObject.data.get("key"),
                "nothing should remain in the target key after calling removeInteger(...)"
        );
        assertNull(JsonObjectTest.getPutRemove_jsonObject.removeInteger("key"),
                "nothing should remain to be removed directly following removeInteger(...)"
        );
    }

    @Test
    @DisplayName("get, put, remove: Double values")
    void getPutRemove_Double() {
        Double value = 1.0;
        /*
        verify put(...) behavior
         */
        JsonObjectTest.getPutRemove_jsonObject.putDouble("key", value);
        assertTrue(JsonObjectTest.getPutRemove_jsonObject.data.get("key") instanceof Double,
                "a Double value should be stored in the JsonObject after calling putDouble(...)"
        );
        /*
        verify get(...) behavior
         */
        assertNotNull(JsonObjectTest.getPutRemove_jsonObject.getDouble("key"),
                "a non-null Double should be returned by getDouble(...) once one is stored at the target key"
        );
        assertNull(JsonObjectTest.getPutRemove_jsonObject.getDouble("empty_key"),
                "null should be returned when a Double is not stored in the target key"
        );
        /*
        verify remove(...) behavior
         */
        assertEquals(value, JsonObjectTest.getPutRemove_jsonObject.removeDouble("key"),
                "the original value should be returned by removeDouble(...)"
        );
        assertNull(JsonObjectTest.getPutRemove_jsonObject.data.get("key"),
                "nothing should remain in the target key after calling removeDouble(...)"
        );
        assertNull(JsonObjectTest.getPutRemove_jsonObject.removeDouble("key"),
                "nothing should remain to be removed directly following removeDouble(...)"
        );
    }

    @Test
    @DisplayName("get, put, remove: Boolean values")
    void getPutRemove_Boolean() {
        Boolean value = false;
        /*
        verify put(...) behavior
         */
        JsonObjectTest.getPutRemove_jsonObject.putBoolean("key", value);
        assertTrue(JsonObjectTest.getPutRemove_jsonObject.data.get("key") instanceof Boolean,
                "a Boolean value should be stored in the JsonObject after calling putBoolean(...)"
        );
        /*
        verify get(...) behavior
         */
        assertNotNull(JsonObjectTest.getPutRemove_jsonObject.getBoolean("key"),
                "a non-null Boolean should be returned by getBoolean(...) once one is stored at the target key"
        );
        assertNull(JsonObjectTest.getPutRemove_jsonObject.getBoolean("empty_key"),
                "null should be returned when a Boolean is not stored in the target key"
        );
        /*
        verify remove(...) behavior
         */
        assertEquals(value, JsonObjectTest.getPutRemove_jsonObject.removeBoolean("key"),
                "the original value should be returned by removeBoolean(...)"
        );
        assertNull(JsonObjectTest.getPutRemove_jsonObject.data.get("key"),
                "nothing should remain in the target key after calling removeBoolean(...)"
        );
        assertNull(JsonObjectTest.getPutRemove_jsonObject.removeBoolean("key"),
                "nothing should remain to be removed directly following removeBoolean(...)"
        );
    }

    @Test
    @DisplayName("deepClone: comprehensive unit test")
    void deepClone_test() {
        /*
        set up original JsonObject to be cloned
         */
        JsonObject jsonObject = new JsonObject() {{
            this.putJsonObject("object_key", new JsonObject());
            this.putJsonArray("array_key", new JsonArray());
            this.putString("string_key", "value");
            this.putInteger("integer_key", 123);
            this.putDouble("double_key", 123.456);
            this.putBoolean("boolean_key", true);
        }};

        /*
        create clone
         */
        JsonObject deepClone = jsonObject.deepClone();

        /*
        modify original JsonObject
         */
        jsonObject.data.clear();
        jsonObject.putString("new_key", "new_value");

        /*
        verify changes did not affect clone
         */
        assertNotNull(deepClone.getJsonObject("object_key"),
                "clone's JsonObject value should persist after original was cleared"
        );
        assertNotNull(deepClone.getJsonArray("array_key"),
                "clone's JsonArray value should persist after original was cleared"
        );
        assertNotNull(deepClone.getString("string_key"),
                "clone's String value should persist after original was cleared"
        );
        assertNotNull(deepClone.getInteger("integer_key"),
                "clone's Integer value should persist after original was cleared"
        );
        assertNotNull(deepClone.getDouble("double_key"),
                "clone's Double value should persist after original was cleared"
        );
        assertNotNull(deepClone.getBoolean("boolean_key"),
                "clone's Boolean value should persist after original was cleared"
        );
        assertNull(deepClone.data.get("new_key"),
                "clone should not include new entries made to original"
        );
    }

}
