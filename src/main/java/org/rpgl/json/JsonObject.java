package org.rpgl.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a JSON object and provides several utility methods which make it easier to interface with that
 * data.
 *
 * @author Calvin Withun
 */
public class JsonObject {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    Map<String, Object> data;

    public JsonObject() {
        this.data = new HashMap<>();
    }

    public JsonObject(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> asMap() {
        return this.data;
    }

    public JsonObject deepClone() {
        JsonObject clone = new JsonObject();
        for (String key : this.data.keySet()) {
            Object value = this.data.get(key);
            if (value instanceof Map) {
                clone.putJsonObject(key, this.getJsonObject(key).deepClone());
            } else if (value instanceof List) {
                clone.putJsonArray(key, this.getJsonArray(key).deepClone());
            } else {
                clone.data.put(key, value);
            }
        }
        return clone;
    }

    public void join(Map<String, Object> other) {
        this.join(new JsonObject(other));
    }

    public void join(JsonObject other) {
        JsonObject otherClone = other.deepClone();
        for (String otherKey : otherClone.data.keySet()) {
            Object otherValue = otherClone.data.get(otherKey);
            if (otherValue instanceof Map otherMap) {
                Object thisValue = this.data.get(otherKey);
                if (thisValue instanceof Map thisMap) {
                    // nested join if a map is being joined to a map
                    JsonObject thisJsonObject = new JsonObject();
                    thisJsonObject.join(new JsonObject(thisMap));
                    thisJsonObject.join(new JsonObject(otherMap));
                    this.putJsonObject(otherKey, thisJsonObject);
                } else {
                    // override this key if this is not also a map
                    this.data.put(otherKey, otherMap);
                }
            } else if (otherValue instanceof List otherList) {
                Object thisValue = this.data.get(otherKey);
                if (thisValue instanceof List thisList) {
                    // union if a list if being joined to a list
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

    public void putJsonObject(String key, JsonObject jsonObject) {
        this.data.put(key, jsonObject.data);
    }

    public void putJsonArray(String key, JsonArray jsonArray) {
        this.data.put(key, jsonArray.asList());
    }

    public void putString(String key, String s) {
        this.data.put(key, s);
    }

    public void putInteger(String key, Integer i) {
        this.data.put(key, i);
    }

    public void putDouble(String key, Double d) {
        this.data.put(key, d);
    }

    public void putBoolean(String key, Boolean b) {
        this.data.put(key, b);
    }

    // =================================================================================================================
    //  get() convenience methods
    // =================================================================================================================

    public JsonObject getJsonObject(String key) {
        return (this.data.get(key) instanceof Map value) ? new JsonObject((Map) value) : null;
    }

    public JsonArray getJsonArray(String key) {
        return (this.data.get(key) instanceof List value) ? new JsonArray((List) value) : null;
    }

    public String getString(String key) {
        return (this.data.get(key) instanceof String value) ? value : null;
    }

    public Integer getInteger(String key) {
        return (this.data.get(key) instanceof Integer value) ? value : null;
    }

    public Double getDouble(String key) {
        return (this.data.get(key) instanceof Double value) ? value : null;
    }

    public Boolean getBoolean(String key) {
        return (this.data.get(key) instanceof Boolean value) ? value : null;
    }

    // =================================================================================================================
    //  remove() convenience methods
    // =================================================================================================================

    public JsonObject removeJsonObject(String key) {
        JsonObject value = this.getJsonObject(key);
        if (value != null) {
            this.data.remove(key);
            return value;
        }
        return null;
    }

    public JsonArray removeJsonArray(String key) {
        JsonArray value = this.getJsonArray(key);
        if (value != null) {
            this.data.remove(key);
            return value;
        }
        return null;
    }

    public String removeString(String key) {
        String value = this.getString(key);
        if (value != null) {
            this.data.remove(key);
            return value;
        }
        return null;
    }

    public Integer removeInteger(String key) {
        Integer value = this.getInteger(key);
        if (value != null) {
            this.data.remove(key);
            return value;
        }
        return null;
    }

    public Double removeDouble(String key) {
        Double value = this.getDouble(key);
        if (value != null) {
            this.data.remove(key);
            return value;
        }
        return null;
    }

    public Boolean removeBoolean(String key) {
        Boolean value = this.getBoolean(key);
        if (value != null) {
            this.data.remove(key);
            return value;
        }
        return null;
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
            if (value instanceof Map) {
                stringBuilder.append(this.getJsonObject(key).toString());
            } else if (value instanceof List) {
                stringBuilder.append(this.getJsonArray(key).toString());
            } else if (value instanceof String string) {
                stringBuilder.append('"').append(string).append('"');
            } else {
                stringBuilder.append(value.toString());
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
            return this.data.equals(otherJsonObject.data);
        }
        return false;
    }

}
