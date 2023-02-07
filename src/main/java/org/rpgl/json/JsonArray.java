package org.rpgl.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * This class represents a JSON array and provides several utility methods which make it easier to interface with that
 * data.
 *
 * @author Calvin Withun
 */
public class JsonArray {

    ArrayList<Object> data;

    public JsonArray() {
        this.data = new ArrayList<>();
    }

    public JsonArray(ArrayList<Object> data) {
        this.data = Objects.requireNonNullElse(data, new ArrayList<>());
    }

    public ArrayList<Object> asList() {
        return this.data;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public JsonArray deepClone() {
        JsonArray clone = new JsonArray();
        for (Object element : this.data) {
            if (element instanceof HashMap map) {
                clone.addJsonObject(new JsonObject(map).deepClone());
            } else if (element instanceof ArrayList list) {
                clone.addJsonArray(new JsonArray(list).deepClone());
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

    @SuppressWarnings({"rawtypes", "unchecked"})
    public JsonObject getJsonObject(int index) {
        return (this.data.get(index) instanceof HashMap map) ? new JsonObject(map) : null;
    }

    @SuppressWarnings({"rawtypes", "unchecked", ""})
    public JsonObject getJsonObjectMatching(Object... keysAndValues) {
        if (keysAndValues.length %2 == 0) { // malformed parameter
            for (int i = 0; i < this.size(); i++) {
                JsonObject listedJsonObject = this.getJsonObject(i);
                if (listedJsonObject != null) {
                    boolean comparisonFailed = false;
                    for (int kvi = 0; kvi < keysAndValues.length; kvi += 2) {
                        if (!(keysAndValues[kvi] instanceof String)) {
                            comparisonFailed = true;
                            break;
                        }
                        @SuppressWarnings("all")
                        String key = (String) keysAndValues[kvi];
                        Object value = keysAndValues[kvi + 1];
                        Object listedObjectKeyValue = listedJsonObject.asMap().get(key);
                        if (listedObjectKeyValue == null) {
                            comparisonFailed = true;
                            break;
                        } else if (listedObjectKeyValue instanceof HashMap map) {
                            if (!new JsonObject(map).equals(value)) {
                                comparisonFailed = true;
                                break;
                            }
                        } else if (listedObjectKeyValue instanceof ArrayList list) {
                            if (!new JsonArray(list).equals(value)) {
                                comparisonFailed = true;
                                break;
                            }
                        } else {
                            if (!listedObjectKeyValue.equals(value)) {
                                comparisonFailed = true;
                                break;
                            }
                        }
                    }
                    if (!comparisonFailed) {
                        return listedJsonObject;
                    }
                }
            }
        }
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public JsonArray getJsonArray(int index) {
        return (this.data.get(index) instanceof ArrayList list) ? new JsonArray(list) : null;
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
    @SuppressWarnings({"rawtypes", "unchecked"})
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[");

        for (Object element : this.data) {
            if (element instanceof HashMap map) {
                stringBuilder.append(new JsonObject(map));
            } else if (element instanceof ArrayList list) {
                stringBuilder.append(new JsonArray(list));
            } else if (element instanceof String string) {
                stringBuilder.append('"').append(string).append('"');
            } else if (element == null) {
                stringBuilder.append("null");
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
