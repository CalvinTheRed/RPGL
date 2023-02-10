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
     * 	<p><b><i>asList</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public ArrayList&lt;Object&gt; asList()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Returns the data encapsulated by this object.
     * 	</p>
     *
     * 	@return an ArrayList
     */
    public ArrayList<Object> asList() {
        return this.data;
    }

    /**
     * 	<p><b><i>deepClone</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public JsonArray deepClone()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Returns a deep clone of this object. The clone contains an exact copy of the contents of this object, but making
     * 	changes to the clone or its contents will not impact this object.
     * 	</p>
     *
     * 	@return a JsonArray
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
     * 	<p><b><i>size</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public int size()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Returns the number of elements in the encapsulated list.
     * 	</p>
     *
     * 	@return the number of elements in the encapsulated list
     */
    public int size() {
        return this.data.size();
    }

    // =================================================================================================================
    //  get() convenience methods
    // =================================================================================================================

    /**
     * 	<p><b><i>getJsonObject</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public JsonObject getJsonObject(int index)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Returns the JsonObject stored at the passed index, or null if the stored element is of a different type.
     * 	</p>
     *
     * 	@return a JsonObject or null
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public JsonObject getJsonObject(int index) {
        return (this.data.get(index) instanceof HashMap map) ? new JsonObject(map) : null;
    }

    /**
     * 	<p><b><i>getJsonObject</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public JsonObject getJsonObjectMatching(Object... keysAndValues)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Returns the first JsonObject element stored in the encapsulated list whose contents match the passed keys and
     * 	values, or null if no matching elements are found. The parameter for this list should be in sets of two: a json
     * 	"key" followed by a json "value". If the parameter is not evenly sized, this method will return null. If any
     * 	"key" element in the parameter is not a String, this method will return null.
     * 	</p>
     *
     * 	@return a JsonObject or null
     *
     * 	TODO consider having a keys parameter and a values parameter? More readable and intuitive that way...
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

    /**
     * 	<p><b><i>getJsonArray</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public JsonArray getJsonArray(int index)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Returns the JsonArray stored at the passed index, or null if the stored element is of a different type.
     * 	</p>
     *
     * 	@return a JsonArray or null
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public JsonArray getJsonArray(int index) {
        return (this.data.get(index) instanceof ArrayList list) ? new JsonArray(list) : null;
    }

    /**
     * 	<p><b><i>getString</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public String getString(int index)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Returns the String stored at the passed index, or null if the stored element is of a different type.
     * 	</p>
     *
     * 	@return a String or null
     */
    public String getString(int index) {
        return (this.data.get(index) instanceof String s) ? s : null;
    }

    /**
     * 	<p><b><i>getInteger</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public Integer getInteger(int index)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Returns the Integer stored at the passed index, or null if the stored element is of a different type.
     * 	</p>
     *
     * 	@return a Integer or null
     */
    public Integer getInteger(int index) {
        return (this.data.get(index) instanceof Integer i) ? i : null;
    }

    /**
     * 	<p><b><i>getDouble</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public Double getDouble(int index)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Returns the Double stored at the passed index, or null if the stored element is of a different type.
     * 	</p>
     *
     * 	@return a Double or null
     */
    public Double getDouble(int index) {
        return (this.data.get(index) instanceof Double d) ? d : null;
    }

    /**
     * 	<p><b><i>getBoolean</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public Boolean getBoolean(int index)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Returns the Boolean stored at the passed index, or null if the stored element is of a different type.
     * 	</p>
     *
     * 	@return a Boolean or null
     */
    public Boolean getBoolean(int index) {
        return (this.data.get(index) instanceof Boolean b) ? b : null;
    }

    // =================================================================================================================
    //  add() convenience methods
    // =================================================================================================================

    /**
     * 	<p><b><i>addJsonObject</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void addJsonObject(JsonObject jsonObject)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Appends the passed JsonObject (as a map) to the encapsulated list.
     * 	</p>
     *
     * 	@param jsonObject a JsonObject
     */
    public void addJsonObject(JsonObject jsonObject) {
        this.data.add(jsonObject.asMap());
    }

    /**
     * 	<p><b><i>addJsonArray</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void addJsonArray(JsonArray jsonArray)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Appends the passed JsonArray (as a list) to the encapsulated list.
     * 	</p>
     *
     * 	@param jsonArray a JsonArray
     */
    public void addJsonArray(JsonArray jsonArray) {
        this.data.add(jsonArray.asList());
    }

    /**
     * 	<p><b><i>addString</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void addString(String s)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Appends the passed String to the encapsulated list.
     * 	</p>
     *
     * 	@param s a String
     */
    public void addString(String s) {
        this.data.add(s);
    }

    /**
     * 	<p><b><i>addInteger</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void addInteger(Integer i)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Appends the passed Integer to the encapsulated list.
     * 	</p>
     *
     * 	@param i a Integer
     */
    public void addInteger(Integer i) {
        this.data.add(i);
    }

    /**
     * 	<p><b><i>addDouble</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void addDouble(Double d)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Appends the passed Double to the encapsulated list.
     * 	</p>
     *
     * 	@param d a Double
     */
    public void addDouble(Double d) {
        this.data.add(d);
    }

    /**
     * 	<p><b><i>addBoolean</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public void addBoolean(Boolean b)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Appends the passed Boolean to the encapsulated list.
     * 	</p>
     *
     * 	@param b a Boolean
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
            return this.data.equals(otherJsonArray.data);
        }
        return false;
    }

}
