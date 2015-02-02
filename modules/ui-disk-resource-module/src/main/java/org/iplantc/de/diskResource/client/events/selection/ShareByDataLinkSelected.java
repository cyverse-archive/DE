package org.iplantc.de.diskResource.client.events.selection;

import org.iplantc.de.client.models.diskResources.DiskResource;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author jstroot
 */
public class ShareByDataLinkSelected extends GwtEvent<ShareByDataLinkSelected.ShareByDataLinkSelectedEventHandler> {

    public interface ShareByDataLinkSelectedEventHandler extends EventHandler {
        void onRequestShareByDataLinkSelected(ShareByDataLinkSelected event);
    }

    public static interface HasShareByDataLinkSelectedEventHandlers {
        HandlerRegistration addShareByDataLinkSelectedEventHandler(ShareByDataLinkSelectedEventHandler handler);
    }

    public static final Type<ShareByDataLinkSelectedEventHandler> TYPE = new Type<>();
    private final DiskResource diskResourceToShare;

    public ShareByDataLinkSelected(DiskResource diskResourceToShare){
        this.diskResourceToShare = diskResourceToShare;
    }
    @Override
    public Type<ShareByDataLinkSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public DiskResource getDiskResourceToShare() {
        return diskResourceToShare;
    }

    @Override
    protected void dispatch(ShareByDataLinkSelectedEventHandler handler) {
        handler.onRequestShareByDataLinkSelected(this);
    }
}
