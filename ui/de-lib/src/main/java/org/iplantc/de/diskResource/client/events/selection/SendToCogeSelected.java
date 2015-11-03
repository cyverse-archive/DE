package org.iplantc.de.diskResource.client.events.selection;

import org.iplantc.de.client.models.diskResources.DiskResource;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.List;

/**
 * Created by jstroot on 1/30/15.
 * @author jstroot
 */
public class SendToCogeSelected extends GwtEvent<SendToCogeSelected.SendToCogeSelectedHandler> {
    public static interface SendToCogeSelectedHandler extends EventHandler {
        void onSendToCogeSelected(SendToCogeSelected event);
    }

    public static interface HasSendToCogeSelectedHandlers {
        HandlerRegistration addSendToCogeSelectedHandler(SendToCogeSelectedHandler handler);
    }

    public static final Type<SendToCogeSelectedHandler> TYPE = new Type<>();
    private final List<DiskResource> selectedDiskResources;

    public SendToCogeSelected(List<DiskResource> selectedDiskResources) {
        this.selectedDiskResources = selectedDiskResources;
    }


    public Type<SendToCogeSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    public List<DiskResource> getResourcesToSend() {
        return selectedDiskResources;
    }

    protected void dispatch(SendToCogeSelectedHandler handler) {
        handler.onSendToCogeSelected(this);
    }
}
