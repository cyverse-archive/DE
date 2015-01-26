package org.iplantc.de.diskResource.client.gin.factory;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.diskResource.client.NavigationView;
import org.iplantc.de.diskResource.client.views.navigation.NavigationViewDnDHandler;

import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.TreeLoader;

/**
 * Created by jstroot on 1/21/15.
 * @author jstroot
 */
public interface NavigationViewFactory {
    NavigationView create(TreeStore<Folder> treeStore,
                          TreeLoader<Folder> treeLoader,
                          NavigationViewDnDHandler dnDHandler);
}
