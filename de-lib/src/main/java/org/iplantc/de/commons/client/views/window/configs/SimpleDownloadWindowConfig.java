package org.iplantc.de.commons.client.views.window.configs;

import org.iplantc.de.client.models.diskResources.DiskResource;

import java.util.List;

public interface SimpleDownloadWindowConfig extends WindowConfig {

    List<DiskResource> getResourcesToDownload();

    void setResourcesToDownload(List<DiskResource> resources);
}
