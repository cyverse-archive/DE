package org.iplantc.de.admin.desktop.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * GwtEvent fired when the CatalogCategoryAdminPanel should be refreshed because the Category hierarchy
 * has been updated.
 * 
 * @author psarando
 * 
 */
public class CatalogCategoryRefreshEvent extends GwtEvent<CatalogCategoryRefreshEventHandler> {

    /**
     * Defines the GWT Event Type.
     * 
     * @see CatalogCategoryRefreshEventHandler
     */
    public static final GwtEvent.Type<CatalogCategoryRefreshEventHandler> TYPE = new GwtEvent.Type<>();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void dispatch(CatalogCategoryRefreshEventHandler handler) {
        handler.onRefresh(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type<CatalogCategoryRefreshEventHandler> getAssociatedType() {
        return TYPE;
    }
}
