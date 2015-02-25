package org.iplantc.de.apps.client.gin.factory;

import org.iplantc.de.apps.client.AppsView;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.TreeStore;

/**
 * Created by jstroot on 2/24/15.
 * @author jstroot
 */
public interface AppsViewFactory {
    AppsView create(ListStore<App> listStore, TreeStore<AppCategory> treeStore);
}
