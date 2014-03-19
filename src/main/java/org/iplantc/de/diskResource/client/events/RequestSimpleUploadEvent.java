package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.diskResource.client.events.RequestSimpleUploadEvent.RequestSimpleUploadEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class RequestSimpleUploadEvent extends GwtEvent<RequestSimpleUploadEventHandler> {
    public interface RequestSimpleUploadEventHandler extends EventHandler {

        void onRequestSimpleUpload(RequestSimpleUploadEvent event);

    }

    public static final GwtEvent.Type<RequestSimpleUploadEventHandler> TYPE = new GwtEvent.Type<RequestSimpleUploadEventHandler>();
    private final Folder destinationFolder;

    public RequestSimpleUploadEvent(Object source, final Folder destinationFolder) {
        setSource(source);
        this.destinationFolder = destinationFolder;
    }

    @Override
    public GwtEvent.Type<RequestSimpleUploadEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RequestSimpleUploadEventHandler handler) {
        handler.onRequestSimpleUpload(this);
    }

    public Folder getDestinationFolder() {
        return destinationFolder;
    }

}
