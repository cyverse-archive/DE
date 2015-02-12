package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.diskResource.client.events.RequestSendToEnsemblEvent.RequestSendToEnsemblEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class RequestSendToEnsemblEvent extends GwtEvent<RequestSendToEnsemblEventHandler> {

    public static final GwtEvent.Type<RequestSendToEnsemblEventHandler> TYPE = new GwtEvent.Type<>();

    public interface RequestSendToEnsemblEventHandler extends EventHandler {

        void onRequestSendToEnsembl(RequestSendToEnsemblEvent event);
    }

    private final File file;
    private final InfoType infoType;

    public RequestSendToEnsemblEvent(File file, InfoType infoType) {
        this.file = file;
        this.infoType = infoType;
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

    public InfoType getInfoType() {
        return infoType;
    }

}
