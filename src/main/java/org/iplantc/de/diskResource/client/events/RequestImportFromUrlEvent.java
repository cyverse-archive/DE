package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.diskResource.client.events.RequestImportFromUrlEvent.RequestImportFromUrlEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class RequestImportFromUrlEvent extends GwtEvent<RequestImportFromUrlEventHandler> {
    public interface RequestImportFromUrlEventHandler extends EventHandler {

        void onRequestUploadFromUrl(RequestImportFromUrlEvent event);

    }

    public static final GwtEvent.Type<RequestImportFromUrlEventHandler> TYPE = new GwtEvent.Type<RequestImportFromUrlEventHandler>();
    private final Folder destinationFolder;

    public RequestImportFromUrlEvent(Object source, final Folder destinationFolder) {
        setSource(source);
        this.destinationFolder = destinationFolder;
    }

    @Override
    public GwtEvent.Type<RequestImportFromUrlEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RequestImportFromUrlEventHandler handler) {
        handler.onRequestUploadFromUrl(this);
    }

    public Folder getDestinationFolder() {
        return destinationFolder;
    }
}
