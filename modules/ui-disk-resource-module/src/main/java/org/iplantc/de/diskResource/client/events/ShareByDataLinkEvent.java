package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.diskResources.DiskResource;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author jstroot
 */
public class ShareByDataLinkEvent extends GwtEvent<ShareByDataLinkEvent.ShareByDataLinkEventHandler> {

    public interface ShareByDataLinkEventHandler extends EventHandler {
        void onRequestShareByDataLink(ShareByDataLinkEvent event);
    }

    public static interface HasShareByDataLinkEventHandlers {
        HandlerRegistration addShareByDataLinkEventHandler(ShareByDataLinkEventHandler handler);
    }

    public static final Type<ShareByDataLinkEventHandler> TYPE = new Type<>();
    private final DiskResource diskResourceToShare;

    public ShareByDataLinkEvent(DiskResource diskResourceToShare){
        this.diskResourceToShare = diskResourceToShare;
    }
    @Override
    public Type<ShareByDataLinkEventHandler> getAssociatedType() {
        return TYPE;
    }

    public DiskResource getDiskResourceToShare() {
        return diskResourceToShare;
    }

    @Override
    protected void dispatch(ShareByDataLinkEventHandler handler) {
        handler.onRequestShareByDataLink(this);
    }
}
