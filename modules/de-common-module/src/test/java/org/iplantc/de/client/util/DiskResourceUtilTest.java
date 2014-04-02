package org.iplantc.de.client.util;

import com.google.gwtmockito.GxtMockitoTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(GxtMockitoTestRunner.class)
public class DiskResourceUtilTest {

    @Test
    public void testParseParent() {
        assertEquals("/home/iplant/ipctest", DiskResourceUtil.parseParent("/home/iplant/ipctest/test"));
        assertNull(DiskResourceUtil.parseParent(null));
        assertEquals("", DiskResourceUtil.parseParent(""));
    }

    @Test
    public void testParseNameFromPath() {
        assertNull(DiskResourceUtil.parseNameFromPath(null));
        assertEquals("", DiskResourceUtil.parseNameFromPath(""));
        assertEquals("test", DiskResourceUtil.parseNameFromPath("/home/iplant/ipctest/test"));
    }

    @Test
    public void testParseNamesFromIdList() {
        assertNull(DiskResourceUtil.parseNamesFromIdList(null));
    }

    @Test
    public void testAppendNameToPath() {
        assertNull(DiskResourceUtil.appendNameToPath(null, null));
        assertNull(DiskResourceUtil.appendNameToPath("", null));
        assertNull(DiskResourceUtil.appendNameToPath(null, ""));
        assertNull(DiskResourceUtil.appendNameToPath("", ""));
        assertEquals("/home/iplant/ipctest/test", DiskResourceUtil.appendNameToPath("/home/iplant/ipctest", "test"));
    }

    @Test
    public void testAsCommaSeperatedNameList() {
        assertNull(DiskResourceUtil.asCommaSeperatedNameList(null));
        ArrayList<String> empty = new ArrayList<String>();
        assertEquals("", DiskResourceUtil.asCommaSeperatedNameList(empty));
    }

}
