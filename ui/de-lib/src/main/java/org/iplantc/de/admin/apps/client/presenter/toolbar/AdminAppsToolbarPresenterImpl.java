package org.iplantc.de.admin.apps.client.presenter.toolbar;

import org.iplantc.de.admin.apps.client.AdminAppsToolbarView;
import org.iplantc.de.admin.apps.client.gin.factory.AdminAppsToolbarFactory;
import org.iplantc.de.apps.client.presenter.toolBar.proxy.AppSearchRpcProxy;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.services.AppServiceFacade;

import com.google.inject.Inject;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;

/**
 * @author jstroot
 */
public class AdminAppsToolbarPresenterImpl implements AdminAppsToolbarView.Presenter {

    private final AdminAppsToolbarView view;

    @Inject
    AdminAppsToolbarPresenterImpl(final AppServiceFacade appService,
                                  final AdminAppsToolbarFactory viewFactory) {
        AppSearchRpcProxy proxy = new AppSearchRpcProxy(appService);
        PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader = new PagingLoader<>(proxy);
        this.view = viewFactory.create(loader);
        proxy.setHasHandlers(view);
    }

    @Override
    public AdminAppsToolbarView getView() {
        return view;
    }

}
