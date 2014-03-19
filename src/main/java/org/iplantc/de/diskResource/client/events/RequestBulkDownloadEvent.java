package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.diskResource.client.events.RequestBulkDownloadEvent.RequestBulkDownloadEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import java.util.Set;

public class RequestBulkDownloadEvent extends GwtEvent<RequestBulkDownloadEventHandler> {
    public interface RequestBulkDownloadEventHandler extends EventHandler {

        void onRequestBulkDownload(RequestBulkDownloadEvent event);
    }

    public static final GwtEvent.Type<RequestBulkDownloadEventHandler> TYPE = new GwtEvent.Type<RequestBulkDownloadEventHandler>();
    private final Set<DiskResource> requestedResources;
    private final Folder currentFolder;
    private boolean selectAll;

    public RequestBulkDownloadEvent(Object source,boolean selectAll, final Set<DiskResource> requestedResources, final Folder currentFolder) {
        setSource(source);
        this.setSelectAll(selectAll);
        this.requestedResources = requestedResources;
        this.currentFolder = currentFolder;
    }

    @Override
    public GwtEvent.Type<RequestBulkDownloadEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RequestBulkDownloadEventHandler handler) {
        handler.onRequestBulkDownload(this);
    }

    public Set<DiskResource> getRequestedResources() {
        return requestedResources;
    }

    public Folder getCurrentFolder() {
        return currentFolder;
    }

    /**
     * @return the selectAll
     */
    public boolean isSelectAll() {
        return selectAll;
    }

    /**
     * @param selectAll the selectAll to set
     */
    public void setSelectAll(boolean selectAll) {
        this.selectAll = selectAll;
    }

}
