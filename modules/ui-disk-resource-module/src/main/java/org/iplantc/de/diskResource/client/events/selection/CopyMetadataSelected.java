package org.iplantc.de.diskResource.client.events.selection;

import org.iplantc.de.client.models.diskResources.DiskResource;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;


public class CopyMetadataSelected extends
                                 GwtEvent<CopyMetadataSelected.CopyMetadataSelectedEventHandler> {

    public interface CopyMetadataSelectedEventHandler extends EventHandler {
        void onRequestCopyMetadataSelected(CopyMetadataSelected event);
    }

    public static interface HasCopyMetadataSelectedEventHandlers {
                HandlerRegistration
                addCopyMetadataSelectedEventHandler(CopyMetadataSelectedEventHandler handler);
    }

    private final DiskResource diskResource;

    public CopyMetadataSelected(DiskResource diskResource) {
        this.diskResource = diskResource;
    }

    public static final Type<CopyMetadataSelectedEventHandler> TYPE = new Type<>();

    @Override
    public Type<CopyMetadataSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public DiskResource getDiskResource() {
        return diskResource;
    }

    @Override
    protected void dispatch(CopyMetadataSelectedEventHandler handler) {
        handler.onRequestCopyMetadataSelected(this);
    }
}
