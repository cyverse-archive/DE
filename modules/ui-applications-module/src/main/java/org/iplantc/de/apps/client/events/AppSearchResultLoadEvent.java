package org.iplantc.de.apps.client.events;

import org.iplantc.de.client.models.apps.App;

import com.google.common.base.Preconditions;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.List;

/**
 * A GwtEvent used to notify listeners that App search results have been loaded from the search service.
 * 
 * @author psarando, jstroot
 * 
 */
public class AppSearchResultLoadEvent extends GwtEvent<AppSearchResultLoadEvent.AppSearchResultLoadEventHandler> {

    public interface AppSearchResultLoadEventHandler extends EventHandler {

        void onAppSearchResultLoad(AppSearchResultLoadEvent event);
    }

    public static interface HasAppSearchResultLoadEventHandlers {
        HandlerRegistration addAppSearchResultLoadEventHandler(AppSearchResultLoadEventHandler handler);
    }


    public static final GwtEvent.Type<AppSearchResultLoadEventHandler> TYPE = new GwtEvent.Type<>();
    private final String searchPattern;
    private final List<App> results;
    private final String searchText;

    public AppSearchResultLoadEvent(final String searchText,
                                    final String searchPattern,
                                    final List<App> results) {
        Preconditions.checkNotNull(results);
        this.searchText = searchText;
        this.searchPattern = searchPattern;
        this.results = results;
    }

    @Override
    public Type<AppSearchResultLoadEventHandler> getAssociatedType() {
        return TYPE;
    }

    public List<App> getResults() {
        return results;
    }

    public String getSearchText() {
        return searchText;
    }

    public String getSearchPattern() {
        return searchPattern;
    }

    @Override
    protected void dispatch(AppSearchResultLoadEventHandler handler) {
        handler.onAppSearchResultLoad(this);
    }
}
