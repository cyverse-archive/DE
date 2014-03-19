package org.iplantc.de.client.util;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.web.bindery.autobean.shared.Splittable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides JSON utility operations.
 */
public class JsonUtil {
    /**
     * Returns a JavaScript array representation of JSON argument data.
     * 
     * @param <T> type of the elements contains in the JavaScript Array.
     * @param json a string representing data in JSON format.
     * @return a JsArray of type T.
     */
    public static final native <T extends JavaScriptObject> JsArray<T> asArrayOf(String json)
    /*-{
		return eval(json);
    }-*/;

    /**
     * Remove quotes surrounding a JSON string value.
     * 
     * @param value string with quotes.
     * @return a string without quotes.
     */
    public static String trim(String value) {
        StringBuilder temp = null;
        if (value != null && !value.isEmpty()) {
            final String QUOTE = "\"";

            temp = new StringBuilder(value);

            if (value.startsWith(QUOTE)) {
                temp.deleteCharAt(0);
            }

            if (value.endsWith(QUOTE)) {
                temp.deleteCharAt(temp.length() - 1);
            }

            return temp.toString();
        } else {
            return value;
        }
    }

    /**
     * 
     * Replace string with unicode 'u0022'
     * 
     * @param value string to escape quotes on
     * @return string after escaping quotes
     */
    public static String escapeQuotes(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        } else {
            return value.replace("\"", "u0022");
        }
    }

    /**
     * Escape new line char in JSON string
     * 
     * @param value string to escape.
     * @return escaped string.
     */
    public static String escapeNewLine(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        } else {
            return value.replace("\n", "\\n");
        }
    }

    /**
     * Format strings with new line, tab spaces and carriage returns
     * 
     * @param value string to format.
     * @return formatted string.
     */
    public static String formatString(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        } else {
            value = value.replace("\\t", "\t");
            value = value.replace("\\r\\n", "\n");
            value = value.replace("\\r", "\n");
            value = value.replace("\\n", "\n");
            value = value.replace("u0022", "\"");
            return value;
        }
    }

    /**
     * Check if the json value is empty
     * 
     * @param in json value to test
     * @return true if value is empty else returns false
     */
    public static boolean isEmpty(JSONValue in) {
        boolean ret = true; // assume we have an empty value

        if (in != null) {
            String test = in.toString();

            if (test.length() > 0 && !test.equals("[]") && !test.equals("{}")) {
                ret = false;
            }
        }

        return ret;
    }

    /**
     * get JSONArry for the given JSONObject and key
     * 
     * @param jsonObj the object in which the key should be looked for
     * @param key key for the requested JSONArray
     * @return JSONArray for the give key. null if the key or jsonObj is null
     */
    public static JSONArray getArray(final JSONObject jsonObj, final String key) {
        JSONArray ret = null; // assume failure

        if (jsonObj != null && key != null) {
            JSONValue val = jsonObj.get(key);

            if (val != null) {
                ret = val.isArray();
            }
        }

        return ret;
    }

    /**
     * Creates a JSON object from a string. If the string parses, but doesn't contain a JSON object, null
     * is returned.
     * 
     * @param json
     * @return
     */
    public static JSONObject getObject(final String json) {
        try {
            JSONValue val = JSONParser.parseStrict(json);
            if (val == null) {
                return null;
            } else {
                return val.isObject();
            }
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * Parse a string from a JSON object
     * 
     * @param jsonObj object to parse.
     * @param key key for string to retrieve.
     * @return desired string. Empty string on failure.
     */
    public static String getString(final JSONObject jsonObj, final String key) {
        String ret = ""; // assume failure

        if (jsonObj != null && key != null) {
            JSONValue val = jsonObj.get(key);

            if (val != null && val.isNull() == null) {
                JSONString strVal = val.isString();

                if (strVal != null) {
                    ret = strVal.stringValue();
                }
            }
        }

        return ret;
    }

    /**
     * 
     * @param jsonObj
     * @param key
     * @return
     */
    public static JSONObject getObject(final JSONObject jsonObj, final String key) {
        JSONObject ret = null; // assume failure

        if (jsonObj != null && key != null) {
            JSONValue val = jsonObj.get(key);

            if (val != null) {
                ret = val.isObject();
            }
        }

        return ret;
    }

    /**
     * Returns the JSONObject at a given array index, or null if there is no JSONObject at that index.
     * 
     * @param array
     * @param index
     * @return
     */
    public static JSONObject getObjectAt(JSONArray array, int index) {
        JSONValue element = array.get(index);

        if (element == null) {
            return null;
        } else {
            return element.isObject();
        }
    }

    /**
     * Build a JSON string array from a key and array list.
     * 
     * @param key key associated with the array
     * @param items items to add to the array
     * @return Correct JSON array.
     */
    public static String buildStringArray(final String key, List<String> items) {
        StringBuffer ret = new StringBuffer();
        ret.append("\"" + key + "\": ");
        String temp = buildJsonArrayString(items);
        if (temp == null || temp.isEmpty()) {
            ret.append("[]");
        } else {
            ret.append(temp);
        }

        return ret.toString();
    }

    /**
     * Build a JSON string array from a key and array list.
     * 
     * @param items items to add to the array
     * @return JSON array as a string.
     */
    public static String buildJsonArrayString(List<String> items) {
        JSONArray arr = buildArrayFromStrings(items);
        if (arr != null) {
            return arr.toString();
        } else {
            return null;
        }
    }

    /**
     * Simple function to wrap quotes around a valid string.
     * 
     * @param in string to be quoted.
     * @return quoted string (if input is not null).
     */
    public static String quoteString(String in) {
        String ret = null; // assume failure

        if (in != null) {
            final String QUOTE = "\"";

            ret = QUOTE + in + QUOTE;
        }

        return ret;
    }

    /**
     * Returns the string value from a JSON string and key.
     * 
     * @param json string to parse.
     * @param key for desired value.
     * @return string representation of value associated with key.
     */
    public static String parseStringValue(String json, String key) {
        JSONObject jsonObj = getObject(json);

        return getString(jsonObj, key);
    }

    /**
     * Build a JSONValue array from a key and array list.
     * 
     * @param items items to add to the array
     * @return Correct JSON array.
     */
    public static JSONArray buildArray(List<JSONValue> items) {
        JSONArray ret = new JSONArray();

        if (items != null) {
            int index = 0;
            for (JSONValue item : items) {
                ret.set(index++, item);
            }
        }

        return ret;
    }

    /**
     * Builds a JSON array from a list of objects. Each object's toString() value is added to the array
     * as a JSON String.
     * 
     * @param items
     * @return a JSON string array, null if the argument is null
     */
    public static JSONArray buildArrayFromStrings(List<?> items) {
        if (items == null) {
            return null;
        }
        
        JSONArray ret = new JSONArray();

        int index = 0;
        for (Object item : items) {
            JSONString str = new JSONString(item.toString());
            ret.set(index++, str);
        }

        return ret;
    }

    /**
     * Builds a string List from a JSON array.
     * @param arr
     * @return a list of strings, null if arr=null
     */
    public static List<String> buildStringList(JSONArray arr) {
        if (arr == null) {
            return null;
        }
        
        List<String> list = new ArrayList<String>(arr.size());
        for (int i=0; i<arr.size(); i++) {
            list.add(arr.get(i).isString().stringValue());
        }
        return list;
    }
    
    public static List<String> buildStringArray(List<JSONValue> items) {
        List<String> list = new ArrayList<String>();

        if (items == null) {
            return list;
        }

        for (JSONValue item : items) {
            list.add(trim(item.toString()));

        }

        return list;
    }

    /**
     * Performs simple check on a string to ensure valid JSON.
     * 
     * @param json to validate.
     * @return true if string is valid JSON.
     */
    public static boolean isValidJson(String json) {
        // TODO: make robust
        return (json != null && json.trim().length() > 0);
    }

    /**
     * Returns a String representation of a JSONValue without adding quotes. Only suitable for simple
     * JSON types, not JSONObjects or JSONArrays.
     * 
     * @param json
     * @return the JSON value as a string
     */
    public static String getRawValueAsString(JSONValue json) {
        if (json == null) {
            return null;
        }

        JSONString str = json.isString();
        if (str != null) {
            return str.stringValue();
        } else {
            return json.toString();
        }
    }

    /**
     * Returns a field value as a boolean. If the key doesn't exist or if there is no boolean value for
     * that key, defaultValue is returned.
     * 
     * @param json
     * @param key
     * @param defaultValue
     * @return
     */
    public static boolean getBoolean(JSONObject json, String key, boolean defaultValue) {
        if (json == null || key == null) {
            return defaultValue;
        }

        JSONValue val = json.get(key);
        if (val == null) {
            return defaultValue;
        }
        JSONBoolean bool = val.isBoolean();
        if (bool == null) {
            return defaultValue;
        }
        return bool.booleanValue();
    }

    /**
     * Returns a Number value for the given JSONObject and key.
     *
     * @param jsonObj object to parse.
     * @param key key in jsonObj with a Number value
     *
     * @return Number value for the given key; null if the key or jsonObj is null, or the key is not in
     *         jsonObj, or key does not contain a numeric value.
     */
    public static Number getNumber(JSONObject jsonObj, String key) {
        Number ret = null; // assume failure

        if (jsonObj != null && key != null) {
            JSONValue val = jsonObj.get(key);

            if (val != null && val.isNull() == null) {
                JSONNumber number = val.isNumber();

                if (number != null) {
                    ret = new Double(number.doubleValue());
                }
            }
        }

        return ret;
    }

    /**
     * A util method to generate MD5 hash
     * 
     * 
     * @param json input json as string
     * @return a hash based on the input
     */

    public static byte[] generateHash(String json) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5"); //$NON-NLS-1$
            md.reset();
            md.update(json.getBytes());
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }
    
    /**
     * A convenience method which passes a splittable's payload to the native
     * {@link #prettyPrint(String, String, int)} method with default values.
     * 
     * @param split the splittable whose payload will be pretty printed
     * @return
     */
    public static String prettyPrint(Splittable split){
        return prettyPrint(split.getPayload(), "", 4);
    }
    
    /**
     * Returns the pretty printed output of the given splittable's payload, printed with the given
     * spaces.
     * 
     * @param split the splittable whose payload will be pretty printed
     * @param space
     * @return
     */
    public static String prettyPrint(Splittable split, int space) {
        return prettyPrint(split.getPayload(), "", space);
    }

    /**
     * 
     * A native method that calls java script method to pretty print json.
     * 
     * @param json the json to pretty print
     * @param replacer
     * @param space the char to used for formatting
     * @return the pretty print version of json
     */
    public native static String prettyPrint(String json, String replacer, int space) /*-{
        return $wnd.JSON.stringify($wnd.JSON.parse(json), replacer, space);
    }-*/;

}
