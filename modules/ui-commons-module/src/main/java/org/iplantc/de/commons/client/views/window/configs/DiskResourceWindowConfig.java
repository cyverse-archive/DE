package org.iplantc.de.commons.client.views.window.configs;

import org.iplantc.de.client.models.HasId;

import java.util.List;

public interface DiskResourceWindowConfig extends WindowConfig {

    HasId getSelectedFolder();

    List<HasId> getSelectedDiskResources();

    void setSelectedFolder(HasId selectedFolder);

    void setSelectedDiskResources(List<HasId> selectedResources);

    void setMaximized(boolean maximize);

    boolean isMaximized();

}
