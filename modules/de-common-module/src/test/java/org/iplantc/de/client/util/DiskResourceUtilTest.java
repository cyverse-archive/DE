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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(GwtMockitoTestRunner.class)
public class DiskResourceUtilTest {

    @Mock
    Folder folder1, folder2, folder3;
    @Mock
    File file1, file2, file3;

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

    @Test
    public void testTreeTab() {
        Splittable s = createInfoTypeSplittable(InfoType.ACE.toString());
        boolean expected = DiskResourceUtil.isTreeTab(s);
        assertFalse(expected);

        s = createInfoTypeSplittable(InfoType.NEXUS.toString());
        expected = DiskResourceUtil.isTreeTab(s);
        assertTrue(expected);

        s = createInfoTypeSplittable(InfoType.PHYLIP.toString());
        expected = DiskResourceUtil.isTreeTab(s);
        assertFalse(expected);

        s = createInfoTypeSplittable(InfoType.PHYLOXML.toString());
        expected = DiskResourceUtil.isTreeTab(s);
        assertTrue(expected);

        s = createInfoTypeSplittable(InfoType.NEWICK.toString());
        expected = DiskResourceUtil.isTreeTab(s);
        assertTrue(expected);

        s = createInfoTypeSplittable("");
        expected = DiskResourceUtil.isTreeTab(s);
        assertFalse(expected);
    }

    @Test
    public void testGenomeTab() {
        Splittable s = createInfoTypeSplittable(InfoType.ACE.toString());
        boolean expected = DiskResourceUtil.isGenomeVizTab(s);
        assertFalse(expected);

        s = createInfoTypeSplittable(InfoType.FASTA.toString());
        expected = DiskResourceUtil.isGenomeVizTab(s);
        assertTrue(expected);

    }

    @Test
    public void testEnsemblTab() {
        Splittable s = createInfoTypeSplittable(InfoType.ACE.toString());
        boolean expected = DiskResourceUtil.isEnsemblVizTab(s);
        assertFalse(expected);

        s = createInfoTypeSplittable(InfoType.BAM.toString());
        expected = DiskResourceUtil.isEnsemblVizTab(s);
        assertTrue(expected);

        System.out.println("will it run ?");

        s = createInfoTypeSplittable(InfoType.VCF.toString());
        expected = DiskResourceUtil.isEnsemblVizTab(s);
        assertTrue(expected);

        s = createInfoTypeSplittable(InfoType.GFF.toString());
        expected = DiskResourceUtil.isEnsemblVizTab(s);
        assertTrue(expected);
    }

    @Test
    public void testExtractFolders() {
        List<DiskResource> resources = Arrays.asList(folder1, file1);
        ArrayList<Folder> expected = Lists.newArrayList(DiskResourceUtil.extractFolders(resources));
        assertTrue(expected.size() == 1);

        expected = Lists.newArrayList(DiskResourceUtil.extractFolders(Arrays.asList(file1)));
        assertTrue(expected.size() == 0);
    }

    @Test
    public void testExtractFiles() {
        List<DiskResource> resources = Arrays.asList(folder1, file1);
        ArrayList<File> expected = Lists.newArrayList(DiskResourceUtil.extractFiles(resources));
        assertTrue(expected.size() == 1);

        expected = Lists.newArrayList(DiskResourceUtil.extractFiles(Arrays.asList(folder1)));
        assertTrue(expected.size() == 0);
    }


    private Splittable createInfoTypeSplittable(String infoType) {
        Splittable s = StringQuoter.createSplittable();
        StringQuoter.create(infoType).assign(s, "info-type");
        return s;
    }

}
