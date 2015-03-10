package org.iplantc.de.admin.apps.client.presenter.toolbar;

import org.iplantc.de.admin.apps.client.AdminAppsToolbarView;
import org.iplantc.de.admin.apps.client.gin.factory.AdminAppsToolbarFactory;
import org.iplantc.de.admin.desktop.client.services.AppAdminServiceFacade;
import org.iplantc.de.apps.client.AppsToolbarView;
import org.iplantc.de.apps.client.presenter.toolBar.proxy.AppSearchRpcProxy;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.proxy.AppSearchAutoBeanFactory;

import com.google.inject.Inject;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;

/**
 * @author jstroot
 */
public class AdminAppsToolbarPresenterImpl implements AdminAppsToolbarView.Presenter {

    private final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader;
    private final AppSearchRpcProxy proxy;
    private final AdminAppsToolbarView view;

    @Inject
    AdminAppsToolbarPresenterImpl(final AppAdminServiceFacade appService,
                                  final AppSearchAutoBeanFactory factory,// FIXME Get rid of this
                                  final AppAutoBeanFactory appFactory,// FIXME Get rid of this
                                  final AppsToolbarView.AppsToolbarAppearance appearance,
                                  final AdminAppsToolbarFactory viewFactory) {
        proxy = new AppSearchRpcProxy(appService, factory, appFactory, appearance);
        loader = new PagingLoader<>(proxy);
        this.view = viewFactory.create(loader);
        proxy.setHasHandlers(view);

    }

    @Override
    public AdminAppsToolbarView getView() {
        return view;
    }

}
