package org.iplantc.de.client.views.windows.configs;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;

import java.util.List;
import java.util.Map;

public interface IDropLiteWindowConfig extends WindowConfig {

    Folder getUploadFolderDest();

    List<DiskResource> getResourcesToDownload();

    Folder getCurrentFolder();

    int getDisplayMode();

    void setResourcesToDownload(List<DiskResource> resources);

    void setDisplayMode(int displayMode);

    void setCurrentFolder(Folder currentFolder);

    void setUploadFolderDest(Folder uploadDest);

    void setTypeMap(Map<String, String> map);

    Map<String, String> getTypeMap();

    boolean isSelectAll();

    void setSelectAll(boolean selectAll);

}
