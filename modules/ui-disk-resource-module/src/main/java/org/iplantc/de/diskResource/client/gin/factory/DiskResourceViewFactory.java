package org.iplantc.de.diskResource.client.gin.factory;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.presenters.proxy.FolderContentsLoadConfig;
import org.iplantc.de.diskResource.client.NavigationView;

import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;

/**
 * @author jstroot
 */
public interface DiskResourceViewFactory {
    DiskResourceView create(DiskResourceView.Presenter presenter,
                            NavigationView.Presenter navigationPresenter,
                            PagingLoader<FolderContentsLoadConfig, PagingLoadResult<DiskResource>> gridLoader);
}
