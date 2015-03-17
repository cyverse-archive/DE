package org.iplantc.de.admin.apps.client.gin;

import org.iplantc.de.admin.apps.client.AdminAppsGridView;
import org.iplantc.de.admin.apps.client.AdminAppsToolbarView;
import org.iplantc.de.admin.apps.client.AdminAppsView;
import org.iplantc.de.admin.apps.client.AdminCategoriesView;
import org.iplantc.de.admin.apps.client.gin.factory.AdminAppsGridViewFactory;
import org.iplantc.de.admin.apps.client.gin.factory.AdminAppsToolbarFactory;
import org.iplantc.de.admin.apps.client.gin.factory.AdminAppsViewFactory;
import org.iplantc.de.admin.apps.client.presenter.AdminAppsViewPresenterImpl;
import org.iplantc.de.admin.apps.client.presenter.categories.AdminAppsCategoriesPresenterImpl;
import org.iplantc.de.admin.apps.client.presenter.grid.AdminAppsGridPresenterImpl;
import org.iplantc.de.admin.apps.client.presenter.toolbar.AdminAppsToolbarPresenterImpl;
import org.iplantc.de.admin.apps.client.views.AdminAppViewImpl;
import org.iplantc.de.admin.apps.client.views.grid.AdminAppsGridImpl;
import org.iplantc.de.admin.apps.client.views.toolbar.AdminAppsToolbarViewImpl;
import org.iplantc.de.admin.desktop.client.services.AppAdminServiceFacade;
import org.iplantc.de.admin.desktop.client.services.impl.AppAdminServiceFacadeImpl;
import org.iplantc.de.apps.client.AppCategoriesView;
import org.iplantc.de.apps.client.gin.AppCategoryTreeStoreProvider;
import org.iplantc.de.apps.client.gin.AppListStoreProvider;
import org.iplantc.de.apps.client.gin.factory.AppCategoriesViewFactory;
import org.iplantc.de.apps.client.views.categories.AppCategoriesViewImpl;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.client.services.impl.AppUserServiceFacadeImpl;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.inject.TypeLiteral;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.TreeStore;

/**
 * @author jstroot
 */
public class AdminAppsGinModule extends AbstractGinModule {
    @Override
    protected void configure() {

        bind(new TypeLiteral<TreeStore<AppCategory>>() {
        }).toProvider(AppCategoryTreeStoreProvider.class);
        bind(new TypeLiteral<ListStore<App>>() {})
            .toProvider(AppListStoreProvider.class);

        bind(AppAdminServiceFacade.class).to(AppAdminServiceFacadeImpl.class);
        bind(AppServiceFacade.class).to(AppUserServiceFacadeImpl.class);


        // Main view
        install(new GinFactoryModuleBuilder()
                    .implement(AdminAppsView.class, AdminAppViewImpl.class)
                    .build(AdminAppsViewFactory.class));
        bind(AdminAppsView.AdminPresenter.class).to(AdminAppsViewPresenterImpl.class);

        // Grid
        install(new GinFactoryModuleBuilder()
                    .implement(AdminAppsGridView.class, AdminAppsGridImpl.class)
                    .build(AdminAppsGridViewFactory.class));
        bind(AdminAppsGridView.Presenter.class).to(AdminAppsGridPresenterImpl.class);

        // Categories
        install(new GinFactoryModuleBuilder()
                    .implement(AppCategoriesView.class, AppCategoriesViewImpl.class)
                    .build(AppCategoriesViewFactory.class));
        bind(AdminCategoriesView.Presenter.class).to(AdminAppsCategoriesPresenterImpl.class);

        // Toolbar
        install(new GinFactoryModuleBuilder()
                    .implement(AdminAppsToolbarView.class, AdminAppsToolbarViewImpl.class)
                    .build(AdminAppsToolbarFactory.class));
        bind(AdminAppsToolbarView.Presenter.class).to(AdminAppsToolbarPresenterImpl.class);


    }
}
