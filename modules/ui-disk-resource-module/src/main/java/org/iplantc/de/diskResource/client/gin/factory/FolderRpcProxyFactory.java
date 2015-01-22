package org.iplantc.de.diskResource.client.gin.factory;

import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.diskResource.client.DiskResourceView;

/**
 * @author jstroot
 */
public interface FolderRpcProxyFactory {
    DiskResourceView.FolderRpcProxy create(IsMaskable isMaskable);
}
