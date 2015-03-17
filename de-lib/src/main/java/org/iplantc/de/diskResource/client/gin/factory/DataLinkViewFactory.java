package org.iplantc.de.diskResource.client.gin.factory;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.diskResource.client.DataLinkView;

import java.util.List;

/**
 * Created by jstroot on 2/10/15.
 * @author jstroot
 */
public interface DataLinkViewFactory {
    DataLinkView create(DataLinkView.Presenter presenter, List<DiskResource> resources);
}
