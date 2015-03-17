package org.iplantc.de.admin.desktop.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * EventHandler for CatalogCategoryRefreshEvents.
 * 
 * @author psarando
 * 
 */
public interface CatalogCategoryRefreshEventHandler extends EventHandler {

    void onRefresh(CatalogCategoryRefreshEvent event);
}
