package org.iplantc.de.commons.client.views.window.configs;

import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;

public interface FileViewerWindowConfig extends WindowConfig {

    File getFile();

    Folder getParentFolder();

    void setParentFolder(Folder parentFolder);

    void setFile(File file);

    boolean isEditing();

    void setEditing(boolean editing);

    void setVizTabFirst(boolean b);

    boolean isVizTabFirst();
}
