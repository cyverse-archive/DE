package org.iplantc.de.apps.client.views.widgets.events;

import org.iplantc.de.apps.client.views.widgets.proxy.AppSearchRpcProxy;
import org.iplantc.de.client.models.apps.App;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.List;

/**
 * A GwtEvent used to notify listeners that App search results have been loaded from the search service.
 * 
 * @author psarando
 * 
 */
public class AppSearchResultLoadEvent extends GwtEvent<AppSearchResultLoadEvent.AppSearchResultLoadEventHandler> {

    public interface AppSearchResultLoadEventHandler extends EventHandler {

        void onAppSearchResultLoad(AppSearchResultLoadEvent event);
    }

    public static interface HasAppSearchResultLoadEventHandlers {
        HandlerRegistration addAppSearchResultLoadEventHandler(AppSearchResultLoadEventHandler handler);
    }


    public static final GwtEvent.Type<AppSearchResultLoadEventHandler> TYPE = new GwtEvent.Type<AppSearchResultLoadEventHandler>();
    private List<App> results;
    private String searchText;

    public AppSearchResultLoadEvent(AppSearchRpcProxy proxy, String searchText, List<App> results) {
        setSource(proxy);
        setSearchText(searchText);
        setResults(results);
    }

    @Override
    public Type<AppSearchResultLoadEventHandler> getAssociatedType() {
        return TYPE;
    }

    public List<App> getResults() {
        return results;
    }

    public void setResults(List<App> results) {
        this.results = results;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    @Override
    protected void dispatch(AppSearchResultLoadEventHandler handler) {
        handler.onAppSearchResultLoad(this);
    }
}
