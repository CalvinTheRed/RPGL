package org.rpgl.json;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class for the org.rpgl.json.JsonArray class.
 *
 * @author Calvin Withun
 */
public class JsonArrayTest {

    /*
    The JsonObject used for testing add(... ) and get(...) methods
     */
    private JsonArray addGet_jsonArray;

    @BeforeEach
    void beforeEach() {
        // add, get
        addGet_jsonArray = new JsonArray();
    }

    @Test
    @DisplayName("toString: comprehensive unit test")
    void toString_test() {
        JsonArray toStringJsonArray = new JsonArray() {{
            this.addJsonObject(new JsonObject());
            this.addJsonArray(new JsonArray());
            this.addString("value");
            this.addInteger(123);
            this.addDouble(123.456);
            this.addBoolean(false);
        }};

        String expected = """
                [{},[],"value",123,123.456,false]""";

        assertEquals(expected, toStringJsonArray.toString(),
                ""
        );
    }

    @Test
    @DisplayName("add, get: JsonObject elements")
    void addGet_JsonObject() {
        JsonObject element = new JsonObject() {{
            this.putString("key", "value");
        }};
        addGet_jsonArray.addJsonObject(element);
        assertTrue(addGet_jsonArray.data.get(0) instanceof Map,
                "adding a JsonObject should place a Map in the JsonArray"
        );
        assertEquals(element, addGet_jsonArray.getJsonObject(0),
                "the original element should be returned upon calling getJsonObject(...)"
        );
    }

    @Test
    @DisplayName("add, get: getJsonObjectMatching")
    void addGet_getJsonObjectMatching() {
        JsonObject element1 = new JsonObject() {{
            this.putJsonObject("object_key", new JsonObject());
        }};
        JsonObject element2 = new JsonObject() {{
            this.putJsonObject("object_key", new JsonObject());
            this.putJsonArray("array_key", new JsonArray());
        }};
        JsonObject element3 = new JsonObject() {{
            this.putJsonObject("object_key", new JsonObject());
            this.putJsonArray("array_key", new JsonArray());
            this.putString("string_key", "element");
        }};
        JsonObject element4 = new JsonObject() {{
            this.putJsonObject("object_key", new JsonObject());
            this.putJsonArray("array_key", new JsonArray());
            this.putString("string_key", "element");
            this.putInteger("integer_key", 123);
        }};
        JsonObject element5 = new JsonObject() {{
            this.putJsonObject("object_key", new JsonObject());
            this.putJsonArray("array_key", new JsonArray());
            this.putString("string_key", "element");
            this.putInteger("integer_key", 123);
            this.putDouble("double_key", 123.456);
        }};
        JsonObject element6 = new JsonObject() {{
            this.putJsonObject("object_key", new JsonObject());
            this.putJsonArray("array_key", new JsonArray());
            this.putString("string_key", "element");
            this.putInteger("integer_key", 123);
            this.putDouble("double_key", 123.456);
            this.putBoolean("boolean_key", false);
        }};
        JsonObject element7 = new JsonObject() {{
            this.putJsonObject("object_key", new JsonObject());
            this.putJsonArray("array_key", new JsonArray());
            this.putString("string_key", "element");
            this.putInteger("integer_key", 123);
            this.putDouble("double_key", 123.456);
            this.putBoolean("boolean_key", false);
        }};
        addGet_jsonArray.addJsonObject(element1);
        addGet_jsonArray.addJsonObject(element2);
        addGet_jsonArray.addJsonObject(element3);
        addGet_jsonArray.addJsonObject(element4);
        addGet_jsonArray.addJsonObject(element5);
        addGet_jsonArray.addJsonObject(element6);
        addGet_jsonArray.addJsonObject(element7);
        assertEquals(element1, addGet_jsonArray.getJsonObjectMatching(
                        "object_key", new JsonObject()
                ),
                ""
        );
        assertEquals(element2, addGet_jsonArray.getJsonObjectMatching(
                        "object_key", new JsonObject(),
                        "array_key", new JsonArray()
                ),
                ""
        );
        assertEquals(element3, addGet_jsonArray.getJsonObjectMatching(
                        "object_key", new JsonObject(),
                        "array_key", new JsonArray(),
                        "string_key", "element"
                ),
                ""
        );
        assertEquals(element4, addGet_jsonArray.getJsonObjectMatching(
                        "object_key", new JsonObject(),
                        "array_key", new JsonArray(),
                        "string_key", "element",
                        "integer_key", 123
                ),
                ""
        );
        assertEquals(element5, addGet_jsonArray.getJsonObjectMatching(
                        "object_key", new JsonObject(),
                        "array_key", new JsonArray(),
                        "string_key", "element",
                        "integer_key", 123,
                        "double_key", 123.456
                ),
                ""
        );
        assertEquals(element6, addGet_jsonArray.getJsonObjectMatching(
                        "object_key", new JsonObject(),
                        "array_key", new JsonArray(),
                        "string_key", "element",
                        "integer_key", 123,
                        "double_key", 123.456,
                        "boolean_key", false
                ),
                ""
        );
    }

    @Test
    @DisplayName("add, get: JsonArray elements")
    void addGet_JsonArray() {
        JsonArray element = new JsonArray() {{
            this.addString("value");
        }};
        addGet_jsonArray.addJsonArray(element);
        assertTrue(addGet_jsonArray.data.get(0) instanceof ArrayList,
                "adding a JsonArray should place a ArrayList in the JsonArray"
        );
        assertEquals(element, addGet_jsonArray.getJsonArray(0),
                "the original element should be returned upon calling getJsonArray(...)"
        );
    }

    @Test
    @DisplayName("add, get: String elements")
    void addGet_String() {
        String element = "value";
        addGet_jsonArray.addString(element);
        assertTrue(addGet_jsonArray.data.get(0) instanceof String,
                "adding a String should place a String in the JsonArray"
        );
        assertEquals(element, addGet_jsonArray.getString(0),
                "the original element should be returned upon calling getString(...)"
        );
    }

    @Test
    @DisplayName("add, get: Integer elements")
    void addGet_Integer() {
        Integer element = 123;
        addGet_jsonArray.addInteger(element);
        assertTrue(addGet_jsonArray.data.get(0) instanceof Integer,
                "adding a Integer should place a Integer in the JsonArray"
        );
        assertEquals(element, addGet_jsonArray.getInteger(0),
                "the original element should be returned upon calling getInteger(...)"
        );
    }

    @Test
    @DisplayName("add, get: Double elements")
    void addGet_Double() {
        Double element = 123.456;
        addGet_jsonArray.addDouble(element);
        assertTrue(addGet_jsonArray.data.get(0) instanceof Double,
                "adding a Double should place a Double in the JsonArray"
        );
        assertEquals(element, addGet_jsonArray.getDouble(0),
                "the original element should be returned upon calling getDouble(...)"
        );
    }

    @Test
    @DisplayName("add, get: Boolean elements")
    void addGet_Boolean() {
        Boolean element = false;
        addGet_jsonArray.addBoolean(element);
        assertTrue(addGet_jsonArray.data.get(0) instanceof Boolean,
                "adding a Boolean should place a Boolean in the JsonArray"
        );
        assertEquals(element, addGet_jsonArray.getBoolean(0),
                "the original element should be returned upon calling getBoolean(...)"
        );
    }

    @Test
    @DisplayName("deepClone: comprehensive unit test")
    void deepClone_test() {
        /*
        set up original JsonArray to be cloned
         */
        JsonArray original = new JsonArray() {{
            this.addJsonObject(new JsonObject());
            this.addJsonArray(new JsonArray());
            this.addString("value");
            this.addInteger(123);
            this.addDouble(123.456);
            this.addBoolean(false);
        }};

        /*
        create clone
         */
        JsonArray clone = original.deepClone();

        /*
        modify original JsonArray
         */
        original.getJsonObject(0).putString("string_key", "another_value");
        original.asList().clear();
        original.addString("yet_another_value");

        /*
        verify changes did not affect clone
         */
        assertEquals(6, clone.size(),
                "adding or removing elements in the original should not impact the clone"
        );
        assertTrue(clone.getJsonObject(0).asMap().isEmpty(),
                "modifying values or the original should not impact the values in the clone"
        );
    }

    @Test
    @DisplayName("containsAny returns true (contains exactly one element)")
    void containsAny_returnsTrue_containsExactlyOneElement() {
        JsonArray base = new JsonArray() {{
           this.addString("string 2");
        }};
        JsonArray other = new JsonArray() {{
            this.addString("string 1");
            this.addString("string 2");
            this.addString("string 3");
        }};

        assertTrue(base.containsAny(other.asList()),
                "method should return true since base contains 1 of the other list's elements"
        );
    }

    @Test
    @DisplayName("containsAny returns true (contains multiple elements)")
    void containsAny_returnsTrue_containsMultipleElements() {
        JsonArray base = new JsonArray() {{
            this.addString("string 1");
            this.addString("string 2");
        }};
        JsonArray other = new JsonArray() {{
            this.addString("string 1");
            this.addString("string 2");
            this.addString("string 3");
        }};

        assertTrue(base.containsAny(other.asList()),
                "method should return true since base contains multiple of the other list's elements"
        );
    }

    @Test
    @DisplayName("containsAny returns false (contains no matching elements)")
    void containsAny_returnsFalse_containsNoMatchingElements() {
        JsonArray base = new JsonArray() {{
            this.addString("string 4");
        }};
        JsonArray other = new JsonArray() {{
            this.addString("string 1");
            this.addString("string 2");
            this.addString("string 3");
        }};

        assertFalse(base.containsAny(other.asList()),
                "method should return false since base contains none of the other list's elements"
        );
    }

    @Test
    @DisplayName("containsAny returns false (JsonArray is empty)")
    void containsAny_returnsFalse_JsonArrayIsEmpty() {
        JsonArray base = new JsonArray();
        JsonArray other = new JsonArray() {{
            this.addString("string 1");
            this.addString("string 2");
            this.addString("string 3");
        }};

        assertFalse(base.containsAny(other.asList()),
                "method should return false since base is empty"
        );
    }

    @Test
    @DisplayName("containsAny returns false (JsonArray is empty)")
    void containsAny_returnsFalse_otherIsEmpty() {
        JsonArray base = new JsonArray() {{
            this.addString("string 1");
        }};
        JsonArray other = new JsonArray();

        assertFalse(base.containsAny(other.asList()),
                "method should return false since other is empty"
        );
    }

    @Test
    @DisplayName("containsAny returns false (JsonArray and other are empty)")
    void containsAny_returnsFalse_JsonArrayAndOtherAreEmpty() {
        JsonArray base = new JsonArray();
        JsonArray other = new JsonArray();

        assertFalse(base.containsAny(other.asList()),
                "method should return false since other is empty"
        );
    }

    @Test
    @DisplayName("pretty prints (primitives)")
    void prettyPrints_primitives() {
        JsonArray json = new JsonArray() {{
            this.addString("element");
            this.addInteger(10);
            this.addDouble(123.456);
            this.addBoolean(false);
        }};

        String expected = """
                [ "element", 10, 123.456, false ]""";
        assertEquals(expected, json.prettyPrint(),
                ""
        );
    }

    @Test
    @DisplayName("pretty prints (objects)")
    void prettyPrints_objects() {
        JsonArray json = new JsonArray() {{
            this.addJsonObject(new JsonObject() {{
                this.putString("key1", "value1");
                this.putString("key2", "value2");
            }});
            this.addJsonObject(new JsonObject() {{
                this.putString("key1", "value1");
                this.putString("key2", "value2");
            }});
        }};

        String expected = """
                [ {
                  "key1" : "value1",
                  "key2" : "value2"
                }, {
                  "key1" : "value1",
                  "key2" : "value2"
                } ]""";
        assertEquals(expected, json.prettyPrint(),
                ""
        );
    }

}
