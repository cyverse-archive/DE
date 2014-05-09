package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.diskResource.client.events.RequestSendToEnsemblEvent.RequestSendToEnsemblEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class RequestSendToEnsemblEvent extends GwtEvent<RequestSendToEnsemblEventHandler> {

    public static final GwtEvent.Type<RequestSendToEnsemblEventHandler> TYPE = new GwtEvent.Type<RequestSendToEnsemblEventHandler>();

    public interface RequestSendToEnsemblEventHandler extends EventHandler {

        void onRequestSendToEnsembl(RequestSendToEnsemblEvent event);
    }

    private final File file;

    public RequestSendToEnsemblEvent(File file) {
        this.file = file;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<RequestSendToEnsemblEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RequestSendToEnsemblEventHandler handler) {
        handler.onRequestSendToEnsembl(this);

    }

    public File getFile() {
        return file;
    }

}
