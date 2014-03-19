package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.diskResource.client.events.RequestSimpleDownloadEvent.RequestSimpleDownloadEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import java.util.Set;

public class RequestSimpleDownloadEvent extends GwtEvent<RequestSimpleDownloadEventHandler> {
    public interface RequestSimpleDownloadEventHandler extends EventHandler {

        void onRequestSimpleDownload(RequestSimpleDownloadEvent event);

    }

    public static final GwtEvent.Type<RequestSimpleDownloadEventHandler> TYPE = new GwtEvent.Type<RequestSimpleDownloadEventHandler>();
    private final Set<DiskResource> requestedResources;
    private final Folder currentFolder;

    public RequestSimpleDownloadEvent(Object source, final Set<DiskResource> requestedResources, final Folder currentFolder) {
        setSource(source);
        this.requestedResources = requestedResources;
        this.currentFolder = currentFolder;
    }

    @Override
    public GwtEvent.Type<RequestSimpleDownloadEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RequestSimpleDownloadEventHandler handler) {
        handler.onRequestSimpleDownload(this);
    }

    public Set<DiskResource> getRequestedResources() {
        return requestedResources;
    }

    public Folder getCurrentFolder() {
        return currentFolder;
    }
}
