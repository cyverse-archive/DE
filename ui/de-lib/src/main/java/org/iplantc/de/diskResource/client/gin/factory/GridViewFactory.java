package org.iplantc.de.diskResource.client.gin.factory;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.diskResource.client.GridView;

import com.sencha.gxt.data.shared.ListStore;

/**
 * Created by jstroot on 1/26/15.
 * @author jstroot
 */
public interface GridViewFactory {
    GridView create(GridView.Presenter presenter,
                    ListStore<DiskResource> listStore,
                    GridView.FolderContentsRpcProxy folderContentsRpcProxy);
}
