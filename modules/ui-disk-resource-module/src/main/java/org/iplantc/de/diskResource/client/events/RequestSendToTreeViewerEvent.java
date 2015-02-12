package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.diskResource.client.events.RequestSendToTreeViewerEvent.RequestSendToTreeViewerEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class RequestSendToTreeViewerEvent extends GwtEvent<RequestSendToTreeViewerEventHandler> {

    public static final GwtEvent.Type<RequestSendToTreeViewerEventHandler> TYPE = new GwtEvent.Type<>();

    public interface RequestSendToTreeViewerEventHandler extends EventHandler {
        void onRequestSendToTreeViewer(RequestSendToTreeViewerEvent event);
    }

    private final File file;

    public RequestSendToTreeViewerEvent(File file) {
        this.file = file;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<RequestSendToTreeViewerEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RequestSendToTreeViewerEventHandler handler) {
        handler.onRequestSendToTreeViewer(this);
    }

    public File getFile() {
        return file;
    }

}
