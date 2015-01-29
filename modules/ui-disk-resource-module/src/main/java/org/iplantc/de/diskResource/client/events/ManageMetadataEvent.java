package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.diskResources.DiskResource;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author jstroot
 */
public class ManageMetadataEvent extends GwtEvent<ManageMetadataEvent.ManageMetadataEventHandler> {

    public interface ManageMetadataEventHandler extends EventHandler {
        void onRequestManageMetadata(ManageMetadataEvent event);
    }

    public static interface HasManageMetadataEventHandlers {
        HandlerRegistration addManageMetadataEventHandler(ManageMetadataEventHandler handler);
    }

    private final DiskResource diskResource;

    public ManageMetadataEvent(DiskResource diskResource){
        this.diskResource = diskResource;
    }

    public static final Type<ManageMetadataEventHandler> TYPE = new Type<>();

    @Override
    public Type<ManageMetadataEventHandler> getAssociatedType() {
        return TYPE;
    }

    public DiskResource getDiskResource() {
        return diskResource;
    }

    @Override
    protected void dispatch(ManageMetadataEventHandler handler) {
        handler.onRequestManageMetadata(this);
    }
}
