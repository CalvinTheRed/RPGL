package org.rpgl.json;

import java.io.Serial;
import java.util.EmptyStackException;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Calvin Withun 10-16-2022
 *
 * <p>
 * The <code>JsonObject</code> class represents json objects. All virtual
 * representations of json objects, such as <code>{"key":"value"}</code>,
 * are implemented as instances of this class. <code>JsonObject</code> is
 * a class derived from <code>ConcurrentHashMap&ltString,Object&gt</code>.
 * </p>
 * <p>
 * JsonObject objects are mutable; after they are constructed, JsonObject
 * objects can be given new key-value pairs, they can have keys deleted
 * from their contents, and they can have their key-value values modified.
 * </p>
 *
 */
public class JsonObject extends ConcurrentHashMap<String, Object>{

    @Serial
    private static final long serialVersionUID = -1586787919457232015L;

    private final StringBuilder stringBuilder = new StringBuilder();

    @Override
    public Object get(Object key) {
        if (key instanceof String) {
            return super.get(key);
        }
        return null;
    }

    @Override
    public Object put(String key, Object value) {
        if (value instanceof String
                || value instanceof Boolean
                || value instanceof Long
                || value instanceof Double
                || value instanceof JsonObject
                || value instanceof JsonArray) {
            return super.put(key, value);
        }
        return null;
    }

    @Override
    public String toString() {
        stringBuilder.delete(0, stringBuilder.length());
        stringBuilder.append('{');
        int i = 0;
        Set<String> keySet = keySet();
        for (String key : keySet) {
            stringBuilder.append("\"").append(key).append("\":");
            Object value = get(key);
            if (value instanceof String) {
                stringBuilder.append("\"").append(value).append("\"");
            } else {
                stringBuilder.append(value);
            }
            if (i < keySet.size() - 1) {
                stringBuilder.append(',');
            }
            i++;
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    public boolean subsetOf(JsonObject other) {
        boolean subset;
        for (String key : keySet()) {
            try {
                Object thisValue = get(key);
                Object otherValue = other.get(key);
                if (thisValue.getClass() != otherValue.getClass()) {
                    // return false if identical keys map to different value types
                    return false;
                }
                if (thisValue instanceof JsonObject) {
                    // recursively check if the value in "this"
                    // is a subset of the value in "other"
                    subset = ((JsonObject) thisValue).subsetOf((JsonObject) otherValue);
                } else if (thisValue instanceof JsonArray) {
                    // return true iff all elements from "this"
                    // are equal to or subsets of items in "other"
                    subset = ((JsonArray) thisValue).subsetOf((JsonArray) otherValue);
                } else {
                    // key maps to a primitive
                    subset = thisValue.equals(otherValue);
                }
            } catch (NullPointerException ex) {
                // other did not have matching key
                return false;
            }
            if (!subset) {
                return false;
            }
        }
        return true;
    }

    public Object seek(String keypath) throws JsonFormatException {
        Object currentData = this;
        Stack<Character> stack = new Stack<>();
        char[] keypathArray = keypath.toCharArray();
        int length = keypathArray.length;
        int beginIndex = 0;
        int currentIndex = beginIndex;

        while (currentIndex < length) {
            char currentChar = keypathArray[currentIndex];
            if (currentIndex == length - 1) {
                String fragment = keypath.substring(beginIndex, currentIndex + 1);
                // ending a key or an index specification
                if (keypathArray[currentIndex] == ']') {
                    // ending an index specification
                    assert currentData instanceof JsonArray;
                    JsonArray currentArray = (JsonArray) currentData;
                    String specification = fragment.substring(1, fragment.length() - 1);
                    // is index specified by content or by number?
                    if (keypathArray[currentIndex - 1] == '}') {
                        // index specified by content
                        JsonObject specificationObject = JsonParser.parseObjectString(specification);
                        for (Object item : currentArray) {
                            if (item instanceof JsonObject && specificationObject.subsetOf((JsonObject) item)) {
                                currentData = item;
                                break;
                            }
                        }
                    } else {
                        // index specified by index
                        currentData = currentArray.get(Integer.parseInt(specification));
                    }
                } else {
                    // ending a key
                    currentData = ((JsonObject) currentData).get(fragment);
                }
            } else if (currentChar == '[' || currentChar == '.') {
                try {
                    if (stack.peek() == '"') {
                        // character is inside a string
                        currentIndex++;
                        continue;
                    } else {
                        if (currentChar == '[') {
                            stack.push(currentChar);
                        }
                    }
                } catch (EmptyStackException ex) {
                    if (currentChar == '[') {
                        stack.push(currentChar);
                    }
                }
                // if stack has previous content then continue
                if (stack.size() > 1) {
                    currentIndex++;
                    continue;
                }
                String fragment = keypath.substring(beginIndex, currentIndex);
                // ending a key or an index specification
                if (keypathArray[currentIndex - 1] == ']') {
                    // ending an index specification
                    assert currentData instanceof JsonArray;
                    JsonArray currentArray = (JsonArray) currentData;
                    String specification = fragment.substring(1, fragment.length() - 1);
                    // is index specified by content or by number?
                    if (keypathArray[currentIndex - 2] == '}') {
                        // index specified by content
                        JsonObject specificationObject = JsonParser.parseObjectString(specification);
                        for (Object item : currentArray) {
                            if (item instanceof JsonObject && specificationObject.subsetOf((JsonObject) item)) {
                                // first match will be used in the case where there are multiple matches
                                currentData = item;
                                break;
                            }
                        }
                    } else {
                        // index specified by index
                        currentData = currentArray.get(Integer.parseInt(specification));
                    }
                } else {
                    // ending a key
                    currentData = ((JsonObject) currentData).get(fragment);
                }
                // increment beginIndex according to [ or .
                if (currentChar == '[') {
                    beginIndex = currentIndex;
                } else {
                    beginIndex = currentIndex + 1;
                }
            } else if (currentChar == '{') {
                try {
                    if (stack.peek() == '"') {
                        // character is inside a string
                        currentIndex++;
                        continue;
                    } else {
                        stack.push(currentChar);
                    }
                } catch (EmptyStackException ex) {
                    stack.push(currentChar);
                }
            } else if (currentChar == '}') {
                try {
                    if (stack.peek() == '"') {
                        // character is inside a string
                        currentIndex++;
                        continue;
                    } else if (stack.peek() == '{') {
                        stack.pop();
                    } else {
                        throw new JsonFormatException("unexpected token '}'");
                    }
                } catch (EmptyStackException ex) {
                    throw new JsonFormatException("unexpected token '}'");
                }
            } else if (currentChar == ']') {
                try {
                    if (stack.peek() == '"') {
                        // character is inside a string
                        currentIndex++;
                        continue;
                    } else if (stack.peek() == '[') {
                        stack.pop();
                    } else {
                        throw new JsonFormatException("unexpected token ']'");
                    }
                } catch (EmptyStackException ex) {
                    throw new JsonFormatException("unexpected token ']'");
                }
            } else if (currentChar == '"') {
                try {
                    if (stack.peek() == '"') {
                        if (keypathArray[currentIndex - 1] == '\\') {
                            // character is inside a string
                            currentIndex++;
                            continue;
                        } else {
                            stack.pop();
                        }
                    } else {
                        stack.push(currentChar);
                    }
                } catch (EmptyStackException ex) {
                    stack.push(currentChar);
                }
            }
            currentIndex++;
        }
        return currentData;
    }

    public void join(JsonObject other) {
        for (String key : other.keySet()) {
            Object value = other.get(key);
            if (value instanceof JsonObject) {
                Object thisValue = get(key);
                if (thisValue instanceof JsonObject) {
                    ((JsonObject) thisValue).join((JsonObject) value);
                } else {
                    put(key, value);
                }
            } else {
                put(key, value);
            }
        }
    }

}