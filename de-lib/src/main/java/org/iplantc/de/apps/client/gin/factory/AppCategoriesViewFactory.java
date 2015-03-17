package org.iplantc.de.apps.client.gin.factory;

import org.iplantc.de.apps.client.AppCategoriesView;
import org.iplantc.de.client.models.apps.AppCategory;

import com.sencha.gxt.data.shared.TreeStore;

/**
 * Created by jstroot on 3/5/15.
 * @author jstroot
 */
public interface AppCategoriesViewFactory {
    AppCategoriesView create(TreeStore<AppCategory> treeStore,
                             AppCategoriesView.AppCategoryHierarchyProvider hierarchyProvider);
}
