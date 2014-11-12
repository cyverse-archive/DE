package org.iplantc.de.diskResource.client.gin.factory;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.diskResource.client.sharing.views.DataSharingDialog;

import java.util.Set;

/**
 * @author jstroot
 */
public interface DataSharingDialogFactory {
    DataSharingDialog createDataSharingDialog(Set<DiskResource> resources);
}
