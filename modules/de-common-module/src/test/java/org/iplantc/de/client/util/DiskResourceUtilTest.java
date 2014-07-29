package org.iplantc.de.client.util;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.viewer.InfoType;

import com.google.common.collect.Lists;
import com.google.gwt.json.client.JSONObject;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Ignore;
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
    @Ignore
    public void testTreeTab() {
        Splittable s = createInfoTypeSplittable(InfoType.ACE.toString());
        JSONObject obj = JsonUtil.getObject(s.getPayload());
        boolean expected = DiskResourceUtil.isTreeTab(obj);
        assert (expected == false);

        s = createInfoTypeSplittable(InfoType.NEXUS.toString());
        obj = JsonUtil.getObject(s.getPayload());
        expected = DiskResourceUtil.isTreeTab(obj);
        assert (expected == true);

        s = createInfoTypeSplittable(InfoType.PHYLIP.toString());
        obj = JsonUtil.getObject(s.getPayload());
        expected = DiskResourceUtil.isTreeTab(obj);
        assert (expected == true);

        s = createInfoTypeSplittable(InfoType.NEWICK.toString());
        obj = JsonUtil.getObject(s.getPayload());
        expected = DiskResourceUtil.isTreeTab(obj);
        assert (expected == true);

        s = createInfoTypeSplittable("");
        obj = JsonUtil.getObject(s.getPayload());
        expected = DiskResourceUtil.isTreeTab(obj);
        assert (expected == false);
    }

    @Test
    @Ignore
    public void testGenomeTab() {
        Splittable s = createInfoTypeSplittable(InfoType.ACE.toString());
        JSONObject obj = JsonUtil.getObject(s.getPayload());
        boolean expected = DiskResourceUtil.isTreeTab(obj);
        assert (expected == false);

        s = createInfoTypeSplittable(InfoType.FASTA.toString());
        obj = JsonUtil.getObject(s.getPayload());
        expected = DiskResourceUtil.isTreeTab(obj);
        assert (expected == true);

    }

    @Test
    @Ignore
    public void testEnsemblTab() {
        Splittable s = createInfoTypeSplittable(InfoType.ACE.toString());
        JSONObject obj = JsonUtil.getObject(s.getPayload());
        boolean expected = DiskResourceUtil.isTreeTab(obj);
        assert (expected == false);

        s = createInfoTypeSplittable(InfoType.BAM.toString());
        obj = JsonUtil.getObject(s.getPayload());
        expected = DiskResourceUtil.isTreeTab(obj);
        assert (expected == true);

        s = createInfoTypeSplittable(InfoType.VCF.toString());
        obj = JsonUtil.getObject(s.getPayload());
        expected = DiskResourceUtil.isTreeTab(obj);
        assert (expected == true);

        s = createInfoTypeSplittable(InfoType.GFF.toString());
        obj = JsonUtil.getObject(s.getPayload());
        expected = DiskResourceUtil.isTreeTab(obj);
        assert (expected == true);
    }

    @Test
    public void testExtractFolders() {
        List<DiskResource> resources = Arrays.asList(folder1, file1);
        ArrayList<Folder> expected = Lists.newArrayList(DiskResourceUtil.extractFolders(resources));
        assert (expected.size() == 1);

        expected = Lists.newArrayList(DiskResourceUtil.extractFolders(Arrays.asList(file1)));
        assert (expected.size() == 0);
    }

    @Test
    public void testExtractFiles() {
        List<DiskResource> resources = Arrays.asList(folder1, file1);
        ArrayList<File> expected = Lists.newArrayList(DiskResourceUtil.extractFiles(resources));
        assert (expected.size() == 1);

        expected = Lists.newArrayList(DiskResourceUtil.extractFiles(Arrays.asList(folder1)));
        assert (expected.size() == 0);
    }


    private Splittable createInfoTypeSplittable(String infoType) {
        Splittable s = StringQuoter.createSplittable();
        StringQuoter.create(infoType).assign(s, "info-type");
        return s;
    }

}
