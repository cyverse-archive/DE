package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.diskResource.client.events.DiskResourcesDeletedEvent.DiskResourcesDeletedEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import java.util.Collection;

public class DiskResourcesDeletedEvent extends GwtEvent<DiskResourcesDeletedEventHandler> {

    public interface DiskResourcesDeletedEventHandler extends EventHandler {


        void onDiskResourcesDeleted(Collection<DiskResource> resources, Folder parentFolder);
    }

    public static final GwtEvent.Type<DiskResourcesDeletedEventHandler> TYPE = new GwtEvent.Type<DiskResourcesDeletedEventHandler>();
    private final Collection<DiskResource> resources;
    private final Folder parentFolder;

    public DiskResourcesDeletedEvent(Collection<DiskResource> resources, Folder parentFolder) {
        this.resources = resources;
        this.parentFolder = parentFolder;
    }

    @Override
    public GwtEvent.Type<DiskResourcesDeletedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DiskResourcesDeletedEventHandler handler) {
        handler.onDiskResourcesDeleted(resources, parentFolder);
    }
}
