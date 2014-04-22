package org.iplantc.de.apps.client.views.widgets.proxy;

import org.iplantc.de.apps.client.views.widgets.events.AppSearchResultLoadEvent;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.AppList;
import org.iplantc.de.client.models.apps.proxy.AppListLoadResult;
import org.iplantc.de.client.models.apps.proxy.AppSearchAutoBeanFactory;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.common.base.Strings;
import com.google.gwt.event.shared.HasHandlers;
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
    private HasHandlers hasHandlers;
    private String lastQueryText = ""; //$NON-NLS-1$
    private final AppServiceFacade appService;
    private final AppSearchAutoBeanFactory appSearchFactory;
    private final AppAutoBeanFactory appFactory;
    private final IplantDisplayStrings displayStrings;
    private IsMaskable maskable;

    public AppSearchRpcProxy(final AppServiceFacade appService,
                             final AppSearchAutoBeanFactory appSearchFactory,
                             final AppAutoBeanFactory appFactory, IplantDisplayStrings displayStrings) {
        this.appService = appService;
        this.appSearchFactory = appSearchFactory;
        this.appFactory = appFactory;
        this.displayStrings = displayStrings;
    }

    public String getLastQueryText() {
        return lastQueryText;
    }

    public void setHasHandlers(HasHandlers hasHandlers){
        this.hasHandlers = hasHandlers;
    }

    public void setMaskable(IsMaskable maskable){
        this.maskable = maskable;
    }

    @Override
    public void load(FilterPagingLoadConfig loadConfig,
            final AsyncCallback<PagingLoadResult<App>> callback) {
        if(maskable != null){
           maskable.mask(displayStrings.loadingMask());
        }
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
        appService.searchApp(lastQueryText, new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                List<App> apps = AutoBeanCodex.decode(appFactory, AppList.class, result).as().getApps();

                Collections.sort(apps, new AppComparator(searchText));

                // Pass the App list to this proxy's load callback.
                AppListLoadResult searchResult = appSearchFactory.dataLoadResult().as();
                searchResult.setData(apps);
                callback.onSuccess(searchResult);
                if(maskable != null){
                    maskable.unmask();
                }

                // Fire the search results load event.
                if(hasHandlers != null){
                    hasHandlers.fireEvent(new AppSearchResultLoadEvent(source, searchText, apps));
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                // TODO Add user error message or remove post here?
                ErrorHandler.post(caught);
                callback.onFailure(caught);

                if(maskable != null){
                    maskable.unmask();
                }
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
