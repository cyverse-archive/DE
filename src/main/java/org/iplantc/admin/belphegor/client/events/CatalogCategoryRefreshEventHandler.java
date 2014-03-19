package org.iplantc.admin.belphegor.client.events;

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
