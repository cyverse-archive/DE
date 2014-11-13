package org.iplantc.de.diskResource.client.gin.factory;

import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.diskResource.client.views.DiskResourceView;

import java.util.List;

/**
 * @author jstroot
 */
public interface FolderContentsRpcProxyFactory {
    DiskResourceView.FolderContentsRpcProxy createWithEntityType(List<InfoType> infoTypeFilters, TYPE entityType);
}
