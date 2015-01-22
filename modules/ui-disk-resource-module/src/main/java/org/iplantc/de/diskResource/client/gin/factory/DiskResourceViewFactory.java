package org.iplantc.de.diskResource.client.gin.factory;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.diskResource.client.presenters.proxy.FolderContentsLoadConfig;
import org.iplantc.de.diskResource.client.DiskResourceView;

import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.data.shared.loader.TreeLoader;

/**
 * @author jstroot
 */
public interface DiskResourceViewFactory {
    DiskResourceView create(DiskResourceView.Presenter presenter,
                            TreeLoader<Folder> treeLoader,
                            PagingLoader<FolderContentsLoadConfig, PagingLoadResult<DiskResource>> gridLoader);
}
