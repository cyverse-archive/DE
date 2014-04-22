package org.iplantc.de.commons.client.views.window.configs;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.HasPath;

import java.util.List;

public interface DiskResourceWindowConfig extends WindowConfig {

    HasPath getSelectedFolder();

    List<HasId> getSelectedDiskResources();

    void setSelectedFolder(HasPath selectedFolder);

    void setSelectedDiskResources(List<HasId> selectedResources);

    void setMaximized(boolean maximize);

    boolean isMaximized();

}
