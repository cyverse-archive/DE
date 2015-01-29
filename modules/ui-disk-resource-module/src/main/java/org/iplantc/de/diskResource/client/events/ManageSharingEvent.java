package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.diskResources.DiskResource;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author jstroot
 */
public class ManageSharingEvent extends GwtEvent<ManageSharingEvent.ManageSharingEventHandler> {

    public interface ManageSharingEventHandler extends EventHandler {
        void onRequestManageSharing(ManageSharingEvent event);
    }

    public static interface HasManageSharingEventHandlers {
        HandlerRegistration addManageSharingEventHandler(ManageSharingEventHandler handler);
    }

    private final DiskResource diskResourceToShare;

    public ManageSharingEvent(DiskResource diskResourceToShare) {
        this.diskResourceToShare = diskResourceToShare;
    }

    public static final Type<ManageSharingEventHandler> TYPE = new Type<>();

    @Override
    public Type<ManageSharingEventHandler> getAssociatedType() {
        return TYPE;
    }

    public DiskResource getDiskResourceToShare() {
        return diskResourceToShare;
    }

    @Override
    protected void dispatch(ManageSharingEventHandler handler) {
        handler.onRequestManageSharing(this);
    }
}
