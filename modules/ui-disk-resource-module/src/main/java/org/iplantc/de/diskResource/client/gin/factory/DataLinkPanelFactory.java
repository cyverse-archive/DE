package org.iplantc.de.diskResource.client.gin.factory;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.diskResource.client.dataLink.view.DataLinkPanel;

import java.util.List;

/**
 * @author jstroot
 */
public interface DataLinkPanelFactory {
    DataLinkPanel.Presenter createDataLinkPresenter(List<DiskResource> resources);
    DataLinkPanel createDataLinkPanel(DataLinkPanel.Presenter presenter, List<DiskResource> resources);
}
