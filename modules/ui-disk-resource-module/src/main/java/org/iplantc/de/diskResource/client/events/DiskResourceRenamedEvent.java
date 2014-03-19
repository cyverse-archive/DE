package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.diskResource.client.events.DiskResourceRenamedEvent.DiskResourceRenamedEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class DiskResourceRenamedEvent extends GwtEvent<DiskResourceRenamedEventHandler> {

    public interface DiskResourceRenamedEventHandler extends EventHandler {

        void onRename(DiskResource originalDr, DiskResource newDr);

    }

    public static final GwtEvent.Type<DiskResourceRenamedEventHandler> TYPE = new GwtEvent.Type<DiskResourceRenamedEventHandler>();

    private final DiskResource originalDr;
    private final DiskResource newDr;

    public DiskResourceRenamedEvent(DiskResource originalDr, DiskResource newDr) {
        this.originalDr = originalDr;
        this.newDr = newDr;
    }

    @Override
    public GwtEvent.Type<DiskResourceRenamedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DiskResourceRenamedEventHandler handler) {
        handler.onRename(originalDr, newDr);
    }

}
