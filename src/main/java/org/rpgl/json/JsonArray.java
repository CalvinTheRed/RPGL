package org.rpgl.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class represents a JSON array and provides several utility methods which make it easier to interface with that
 * data.
 *
 * @author Calvin Withun
 */
public class JsonArray {

    List<Object> data;

    public JsonArray() {
        this.data = new ArrayList<>();
    }

    public JsonArray(List<Object> data) {
        this.data = data;
    }

    public List<Object> asList() {
        return this.data;
    }

    public JsonArray deepClone() {
        JsonArray clone = new JsonArray();
        for (Object element : this.data) {
            if (element instanceof Map map) {
                clone.data.add(new JsonObject(map).deepClone().asMap());
            } else if (element instanceof List list) {
                clone.data.add(new JsonArray(list).deepClone().asList());
            } else {
                clone.data.add(element);
            }
        }
        return clone;
    }

    public int size() {
        return this.data.size();
    }

    // =================================================================================================================
    //  get() convenience methods
    // =================================================================================================================

    public JsonObject getJsonObject(int index) {
        return (this.data.get(index) instanceof Map map) ? new JsonObject(map) : null;
    }

    public JsonObject getJsonObjectMatching(Object... keysAndValues) {
        for (int i = 0; keysAndValues.length % 2 == 0 && i < this.data.size(); i++) { // odd length of args returns null
            JsonObject jsonObject = this.getJsonObject(i);
            boolean requirementFailed = false;
            for (int kv = 0; kv < keysAndValues.length; kv += 2) {
                String key = (String) keysAndValues[kv];
                Object value = keysAndValues[kv + 1];
                if (!jsonObject.data.get(key).equals(value)) {
                    requirementFailed = true;
                    break;
                }
            }
            if (!requirementFailed) {
                return jsonObject;
            }
        }
        return null;
    }

    public JsonArray getJsonArray(int index) {
        return (this.data.get(index) instanceof List list) ? new JsonArray(list) : null;
    }

    public String getString(int index) {
        return (this.data.get(index) instanceof String s) ? s : null;
    }

    public Integer getInteger(int index) {
        return (this.data.get(index) instanceof Integer i) ? i : null;
    }

    public Double getDouble(int index) {
        return (this.data.get(index) instanceof Double d) ? d : null;
    }

    public Boolean getBoolean(int index) {
        return (this.data.get(index) instanceof Boolean b) ? b : null;
    }

    // =================================================================================================================
    //  add() convenience methods
    // =================================================================================================================

    public void addJsonObject(JsonObject jsonObject) {
        this.data.add(jsonObject.asMap());
    }

    public void addJsonArray(JsonArray jsonArray) {
        this.data.add(jsonArray.asList());
    }

    public void addString(String s) {
        this.data.add(s);
    }

    public void addInteger(Integer i) {
        this.data.add(i);
    }

    public void addDouble(Double d) {
        this.data.add(d);
    }

    public void addBoolean(Boolean b) {
        this.data.add(b);
    }

    // =================================================================================================================
    //  inherited method overrides
    // =================================================================================================================

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[");

        for (Object element : this.data) {
            if (element instanceof Map map) {
                stringBuilder.append(new JsonObject(map));
            } else if (element instanceof List list) {
                stringBuilder.append(new JsonArray(list));
            } else if (element instanceof String string) {
                stringBuilder.append('"').append(string).append('"');
            } else {
                stringBuilder.append(element);
            }
            stringBuilder.append(',');
        }

        if (stringBuilder.charAt(stringBuilder.length() - 1) == ',') {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        return stringBuilder.append(']').toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof JsonArray otherJsonArray) {
            return this.data.equals(otherJsonArray.data);
        }
        return false;
    }

}
