package org.iplantc.de.apps.client.views.widgets.proxy;

import org.iplantc.de.apps.client.views.widgets.events.AppSearchResultLoadEvent;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.AppList;
import org.iplantc.de.client.models.apps.proxy.AppListLoadResult;
import org.iplantc.de.client.models.apps.proxy.AppSearchAutoBeanFactory;
import org.iplantc.de.commons.client.ErrorHandler;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * An RpcProxy for an AppLoadConfig that will call the searchApp service, then process the JSON results
 * into an AppListLoadResult.
 * 
 * @author psarando
 * 
 */
public class AppSearchRpcProxy extends RpcProxy<FilterPagingLoadConfig, PagingLoadResult<App>> {
    private String lastQueryText = ""; //$NON-NLS-1$

    public String getLastQueryText() {
        return lastQueryText;
    }

    @Override
    public void load(FilterPagingLoadConfig loadConfig,
            final AsyncCallback<PagingLoadResult<App>> callback) {
        // Cache the query text.
        lastQueryText = ""; //$NON-NLS-1$

        // Get the proxy's search params.
        List<FilterConfig> filterConfigs = loadConfig.getFilters();
        if (filterConfigs != null && !filterConfigs.isEmpty()) {
            lastQueryText = filterConfigs.get(0).getValue();
        }

        if (Strings.isNullOrEmpty(lastQueryText)) {
            // nothing to search
            return;
        }

        // Cache the search text for this callback; used to sort the results.
        final String searchText = lastQueryText;
        final AppSearchRpcProxy source = this;

        // Call the searchApp service with this proxy's query.
        ServicesInjector.INSTANCE.getAppServiceFacade().searchApp(lastQueryText, new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                AppAutoBeanFactory factory = GWT.create(AppAutoBeanFactory.class);
                List<App> apps = AutoBeanCodex.decode(factory, AppList.class, result).as().getApps();

                Collections.sort(apps, new AppComparator(searchText));

                // Pass the App list to this proxy's load callback.
                AppListLoadResult searchResult = AppSearchAutoBeanFactory.instance.dataLoadResult().as();
                searchResult.setData(apps);
                callback.onSuccess(searchResult);

                // Fire the search results load event.
                EventBus eventBus = EventBus.getInstance();
                eventBus.fireEvent(new AppSearchResultLoadEvent(source, searchText, apps));
            }

            @Override
            public void onFailure(Throwable caught) {
                // TODO Add user error message or remove post here?
                ErrorHandler.post(caught);
                callback.onFailure(caught);
            }
        });
    }

    private final class AppComparator implements Comparator<App> {
        final String searchTextLowerCase;

        AppComparator(String searchText) {
            searchTextLowerCase = searchText == null ? "" : searchText.toLowerCase(); //$NON-NLS-1$
        }

        @Override
        public int compare(App app1, App app2) {
            String app1Name = app1.getName();
            String app2Name = app2.getName();

            boolean app1NameMatches = app1Name.toLowerCase().contains(searchTextLowerCase);
            boolean app2NameMatches = app2Name.toLowerCase().contains(searchTextLowerCase);

            if (app1NameMatches && !app2NameMatches) {
                // Only app1's name contains the search term, so order it before app2
                return -1;
            }
            if (!app1NameMatches && app2NameMatches) {
                // Only app2's name contains the search term, so order it before app1
                return 1;
            }

            return app1Name.compareToIgnoreCase(app2Name);
        }
    }
}
