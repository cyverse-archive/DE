package org.iplantc.de.admin.apps.client.gin.factory;

import org.iplantc.de.admin.apps.client.AdminAppsGridView;
import org.iplantc.de.admin.apps.client.AdminAppsToolbarView;
import org.iplantc.de.admin.apps.client.AdminAppsView;
import org.iplantc.de.admin.apps.client.AdminCategoriesView;

/**
 * Created by jstroot on 3/9/15.
 * @author jstroot
 */
public interface AdminAppsViewFactory {
    AdminAppsView create(AdminCategoriesView.Presenter categoriesPresenter,
                         AdminAppsToolbarView.Presenter toolbarPresenter,
                         AdminAppsGridView.Presenter gridPresenter);
}
