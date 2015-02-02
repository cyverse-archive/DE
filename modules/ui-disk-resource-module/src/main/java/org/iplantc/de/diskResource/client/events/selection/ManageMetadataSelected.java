package org.iplantc.de.diskResource.client.events.selection;

import org.iplantc.de.client.models.diskResources.DiskResource;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author jstroot
 */
public class ManageMetadataSelected extends GwtEvent<ManageMetadataSelected.ManageMetadataSelectedEventHandler> {

    public interface ManageMetadataSelectedEventHandler extends EventHandler {
        void onRequestManageMetadataSelected(ManageMetadataSelected event);
    }

    public static interface HasManageMetadataSelectedEventHandlers {
        HandlerRegistration addManageMetadataSelectedEventHandler(ManageMetadataSelectedEventHandler handler);
    }

    private final DiskResource diskResource;

    public ManageMetadataSelected(DiskResource diskResource){
        this.diskResource = diskResource;
    }

    public static final Type<ManageMetadataSelectedEventHandler> TYPE = new Type<>();

    @Override
    public Type<ManageMetadataSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public DiskResource getDiskResource() {
        return diskResource;
    }

    @Override
    protected void dispatch(ManageMetadataSelectedEventHandler handler) {
        handler.onRequestManageMetadataSelected(this);
    }
}
