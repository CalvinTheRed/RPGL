package org.rpgl.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rpgl.exception.JsonObjectSeekException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonObject extends HashMap<String, Object> {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    public static JsonObject mapToJsonObject(Map<String, Object> map) {
        JsonObject returnObject = new JsonObject();
        returnObject.putAll(map);
        return returnObject;
    }

    public static String mapToString(Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder("{");
        for (String key : map.keySet()) {
            stringBuilder.append("\"").append(key).append("\":");
            Object value = map.get(key);
            if (value instanceof Map nestedMap) {
                stringBuilder.append(JsonObject.mapToString(nestedMap));
            } else if (value instanceof List nestedList) {
                stringBuilder.append(JsonObject.listToString(nestedList));
            } else {
                stringBuilder.append(value.toString());
            }
            stringBuilder.append(',');
        }
        return stringBuilder
                .deleteCharAt(stringBuilder.length() - 1)
                .append('}')
                .toString();
    }

    public static String listToString(List<Object> list) {
        StringBuilder stringBuilder = new StringBuilder("[");
        for (Object element : list) {
            if (element instanceof Map mapElement) {
                stringBuilder.append(JsonObject.mapToString(mapElement));
            } else if (element instanceof List listElement) {
                stringBuilder.append(JsonObject.listToString(listElement));
            } else {
                stringBuilder.append(element.toString());
            }
            stringBuilder.append(',');
        }
        return stringBuilder
                .deleteCharAt(stringBuilder.length() - 1)
                .append(']')
                .toString();
    }

    public static Map<String, Object> deepClone(Map<String, Object> map) {
        Map<String, Object> clone = new HashMap<>();
        for (String key : map.keySet()) {
            Object value = map.get(key);
            if (value instanceof Map nestedMap) {
                clone.put(key, JsonObject.deepClone(nestedMap));
            } else if (value instanceof List nestedList) {
                clone.put(key, JsonObject.deepClone(nestedList));
            } else {
                clone.put(key, value);
            }
        }
        return clone;
    }

    public static List<Object> deepClone(List<Object> list) {
        List<Object> clone = new ArrayList<>();
        for (Object element : list) {
            if (element instanceof Map mapElement) {
                clone.add(JsonObject.deepClone(mapElement));
            } else if (element instanceof List listElement) {
                clone.add(JsonObject.deepClone(listElement));
            } else {
                clone.add(element);
            }
        }
        return clone;
    }

    public Object seek(String keyPath) throws JsonObjectSeekException {
        // TODO
        return null;
    }

    public void join(Map<String, Object> other) {
        for (String otherKey : other.keySet()) {
            Object otherValue = other.get(otherKey);
            if (otherValue instanceof Map otherMap) {
                Object thisValue = this.get(otherKey);
                if (thisValue instanceof Map thisMap) {
                    // nested join if a map is being joined to a map
                    JsonObject thisJsonObject = new JsonObject();
                    thisJsonObject.join(thisMap);
                    thisJsonObject.join(otherMap);
                    this.put(otherKey, thisJsonObject);
                } else {
                    // override this key if this is not also a map
                    this.put(otherKey, otherMap);
                }
            } else if (otherValue instanceof List otherList) {
                Object thisValue = this.get(otherKey);
                if (thisValue instanceof List thisList) {
                    // union if a list if being joined to a list
                    for (Object element : otherList) {
                        if (!thisList.contains(element)) {
                            thisList.add(element);
                        }
                    }
                } else {
                    // override this key if this is not also a list
                    this.put(otherKey, otherList);
                }
            } else {
                // override any primitives being joined
                this.put(otherKey, otherValue);
            }
        }
    }

    /*
     * get() helper methods
     */

    public Map<String, Object> getMap(String key) {
        return (this.get(key) instanceof Map value) ? value : null;
    }

    public List<Object> getList(String key) {
        return (this.get(key) instanceof List value) ? value : null;
    }

    public String getString(String key) {
        return (this.get(key) instanceof String value) ? value : null;
    }

    public Integer getInteger(String key) {
        return (this.get(key) instanceof Integer value) ? value : null;
    }

    public Double getDouble(String key) {
        return (this.get(key) instanceof Double value) ? value : null;
    }

    public Boolean getBoolean(String key) {
        return (this.get(key) instanceof Boolean value) ? value : null;
    }

    /*
     * seek() helper methods
     */

    public Map<String, Object> seekMap(String keyPath) {
        try {
            return (this.seek(keyPath) instanceof Map value) ? value : null;
        } catch (JsonObjectSeekException e) {
            // TODO log the error
            return null;
        }
    }

    public List<Object> seekList(String keyPath) {
        try {
            return (this.seek(keyPath) instanceof List value) ? value : null;
        } catch (JsonObjectSeekException e) {
            // TODO log the error
            return null;
        }
    }

    public String seekString(String keyPath) {
        try {
            return (this.seek(keyPath) instanceof String value) ? value : null;
        } catch (JsonObjectSeekException e) {
            // TODO log the error
            return null;
        }
    }

    public Integer seekInteger(String keyPath) {
        try {
            return (this.seek(keyPath) instanceof Integer value) ? value : null;
        } catch (JsonObjectSeekException e) {
            // TODO log the error
            return null;
        }
    }

    public Double seekDouble(String keyPath) {
        try {
            return (this.seek(keyPath) instanceof Double value) ? value : null;
        } catch (JsonObjectSeekException e) {
            // TODO log the error
            return null;
        }
    }

    public Boolean seekBoolean(String keyPath) {
        try {
            return (this.seek(keyPath) instanceof Boolean value) ? value : null;
        } catch (JsonObjectSeekException e) {
            // TODO log the error
            return null;
        }
    }

}
