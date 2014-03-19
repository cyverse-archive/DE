package org.iplantc.de.commons.client.util;

import org.iplantc.de.client.util.JsonUtil;

import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import static org.junit.Assert.*;

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

	@Test
    public void testTrimNull() {
        assertNull(JsonUtil.trim(null));
    }

	@Test
    public void testTrimEmpty() {
        String empty = JsonUtil.trim("");
        assertNotNull(empty);
        assertTrue("".equals(empty));
    }

	@Test
    public void testTrimNoQuotes() {
        String tmp = JsonUtil.trim("test");
        assertNotNull(tmp);
        assertTrue("test".equals(tmp));

    }

	@Test
    public void testTrimLeftUnbalanced() {
        String tmp = JsonUtil.trim("\"test");
        assertNotNull(tmp);
        assertTrue("test".equals(tmp));
    }

	@Test
    public void testTrimRightUnbalanced() {
        String tmp = JsonUtil.trim("test\"");
        assertNotNull(tmp);
        assertTrue("test".equals(tmp));
    }

	@Test
    public void testTrimQuoteinMiddle() {
        String str = "\"te\"st\"";
        String tmp = JsonUtil.trim(str);
        assertNotNull(tmp);
        assertTrue("te\"st".equals(tmp));

    }

	@Test
    public void testTrim() {
        String tmp = JsonUtil.trim("\"test\"");
        assertNotNull(tmp);
        assertTrue("test".equals(tmp));
    }

	@Test
    public void testEscapeNewLine() {
        String tmp = "this is a string with newline\n";
        assertEquals(JsonUtil.escapeNewLine(tmp), "this is a string with newline\\n");
    }

	@Test
    public void testEscapeNewLineNull() {
        String tmp = null;
        assertNull(JsonUtil.escapeNewLine(tmp));
    }

	@Test
    public void testEscapeNewLineEmpty() {
        String tmp = "";
        assertEquals("", JsonUtil.escapeNewLine(tmp));

    }

	@Test
    public void testFormatString() {
        String tmp = "this is a string with newline\\n";
        assertEquals(JsonUtil.formatString(tmp), "this is a string with newline\n");
    }

	@Test
    public void testtestFormatStringNull() {
        String tmp = null;
        assertNull(JsonUtil.formatString(tmp));
    }

	@Test
    public void testFormatStringEmpty() {
        String tmp = "";
        assertEquals("", JsonUtil.formatString(tmp));

    }

	@Test
    public void testNullIsEmpty() {
        assertEquals(true, JsonUtil.isEmpty(null));
    }

	@Test
	@Ignore
    public void testEmptyIsEmpty1() {
        String tmp = "{}";
        JSONValue val = JSONParser.parseStrict(tmp);
        assertEquals(true, JsonUtil.isEmpty(val));
    }

	@Test
	@Ignore
    public void testEmptyIsEmpty2() {
        String tmp = "[]";
        JSONValue val = JSONParser.parseStrict(tmp);
        assertEquals(true, JsonUtil.isEmpty(val));
    }

	@Test
	@Ignore
    public void testNonEmptyIsEmpty() {
        String tmp = "{\"testkey\": \"testvalue\"}";
        JSONValue val = JSONParser.parseStrict(tmp);
        assertEquals(false, JsonUtil.isEmpty(val));
    }

	@Test
	@Ignore
    public void testGetRawValueAsString() {
        JSONNumber number = new JSONNumber(12345);
        assertEquals("12345", JsonUtil.getRawValueAsString(number));

        JSONString string = new JSONString("test 123 test");
        assertEquals("test 123 test", JsonUtil.getRawValueAsString(string));

        JSONBoolean bool = JSONBoolean.getInstance(true);
        assertEquals("true", JsonUtil.getRawValueAsString(bool));
    }

	@Test
	@Ignore
    public void testBuildStringArray() {
        // test buildStringArray(String, List)
        assertEquals("\"test 123\": []", JsonUtil.buildStringArray("test 123", null));
        String exp = "\"blah\": [\"asdf\",\"2324523\",\"lkjlkjlkj\"]";
        String act =  JsonUtil.buildStringArray("blah", Arrays.asList("asdf", "2324523", "lkjlkjlkj"));
        System.out.println("|" + exp + "|");
        System.out.println("|" + act + "|");
        assertEquals(exp, act);

        // test buildStringArray(List)
        assertTrue(JsonUtil.buildStringArray(null).isEmpty());
        List<JSONValue> jsonList = Arrays.asList(new JSONString("asdf"), new JSONNumber(2324523),
                JSONBoolean.getInstance(false));
        List<String> stringList = JsonUtil.buildStringArray(jsonList);
        assertEquals(3, stringList.size());
        assertEquals("asdf", stringList.get(0));
        assertEquals("2324523", stringList.get(1));
        assertEquals("false", stringList.get(2));
    }

	@Test
	@Ignore
    public void testBuildJsonArrayString() {
        assertNull(JsonUtil.buildJsonArrayString(null));
        assertEquals("[\"asdf\",\"2324523\",\"lkjlkjlkj\"]",
                JsonUtil.buildJsonArrayString(Arrays.asList("asdf", "2324523", "lkjlkjlkj")));
    }
}
