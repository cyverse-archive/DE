package org.iplantc.de.diskResource.client.gin.factory;

import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.diskResource.client.GridView;

import java.util.List;

/**
 * @author jstroot
 */
public interface FolderContentsRpcProxyFactory {
    GridView.FolderContentsRpcProxy createWithEntityType(List<InfoType> infoTypeFilters, TYPE entityType);
}
