package org.rpgl.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

/**
 * This class represents a JSON object and provides several utility methods which make it easier to interface with that
 * data.
 *
 * @author Calvin Withun
 */
public class JsonObject {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    static final String INDENT = "  ";

    HashMap<String, Object> data;

    /**
     * Default constructor for JsonObject class.
     */
    public JsonObject() {
        this.data = new HashMap<>();
    }

    /**
     * Constructor for JsonObject class. This constructor causes the object to encapsulate the passed value (not a deep
     * clone of it).
     *
     * @param data the data to be encapsulated by this object
     */
    public JsonObject(HashMap<String, Object> data) {
        this.data = Objects.requireNonNullElse(data, new HashMap<>());
    }

    /**
     * Returns the data encapsulated by this object.
     *
     * @return a HashMap
     */
    public HashMap<String, Object> asMap() {
        return this.data;
    }

    /**
     * Returns a deep clone of this object. The clone contains an exact copy of the contents of this object, but making
     * changes to the clone or its contents will not impact this object.
     *
     * @return a JsonObject
     */
    public JsonObject deepClone() {
        JsonObject clone = new JsonObject();
        for (String key : this.data.keySet()) {
            Object value = this.data.get(key);
            if (value instanceof HashMap) {
                clone.putJsonObject(key, this.getJsonObject(key).deepClone());
            } else if (value instanceof ArrayList) {
                clone.putJsonArray(key, this.getJsonArray(key).deepClone());
            } else {
                clone.data.put(key, value);
            }
        }
        return clone;
    }

    /**
     * This method modifies this object to be a nested union of itself and a deep clone of the parameter map. The value
     * of the parameter map takes priority if a key collision occurs, unless both this and the other map store nested
     * JsonObjects (HashMaps) there. In that case, a recursive call to <code>join()</code> is made.
     *
     * @param other a HashMap representing a JsonObject
     */
    public void join(HashMap<String, Object> other) {
        this.join(new JsonObject(other));
    }

    /**
     * This method modifies this object to be a nested union of itself and a deep clone of the parameter JsonObject.
     * The value of the parameter JsonObject takes priority if a key collision occurs, unless both this and the other
     * JsonObject store nested JsonObjects (HashMaps) there. In that case, a recursive call to <code>join()</code> is
     * made.
     *
     * @param other a JsonObject
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void join(JsonObject other) {
        JsonObject otherClone = other.deepClone();
        for (String otherKey : otherClone.data.keySet()) {
            Object otherValue = otherClone.data.get(otherKey);
            if (otherValue instanceof HashMap otherMap) {
                Object thisValue = this.data.get(otherKey);
                if (thisValue instanceof HashMap thisMap) {
                    // nested join if a map is being joined to a map
                    JsonObject thisJsonObject = new JsonObject();
                    thisJsonObject.join(new JsonObject(thisMap));
                    thisJsonObject.join(new JsonObject(otherMap));
                    this.putJsonObject(otherKey, thisJsonObject);
                } else {
                    // override this key if this is not also a map
                    this.data.put(otherKey, otherMap);
                }
            } else if (otherValue instanceof ArrayList otherList) {
                Object thisValue = this.data.get(otherKey);
                if (thisValue instanceof ArrayList thisList) {
                    // union if a list is being joined to a list
                    for (Object element : otherList) {
                        if (!thisList.contains(element)) {
                            thisList.add(element);
                        }
                    }
                } else {
                    // override this key if this is not also a list
                    this.data.put(otherKey, otherList);
                }
            } else {
                // override any primitives being joined
                this.data.put(otherKey, otherValue);
            }
        }
    }

    // =================================================================================================================
    //  put() convenience methods
    // =================================================================================================================

    /**
     * This method maps the given JsonObject to the given key, if it is not null.
     *
     * @param key a String representing a json object key
     * @param jsonObject a JsonObject
     */
    public void putJsonObject(String key, JsonObject jsonObject) {
        if (key != null) {
            this.data.put(key, jsonObject.data);
        }
    }

    /**
     * This method maps the given JsonArray to the given key, if it is not null.
     *
     * @param key a String representing a json object key
     * @param jsonArray a JsonArray
     */
    public void putJsonArray(String key, JsonArray jsonArray) {
        if (key != null) {
            this.data.put(key, jsonArray.asList());
        }
    }

    /**
     * This method maps the given String to the given key, if it is not null.
     *
     * @param key a String representing a json object key
     * @param s a String
     */
    public void putString(String key, String s) {
        if (key != null) {
            this.data.put(key, s);
        }
    }

    /**
     * This method maps the given Integer to the given key, if it is not null.
     *
     * @param key a String representing a json object key
     * @param i a Integer
     */
    public void putInteger(String key, Integer i) {
        if (key != null) {
            this.data.put(key, i);
        }
    }

    /**
     * This method maps the given Double to the given key, if it is not null.
     *
     * @param key a String representing a json object key
     * @param d a Double
     */
    public void putDouble(String key, Double d) {
        if (key != null) {
            this.data.put(key, d);
        }
    }

    /**
     * This method maps the given Boolean to the given key, if it is not null.
     *
     * @param key a String representing a json object key
     * @param b a Boolean
     */
    public void putBoolean(String key, Boolean b) {
        if (key != null) {
            this.data.put(key, b);
        }
    }

    // =================================================================================================================
    //  get() convenience methods
    // =================================================================================================================

    /**
     * This method returns a JsonObject mapped to the given key, or null if the value mapped there is of a different
     *
     * @param key a String representing a json object key
     * @return a JsonObject or null
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public JsonObject getJsonObject(String key) {
        return (this.data.get(key) instanceof HashMap value) ? new JsonObject((HashMap) value) : null;
    }

    /**
     * This method returns a JsonArray mapped to the given key, or null if the value mapped there is of a different
     *
     * @param key a String representing a json object key
     * @return a JsonArray or null
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public JsonArray getJsonArray(String key) {
        return (this.data.get(key) instanceof ArrayList value) ? new JsonArray((ArrayList) value) : null;
    }

    /**
     * This method returns a String mapped to the given key, or null if the value mapped there is of a different
     * type.
     *
     * @param key a String representing a json object key
     * @return a String or null
     */
    public String getString(String key) {
        return (this.data.get(key) instanceof String value) ? value : null;
    }

    /**
     * This method returns a Integer mapped to the given key, or null if the value mapped there is of a different
     *
     * @param key a String representing a json object key
     * @return a Integer or null
     */
    public Integer getInteger(String key) {
        return (this.data.get(key) instanceof Integer value) ? value : null;
    }

    /**
     * This method returns a Double mapped to the given key, or null if the value mapped there is of a different
     *
     * @param key a String representing a json object key
     * @return a Double or null
     */
    public Double getDouble(String key) {
        return (this.data.get(key) instanceof Double value) ? value : null;
    }

    /**
     * This method returns a Boolean mapped to the given key, or null if the value mapped there is of a different
     *
     * @param key a String representing a json object key
     * @return a Boolean or null
     */
    public Boolean getBoolean(String key) {
        return (this.data.get(key) instanceof Boolean value) ? value : null;
    }

    // =================================================================================================================
    //  remove() convenience methods
    // =================================================================================================================

    /**
     * This method removes a JsonObject from the given key mapping, if one is present. If a JsonObject is successfully
     * removed, this method returns it. Otherwise, it returns null.
     *
     * @param key a String representing a json object key
     * @return a JsonObject or null
     */
    public JsonObject removeJsonObject(String key) {
        JsonObject value = this.getJsonObject(key);
        if (value != null) {
            this.data.remove(key);
            return value;
        }
        return null;
    }

    /**
     * This method removes a JsonArray from the given key mapping, if one is present. If a JsonArray is successfully
     * removed, this method returns it. Otherwise, it returns null.
     *
     * @param key a String representing a json object key
     * @return a JsonArray or null
     */
    public JsonArray removeJsonArray(String key) {
        JsonArray value = this.getJsonArray(key);
        if (value != null) {
            this.data.remove(key);
            return value;
        }
        return null;
    }

    /**
     * This method removes a String from the given key mapping, if one is present. If a String is successfully
     * removed, this method returns it. Otherwise, it returns null.
     *
     * @param key a String representing a json object key
     * @return a String or null
     */
    public String removeString(String key) {
        String value = this.getString(key);
        if (value != null) {
            this.data.remove(key);
            return value;
        }
        return null;
    }

    /**
     * This method removes a Integer from the given key mapping, if one is present. If a Integer is successfully
     * removed, this method returns it. Otherwise, it returns null.
     *
     * @param key a String representing a json object key
     * @return a Integer or null
     */
    public Integer removeInteger(String key) {
        Integer value = this.getInteger(key);
        if (value != null) {
            this.data.remove(key);
            return value;
        }
        return null;
    }

    /**
     * This method removes a Double from the given key mapping, if one is present. If a Double is successfully
     * removed, this method returns it. Otherwise, it returns null.
     *
     * @param key a String representing a json object key
     * @return a Double or null
     */
    public Double removeDouble(String key) {
        Double value = this.getDouble(key);
        if (value != null) {
            this.data.remove(key);
            return value;
        }
        return null;
    }

    /**
     * This method removes a Boolean from the given key mapping, if one is present. If a Boolean is successfully
     * removed, this method returns it. Otherwise, it returns null.
     *
     * @param key a String representing a json object key
     * @return a Boolean or null
     */
    public Boolean removeBoolean(String key) {
        Boolean value = this.getBoolean(key);
        if (value != null) {
            this.data.remove(key);
            return value;
        }
        return null;
    }

    // =================================================================================================================
    //  seek methods
    // =================================================================================================================

    /**
     * Returns a JsonObject at the target field path.
     *
     * @param path a field path
     * @return a JsonObject, or null if none exists at the target field path.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public JsonObject seekJsonObject(String path) {
        return this.seek(path) instanceof HashMap value ? new JsonObject(value) : null;
    }

    /**
     * Returns a JsonArray at the target field path.
     *
     * @param path a field path
     * @return a JsonArray, or null if none exists at the target field path.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public JsonArray seekJsonArray(String path) {
        return this.seek(path) instanceof ArrayList value ? new JsonArray(value) : null;
    }

    /**
     * Returns a String at the target field path.
     *
     * @param path a field path
     * @return a String, or null if none exists at the target field path.
     */
    public String seekString(String path) {
        return this.seek(path) instanceof String value ? value : null;
    }

    /**
     * Returns an Integer at the target field path.
     *
     * @param path a field path
     * @return an Integer, or null if none exists at the target field path.
     */
    public Integer seekInteger(String path) {
        return this.seek(path) instanceof Integer value ? value : null;
    }

    /**
     * Returns a Double at the target field path.
     *
     * @param path a field path
     * @return a Double, or null if none exists at the target field path.
     */
    public Double seekDouble(String path) {
        return this.seek(path) instanceof Double value ? value : null;
    }

    /**
     * Returns a Boolean at the target field path.
     *
     * @param path a field path
     * @return a Boolean, or null if none exists at the target field path.
     */
    public Boolean seekBoolean(String path) {
        return this.seek(path) instanceof Boolean value ? value : null;
    }

    /**
     * Returns an Object at the target field path.
     *
     * @param path a field path
     * @return an Object, or null if none exists at the target field path.
     */
    public Object seek(String path) {
        Object focus = this.asMap();
        for (String key : path.split("\\.")) {
            if (key.contains("[")) {
                String arrayKey = key.substring(0, key.indexOf("["));
                focus = ((HashMap<?,?>) focus).get(arrayKey);

                String[] indices = key.substring(key.indexOf("[") + 1, key.length() - 1).split("]\\[");
                for (String index : indices) {
                    focus = ((ArrayList<?>) focus).get(Integer.parseInt(index));
                }
            } else {
                focus = ((HashMap<?,?>) focus).get(key);
            }
        }
        return focus;
    }

    // =================================================================================================================
    //  insert methods
    // =================================================================================================================

    /**
     * Inserts a JsonObject at the target field path.
     *
     * @param path a field path
     * @param jsonObject a JsonObject
     */
    public void insertJsonObject(String path, JsonObject jsonObject) {
        if (path.contains(".")) {
            String relativeRoot = path.substring(0, path.lastIndexOf('.'));
            String key = path.substring(path.lastIndexOf('.') + 1);
            JsonObject object = this.seekJsonObject(relativeRoot);
            object.putJsonObject(key, jsonObject);
        } else {
            this.putJsonObject(path, jsonObject);
        }
    }

    /**
     * Inserts a JsonArray at the target field path.
     *
     * @param path a field path
     * @param jsonArray a JsonArray
     */
    public void insertJsonArray(String path, JsonArray jsonArray) {
        if (path.contains(".")) {
            String relativeRoot = path.substring(0, path.lastIndexOf('.'));
            String key = path.substring(path.lastIndexOf('.') + 1);
            JsonObject object = this.seekJsonObject(relativeRoot);
            object.putJsonArray(key, jsonArray);
        } else {
            this.putJsonArray(path, jsonArray);
        }
    }

    /**
     * Inserts a String at the target field path.
     *
     * @param path a field path
     * @param s a String
     */
    public void insertString(String path, String s) {
        if (path.contains(".")) {
            String relativeRoot = path.substring(0, path.lastIndexOf('.'));
            String key = path.substring(path.lastIndexOf('.') + 1);
            JsonObject object = this.seekJsonObject(relativeRoot);
            object.putString(key, s);
        } else {
            this.putString(path, s);
        }
    }

    /**
     * Inserts an Integer at the target field path.
     *
     * @param path a field path
     * @param i an Integer
     */
    public void insertInteger(String path, Integer i) {
        if (path.contains(".")) {
            String relativeRoot = path.substring(0, path.lastIndexOf('.'));
            String key = path.substring(path.lastIndexOf('.') + 1);
            JsonObject object = this.seekJsonObject(relativeRoot);
            object.putInteger(key, i);
        } else {
            this.putInteger(path, i);
        }
    }

    /**
     * Inserts a Double at the target field path.
     *
     * @param path a field path
     * @param d a Double
     */
    public void insertDouble(String path, Double d) {
        if (path.contains(".")) {
            String relativeRoot = path.substring(0, path.lastIndexOf('.'));
            String key = path.substring(path.lastIndexOf('.') + 1);
            JsonObject object = this.seekJsonObject(relativeRoot);
            object.putDouble(key, d);
        } else {
            this.putDouble(path, d);
        }
    }

    /**
     * Inserts a Boolean at the target field path.
     *
     * @param path a field path
     * @param b a Boolean
     */
    public void insertBoolean(String path, Boolean b) {
        if (path.contains(".")) {
            String relativeRoot = path.substring(0, path.lastIndexOf('.'));
            String key = path.substring(path.lastIndexOf('.') + 1);
            JsonObject object = this.seekJsonObject(relativeRoot);
            object.putBoolean(key, b);
        } else {
            this.putBoolean(path, b);
        }
    }

    /**
     * Generates an easy-to-read String representation of the object.
     *
     * @return a String
     */
    public String prettyPrint() {
        return this.prettyPrint(0);
    }

    /**
     * This recursive helper method is what generates the return value for the public prettyPrint() method.
     *
     * @param indent how deeply indented this iteration of the method is.
     * @return a String
     */
    String prettyPrint(int indent) {
        StringBuilder stringBuilder = new StringBuilder();
        Optional<String> lastElement = this.data.keySet().stream().sorted().reduce((first, second) -> second);
        if (lastElement.isEmpty()) {
            stringBuilder.append("{ }");
        } else {
            stringBuilder.append("{\n");
            this.data.keySet().stream().sorted().forEach(key -> {
                Object value = this.data.get(key);
                stringBuilder.append(JsonObject.INDENT.repeat(Math.max(0, indent + 1)))
                        .append('"').append(key).append("\": ");
                if (value instanceof HashMap) {
                    stringBuilder.append(this.getJsonObject(key).prettyPrint(indent + 1));
                } else if (value instanceof ArrayList) {
                    stringBuilder.append(this.getJsonArray(key).prettyPrint(indent + 1));
                } else if (value instanceof String string) {
                    stringBuilder.append('"').append(string).append('"');
                } else if (value == null) {
                    stringBuilder.append("null");
                } else {
                    stringBuilder.append(value);
                }
                if (!Objects.equals(lastElement.get(), key)) {
                    stringBuilder.append(',');
                }
                stringBuilder.append('\n');
            });
            stringBuilder.append(JsonObject.INDENT.repeat(Math.max(0, indent))).append('}');
        }
        return stringBuilder.toString();
    }

    // =================================================================================================================
    //  inherited method overrides
    // =================================================================================================================

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");

        this.data.keySet().stream().sorted().forEach(key -> {
            stringBuilder.append("\"").append(key).append("\":");

            Object value = this.data.get(key);
            if (value instanceof HashMap) {
                stringBuilder.append(this.getJsonObject(key).toString());
            } else if (value instanceof ArrayList) {
                stringBuilder.append(this.getJsonArray(key).toString());
            } else if (value instanceof String string) {
                stringBuilder.append('"').append(string).append('"');
            } else if (value == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(value);
            }
            stringBuilder.append(',');
        });

        if (stringBuilder.charAt(stringBuilder.length() - 1) == ',') {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        return stringBuilder.append('}').toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof JsonObject otherJsonObject) {
            return Objects.equals(this.data, otherJsonObject.data);
        }
        return false;
    }

}
