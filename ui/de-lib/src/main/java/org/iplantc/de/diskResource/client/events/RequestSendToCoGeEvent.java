package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.diskResource.client.events.RequestSendToCoGeEvent.RequestSendToCoGeEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class RequestSendToCoGeEvent extends GwtEvent<RequestSendToCoGeEventHandler> {

    public static final GwtEvent.Type<RequestSendToCoGeEventHandler> TYPE = new GwtEvent.Type<>();

    public interface RequestSendToCoGeEventHandler extends EventHandler {
        void onRequestSendToCoGe(RequestSendToCoGeEvent event);
    }

    private final File file;

    public RequestSendToCoGeEvent(File file) {
        this.file = file;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<RequestSendToCoGeEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RequestSendToCoGeEventHandler handler) {
        handler.onRequestSendToCoGe(this);

    }

    public File getFile() {
        return file;
    }

}
