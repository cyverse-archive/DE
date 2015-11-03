package org.iplantc.de.apps.client.presenter.toolBar.proxy;

import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.BeforeAppSearchEvent;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.proxy.AppListLoadResult;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;

import com.google.common.base.Strings;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.rpc.AsyncCallback;

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
 * @author psarando, jstroot
 * 
 */
public class AppSearchRpcProxy extends RpcProxy<FilterPagingLoadConfig, PagingLoadResult<App>> {
    private HasHandlers hasHandlers;
    private final AppServiceFacade appService;

    public AppSearchRpcProxy(final AppServiceFacade appService) {
        this.appService = appService;
    }

    public void setHasHandlers(HasHandlers hasHandlers){
        this.hasHandlers = hasHandlers;
    }

    @Override
    public void load(FilterPagingLoadConfig loadConfig,
            final AsyncCallback<PagingLoadResult<App>> callback) {
        // Cache the query text.
        String lastQueryText = "";

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
        if(hasHandlers != null){
            hasHandlers.fireEvent(new BeforeAppSearchEvent());
        }

        // Call the searchApp service with this proxy's query.
        appService.searchApp(lastQueryText, new AsyncCallback<AppListLoadResult>() {
            @Override
            public void onSuccess(final AppListLoadResult loadResult) {
                List<App> apps = loadResult.getData();
                // FIXME Sorting should not be done here.
                Collections.sort(apps, new AppComparator(searchText));

                // Fire the search results load event.
                if(hasHandlers != null){
                    // The search service accepts * and ? wildcards, so convert them for the pattern group.
                    String pattern = "(" + searchText.replace("*", ".*").replace('?', '.') + ")";
                    hasHandlers.fireEvent(new AppSearchResultLoadEvent(searchText, pattern, apps));
                }

                // Pass the App list to this proxy's load callback.
                callback.onSuccess(loadResult);
            }

            @Override
            public void onFailure(Throwable caught) {
                // TODO Add user error message or remove post here?
                ErrorHandler.post(caught);
                if(hasHandlers != null){
                    // The search service accepts * and ? wildcards, so convert them for the pattern group.
                    String pattern = "(" + searchText.replace("*", ".*").replace('?', '.') + ")";
                    hasHandlers.fireEvent(new AppSearchResultLoadEvent(searchText, pattern, Collections.<App>emptyList()));
                }
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
