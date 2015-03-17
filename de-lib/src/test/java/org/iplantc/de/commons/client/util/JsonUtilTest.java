package org.iplantc.de.commons.client.util;

import org.iplantc.de.client.util.JsonUtil;

import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;


/**
 * Unit tests for the {@link org.iplantc.de.client.util.JsonUtil} class.
 * 
 * TODO JDS Evaluate necessity of ignored test cases.
 * If ignored test cases are necessary, they will need to be in a 
 * GWTTestCase
 * @author jstroot
 *
 */
public class JsonUtilTest {

    private JsonUtil jsonUtil;

    @Before public void setup(){
        jsonUtil = JsonUtil.getInstance();

    }


    @Test public void testTrimNull() {
        assertNull(jsonUtil.trim(null));
    }

    @Test public void testTrimEmpty() {
        String empty = jsonUtil.trim("");
        assertNotNull(empty);
        assertTrue("".equals(empty));
    }

    @Test public void testTrimNoQuotes() {
        String tmp = jsonUtil.trim("test");
        assertNotNull(tmp);
        assertTrue("test".equals(tmp));

    }

    @Test public void testTrimLeftUnbalanced() {
        String tmp = jsonUtil.trim("\"test");
        assertNotNull(tmp);
        assertTrue("test".equals(tmp));
    }

    @Test public void testTrimRightUnbalanced() {
        String tmp = jsonUtil.trim("test\"");
        assertNotNull(tmp);
        assertTrue("test".equals(tmp));
    }

    @Test public void testTrimQuoteinMiddle() {
        String str = "\"te\"st\"";
        String tmp = jsonUtil.trim(str);
        assertNotNull(tmp);
        assertTrue("te\"st".equals(tmp));

    }

    @Test public void testTrim() {
        String tmp = jsonUtil.trim("\"test\"");
        assertNotNull(tmp);
        assertTrue("test".equals(tmp));
    }

    @Test public void testEscapeNewLine() {
        String tmp = "this is a string with newline\n";
        assertEquals(jsonUtil.escapeNewLine(tmp), "this is a string with newline\\n");
    }

    @Test public void testEscapeNewLineNull() {
        String tmp = null;
        assertNull(jsonUtil.escapeNewLine(tmp));
    }

    @Test public void testEscapeNewLineEmpty() {
        String tmp = "";
        assertEquals("", jsonUtil.escapeNewLine(tmp));

    }

    @Test public void testFormatString() {
        String tmp = "this is a string with newline\\n";
        assertEquals(jsonUtil.formatString(tmp), "this is a string with newline\n");
    }

    @Test public void testtestFormatStringNull() {
        String tmp = null;
        assertNull(jsonUtil.formatString(tmp));
    }

    @Test public void testFormatStringEmpty() {
        String tmp = "";
        assertEquals("", jsonUtil.formatString(tmp));

    }

    @Test public void testNullIsEmpty() {
        assertEquals(true, jsonUtil.isEmpty(null));
    }

    @Test @Ignore public void testEmptyIsEmpty1() {
        String tmp = "{}";
        JSONValue val = JSONParser.parseStrict(tmp);
        assertEquals(true, jsonUtil.isEmpty(val));
    }

    @Test @Ignore public void testEmptyIsEmpty2() {
        String tmp = "[]";
        JSONValue val = JSONParser.parseStrict(tmp);
        assertEquals(true, jsonUtil.isEmpty(val));
    }

    @Test @Ignore public void testNonEmptyIsEmpty() {
        String tmp = "{\"testkey\": \"testvalue\"}";
        JSONValue val = JSONParser.parseStrict(tmp);
        assertEquals(false, jsonUtil.isEmpty(val));
    }

    @Test @Ignore public void testGetRawValueAsString() {
        JSONNumber number = new JSONNumber(12345);
        assertEquals("12345", jsonUtil.getRawValueAsString(number));

        JSONString string = new JSONString("test 123 test");
        assertEquals("test 123 test", jsonUtil.getRawValueAsString(string));

        JSONBoolean bool = JSONBoolean.getInstance(true);
        assertEquals("true", jsonUtil.getRawValueAsString(bool));
    }

    @Test @Ignore public void testBuildStringArray() {
        // test buildStringArray(String, List)
        assertEquals("\"test 123\": []", jsonUtil.buildStringArray("test 123", null));
        String exp = "\"blah\": [\"asdf\",\"2324523\",\"lkjlkjlkj\"]";
        String act =  jsonUtil.buildStringArray("blah", Arrays.asList("asdf", "2324523", "lkjlkjlkj"));
        System.out.println("|" + exp + "|");
        System.out.println("|" + act + "|");
        assertEquals(exp, act);

        // test buildStringArray(List)
        assertTrue(jsonUtil.buildStringArray(null).isEmpty());
        List<JSONValue> jsonList = Arrays.asList(new JSONString("asdf"), new JSONNumber(2324523),
                JSONBoolean.getInstance(false));
        List<String> stringList = jsonUtil.buildStringArray(jsonList);
        assertEquals(3, stringList.size());
        assertEquals("asdf", stringList.get(0));
        assertEquals("2324523", stringList.get(1));
        assertEquals("false", stringList.get(2));
    }

    @Test @Ignore public void testBuildJsonArrayString() {
        assertNull(jsonUtil.buildJsonArrayString(null));
        assertEquals("[\"asdf\",\"2324523\",\"lkjlkjlkj\"]",
                jsonUtil.buildJsonArrayString(Arrays.asList("asdf", "2324523", "lkjlkjlkj")));
    }
}
