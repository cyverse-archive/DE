package org.iplantc.de.diskResource.client.gin.factory;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.diskResource.client.DataLinkView;

import java.util.List;

/**
 * @author jstroot
 */
public interface DataLinkPresenterFactory {
    DataLinkView.Presenter createDataLinkPresenter(List<DiskResource> resources);
}
