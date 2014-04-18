package org.iplantc.de.commons.client.views.window.configs;

import org.iplantc.de.client.models.diskResources.File;

public interface FileViewerWindowConfig extends WindowConfig {

    File getFile();

    void setFile(File file);

    boolean isEditing();

    void setEditing(boolean editing);

    boolean isShowTreeTab();

    void setShowTreeTab(boolean b);

    void setVizTabFirst(boolean b);

    boolean isVizTabFirst();

}
