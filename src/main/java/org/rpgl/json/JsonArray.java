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

    /**
     * Default constructor for JsonArray class.
     */
    public JsonArray() {
        this.data = new ArrayList<>();
    }

    /**
     * Constructor for JsonArray class. This constructor causes the object to encapsulate the passed value (not a deep
     * clone of it).
     *
     * @param data the data to be encapsulated by this object
     */
    public JsonArray(ArrayList<Object> data) {
        this.data = Objects.requireNonNullElse(data, new ArrayList<>());
    }

    /**
     * Returns the data encapsulated by this object.
     *
     * @return an ArrayList
     */
    public ArrayList<Object> asList() {
        return this.data;
    }

    /**
     * Returns a deep clone of this object. The clone contains an exact copy of the contents of this object, but making
     * changes to the clone or its contents will not impact this object.
     *
     * @return a JsonArray
     */
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

    /**
     * Returns the number of elements in the encapsulated list.
     *
     * @return the number of elements in the encapsulated list
     */
    public int size() {
        return this.data.size();
    }

    // =================================================================================================================
    //  get() convenience methods
    // =================================================================================================================

    /**
     * Returns the JsonObject stored at the passed index, or null if the stored element is of a different type.
     *
     * @return a JsonObject or null
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public JsonObject getJsonObject(int index) {
        return (this.data.get(index) instanceof HashMap map) ? new JsonObject(map) : null;
    }

    /**
     * Returns the first JsonObject element stored in the encapsulated list whose contents match the passed keys and
     * values, or null if no matching elements are found. The parameter for this list should be in sets of two: a json
     * "key" followed by a json "value". If the parameter is not evenly sized, this method will return null. If any
     * "key" element in the parameter is not a String, this method will return null.
     *
     * @return a JsonObject or null
     *
     * TODO consider having a keys parameter and a values parameter? More readable and intuitive that way...
     */
    @SuppressWarnings({"rawtypes", "unchecked", ""})
    public JsonObject getJsonObjectMatching(Object... keysAndValues) {
        if (keysAndValues.length %2 == 0) { // malformed parameter
            for (int i = 0; i < this.size(); i++) {
                JsonObject listedJsonObject = this.getJsonObject(i);
                if (listedJsonObject != null) {
                    boolean comparisonFailed = false;
                    for (int kvi = 0; kvi < keysAndValues.length; kvi += 2) {
                        if (!(keysAndValues[kvi] instanceof String)) {
                            return null; // malformed parameter
                        }
                        @SuppressWarnings("all") // silence compiler warning requesting an unintuitive code change
                        String key = (String) keysAndValues[kvi];
                        Object value = keysAndValues[kvi + 1];
                        Object listedObjectKeyValue = listedJsonObject.asMap().get(key);
                        if (listedObjectKeyValue == null) {
                            comparisonFailed = true;
                            break;
                        } else if (listedObjectKeyValue instanceof HashMap map) {
                            if (!Objects.equals(new JsonObject(map), value)) {
                                comparisonFailed = true;
                                break;
                            }
                        } else if (listedObjectKeyValue instanceof ArrayList list) {
                            if (!Objects.equals(new JsonArray(list), value)) {
                                comparisonFailed = true;
                                break;
                            }
                        } else {
                            if (!Objects.equals(listedObjectKeyValue, value)) {
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

    /**
     * Returns the JsonArray stored at the passed index, or null if the stored element is of a different type.
     *
     * @return a JsonArray or null
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public JsonArray getJsonArray(int index) {
        return (this.data.get(index) instanceof ArrayList list) ? new JsonArray(list) : null;
    }

    /**
     * Returns the String stored at the passed index, or null if the stored element is of a different type.
     *
     * @return a String or null
     */
    public String getString(int index) {
        return (this.data.get(index) instanceof String s) ? s : null;
    }

    /**
     * Returns the Integer stored at the passed index, or null if the stored element is of a different type.
     *
     * @return a Integer or null
     */
    public Integer getInteger(int index) {
        return (this.data.get(index) instanceof Integer i) ? i : null;
    }

    /**
     * Returns the Double stored at the passed index, or null if the stored element is of a different type.
     *
     * @return a Double or null
     */
    public Double getDouble(int index) {
        return (this.data.get(index) instanceof Double d) ? d : null;
    }

    /**
     * Returns the Boolean stored at the passed index, or null if the stored element is of a different type.
     *
     * @return a Boolean or null
     */
    public Boolean getBoolean(int index) {
        return (this.data.get(index) instanceof Boolean b) ? b : null;
    }

    // =================================================================================================================
    //  add() convenience methods
    // =================================================================================================================

    /**
     * Appends the passed JsonObject (as a map) to the encapsulated list.
     *
     * @param jsonObject a JsonObject
     */
    public void addJsonObject(JsonObject jsonObject) {
        this.data.add(jsonObject.asMap());
    }

    /**
     * Appends the passed JsonArray (as a list) to the encapsulated list.
     *
     * @param jsonArray a JsonArray
     */
    public void addJsonArray(JsonArray jsonArray) {
        this.data.add(jsonArray.asList());
    }

    /**
     * Appends the passed String to the encapsulated list.
     *
     * @param s a String
     */
    public void addString(String s) {
        this.data.add(s);
    }

    /**
     * Appends the passed Integer to the encapsulated list.
     *
     * @param i a Integer
     */
    public void addInteger(Integer i) {
        this.data.add(i);
    }

    /**
     * Appends the passed Double to the encapsulated list.
     *
     * @param d a Double
     */
    public void addDouble(Double d) {
        this.data.add(d);
    }

    /**
     * Appends the passed Boolean to the encapsulated list.
     *
     * @param b a Boolean
     */
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
            return Objects.equals(this.data, otherJsonArray.data);
        }
        return false;
    }

}
