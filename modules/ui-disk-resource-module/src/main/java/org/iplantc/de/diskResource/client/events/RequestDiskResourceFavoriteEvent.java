package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.diskResource.client.events.RequestDiskResourceFavoriteEvent.RequestDiskResourceFavoriteEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class RequestDiskResourceFavoriteEvent extends GwtEvent<RequestDiskResourceFavoriteEventHandler> {

    public static final GwtEvent.Type<RequestDiskResourceFavoriteEventHandler> TYPE = new GwtEvent.Type<RequestDiskResourceFavoriteEventHandler>();

    private final DiskResource dr;

    public RequestDiskResourceFavoriteEvent(DiskResource dr) {
        this.dr = dr;
    }

    public DiskResource getDiskResource() {
        return dr;
    }

    @Override
    public Type<RequestDiskResourceFavoriteEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RequestDiskResourceFavoriteEventHandler handler) {
        handler.onFavoriteRequest(this);
    }

    public interface RequestDiskResourceFavoriteEventHandler extends EventHandler {
        void onFavoriteRequest(RequestDiskResourceFavoriteEvent event);
    }

    public static interface HasManageFavoritesEventHandlers {
        HandlerRegistration addManageFavoritesEventHandler(RequestDiskResourceFavoriteEventHandler handler);
    }

}