package org.rpgl.json;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.json.JsonObject class.
 *
 * @author Calvin Withun
 */
public class JsonObjectTest {

    /**
     * The JsonObject whose join methods are being called for join(...) tests.
     */
    private JsonObject join_jsonObject;

    /**
     * The JsonObject passed as a parameter to another JsonObject's join(...) method.
     */
    private JsonObject join_parameterJsonObject;

    /**
     * The JsonObject used to test the get(...), put(...), and remove(...) methods
     */
    private JsonObject getPutRemove_jsonObject;

    @BeforeEach
    public void beforeEach() {
        // join
        join_jsonObject = new JsonObject();
        join_parameterJsonObject = new JsonObject();
        // get, put, remove
        getPutRemove_jsonObject = new JsonObject();
    }

    @Test
    @DisplayName("toString: comprehensive unit test")
    void toString_test() {
        JsonObject toStringJsonObject = new JsonObject() {{
            /*{
                "empty_object": { },
                "populated_object": {
                    "nested_string_key": "nested_string_value"
                },
                "empty_array": [ ],
                "populated_array": [
                    false
                ],
                "string_key": "string_value",
                "integer_key": 123,
                "double_key": 123.456,
                "boolean_key": true
             }*/
            this.putJsonObject("empty_object", new JsonObject());
            this.putJsonObject("populated_object", new JsonObject() {{
                this.putString("nested_string_key", "nested_string_value");
            }});
            this.putJsonArray("empty_array", new JsonArray());
            this.putJsonArray("populated_array", new JsonArray() {{
                this.addBoolean(false);
            }});
            this.putString("string_key", "string_value");
            this.putInteger("integer_key", 123);
            this.putDouble("double_key", 123.456);
            this.putBoolean("boolean_key", true);
        }};

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
        join_jsonObject.putInteger("key1", 1);
        join_parameterJsonObject.putInteger("key2", 2);

        /*
        Invoke join(...) method for testing
         */
        join_jsonObject.join(join_parameterJsonObject);

        /*
        Assertions to confirm expected behavior
         */
        assertEquals(2, join_jsonObject.asMap().keySet().size(),
                "second key should be present after joining JsonObject with a new key"
        );
        assertTrue(join_jsonObject.asMap().containsKey("key1"),
                "original key should persist after join"
        );
        assertTrue(join_jsonObject.asMap().containsKey("key2"),
                "new key should transfer over after join"
        );
        assertEquals(1, join_jsonObject.getInteger("key1"),
                "original key's value should persist after join"
        );
        assertEquals(2, join_jsonObject.getInteger("key2"),
                "new key's value should transfer after join"
        );
    }

    @Test
    @DisplayName("join: key collision, primitive value overrides primitive value")
    void join_keyCollision_primitiveOverridesPrimitive() {
        /*
        Set up JsonObject objects for testing
         */
        join_jsonObject.putInteger("key1", 1);
        join_parameterJsonObject.putInteger("key1", 2);

        /*
        Invoke join(...) method for testing
         */
        join_jsonObject.join(join_parameterJsonObject);

        /*
        Assertions to confirm expected behavior
         */
        assertEquals(1, join_jsonObject.asMap().keySet().size(),
                "only 1 key should be present after joining a JsonObject with a shared key"
        );
        assertTrue(join_jsonObject.asMap().containsKey("key1"),
                "original key should not change after joining a JsonObject with a shared key"
        );
        assertEquals(2, join_jsonObject.getInteger("key1"),
                "shared key should store the new value after join"
        );
    }

    @Test
    @DisplayName("join: key collision, map value overrides primitive value")
    void join_keyCollision_mapOverridesPrimitive() {
        /*
        Set up JsonObject objects for testing
         */
        join_jsonObject.putInteger("key1", 1);
        join_parameterJsonObject.putJsonObject("key1", new JsonObject() {{
            this.putInteger("key2", 2);
        }});

        /*
        Invoke join(...) method for testing
         */
        join_jsonObject.join(join_parameterJsonObject);

        /*
        Assertions to confirm expected behavior
         */
        assertEquals(1, join_jsonObject.asMap().keySet().size(),
                "only 1 key should be present after joining a JsonObject with a shared key"
        );
        assertTrue(join_jsonObject.asMap().containsKey("key1"),
                "original key should not change after joining a JsonObject with a shared key"
        );
        assertNotNull(join_jsonObject.getJsonObject("key1"),
                "non-null JsonObject value should be present after join"
        );
        assertTrue(join_jsonObject.getJsonObject("key1").asMap().containsKey("key2"),
                "new map value should contain the correct key"
        );
        assertEquals(2, join_jsonObject.getJsonObject("key1").asMap().get("key2"),
                "new map value should contain the correct value"
        );
    }

    @Test
    @DisplayName("join: key collision, map value joins to map value")
    void join_keyCollision_mapJoinsToMap() {
        /*
        Set up JsonObject objects for testing
         */
        join_jsonObject.putJsonObject("key1", new JsonObject() {{
            this.putInteger("key2", 2);
        }});
        join_parameterJsonObject.putJsonObject("key1", new JsonObject() {{
            this.putInteger("key3", 3);
        }});

        /*
        Invoke join(...) method for testing
         */
        join_jsonObject.join(join_parameterJsonObject);

        /*
        Assertions to confirm expected behavior
         */
        assertEquals(1, join_jsonObject.asMap().keySet().size(),
                "JsonObject join should have 1 key"
        );
        assertTrue(join_jsonObject.asMap().containsKey("key1"),
                "object 1 should hold on to its original key"
        );
        JsonObject value = join_jsonObject.getJsonObject("key1");
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
        join_jsonObject.putInteger("key1", 1);
        join_parameterJsonObject.putJsonArray("key1", new JsonArray() {{
            this.addString("value");
        }});

        /*
        Invoke join(...) method for testing
         */
        join_jsonObject.join(join_parameterJsonObject);

        /*
        Assertions to confirm expected behavior
         */
        assertEquals(1, join_jsonObject.asMap().keySet().size(),
                "JsonObject join should have 1 key"
        );
        assertTrue(join_jsonObject.asMap().containsKey("key1"),
                "object 1 should hold on to its original key"
        );
        JsonArray value = join_jsonObject.getJsonArray("key1");
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
        join_jsonObject.putJsonArray("key1", new JsonArray() {{
            this.addString("value1");
        }});
        join_parameterJsonObject.putJsonArray("key1", new JsonArray() {{
            this.addString("value2");
        }});

        /*
        Invoke join(...) method for testing
         */
        join_jsonObject.join(join_parameterJsonObject);

        /*
        Assertions to confirm expected behavior
         */
        assertEquals(1, join_jsonObject.asMap().keySet().size(),
                "JsonObject join should have 1 key"
        );
        assertTrue(join_jsonObject.asMap().containsKey("key1"),
                "object 1 should hold on to its original key"
        );
        JsonArray value = join_jsonObject.getJsonArray("key1");
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
        join_jsonObject.putJsonArray("key1", new JsonArray() {{
            this.addString("value");
        }});
        join_parameterJsonObject.putJsonArray("key1", new JsonArray() {{
            this.addString("value");
        }});

        /*
        Invoke join(...) method for testing
         */
        join_jsonObject.join(join_parameterJsonObject);

        /*
        Assertions to confirm expected behavior
         */
        assertEquals(1, join_jsonObject.asMap().keySet().size(),
                "JsonObject join should have 1 key"
        );
        assertTrue(join_jsonObject.asMap().containsKey("key1"),
                "object 1 should hold on to its original key"
        );
        JsonArray value = join_jsonObject.getJsonArray("key1");
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
        join_jsonObject.putJsonArray("key1", new JsonArray() {{
            this.addString("value1");
        }});
        join_parameterJsonObject.putJsonArray("key1", new JsonArray() {{
            this.addString("value2");
        }});

        /*
        Invoke join(...) method for testing
         */
        join_jsonObject.join(join_parameterJsonObject.asMap());

        /*
        Assertions to confirm expected behavior
         */
        assertEquals(1, join_jsonObject.asMap().keySet().size(),
                "JsonObject join should have 1 key"
        );
        assertTrue(join_jsonObject.asMap().containsKey("key1"),
                "object 1 should hold on to its original key"
        );
        JsonArray value = join_jsonObject.getJsonArray("key1");
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
        join_parameterJsonObject.putJsonObject("object_key", nestedJsonObject);

        /*
        perform join
         */
        join_jsonObject.join(join_parameterJsonObject);

        /*
        make changes to parameter JsonObject
         */
        nestedJsonObject.asMap().clear();
        join_parameterJsonObject.putInteger("integer_key", 123);

        /*
        verify changes made to parameter JsonObject do not impact the calling JsonObject
         */
        assertFalse(join_jsonObject.getJsonObject("object_key").asMap().isEmpty(),
                "changing the parameter JsonObject should not impact the calling JsonObject"
        );
        assertNull(join_jsonObject.getInteger("integer_key"),
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
        getPutRemove_jsonObject.putJsonObject("key", value);
        assertTrue(getPutRemove_jsonObject.data.get("key") instanceof Map,
                "a Map value should be stored in the JsonObject after calling putJsonObject(...)"
        );
        /*
        verify get(...) behavior
         */
        assertNotNull(getPutRemove_jsonObject.getJsonObject("key"),
                "a non-null JsonObject should be returned by getJsonObject(...) once one is stored at the target key"
        );
        assertNull(getPutRemove_jsonObject.getJsonObject("empty_key"),
                "null should be returned when a JsonObject is not stored in the target key"
        );
        /*
        verify remove(...) behavior
         */
        assertEquals(value, getPutRemove_jsonObject.removeJsonObject("key"),
                "the original value should be returned by removeJsonObject(...)"
        );
        assertNull(getPutRemove_jsonObject.data.get("key"),
                "nothing should remain in the target key after calling removeJsonObject(...)"
        );
        assertNull(getPutRemove_jsonObject.removeJsonObject("key"),
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
        getPutRemove_jsonObject.putJsonArray("key", value);
        assertTrue(getPutRemove_jsonObject.data.get("key") instanceof ArrayList,
                "a ArrayList value should be stored in the JsonObject after calling putJsonArray(...)"
        );
        /*
        verify get(...) behavior
         */
        assertNotNull(getPutRemove_jsonObject.getJsonArray("key"),
                "a non-null JsonArray should be returned by getJsonArray(...) once one is stored at the target key"
        );
        assertNull(getPutRemove_jsonObject.getJsonArray("empty_key"),
                "null should be returned when a JsonArray is not stored in the target key"
        );
        /*
        verify remove(...) behavior
         */
        assertEquals(value, getPutRemove_jsonObject.removeJsonArray("key"),
                "the original value should be returned by removeJsonArray(...)"
        );
        assertNull(getPutRemove_jsonObject.data.get("key"),
                "nothing should remain in the target key after calling removeJsonArray(...)"
        );
        assertNull(getPutRemove_jsonObject.removeJsonArray("key"),
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
        getPutRemove_jsonObject.putString("key", value);
        assertTrue(getPutRemove_jsonObject.data.get("key") instanceof String,
                "a String value should be stored in the JsonObject after calling putString(...)"
        );
        /*
        verify get(...) behavior
         */
        assertNotNull(getPutRemove_jsonObject.getString("key"),
                "a non-null String should be returned by getString(...) once one is stored at the target key"
        );
        assertNull(getPutRemove_jsonObject.getString("empty_key"),
                "null should be returned when a String is not stored in the target key"
        );
        /*
        verify remove(...) behavior
         */
        assertEquals(value, getPutRemove_jsonObject.removeString("key"),
                "the original value should be returned by removeString(...)"
        );
        assertNull(getPutRemove_jsonObject.data.get("key"),
                "nothing should remain in the target key after calling removeString(...)"
        );
        assertNull(getPutRemove_jsonObject.removeString("key"),
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
        getPutRemove_jsonObject.putInteger("key", value);
        assertTrue(getPutRemove_jsonObject.data.get("key") instanceof Integer,
                "a Integer value should be stored in the JsonObject after calling putInteger(...)"
        );
        /*
        verify get(...) behavior
         */
        assertNotNull(getPutRemove_jsonObject.getInteger("key"),
                "a non-null Integer should be returned by getInteger(...) once one is stored at the target key"
        );
        assertNull(getPutRemove_jsonObject.getInteger("empty_key"),
                "null should be returned when a Integer is not stored in the target key"
        );
        /*
        verify remove(...) behavior
         */
        assertEquals(value, getPutRemove_jsonObject.removeInteger("key"),
                "the original value should be returned by removeInteger(...)"
        );
        assertNull(getPutRemove_jsonObject.data.get("key"),
                "nothing should remain in the target key after calling removeInteger(...)"
        );
        assertNull(getPutRemove_jsonObject.removeInteger("key"),
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
        getPutRemove_jsonObject.putDouble("key", value);
        assertTrue(getPutRemove_jsonObject.data.get("key") instanceof Double,
                "a Double value should be stored in the JsonObject after calling putDouble(...)"
        );
        /*
        verify get(...) behavior
         */
        assertNotNull(getPutRemove_jsonObject.getDouble("key"),
                "a non-null Double should be returned by getDouble(...) once one is stored at the target key"
        );
        assertNull(getPutRemove_jsonObject.getDouble("empty_key"),
                "null should be returned when a Double is not stored in the target key"
        );
        /*
        verify remove(...) behavior
         */
        assertEquals(value, getPutRemove_jsonObject.removeDouble("key"),
                "the original value should be returned by removeDouble(...)"
        );
        assertNull(getPutRemove_jsonObject.data.get("key"),
                "nothing should remain in the target key after calling removeDouble(...)"
        );
        assertNull(getPutRemove_jsonObject.removeDouble("key"),
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
        getPutRemove_jsonObject.putBoolean("key", value);
        assertTrue(getPutRemove_jsonObject.data.get("key") instanceof Boolean,
                "a Boolean value should be stored in the JsonObject after calling putBoolean(...)"
        );
        /*
        verify get(...) behavior
         */
        assertNotNull(getPutRemove_jsonObject.getBoolean("key"),
                "a non-null Boolean should be returned by getBoolean(...) once one is stored at the target key"
        );
        assertNull(getPutRemove_jsonObject.getBoolean("empty_key"),
                "null should be returned when a Boolean is not stored in the target key"
        );
        /*
        verify remove(...) behavior
         */
        assertEquals(value, getPutRemove_jsonObject.removeBoolean("key"),
                "the original value should be returned by removeBoolean(...)"
        );
        assertNull(getPutRemove_jsonObject.data.get("key"),
                "nothing should remain in the target key after calling removeBoolean(...)"
        );
        assertNull(getPutRemove_jsonObject.removeBoolean("key"),
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

    @Test
    @DisplayName("seek depth of one")
    void seek_depthOfOne() {
        JsonObject json = new JsonObject() {{
           this.putString("key", "value");
            this.putString("otherKey", "otherValue");
        }};
        assertEquals("value", json.seek("key"),
                "key of 'key' should have value of 'value'"
        );
    }

    @Test
    @DisplayName("seek greater depths")
    void seek_greaterDepths() {
        JsonObject json = new JsonObject() {{
            this.putJsonObject("key", new JsonObject() {{
                this.putJsonObject("key", new JsonObject() {{
                    this.putString("key", "value");
                }});
            }});
        }};
        assertEquals("value", json.seek("key.key.key"),
                "key of 'key.key.key' should have value of 'value'"
        );
    }

    @Test
    @DisplayName("seek can return HashMap")
    void seek_canReturnHashMap() {
        JsonObject json = new JsonObject() {{
            this.putJsonObject("key", new JsonObject() {{
                this.putJsonObject("nestedKey", new JsonObject());
            }});
        }};
        assertTrue(json.seek("key.nestedKey") instanceof HashMap<?,?>,
                "key of 'key.nestedKey' should contain a HashMap (not yet converted to JsonObject)"
        );
    }

    @Test
    @DisplayName("seek can return ArrayList")
    void seek_canReturnArrayList() {
        JsonObject json = new JsonObject() {{
            this.putJsonObject("key", new JsonObject() {{
                this.putJsonArray("nestedKey", new JsonArray());
            }});
        }};
        assertTrue(json.seek("key.nestedKey") instanceof ArrayList<?>,
                "key of 'key.nestedKey' should contain an ArrayList (not yet converted to JsonArray)"
        );
    }

    @Test
    @DisplayName("seek traverses JsonArray")
    void seek_traversesJsonArray() {
        JsonObject json = new JsonObject() {{
            this.putJsonArray("key", new JsonArray() {{
                this.addString("value");
            }});
        }};
        assertEquals("value", json.seek("key[0]"),
                "key of 'key[0]' should have value of 'value'"
        );
    }

    @Test
    @DisplayName("seek traverses nested JsonArray")
    void seek_traversesNestedJsonArray() {
        JsonObject json = new JsonObject() {{
            this.putJsonArray("key", new JsonArray() {{
                this.addJsonArray(new JsonArray() {{
                    this.addString("value");
                }});
            }});
        }};
        assertEquals("value", json.seek("key[0][0]"),
                "key of 'key[0][0]' should have value of 'value'"
        );
    }

    @Test
    @DisplayName("seek traverses and continues past JsonArray")
    void seek_traversesAndContinuesPastJsonArray() {
        JsonObject json = new JsonObject() {{
            this.putJsonArray("key", new JsonArray() {{
                this.addJsonObject(new JsonObject() {{
                    this.putString("nestedKey", "value");
                }});
            }});
        }};
        assertEquals("value", json.seek("key[0].nestedKey"),
                "key of 'key[0].nestedKey' should have value of 'value'"
        );
    }

    @Test
    @DisplayName("seek contains references to original objects")
    void seek_containsReferencesToOriginalObjects() {
        JsonObject innerObject = new JsonObject();
        JsonObject middleObject = new JsonObject() {{
            this.putJsonObject("key", innerObject);
        }};
        JsonObject outerObject = new JsonObject() {{
            this.putJsonObject("key", middleObject);
        }};

        JsonObject seekResultForInnerObject = outerObject.seekJsonObject("key.key");
        seekResultForInnerObject.putString("key", "value");
        assertEquals(innerObject.toString(), seekResultForInnerObject.toString(),
                "seek should return references to original objects"
        );
    }

    @Test
    @DisplayName("seekJsonObject returnsCorrectValue")
    void seekJsonObject_returnsCorrectValue() {
        JsonObject json = new JsonObject() {{
            this.putJsonObject("key", new JsonObject() {{
                this.putString("nestedKey", "value");
            }});
        }};
        String expected = """
                {"nestedKey":"value"}""";
        assertEquals(expected, json.seekJsonObject("key").toString(),
                "seekJsonObject should return the target JsonObject"
        );
    }

    @Test
    @DisplayName("seekJsonArray returnsCorrectValue")
    void seekJsonArray_returnsCorrectValue() {
        JsonObject json = new JsonObject() {{
            this.putJsonArray("key", new JsonArray() {{
                this.addString("value");
            }});
        }};
        String expected = """
                ["value"]""";
        assertEquals(expected, json.seekJsonArray("key").toString(),
                "seekJsonArray should return the target JsonArray"
        );
    }

    @Test
    @DisplayName("seekString returnsCorrectValue")
    void seekString_returnsCorrectValue() {
        JsonObject json = new JsonObject() {{
            this.putString("key", "value");
        }};
        assertEquals("value", json.seekString("key"),
                "seekString should return the target String"
        );
    }

    @Test
    @DisplayName("seekInteger returnsCorrectValue")
    void seekInteger_returnsCorrectValue() {
        JsonObject json = new JsonObject() {{
            this.putInteger("key", 100);
        }};
        assertEquals(100, json.seekInteger("key"),
                "seekInteger should return the target integer"
        );
    }

    @Test
    @DisplayName("seekDouble returnsCorrectValue")
    void seekDouble_returnsCorrectValue() {
        JsonObject json = new JsonObject() {{
            this.putDouble("key", 123.456);
        }};
        assertEquals(123.456, json.seekDouble("key"),
                "seekDouble should return the target double"
        );
    }

    @Test
    @DisplayName("seekBoolean returnsCorrectValue")
    void seekBoolean_returnsCorrectValue() {
        JsonObject json = new JsonObject() {{
            this.putBoolean("key", true);
        }};
        assertTrue(json.seekBoolean("key"),
                "seekBoolean should return the target boolean"
        );
    }

    @Test
    @DisplayName("insertJsonObject works for surface depth")
    void insertJsonObject_worksForSurfaceDepth() {
        JsonObject json = new JsonObject();
        json.insertJsonObject("key", new JsonObject() {{
            this.putString("nestedKey", "value");
        }});
        String expected = """
                {"nestedKey":"value"}""";
        assertEquals(expected, json.getJsonObject("key").toString(),
                "key 'key' should have the correct JsonObject value"
        );
    }

    @Test
    @DisplayName("insertJsonObject works for nested depth")
    void insertJsonObject_worksForNestedDepth() {
        JsonObject json = new JsonObject() {{
            this.putJsonObject("key", new JsonObject());
        }};
        json.insertJsonObject("key.nestedKey", new JsonObject() {{
            this.putString("anotherKey", "value");
        }});
        String expected = """
                {"anotherKey":"value"}""";
        assertEquals(expected, json.seekJsonObject("key.nestedKey").toString(),
                "key 'key.nestedKey' should have the correct JsonObject value"
        );
    }

    @Test
    @DisplayName("insertJsonArray works for surface depth")
    void insertJsonArray_worksForSurfaceDepth() {
        JsonObject json = new JsonObject();
        json.insertJsonArray("key", new JsonArray() {{
            this.addString("value");
        }});
        String expected = """
                ["value"]""";
        assertEquals(expected, json.getJsonArray("key").toString(),
                "key 'key' should have the correct JsonArray value"
        );
    }

    @Test
    @DisplayName("insertJsonArray works for nested depth")
    void insertJsonArray_worksForNestedDepth() {
        JsonObject json = new JsonObject() {{
            this.putJsonObject("key", new JsonObject());
        }};
        json.insertJsonArray("key.nestedKey", new JsonArray() {{
            this.addString("value");
        }});
        String expected = """
                ["value"]""";
        assertEquals(expected, json.seekJsonArray("key.nestedKey").toString(),
                "key 'key.nestedKey' should have the correct JsonArray value"
        );
    }

    @Test
    @DisplayName("insertString works for surface depth")
    void insertString_worksForSurfaceDepth() {
        JsonObject json = new JsonObject();

        json.insertString("key", "value");
        assertEquals("value", json.getString("key"),
                "key 'key' should have value of 'value'"
        );
    }

    @Test
    @DisplayName("insertString works for nested depth")
    void insertString_worksForNestedDepth() {
        JsonObject json = new JsonObject() {{
            this.putJsonObject("key", new JsonObject());
        }};

        json.insertString("key.nestedKey", "value");
        assertEquals("value", json.seekString("key.nestedKey"),
                "key 'key.nestedKey' should have value of 'value'"
        );
    }

    @Test
    @DisplayName("insertInteger works for surface depth")
    void insertInteger_worksForSurfaceDepth() {
        JsonObject json = new JsonObject();

        json.insertInteger("key", 100);
        assertEquals(100, json.getInteger("key"),
                "key 'key' should have value of 100"
        );
    }

    @Test
    @DisplayName("insertInteger works for nested depth")
    void insertInteger_worksForNestedDepth() {
        JsonObject json = new JsonObject() {{
            this.putJsonObject("key", new JsonObject());
        }};

        json.insertInteger("key.nestedKey", 100);
        assertEquals(100, json.seekInteger("key.nestedKey"),
                "key 'key.nestedKey' should have value of 100"
        );
    }

    @Test
    @DisplayName("insertDouble works for surface depth")
    void insertDouble_worksForSurfaceDepth() {
        JsonObject json = new JsonObject();

        json.insertDouble("key", 123.456);
        assertEquals(123.456, json.getDouble("key"),
                "key 'key' should have value of 123.456"
        );
    }

    @Test
    @DisplayName("insertDouble works for nested depth")
    void insertDouble_worksForNestedDepth() {
        JsonObject json = new JsonObject() {{
            this.putJsonObject("key", new JsonObject());
        }};

        json.insertDouble("key.nestedKey", 123.456);
        assertEquals(123.456, json.seekDouble("key.nestedKey"),
                "key 'key.nestedKey' should have value of 123.456"
        );
    }

    @Test
    @DisplayName("insertBoolean works for surface depth")
    void insertBoolean_worksForSurfaceDepth() {
        JsonObject json = new JsonObject();

        json.insertBoolean("key", true);
        assertTrue(json.getBoolean("key"),
                "key 'key' should have value of true"
        );
    }

    @Test
    @DisplayName("insertBoolean works for nested depth")
    void insertBoolean_worksForNestedDepth() {
        JsonObject json = new JsonObject() {{
            this.putJsonObject("key", new JsonObject());
        }};

        json.insertBoolean("key.nestedKey", true);
        assertTrue(json.seekBoolean("key.nestedKey"),
                "key 'key.nestedKey' should have value of true"
        );
    }

}
