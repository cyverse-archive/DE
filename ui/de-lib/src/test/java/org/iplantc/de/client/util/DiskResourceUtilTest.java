package org.iplantc.de.client.util;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.viewer.InfoType;

import com.google.common.collect.Lists;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(GwtMockitoTestRunner.class)
public class DiskResourceUtilTest {

    @Mock Folder folder1, folder2, folder3;
    @Mock File file1, file2, file3;

    private DiskResourceUtil diskResourceUtil;

    @Before public void setup(){
        diskResourceUtil = diskResourceUtil.getInstance();

    }

    @Test public void testParseParent() {
        assertEquals("/home/iplant/ipctest", diskResourceUtil.parseParent("/home/iplant/ipctest/test"));
        assertNull(diskResourceUtil.parseParent(null));
        assertEquals("", diskResourceUtil.parseParent(""));
    }

    @Test public void testParseNameFromPath() {
        assertNull(diskResourceUtil.parseNameFromPath(null));
        assertEquals("", diskResourceUtil.parseNameFromPath(""));
        assertEquals("test", diskResourceUtil.parseNameFromPath("/home/iplant/ipctest/test"));
    }

    @Test public void testParseNamesFromIdList() {
        assertNull(diskResourceUtil.parseNamesFromIdList(null));
    }

    @Test public void testAppendNameToPath() {
        assertNull(diskResourceUtil.appendNameToPath(null, null));
        assertNull(diskResourceUtil.appendNameToPath("", null));
        assertNull(diskResourceUtil.appendNameToPath(null, ""));
        assertNull(diskResourceUtil.appendNameToPath("", ""));
        assertEquals("/home/iplant/ipctest/test", diskResourceUtil.appendNameToPath("/home/iplant/ipctest", "test"));
    }

    @Test public void testAsCommaSeperatedNameList() {
        assertNull(diskResourceUtil.asCommaSeparatedNameList(null));
        ArrayList<String> empty = new ArrayList<String>();
        assertEquals("", diskResourceUtil.asCommaSeparatedNameList(empty));
    }

    @Test public void testTreeTab() {
        Splittable s = createInfoTypeSplittable(InfoType.ACE.toString());
        boolean expected = diskResourceUtil.isTreeTab(s);
        assertFalse(expected);

        s = createInfoTypeSplittable(InfoType.NEXUS.toString());
        expected = diskResourceUtil.isTreeTab(s);
        assertTrue(expected);

        s = createInfoTypeSplittable(InfoType.PHYLIP.toString());
        expected = diskResourceUtil.isTreeTab(s);
        assertFalse(expected);

        s = createInfoTypeSplittable(InfoType.PHYLOXML.toString());
        expected = diskResourceUtil.isTreeTab(s);
        assertTrue(expected);

        s = createInfoTypeSplittable(InfoType.NEWICK.toString());
        expected = diskResourceUtil.isTreeTab(s);
        assertTrue(expected);

        s = createInfoTypeSplittable("");
        expected = diskResourceUtil.isTreeTab(s);
        assertFalse(expected);
    }

    @Test public void testGenomeTab() {
        Splittable s = createInfoTypeSplittable(InfoType.ACE.toString());
        boolean expected = diskResourceUtil.isGenomeVizTab(s);
        assertFalse(expected);

        s = createInfoTypeSplittable(InfoType.FASTA.toString());
        expected = diskResourceUtil.isGenomeVizTab(s);
        assertTrue(expected);

    }

    @Test public void testEnsemblTab() {
        Splittable s = createInfoTypeSplittable(InfoType.ACE.toString());
        boolean expected = diskResourceUtil.isEnsemblVizTab(s);
        assertFalse(expected);

        s = createInfoTypeSplittable(InfoType.BAM.toString());
        expected = diskResourceUtil.isEnsemblVizTab(s);
        assertTrue(expected);

        s = createInfoTypeSplittable(InfoType.VCF.toString());
        expected = diskResourceUtil.isEnsemblVizTab(s);
        assertTrue(expected);

        s = createInfoTypeSplittable(InfoType.GFF.toString());
        expected = diskResourceUtil.isEnsemblVizTab(s);
        assertTrue(expected);
    }

    @Test public void testExtractFolders() {
        List<DiskResource> resources = Arrays.asList(folder1, file1);
        ArrayList<Folder> expected = Lists.newArrayList(diskResourceUtil.extractFolders(resources));
        assertTrue(expected.size() == 1);

        expected = Lists.newArrayList(diskResourceUtil.extractFolders(Arrays.asList(file1)));
        assertTrue(expected.size() == 0);
    }

    @Test public void testExtractFiles() {
        List<DiskResource> resources = Arrays.asList(folder1, file1);
        ArrayList<File> expected = Lists.newArrayList(diskResourceUtil.extractFiles(resources));
        assertTrue(expected.size() == 1);

        expected = Lists.newArrayList(diskResourceUtil.extractFiles(Arrays.asList(folder1)));
        assertTrue(expected.size() == 0);
    }

    @Test(expected = NumberFormatException.class) public void testFormatFileSize() {
        String expected = diskResourceUtil.formatFileSize(null);
        assertNull(expected);
        expected = diskResourceUtil.formatFileSize("");
        assertNull(expected);

        expected = diskResourceUtil.formatFileSize("foo");
        fail("Cannot parse this string into Double");

    }

    private Splittable createInfoTypeSplittable(String infoType) {
        Splittable s = StringQuoter.createSplittable();
        StringQuoter.create(infoType).assign(s, "infoType");
        return s;
    }

}
