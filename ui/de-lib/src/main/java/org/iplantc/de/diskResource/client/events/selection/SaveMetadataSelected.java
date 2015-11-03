package org.iplantc.de.diskResource.client.events.selection;

import org.iplantc.de.client.models.diskResources.DiskResource;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;



public class SaveMetadataSelected extends
                                 com.google.gwt.event.shared.GwtEvent<SaveMetadataSelected.SaveMetadataSelectedEventHandler> {
    public interface SaveMetadataSelectedEventHandler extends EventHandler {
        void onRequestSaveMetadataSelected(SaveMetadataSelected event);
    }

    public static interface HasSaveMetadataSelectedEventHandlers {
                HandlerRegistration
                addSaveMetadataSelectedEventHandler(SaveMetadataSelectedEventHandler handler);
    }

    private final DiskResource diskResource;

    public SaveMetadataSelected(DiskResource diskResource) {
        this.diskResource = diskResource;
    }

    public static final Type<SaveMetadataSelectedEventHandler> TYPE = new Type<>();

    @Override
    public Type<SaveMetadataSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public DiskResource getDiskResource() {
        return diskResource;
    }

    @Override
    protected void dispatch(SaveMetadataSelectedEventHandler handler) {
        handler.onRequestSaveMetadataSelected(this);
    }
}
