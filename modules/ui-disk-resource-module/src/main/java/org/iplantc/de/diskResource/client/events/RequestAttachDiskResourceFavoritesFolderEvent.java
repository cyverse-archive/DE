package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.diskResource.client.events.RequestAttachDiskResourceFavoritesFolderEvent.RequestAttachDiskResourceFavoritesFolderEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class RequestAttachDiskResourceFavoritesFolderEvent extends GwtEvent<RequestAttachDiskResourceFavoritesFolderEventHandler> {

    public interface RequestAttachDiskResourceFavoritesFolderEventHandler extends EventHandler {
        void onRequest(RequestAttachDiskResourceFavoritesFolderEvent event);
    }

    public static final GwtEvent.Type<RequestAttachDiskResourceFavoritesFolderEventHandler> TYPE = new GwtEvent.Type<RequestAttachDiskResourceFavoritesFolderEvent.RequestAttachDiskResourceFavoritesFolderEventHandler>();

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<RequestAttachDiskResourceFavoritesFolderEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RequestAttachDiskResourceFavoritesFolderEventHandler handler) {
        handler.onRequest(this);

    }

}
