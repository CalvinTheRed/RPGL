package org.rpgl.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.EmptyStackException;
import java.util.Scanner;
import java.util.Stack;

/**
 *
 * @author Calvin Withun 10-16-2022
 *
 * <p>
 * The <code>JsonParser</code> class is used to load <code>JsonObject</code> objects from .json files and from Strings
 * containing valid json data. This process ignores whitespace (spaces, tabs, newlines,and carriage returns) which is
 * not located within a String value.
 * </p>
 *
 */
public final class JsonParser {

    /**
     *	<p>
     * 	<b><i>parseObjectFile</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     *  public static JsonObject parseObjectFile(String filepath)
     * 	throws FileNotFoundException, JsonFormatException
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Returns the JSON object represented by the data in the file located at the given file path. A JSON object may
     * 	not include any keys which contain the period ( . ) character.
     * 	</p>
     *
     * 	@param filepath a file path String
     * 	@return the JSON object represented by the data in the file located at the given file path.
     * 	@throws FileNotFoundException if the file does not exist
     * 	@throws JsonFormatException if there is a JSON formatting error
     */
    public static JsonObject parseObjectFile(String filepath) throws FileNotFoundException, JsonFormatException {
        return parseObjectFile(new File(filepath));
    }

    /**
     * 	<p>
     * 	<b><i>parseObjectFile</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     *  public static JsonObject parseObjectFile(File file)
     * 	throws FileNotFoundException, JsonFormatException
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Returns the JSON object represented by the data in the given file. A JSON object may not include any keys which
     * 	contain the period ( . ) character.
     * 	</p>
     *
     * 	@param file a file containing a JSON object to be parsed
     * 	@return the JsonObject represented by the data in the given file.
     * 	@throws FileNotFoundException if the file does not exist
     * 	@throws JsonFormatException if there is a JSON formatting error
     */
    public static JsonObject parseObjectFile(File file) throws FileNotFoundException, JsonFormatException {
        Scanner scanner = new Scanner(file);
        StringBuilder stringBuilder = new StringBuilder();

        while (scanner.hasNextLine()) {
            stringBuilder.append(scanner.nextLine());
        }
        scanner.close();
        return parseObjectString(stringBuilder.toString());
    }

    /**
     * 	<p>
     * 	<b><i>parseObjectString</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     *  public static JsonObject parseObjectString(String data)
     * 	throws JsonFormatException
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Returns the JSON object represented in the given String. A JSON object may not include any keys which contain
     * 	the period ( . ) character.
     * 	</p>
     *
     * 	@param data a String representation of a JSON object
     * 	@return the JSON object represented in the given String.
     * 	@throws JsonFormatException if there is a JSON formatting error
     */
    public static JsonObject parseObjectString(String data) throws JsonFormatException {
        return constructJsonObject(removeWhitespace(data));
    }

    /**
     * 	<p>
     * 	<b><i>parseArrayFile</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     *  public static JsonObject parseArrayFile(File file)
     * 	throws FileNotFoundException, JsonFormatException
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Returns the JSON array represented by the data in the file located at the given file path.
     * 	</p>
     *
     * 	@param filepath a file path String
     * 	@return the JSON array represented by the data in the file located at the given file path.
     * 	@throws FileNotFoundException if the file does not exist
     * 	@throws JsonFormatException if there is a JSON formatting error
     */
    public static JsonArray parseArrayFile(String filepath) throws FileNotFoundException, JsonFormatException {
        return parseArrayFile(new File(filepath));
    }

    /**
     * 	<p>
     * 	<b><i>parseArrayFile</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     *  public static JsonObject parseArrayFile(File file)
     * 	throws FileNotFoundException, JsonFormatException
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Returns the JSON array represented by the data in the given file.
     * 	</p>
     *
     * 	@param file a file containing a JSON array to be parsed
     * 	@return the JSON array represented by the data in the given file.
     * 	@throws FileNotFoundException if the file does not exist
     * 	@throws JsonFormatException if there is a JSON formatting error
     */
    public static JsonArray parseArrayFile(File file) throws FileNotFoundException, JsonFormatException {
        Scanner scanner = new Scanner(file);
        StringBuilder stringBuilder = new StringBuilder();
        while (scanner.hasNextLine()) {
            stringBuilder.append(scanner.nextLine());
        }
        scanner.close();
        return parseArrayString(stringBuilder.toString());
    }

    /**
     * 	<p>
     * 	<b><i>parseArrayString</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     *  public static JsonObject parseArrayString(String data)
     * 	throws JsonFormatException
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Returns the JSON array represented by the given String.
     * 	</p>
     *
     * 	@param data a String representing a JSON array
     * 	@return the JSON array represented by the given String.
     * 	@throws JsonFormatException if there is a JSON formatting error
     */
    public static JsonArray parseArrayString(String data) throws JsonFormatException {
        return constructJsonArray(removeWhitespace(data));
    }

    /**
     * 	<p>
     * 	<b><i>removeWhitespace</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     *  public static String removeWhitespace(String line)
     * 	</pre></code>
     * 	</p>
     * 	<p>
     * 	Returns the String after removing all whitespace characters (' ', '\t', '\n', '\r') not contained within a
     * 	nested String (e.g. "my string is cool" would become "mystringiscool", but "my string is \"very cool\"" would
     * 	become "mystringis\"verycool\"")
     * 	</p>
     *
     * 	@param line a String to be processed
     * 	@return the given String after removing all whitespace characters not contained within a nested String.
     */
    public static String removeWhitespace(String line) {
        Stack<Character> stack = new Stack<>();
        StringBuilder stringBuilder = new StringBuilder();

        char currentChar;
        for (int i = 0; i < line.length(); i++) {
            currentChar = line.charAt(i);
            if (currentChar == '"') {
                try {
                    if (stack.peek() == '"') {
                        if (line.charAt(i - 1) != '\\') {
                            // ending a string (quotes are not escaped)
                            stack.pop();
                        }
                    } else {
                        // starting a string
                        stack.push(currentChar);
                    }
                } catch (EmptyStackException ex) {
                    stack.push(currentChar);
                }
                // currentChar is never whitespace in this case
                stringBuilder.append(currentChar);
            } else if (stack.size() == 0) {
                if (currentChar != ' '
                        && currentChar != '\t'
                        && currentChar != '\n'
                        && currentChar != '\r' ) {
                    // non-whitespace not in a String is accepted
                    stringBuilder.append(currentChar);
                }
            } else {
                // any characters including whitespace in a String is accepted
                stringBuilder.append(currentChar);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 	<p>
     * 	<b><i>constructJsonObject</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     *  private static JsonObject constructJsonObject(String data)
     * 	throws JsonFormatException
     * 	</pre></code>
     * 	</p>
     * 	<p>
     * 	Returns the JSON object represented in a given String. A JSON object may not include any keys which contain the
     * 	period ( . ) character.
     * 	</p>
     *
     * 	@param data a String representing a JSON object
     * 	@return the JSON object represented in the given String.
     * 	@throws JsonFormatException if there is a JSON formatting error
     */
    private static JsonObject constructJsonObject(String data) throws JsonFormatException {
        char[] dataArray = data.toCharArray();
        int length = dataArray.length;
        if (dataArray[0] != '{') {
            throw new JsonFormatException("json object does not begin with '{'");
        }
        if (dataArray[length - 1] != '}') {
            throw new JsonFormatException("json object does not end with '}'");
        }

        Stack<Character> stack = new Stack<>();
        JsonObject jsonObject = new JsonObject();

        int beginIndex = 1;
        int currentIndex = beginIndex;
        while (currentIndex < length - 1) {
            char currentChar = dataArray[currentIndex];

            if (currentChar == '{') {
                // starting a json object
                // verify character is not inside a string
                try {
                    if (stack.peek() != '"') {
                        stack.push(currentChar);
                    }
                } catch (EmptyStackException ex) {
                    stack.push(currentChar);
                }
            } else if (currentChar == '[') {
                // starting a json list
                // verify character is not inside a string
                try {
                    if (stack.peek() != '"') {
                        stack.push(currentChar);
                    }
                } catch (EmptyStackException ex) {
                    stack.push(currentChar);
                }
            } else if (currentChar == '"') {
                // starting or ending a string
                // check if character is inside a string
                try {
                    if (stack.peek() == '"') {
                        if (dataArray[currentIndex - 1] != '\\') {
                            // ending a string (quotes are not escaped)
                            stack.pop();
                        }
                    } else {
                        // starting a string
                        stack.push(currentChar);
                    }
                } catch (EmptyStackException ex) {
                    stack.push(currentChar);
                }
            } else if (currentChar == '}') {
                // ending a json object
                // check if character is in a string
                try {
                    if (stack.peek() == '"') {
                        currentIndex++;
                        continue;
                    }
                } catch (EmptyStackException ex) {
                    throw new JsonFormatException("unexpected token '}'");
                }
                // check for valid nesting
                if (stack.peek() == '{') {
                    // exiting a json object
                    stack.pop();
                } else {
                    throw new JsonFormatException("unexpected token '}'");
                }
                // check if json object is at root level
                if (stack.size() == 0) {
                    String entry = data.substring(beginIndex, currentIndex + 1);
                    String key = validateKey(entry);
                    jsonObject.put(key, constructJsonObject(entry.substring(key.length() + 3)));
                    beginIndex = currentIndex + 2;
                    if (dataArray[currentIndex + 1] == ',') {
                        currentIndex++;
                    }
                }
            } else if (currentChar == ']') {
                // ending a json list
                // check if character is in a string
                try {
                    if (stack.peek() == '"') {
                        currentIndex++;
                        continue;
                    }
                } catch (EmptyStackException ex) {
                    throw new JsonFormatException("unexpected token ']'");
                }
                // check for valid nesting
                if (stack.peek() == '[') {
                    // exiting a json object
                    stack.pop();
                } else {
                    throw new JsonFormatException("unexpected token ']'");
                }
                // check if json object is at root level
                if (stack.size() == 0) {
                    String entry = data.substring(beginIndex, currentIndex + 1);
                    String key = validateKey(entry);
                    jsonObject.put(key, constructJsonArray(entry.substring(key.length() + 3)));
                    beginIndex = currentIndex + 2;
                    if (dataArray[currentIndex + 1] == ',') {
                        currentIndex++;
                    }
                }
            } else if (currentChar == ',') {
                // ending an entry
                // verify comma is ending a primitive and if primitive is at root level
                char prev = dataArray[currentIndex - 1];
                if (prev != '}' && prev != ']' && stack.size() == 0) {
                    String entry = data.substring(beginIndex, currentIndex);
                    String key = validateKey(entry);
                    jsonObject.put(key, constructJsonPrimitive(entry.substring(key.length() + 3)));
                    beginIndex = currentIndex + 1;
                }

            }
            currentIndex++;
        }
        if (beginIndex < currentIndex) {
            // there is an unrecognized primitive at the end
            // of the json file if this code is hit
            String entry = data.substring(beginIndex, currentIndex);
            String key = validateKey(entry);
            jsonObject.put(key, constructJsonPrimitive(entry.substring(key.length() + 3)));
        }
        return jsonObject;
    }

    /**
     * 	<p>
     * 	<b><i>constructJsonList</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     *  private static JsonList constructJsonList(String data)
     * 	throws JsonFormatException
     * 	</pre></code>
     * 	</p>
     * 	<p>
     * 	Returns the JSON list (array) represented by the given String.
     * 	</p>
     *
     * 	@param data a String representing a JSON list (array)
     * 	@return the JSON list (array) represented by the given String.
     * 	@throws JsonFormatException if there is a JSON formatting error
     */
    private static JsonArray constructJsonArray(String data) throws JsonFormatException {
        char[] dataArray = data.toCharArray();
        int length = dataArray.length;
        if (dataArray[0] != '[') {
            throw new JsonFormatException("json list does not begin with '['");
        }
        if (dataArray[length - 1] != ']') {
            throw new JsonFormatException("json list does not end with ']'");
        }

        Stack<Character> stack = new Stack<>();
        JsonArray jlist = new JsonArray();

        int beginIndex = 1;
        int currentIndex = beginIndex;
        while (currentIndex < length - 1) {
            char currentChar = dataArray[currentIndex];

            if (currentChar == '{') {
                // starting a json object
                // verify character is not inside a string
                try {
                    if (stack.peek() != '"') {
                        stack.push(currentChar);
                    }
                } catch (EmptyStackException ex) {
                    stack.push(currentChar);
                }
            } else if (currentChar == '[') {
                // starting a json list
                // verify character is not inside a string
                try {
                    if (stack.peek() != '"') {
                        stack.push(currentChar);
                    }
                } catch (EmptyStackException ex) {
                    stack.push(currentChar);
                }
            } else if (currentChar == '"') {
                // starting or ending a string
                // check if character is inside a string
                try {
                    if (stack.peek() == '"') {
                        if (dataArray[currentIndex - 1] != '\\') {
                            // ending a string (quotes are not escaped)
                            stack.pop();
                        }
                    } else {
                        // starting a string
                        stack.push(currentChar);
                    }
                } catch (EmptyStackException ex) {
                    stack.push(currentChar);
                }
            } else if (currentChar == '}') {
                // ending a json object
                // check if character is in a string
                try {
                    if (stack.peek() == '"') {
                        currentIndex++;
                        continue;
                    }
                } catch (EmptyStackException ex) {
                    throw new JsonFormatException("unexpected token '}'");
                }
                // check for valid nesting
                if (stack.peek() == '{') {
                    // exiting a json object
                    stack.pop();
                } else {
                    throw new JsonFormatException("unexpected token '}'");
                }
                // check if json object is at root level
                if (stack.size() == 0) {
                    jlist.add(constructJsonObject(data.substring(beginIndex, currentIndex + 1)));
                    beginIndex = currentIndex + 2;
                }
            } else if (currentChar == ']') {
                // ending a json list
                // check if character is in a string
                try {
                    if (stack.peek() == '"') {
                        currentIndex++;
                        continue;
                    }
                } catch (EmptyStackException ex) {
                    throw new JsonFormatException("unexpected token ']'");
                }
                // check for valid nesting
                if (stack.peek() == '[') {
                    // exiting a json object
                    stack.pop();
                } else {
                    throw new JsonFormatException("unexpected token ']'");
                }
                // check if json object is at root level
                if (stack.size() == 0) {
                    jlist.add(constructJsonArray(data.substring(beginIndex, currentIndex + 1)));
                    beginIndex = currentIndex + 2;
                    if (dataArray[currentIndex + 1] == ',') {
                        currentIndex++;
                    }
                }
            } else if (currentChar == ',') {
                // ending an entry
                // verify comma is ending a primitive and if primitive is at root level
                char prev = dataArray[currentIndex - 1];
                if (prev != '}' && prev != ']' && stack.size() == 0) {
                    jlist.add(constructJsonPrimitive(data.substring(beginIndex, currentIndex)));
                    beginIndex = currentIndex + 1;
                }

            }
            currentIndex++;
        }
        if (beginIndex < currentIndex) {
            // there is an unrecognized primitive at the end
            // of the json list if this code is hit
            jlist.add(constructJsonPrimitive(data.substring(beginIndex, currentIndex)));
        }
        return jlist;
    }

    /**
     * 	<p>
     * 	<b><i>constructJsonPrimitive</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     *  private static String constructJsonPrimitive(String line)
     * 	throws JsonFormatException
     * 	</pre></code>
     * 	</p>
     * 	<p>
     * 	Returns the primitive data type (String, boolean, long, or double) represented by the given String.
     * 	</p>
     *
     * 	@param data a String representation of a primitive data value
     * 	@return the primitive data value represented by the given String.
     * 	@throws JsonFormatException if the given String does not contain a String representation of a primitive data
     * 	value
     */
    private static Object constructJsonPrimitive(String data) throws JsonFormatException {
        if (data.charAt(0) == '"' && data.charAt(data.length() - 1) == '"') {
            // check for string
            return data.substring(1, data.length() - 1);
        } else if (data.equals("true")) {
            // check for bool (true)
            return true;
        } else if (data.equals("false")) {
            // check for bool (false)
            return false;
        } else {
            try {
                return Long.parseLong(data);
            } catch (NumberFormatException ex1) {
                // not a Long
                try {
                    return Double.parseDouble(data);
                } catch (NumberFormatException ex2) {
                    // not a Double
                    throw new JsonFormatException("(" + data + ") is not a valid value");
                }
            }
        }
    }

    /**
     * 	<p>
     * 	<b><i>validateKey</i></b>
     * 	</p>
     * 	<p>
     * 	<pre class="tab"><code>
     *  private static String validateKey(String entry)
     * 	throws JsonFormatException
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Returns the key in a JSON key-value pair. All characters other than the period ( . ) may be used as a part of a
     * 	key. You may pass either an entire key-value pair such as"\"key\":\"value\"", or just a key such as "\"key\"".
     * 	</p>
     * 	<p></p>
     * 	<p>
     * 	Note that if you pass an entire key-value pair, no characters past the first colon ( : ) character will be
     * 	considered (in other words, this function will work as intended even if the value of the key-value pair contains
     * 	a JSON formatting error).
     * 	</p>
     *
     * 	@param entry a String containing a JSON key
     * 	@return the key in a JSON key-value pair.
     * 	@throws JsonFormatException if the key is not formatted correctly
     */
    private static String validateKey(String entry) throws JsonFormatException {
        String[] split = entry.split(":");
        String key = split[0];
        // validate that the key starts and ends with double-quotes,
        // and that it does not contain a period
        if (key.charAt(0) == '"' && key.charAt(key.length() - 1) == '"' && !key.contains(".")) {
            key = key.substring(1, key.length() - 1);
        } else {
            throw new JsonFormatException("(" + key + ") is not a valid key");
        }
        return key;
    }

}
